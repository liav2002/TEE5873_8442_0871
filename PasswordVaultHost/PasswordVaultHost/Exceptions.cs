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
}
