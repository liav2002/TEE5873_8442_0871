using Intel.Dal;
using System;
using System.Text;
using System.IO;
using System.Linq;

namespace TEE_8442_0871Host
{
    class Program
    {
        static JhiSession session;
        static Jhi jhi; 
        static void Main(string[] args)
        {
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

            // Send and Receive data to/from the Trusted Application
            Console.Write("Fibonacci sequence up to 10th  element:\n");
            int res = Fib(10);
            Console.WriteLine("\n");

            // Close the session
            Console.WriteLine("Closing the session.");
            jhi.CloseSession(session);

            //Uninstall the Trusted Application
            Console.WriteLine("Uninstalling the applet.");
            jhi.Uninstall(appletID);

            Console.WriteLine("Press Enter to finish.");
            Console.Read();
        }

        static int Add(int a, int b)
        {
            byte[] aBytes = BitConverter.GetBytes(a);
            Array.Reverse(aBytes);

            byte[] bBytes = BitConverter.GetBytes(b);
            Array.Reverse(bBytes);

            byte[] sendBuff = aBytes.Concat(bBytes).ToArray(); //sendBuff includes both numbers

            // A buffer to hold the output data from the TA
            //the size of 2000 can change by itself...
            byte[] recvBuff = new byte[2000];

            // The return value that the TA provides using the IntelApplet.setResponseCode method
            int responseCode;

            // The ID of the command to be performed by the TA
            int cmdId = 1; // 1 stands for Add
            
            Console.Write("Performing send and receive operation...");
            jhi.SendAndRecv2(session, cmdId, sendBuff, ref recvBuff, out responseCode);
            //the "sendBuff" becomes the "request" in eclipse...
            //send "recvBuff" by reference... and it changes acc to what is done in eclipse

            Array.Reverse(recvBuff);
            int res = BitConverter.ToInt32(recvBuff, 0);

            return res;
        }

        static int Fib(int n)
        {
            int a = 0;
            int b = 1;
            int c = 0;

            if (n > 0)
            {
                --n;
                Console.WriteLine("Fib iteration 1: " + a);
            }
            if (n > 0)
            {
                --n;
                Console.WriteLine("Fib iteration 2: " + b);
            }
            for (int i = 0; i < n; ++i)
            {
                if(i > 0)
                {
                    a = b;
                    b = c;
                }
                Console.Write("fib iteration " + (i + 3) + ":");
                c = Add(a, b);
                Console.WriteLine(c);
            }

            return c;
        }
    }
}