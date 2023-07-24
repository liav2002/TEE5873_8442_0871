﻿using System;
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
