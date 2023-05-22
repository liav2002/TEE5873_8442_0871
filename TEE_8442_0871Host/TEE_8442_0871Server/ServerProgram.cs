using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Security.Cryptography;
using System.Text;

namespace TEE_8442_0871Server
{
    public class DataBlock
    {
        private int randomSeed;
        private int userId;

        public DataBlock(int _userId, int _randomSeed)
        {
            userId = _userId;
            randomSeed = _randomSeed;
        }
    }

    enum HostOperation
    {
        REGISTER = 0,  //Client (Host) sends UserId, gets a seed..
        LOGIN = 1,    //Client (Host) sends userID and OTP, trys to enter
        EXIT = 2       //stops connection with server (to close server program)

    }

    public class ServerProgram
    {
        private static readonly string END_OF_MESSAGE = "<EOF>";
        private static readonly string SPACE = " ";
        private static readonly List<DataBlock> lstDataBlock = new List<DataBlock>();

        public static int Main(string[] args)
        {
            Console.WriteLine("SERVER'S CONSOLE:");

            var host = Dns.GetHostEntry("localhost");
            var ipAddress = host.AddressList[0];
            var localEndPoint = new IPEndPoint(ipAddress, 12000);

            startServer(ipAddress.AddressFamily, localEndPoint);

            Console.WriteLine("\n Press any key to continue...");
            Console.ReadKey();

            return 0;
        }

        public static bool startServer(AddressFamily addressFamily, IPEndPoint localEndPoint)
        {

            try
            { 
                Socket listener = new Socket(addressFamily, SocketType.Stream, ProtocolType.Tcp);

                listener.Bind(localEndPoint);

                bool keepListening = true;

                while (keepListening)
                {
                    listener.Listen(10);

                    Console.WriteLine("Waiting for a connection...");
                    Socket handler = listener.Accept(); 
                    Console.WriteLine("Message came thru! processing...");

                    string data = null;
                    byte[] recvdBytes = null; 

                    while (true)
                    {
                        recvdBytes = new byte[1024];
                        var numBytesRec = handler.Receive(recvdBytes);
                        data += Encoding.ASCII.GetString(recvdBytes, 0, numBytesRec);

                        if (data.IndexOf(END_OF_MESSAGE) > -1) break;
                    }

                    Console.WriteLine("Text received : {0}", data);

                    int sizeOfData = data.Length;
                    string recvString = "";
                    for (var i = 0; i < sizeOfData; i++) recvString += data[i];

                    string cmdString = "";
                    int counter = 0;

                    while (recvString[counter] != SPACE[0]
                        && recvString[counter] != END_OF_MESSAGE[0])
                    //get all letters of command id until we hit a space or end of message
                    {
                        cmdString += recvString.Substring(counter, counter + 1);
                        counter++;
                    }

                    //get cmd id
                    int command = Int32.Parse(cmdString);

                    //take off cmd id of string:
                    recvString = recvString.Substring(counter, recvString.Length - cmdString.Length);
                    if (recvString[0] == SPACE[0]) //remove space..
                        recvString = recvString.Substring(SPACE.Length, recvString.Length - SPACE.Length);

                    byte[] msgToSend = new byte[0]; //we will overwrite this memory allocation..

                    //take off end of message:
                    recvString = recvString.Substring(0, recvString.Length - END_OF_MESSAGE.Length);


                    switch (command)
                    {
                        case (int)HostOperation.REGISTER:
                            msgToSend = Encoding.ASCII.GetBytes(registerUser(recvString));
                            break;
                        case (int)HostOperation.LOGIN:
                            //gets userId, generates otp from seed (saved in , and verify with client's otp
                            break;
                        case (int)HostOperation.EXIT:
                            keepListening = false;
                            break;
                        default:
                            Console.WriteLine("error! received bad command id...");
                            break;
                    }

                    handler.Send(msgToSend);
                    handler.Shutdown(SocketShutdown.Send);
                    handler.Close();
                }
                //listener.Shutdown(SocketShutdown.Receive);
                listener.Close();
            }

            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }

            return true;
        }

        public static string registerUser(string receivedMsg)
        //registers User, generates and sends random seed for OTP
        //returns msg to send back to Client (Host)
        {
            var userId = int.Parse(receivedMsg);

            var rnd = new Random();
            var randomSeed = rnd.Next(1, 100);

            lstDataBlock.Add(new DataBlock(userId, randomSeed));

            return randomSeed + END_OF_MESSAGE;
        }

        public static byte[] getOTP(int seed)
        {
            // get the otp based on the seed and the time
            int hour = 60 * 60;  // number of seconds in hour 
            long current_time = DateTimeOffset.Now.ToUnixTimeSeconds();
            int time_slot = (int)(current_time / hour);

            long the_calculate_seed = time_slot * seed;

            byte[] otp = new SHA1Managed().ComputeHash(
                BitConverter.GetBytes(the_calculate_seed)
                );
            return otp;
        }
    }
}
