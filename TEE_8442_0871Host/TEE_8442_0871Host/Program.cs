using Intel.Dal;
using System;
using System.Text;
using System.IO;
using System.Linq;
using System.Security.Cryptography;

namespace TEE_8442_0871Host
{
    class Program
    {
        enum Commands
        {
            REGISTER = 1, LOGIN = 2, RESET = 3, GENERATE_PAIR_KEYS = 4,
            GET_PUBLIC_KEY = 5, SIGN_MSG = 6
        };

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

            PrintMenu();

            int cmd = int.Parse(Console.ReadLine());
            bool success = false;

            while(cmd >= 1 && cmd <= 6)
            {
                switch(cmd)
                {
                    case (int)Commands.REGISTER:
                    {
                        success = Register();

                        if (!success)
                            Console.WriteLine("User already registered");
                        else
                            Console.WriteLine("Successfully registered!");
                        break;
                    }

                    case (int)Commands.LOGIN:
                    {
                        success = Login();

                        if (!success)
                            Console.WriteLine("Wrong password");
                        else
                            Console.WriteLine("Succefuly login!");
                        break;
                    }

                    case (int)Commands.RESET:
                    {
                        try
                        {
                            ResetPassword();
                            Console.WriteLine("Successfully reset password!");
                        }
                        catch (EXUserNotLoggedIn ex)
                        {
                            Console.WriteLine(ex.ToString());
                        }
                        break;
                    }

                    case (int)Commands.GENERATE_PAIR_KEYS:
                    {
                        try
                        {
                            generatePairKeys();
                            Console.WriteLine("Generated public and private keys!");
                        }
                        catch (EXUserNotLoggedIn ex)
                        {
                            Console.WriteLine(ex.ToString());
                        }
                        break;
                    }

                    case (int)Commands.GET_PUBLIC_KEY:
                    {
                        try
                        {
                            Console.WriteLine(ConvertByteArrayToString(getPublicKey()));
                        }
                        catch (EXUserNotLoggedIn ex)
                        {
                            Console.WriteLine(ex.ToString());
                        }
                        catch (EXNoKeyGenerated ex)
                        {
                            Console.WriteLine(ex.ToString());
                        }
                        break;
                    }

                    case (int)Commands.SIGN_MSG:
                    {
                        try
                        {
                            signMessage(); //this method prints out signed msg and verifies...
                        }
                        catch (EXUserNotLoggedIn ex)
                        {
                            Console.WriteLine(ex.ToString());
                        }
                        catch (EXNoKeyGenerated ex)
                        {
                            Console.WriteLine(ex.ToString());
                        }
                        break;
                    }

                    default:
                    {
                        Console.WriteLine("GoodBye !!");
                        break;
                    }
                }

                Console.WriteLine("\nEnter next command: ");
                try
                {
                    Int32.TryParse(Console.ReadLine(), out cmd);
                }
                catch (Exception)
                {
                    Console.WriteLine("ERROR: input isn't a number - please try again!");
                }
            }

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

        static void PrintMenu()
        {
            Console.WriteLine("\nChoose one of the following:");
            Console.WriteLine((int)Commands.REGISTER + ": to register");
            Console.WriteLine((int)Commands.LOGIN + ": to login");
            Console.WriteLine((int)Commands.RESET + ": to reset password");
            Console.WriteLine((int)Commands.GENERATE_PAIR_KEYS + ": to generate a pair of public and private keys");
            Console.WriteLine((int)Commands.GET_PUBLIC_KEY + ": to get the applet's public key");
            Console.WriteLine((int)Commands.SIGN_MSG + ": to send a msg, " +
                "and receive an ecnrypted msg (signed with Applet's private key)");
            Console.WriteLine("anything else: for exit\n\n");
        }

        static bool Register()
        {
            Console.WriteLine("Enter your password: ");
            string text = Console.ReadLine();
            byte[] password = System.Text.Encoding.UTF8.GetBytes(text);

            byte[] recvBuff = new byte[2000];
            int responseCode;
            int cmdId = (int)Commands.REGISTER;
            jhi.SendAndRecv2(session, cmdId, password, ref recvBuff, out responseCode);
            return responseCode == RESPONSE_SUCCESS; // if the responseCode is 1, the request succesfuly delivered
        }

        static bool Login()
        {
            Console.WriteLine("Enter your password: ");
            string text = Console.ReadLine();
            byte[] password = System.Text.Encoding.UTF8.GetBytes(text);

            byte[] recvBuff = new byte[2000];
            int responseCode;
            int cmdId = (int)Commands.LOGIN;
            jhi.SendAndRecv2(session, cmdId, password, ref recvBuff, out responseCode);
            return responseCode == RESPONSE_SUCCESS; // if the responseCode is 1, the request succesfuly delivered
        }

        static bool ResetPassword()
        {
            Console.WriteLine("Enter new password: ");
            string text = Console.ReadLine();
            byte[] password = System.Text.Encoding.UTF8.GetBytes(text);

            byte[] recvBuff = new byte[2000];
            int responseCode;
            int cmdId = (int)Commands.RESET;
            jhi.SendAndRecv2(session, cmdId, password, ref recvBuff, out responseCode);
            return responseCode == RESPONSE_SUCCESS; // if the responseCode is 1, the request succesfuly delivered
        }

        static void generatePairKeys() //tells applet to generate the key, save in flash storage
        {
            byte[] recvBuff = new byte[1000];
            int responseCode;
            int cmdId = (int)Commands.GENERATE_PAIR_KEYS;
            jhi.SendAndRecv2(session, cmdId, new byte[0], ref recvBuff, out responseCode);
            if (responseCode == RES_FAIL_NOT_LOGGED_IN)
                throw new EXUserNotLoggedIn();
        }

        static void signMessage()
        {
            //receive message,
            string origStringMsg = "shalom";
            
            //ask applet to sign with its private key, receive signed message
            byte[] signedMsg = new byte[1000];
            int responseCode;
            int cmdId = (int)Commands.SIGN_MSG;
            jhi.SendAndRecv2(session, cmdId, Encoding.ASCII.GetBytes(origStringMsg), ref signedMsg, out responseCode);
           
            if (responseCode == RES_FAIL_NOT_LOGGED_IN)
                throw new EXUserNotLoggedIn();
            if (responseCode == RES_FAIL_NO_KEY_GENERATED)
                throw new EXNoKeyGenerated();

            //verify with applet's public key...
            Console.WriteLine("original message: " + origStringMsg.ToString() + "\n");
            Console.WriteLine("signed message: " + ConvertByteArrayToString(signedMsg).ToString() + "\n");

            byte[] publicKeyBtyeArr = getPublicKey();

            RSACryptoServiceProvider rsaObj = new RSACryptoServiceProvider();
            //MUST  SET RSAOBJ's public key and modulus based on "publicKeyByteArr"...
            /* otherwise, it is checking the data with the wrong key..
             * somewhow put parameters in here: CspParameters csp = new CspParameters(); ??
             *
             * https://docs.microsoft.com/en-us/dotnet/api/system.security.cryptography.rsacryptoserviceprovider.-ctor?view=net-6.0#system-security-cryptography-rsacryptoserviceprovider-ctor(system-security-cryptography-cspparameters)
             */

            bool verified = rsaObj.VerifyData(
                Encoding.ASCII.GetBytes(origStringMsg), SHA256.Create(), signedMsg
                );
            
            if (verified)
                Console.WriteLine("verified!");
            else
                Console.WriteLine("msg was not verified correctly..");
        }

        void foo()
        {
            RSACryptoServiceProvider RSAalg = new RSACryptoServiceProvider();

            RSAParameters Key = RSAalg.ExportParameters(true);
            Console.WriteLine(Key.Exponent);
            //this link? https://stackoverflow.com/questions/34080813/how-to-set-rsacryptoserviceprovider-public-key
        }

        static byte[] getPublicKey()
        {
            byte[] recvBuff = new byte[1000];
            int responseCode;
            int cmdId = (int)Commands.GET_PUBLIC_KEY;
            jhi.SendAndRecv2(session, cmdId, new byte[0], ref recvBuff, out responseCode);
            if (responseCode == RES_FAIL_NOT_LOGGED_IN)
                throw new EXUserNotLoggedIn();
            else if (responseCode == RES_FAIL_NO_KEY_GENERATED)
                throw new EXNoKeyGenerated();
            return recvBuff; //receives public key in byte[] format..
        }

        static public StringBuilder ConvertByteArrayToString(byte[] bytes)
        {
            //this func only works for "int" in byte array??
            var sb = new StringBuilder("");
            foreach (var b in bytes)
            {
                sb.Append(b + " ");
            }
            return sb;
        }
    }
}


namespace TEE_8442_0871Host
{
    class EXNoAccessToApplet : Exception
    {
        public override string ToString()
        {
            return "Can't access the Applet";
        }
    }

    class EXUserNotLoggedIn : EXNoAccessToApplet
    {
        public override string ToString()
        {
            return base.ToString() + "- User not logged in.";
        }
    }
    class EXNoKeyGenerated : EXNoAccessToApplet
    {
        public override string ToString()
        {
            return base.ToString() + "- no key generated!";
        }
    }
}