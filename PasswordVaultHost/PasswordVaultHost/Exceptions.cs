using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PasswordVaultHost
{
    class AppletException : Exception
    {
        public string msg = "";

        public override string ToString()
        {
            return msg;
        }
    }

    class ERROR_NotSignedIn : AppletException
    {
        public ERROR_NotSignedIn()
        {
            msg = "User is not signed in.";
        }
    }
    class ERROR_ResetMemoryFailed : AppletException
    { 
        public ERROR_ResetMemoryFailed()
        {
            msg = "Reset memory failed - you should see log messages.";
        }
    }
    class ERROR_Unknown : AppletException
    {
        public ERROR_Unknown()
        {
            msg = "Operation failed from unkown reason.";
        }
    }
    class ERROR_Missing_parameters : AppletException
    { 
        public ERROR_Missing_parameters()
        {
            msg = "Some parameters are missing.";
        }
    }
    class ERROR_Username_Missing : AppletException
    {
        public ERROR_Username_Missing(string url)
        {
            msg = "Username for " + url + " is not found.";
        }
    }
    class ERROR_Password_Missing : AppletException
    {
        public ERROR_Password_Missing(string url)
        {
            msg = "Password for " + url + " is not found.";
        }
    }

    class ERROR_Url_Exists : AppletException
    { 
        public ERROR_Url_Exists()
        {
            msg = "Url is already exists in data. (In this version updating data is not allowed).";
        }
    }

    class SocketException : Exception
    {
        public string msg = "";
    }
    class ERROR_CantGetCmdId : SocketException
    {
        public ERROR_CantGetCmdId()
        {
            msg = "Can't get Command Id!";
        }
    }
}
