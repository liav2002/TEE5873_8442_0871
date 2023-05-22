using Intel.Dal;
using System;
using System.Text;
using System.Security.Cryptography;
using System.Net;
using System.Net.Sockets;
using System.Collections.Generic;
using System.IO;

namespace TEE_8442_0871Host
{
    class HostProgram
    {
        enum AppletOperation
        {
            REGISTER = 1, LOGIN = 2, RESET_PASSWRD = 3, GENERATE_PAIR_KEYS = 4,
            GET_PUBLIC_KEY = 5, SIGN_MSG = 6, RESET_FLASH = 7,
            SET_SEED = 8, GET_OTP = 9
        };
        enum ServerOperation
        {
            REGISTER = 0, //sends UserId, gets a seed..
            LOGIN = 1,      //sends userID and OTP, trys to enter
            EXIT = 2        //stops connection with server (to close server program)
        }

        //response codes for server:
        private static readonly string END_OF_MESSAGE = "<EOF>";
        private static readonly string SPACE = " ";

        //response codes from applet:
        const int RES_FAIL_NOT_LOGGED_IN = 0;
        const int RESPONSE_SUCCESS = 1;
        const int RES_FAIL_NO_KEY_GENERATED = 2;
        const int RES_KEY_ALREADY_GENERATED = 3;


        static public JhiSession session;
        static public Jhi jhi;

        static void Main(string[] args)
        {
            /************************************************************************************************************
             *                                              START                                                       *
             ***********************************************************************************************************/

#if AMULET
            // When compiled for Amulet the Jhi.DisableDllValidation flag is set to true 
            // in order to load the JHI.dll without DLL verification.
            // This is done because the JHI.dll is not in the regular JHI installation folder, 
            // and therefore will not be found by the JhiSharp.dll.
            // After disabling the .dll validation, the JHI.dll will be loaded using the Windows search path
            // and not by the JhiSharp.dll (see http://msdn.microsoft.com/en-us/library/7d83bc18(v=vs.100).aspx for 
            // details on the search path that is used by Windows to locate a DLL) 
            // In this case the JHI.dll will be loaded from the $(OutDir) folder (bin\Amulet by default),
            // which is the directory where the executable module for the current process is located.
            // The JHI.dll was placed in the bin\Amulet folder during project build.
            Jhi.DisableDllValidation = true;
#endif
            jhi = Jhi.Instance;

            // This is the UUID of this Trusted Application (TA).
            //The UUID is the same value as the applet.id field in the Intel(R) DAL Trusted Application manifest.
            string appletID = "d29f1599-d31a-4d31-9a63-01b67e5b5a48";
            // This is the path to the Intel Intel(R) DAL Trusted Application .dalp file that was created by the Intel(R) DAL Eclipse plug-in.
            string project_directory = Directory.GetParent(Environment.CurrentDirectory).Parent.Parent.Parent.FullName;
            string dalpPath = "\\TEE_8442_0871\\bin\\TEE_8442_0871.dalp";
            Console.Write("$appletPath = " + project_directory + dalpPath + "\n\n");
            string appletPath = project_directory + dalpPath;

            // Install the Trusted Application
            Console.WriteLine("Installing the applet.");
            jhi.Install(appletID, appletPath);

            // Start a session with the Trusted Application
            byte[] initBuffer = new byte[] { }; // Data to send to the applet onInit function
            Console.WriteLine("Opening a session.");
            jhi.CreateSession(appletID, JHI_SESSION_FLAGS.None, initBuffer, out session);

            /************************************************************************************************************
             *                                               BODY                                                       *
             ***********************************************************************************************************/
            int userId = 0;
            int seed = 10;

            // send seed to be saved
            byte[] seedByte = new byte[20];
            ConvertIntToByteArr(seed).CopyTo(seedByte, 0);
            sendToApplet((int)AppletOperation.SET_SEED, seedByte);

            // get otp
            byte[] otp = getOTPFromApplet();

            // verify with server
            bool success = sendOTP_toServer(userId, otp);

            //Close Server's session
            sendAndRecvFromServer(Encoding.ASCII.GetBytes(((int)ServerOperation.EXIT).ToString() + END_OF_MESSAGE), new byte[0]);
            Console.WriteLine("closed the server");

            /************************************************************************************************************
             *                                               END                                                        *
             ***********************************************************************************************************/

            // Close the session
            Console.WriteLine("Closing the session.");
            jhi.CloseSession(session);

            //Uninstall the Trusted Application
            Console.WriteLine("Uninstalling the applet.");
            jhi.Uninstall(appletID);

            Console.WriteLine("Press Enter to finish.");
            Console.Read();
        }

        public static void sendAndRecvFromServer(byte[] msgToSend, byte[] msgToRecv)
        {
            try
            {
                // Connect to a Remote server
                // Get Host IP Address that is used to establish a connection
                // In this case, we get one IP address of localhost that is IP : 127.0.0.1
                // If a host has multiple addresses, you will get a list of addresses
                IPHostEntry host = Dns.GetHostEntry("localhost"); //gets {127.0.0.1}
                IPAddress ipAddress = host.AddressList[0];
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, 12000);

                // Create a TCP/IP  socket.
                Socket sender = new Socket(ipAddress.AddressFamily,
                    SocketType.Stream, ProtocolType.Tcp);

                // Connect the socket to the remote endpoint. Catch any errors.
                try
                {
                    // Connect to Remote EndPoint
                    sender.Connect(remoteEP); //throws exception if server's socket is closed

                    Console.WriteLine("Socket connected to {0}",
                        sender.RemoteEndPoint.ToString());

                    // Send the data through the socket.
                    int bytesSent = sender.Send(msgToSend); //sends Msg received as parameter

                    // Receive the response from the remote device.
                    int bytesRec = sender.Receive(msgToRecv); //puts received msg into parameter...
                    Console.WriteLine("Echoed test = {0}",
                        Encoding.ASCII.GetString(msgToRecv, 0, bytesRec));

                    // Release the socket.
                    Console.WriteLine("Closing socket");
                    sender.Shutdown(SocketShutdown.Both);
                    sender.Close();
                }
                catch (ArgumentNullException ane)
                {
                    Console.WriteLine("ArgumentNullException : {0}", ane.ToString());
                }
                catch (SocketException se) //throw when tries to connect to server's socket, but it is not open..
                {
                    Console.WriteLine("SocketException : {0}", se.ToString());
                }
                catch (Exception e)
                {
                    Console.WriteLine("Unexpected exception : {0}", e.ToString());
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        private static int registerAndGetSeed(int userId)  //Registers with Server, get's seed (for the OTP)
        {
            byte[] recvMsg = new byte[100]; //to receive userId, and "seed" from Server
            // Encode the data string into a byte array
            byte[] msg = Encoding.ASCII.GetBytes(((int)ServerOperation.REGISTER).ToString() + SPACE +
                userId.ToString() + END_OF_MESSAGE);
            sendAndRecvFromServer(msg, recvMsg);

            //  string receivedStr = ConvertByteArrayToString(recvMsg).ToString();
            string receivedStr = Encoding.ASCII.GetString(recvMsg, 0, recvMsg.Length);
            //not good to use "recvMsg.Length" ... should think of better solution

            int counter = 0;
            string bufferStr = "";

            while (receivedStr[counter] != END_OF_MESSAGE[0])
            {
                bufferStr += receivedStr[counter++];
            }
            return Int32.Parse(bufferStr);
        }

        private static bool sendToApplet(int cmd, byte[] seedFromServer)
        {
            //used to send Seed to Applet
            byte[] recvBuff = new byte[2000];
            int responseCode;
            jhi.SendAndRecv2(session, cmd, seedFromServer, ref recvBuff, out responseCode);
            return responseCode == RESPONSE_SUCCESS; // if the responseCode is 1, the request succesfuly delivered
        }

        private static byte[] getOTPFromApplet()
        { 
            byte[] recvBuff = new byte[2000];
            int responseCode;
            DateTimeOffset currentTime = DateTimeOffset.Now;
            long unixTimestampSeconds = (long)currentTime.Subtract(new DateTimeOffset(1970, 1, 1, 0, 0, 0, TimeSpan.Zero)).TotalSeconds;
            byte[] epochTime = BitConverter.GetBytes(unixTimestampSeconds);

            jhi.SendAndRecv2(session, (int)AppletOperation.GET_OTP, epochTime, ref recvBuff, out responseCode);

            Console.WriteLine("received time seed: " + BitConverter.ToInt32(recvBuff, 0).ToString());

            if (responseCode == RESPONSE_SUCCESS)
                return recvBuff;
            else
                throw new Exception("problem");
        }

        private static bool sendOTP_toServer(int userId, byte[] otp)
        {

            return false; // not implemented yet.
        }

        private static byte[] ConvertIntToByteArr(int intValue)
        {
            byte[] intBytes = BitConverter.GetBytes(intValue);
            if (BitConverter.IsLittleEndian)
                Array.Reverse(intBytes);
            return intBytes;
        }

        static private byte[] concatByteArr(params byte[][] arrays)
        {
            List<byte> resList = new List<byte>();

            for (int i = 0; i < arrays.GetLength(0); i++)
            {
                for (int j = 0; j < arrays[i].GetLength(0); j++)
                {
                    resList.Add(arrays[i][j]);
                }
            }
            return resList.ToArray();
        }

        static public StringBuilder ConvertByteArrayToString(byte[] bytes)
        {
            var sb = new StringBuilder("");
            foreach (var b in bytes)
            {
                sb.Append(b + " ");
            }
            return sb;
        }
    }
}