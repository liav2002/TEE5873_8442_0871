using Intel.Dal;
using System;
using System.IO;
using System.Text;

namespace PasswordVaultHost
{
    class AppletAPI
    {
        static public JhiSession session;
        static public Jhi jhi;

        // This is the UUID of this Trusted Application (TA).
        //The UUID is the same value as the applet.id field in the Intel(R) DAL Trusted Application manifest.
        string appletID = "df22bf3d-ccde-454d-97ab-8fec28f6805d";

        public AppletAPI()
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

            // This is the path to the Intel Intel(R) DAL Trusted Application .dalp file that was created by the Intel(R) DAL Eclipse plug-in.
            string project_directory = Directory.GetParent(Environment.CurrentDirectory).Parent.Parent.Parent.FullName;
            string dalpPath = "\\PasswordVault\\bin\\PasswordVault.dalp";
            string appletPath = project_directory + dalpPath;

            // Install the Trusted Application
            Log.Default_LOG("AppletApi > Installing the applet.");
            jhi.Install(appletID, appletPath);

            // Start a session with the Trusted Application
            byte[] initBuffer = new byte[] { }; // Data to send to the applet onInit function
            Log.Default_LOG("AppletApi > Opening a session.");
            jhi.CreateSession(appletID, JHI_SESSION_FLAGS.None, initBuffer, out session);
        }

        public void Close()
        {
            // Close the session
            Log.Default_LOG("AppletApi > Closing the session.");
            jhi.CloseSession(session);

            //Uninstall the Trusted Application
            Log.Default_LOG("AppletApi > Uninstalling the applet.");
            jhi.Uninstall(appletID);
        }

        public void ResetMemory()
        {
            Log.Debug_Log("AppletApi on 'ResetMemory' operation.");
            // initialized parameters for applet operaion.
            byte[] recvBuff = new byte[0];
            int responseCode = (int)Symbols.NOT_INITIATED;
            int cmdId = (int)AppletOperation.RESET_MEMORY;
            
            // communicate applet for make an operation.
            jhi.SendAndRecv2(session, cmdId, null, ref recvBuff, out responseCode);
            
            // log response messages
            if (responseCode == (int)AppletResult.RES_SUCCESS)
                Log.Default_LOG("Memory reset successfully!");
            else
            {
                Log.Error_LOG("'ResetMemory' Operation failed with code: " + responseCode.ToString());
                throw new ERROR_ResetMemoryFailed();
            }
        }

        public void AddData(string data)
        {
            Log.Debug_Log("AppletApi on 'AddData' operation.");
            // initialized parameters for applet operaion.
            byte[] recvBuff = new byte[100];
            int responseCode = (int)Symbols.NOT_INITIATED;
            byte[] bytesData = Encoding.ASCII.GetBytes(data);
            int cmdId = (int)AppletOperation.ADD_DATA;

            // communicate applet for make an operation.
            jhi.SendAndRecv2(session, cmdId, bytesData, ref recvBuff, out responseCode);

            // log response messages
            if (responseCode == (int)AppletResult.RES_NOT_SIGNED_IN)
            {
                Log.Error_LOG("Failed to add url, username and password because user is not signed in.");
                throw new ERROR_NotSignedIn();
            }

            else if (responseCode == (int)AppletResult.RES_MISSING_PARAMETERS)
            {
                Log.Error_LOG("Failed to add data, becaues of missing parameters.");
                throw new ERROR_Missing_parameters();
            }

            else if (responseCode == (int)AppletResult.RES_SUCCESS)
                Log.Default_LOG("url, username and password was successfuly added.");

            else if (responseCode == (int)AppletResult.RES_NEW_PSWD)
                Log.Default_LOG("url, username and password was successfuly added. (new password generated).");

            else
            {
                Log.Error_LOG("'AddData' Operation failed with code: " + responseCode.ToString());
                throw new ERROR_Unknown();
            }
        }

        public string GetPassword(string url)
        {
            Log.Debug_Log("AppletApi on 'GetPassword' operation.");
            // initialized parameters for applet operaion.
            byte[] recvBuff = new byte[100];
            int responseCode = (int)Symbols.NOT_INITIATED;
            byte[] bytesUrl = Encoding.ASCII.GetBytes(url);
            int cmdId = (int)AppletOperation.GET_PASSWORD;

            // communicate applet for make an operation.
            jhi.SendAndRecv2(session, cmdId, bytesUrl, ref recvBuff, out responseCode);

            // log response messages
            if (responseCode == (int)AppletResult.RES_NOT_SIGNED_IN)
            {
                Log.Error_LOG("Failed to get password because user is not signed in.");
                throw new ERROR_NotSignedIn();
            }

            else if (responseCode == (int)AppletResult.RES_PASSWORD_MISSING)
            {
                Log.Error_LOG("Failed to get password because url is not found.");
                throw new ERROR_Password_Missing(url);
            }

            else if (responseCode == (int)AppletResult.RES_SUCCESS)
                Log.Default_LOG("Password retrieved.");

            else
            {
                Log.Error_LOG("'GetPassword' Operation failed with code: " + responseCode.ToString());
                throw new ERROR_Unknown();
            }

            // return password
            return ConvertByteArrToString(recvBuff);
        }
        
        public string GetUsername(string url)
        {
            Log.Debug_Log("AppletApi on 'GetUsername' operation.");
            // initialized parameters for applet operaion.
            byte[] recvBuff = new byte[100];
            int responseCode = (int)Symbols.NOT_INITIATED;
            byte[] bytesUrl = Encoding.ASCII.GetBytes(url);
            int cmdId = (int)AppletOperation.GET_USERNAME;

            // communicate applet for make an operation.
            jhi.SendAndRecv2(session, cmdId, bytesUrl, ref recvBuff, out responseCode);

            // log response messages
            if (responseCode == (int)AppletResult.RES_NOT_SIGNED_IN)
            {
                Log.Error_LOG("Failed to get username because user is not signed in.");
                throw new ERROR_NotSignedIn();
            }

            else if (responseCode == (int)AppletResult.RES_USERNAME_MISSING)
            {
                Log.Error_LOG("Failed to get username because url is not found.");
                throw new ERROR_Username_Missing(url);
            }

            else if (responseCode == (int)AppletResult.RES_SUCCESS)
                Log.Default_LOG("Username retrieved.");

            else
            {
                Log.Error_LOG("'GetUsername' Operation failed with code: " + responseCode.ToString());
                throw new ERROR_Unknown();
            }

            // return username
            return ConvertByteArrToString(recvBuff);
        }

        public int SignIn(string password)
        {
            Log.Debug_Log("AppletApi on 'SignIn' operation.");
            // initialized parameters for applet operaion.
            byte[] recvBuff = new byte[100];
            int responseCode = (int)Symbols.NOT_INITIATED;
            byte[] bytePassword = Encoding.ASCII.GetBytes(password);
            int cmdId = (int)AppletOperation.SIGN_IN;

            // communicate applet for make an operation.
            jhi.SendAndRecv2(session, cmdId, bytePassword, ref recvBuff, out responseCode);

            // log response messages
            if (responseCode == (int)AppletResult.RES_SUCCESS)
                Log.Default_LOG("User successfully signed in.");
            else
                Log.Error_LOG("'SignIn' Operation failed with code: " + responseCode.ToString());

            return responseCode;
        }

        public int Register(string newPassword)
        {
            Log.Debug_Log("AppletAPI on 'Register' operation.");
            // initialized parameters for applet operaion.
            byte[] recvBuff = new byte[100];
            int responseCode = (int)Symbols.NOT_INITIATED;
            byte[] bytePassword = Encoding.ASCII.GetBytes(newPassword);
            int cmdId = (int)AppletOperation.REGISTER;

            // communicate applet for make an operation.
            jhi.SendAndRecv2(session, cmdId, bytePassword, ref recvBuff, out responseCode);

            // log response messages
            if (responseCode == (int)AppletResult.RES_SUCCESS)
                Log.Default_LOG("User successfully registered.");
            else
                Log.Error_LOG("'Register' Operation failed with code: " + responseCode.ToString());

            return responseCode;
        }

        private string ConvertByteArrToString(byte[] bytes)
        {
            if (bytes == null || bytes.Length == 0)
            {
                return string.Empty;
            }

            // Use UTF-8 encoding to convert the byte array to a string
            string result = Encoding.UTF8.GetString(bytes);

            return result;
        }
    }
}