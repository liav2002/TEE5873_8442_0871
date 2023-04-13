using Intel.Dal;
using System;
using System.Text;
using System.IO;
using System.Linq;

namespace TEE_8442_0871Host
{
    class Program
    {
        enum Commands { Register = 1, Login = 2, Reset = 3, GetRandom = 4 };
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

            while(cmd >= 1 && cmd <= 4)
            {
                switch(cmd)
                {
                    case (int)Commands.Register:
                    {
                        success = Register();

                        if (!success)
                            Console.WriteLine("User already registered");
                        else
                            Console.WriteLine("Successfully registered!");
                        break;
                    }

                    case (int)Commands.Login:
                    {
                        success = Login();

                        if (!success)
                            Console.WriteLine("Wrong password !!");
                        else
                            Console.WriteLine("Successfully login!");
                        break;
                    }

                    case (int)Commands.Reset:
                    {
                        success = ResetPassword();

                        if (!success)
                            Console.WriteLine("User is not login.");
                        else
                            Console.WriteLine("Successfully reset password!");
                        break;
                    }

                    case (int)Commands.GetRandom:
                    {
                        Console.WriteLine("Enter number of bytes: ");
                        int length = int.Parse(Console.ReadLine());

                        byte[] random = new byte[length];
                        success = GetRandom(length, ref random);

                        if (!success)
                            Console.WriteLine("User not login");
                        else
                        {
                            Console.WriteLine("The random bytes: ");
                            Console.WriteLine(PrintByteArray(random).ToString());
                        }
                        break;
                    }

                    default:
                    {
                        Console.WriteLine("GoodBye !!");
                        break;
                    }
                }

                Console.WriteLine("Enter next command: ");
                cmd = int.Parse(Console.ReadLine());
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
            Console.WriteLine("Commands:");
            Console.WriteLine((int)Commands.Register + ": register.");
            Console.WriteLine((int)Commands.Login + ": login.");
            Console.WriteLine((int)Commands.Reset + ": reset password (login required first).");
            Console.WriteLine((int)Commands.GetRandom + ": get random number (login required first).");
            Console.WriteLine("anything else: for exit.");
            Console.WriteLine("");
        }

        static bool Register()
        {
            Console.WriteLine("Enter your password: ");
            string text = Console.ReadLine();
            byte[] password = System.Text.Encoding.UTF8.GetBytes(text);

            byte[] recvBuff = new byte[2000];
            int responseCode;
            int cmdId = (int)Commands.Register;
            jhi.SendAndRecv2(session, cmdId, password, ref recvBuff, out responseCode);
            return responseCode == 1; // if the responseCode is 1, the request succesfuly delivered
        }

        static bool Login()
        {
            Console.WriteLine("Enter your password: ");
            string text = Console.ReadLine();
            byte[] password = System.Text.Encoding.UTF8.GetBytes(text);

            byte[] recvBuff = new byte[2000];
            int responseCode;
            int cmdId = (int)Commands.Login;
            jhi.SendAndRecv2(session, cmdId, password, ref recvBuff, out responseCode);
            return responseCode == 1; // if the responseCode is 1, the request succesfuly delivered
        }

        static bool ResetPassword()
        {
            Console.WriteLine("Enter new password: ");
            string text = Console.ReadLine();
            byte[] password = System.Text.Encoding.UTF8.GetBytes(text);

            byte[] recvBuff = new byte[2000];
            int responseCode;
            int cmdId = (int)Commands.Reset;
            jhi.SendAndRecv2(session, cmdId, password, ref recvBuff, out responseCode);
            return responseCode == 1; // if the responseCode is 1, the request succesfuly delivered
        }

        static bool GetRandom(int length, ref byte[] randomBytes)
        {
            byte[] numBytes = BitConverter.GetBytes(length);
            int cmdId = (int)Commands.GetRandom;
            int responseCode;

            jhi.SendAndRecv2(session, cmdId, numBytes, ref randomBytes, out responseCode);

            return responseCode == 1; // if the responseCode is 1, the request succesfuly delivered
        }

        static public StringBuilder PrintByteArray(byte[] bytes)
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