using Intel.Dal;
using System;
using System.IO;
using System.Text;

namespace PasswordVaultHost
{
    class Program
    {
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
    }
}