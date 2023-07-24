using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PasswordVaultHost
{
    enum Symbols
    { 
        NOT_INITIATED = -1
    }

    enum AppletResult
    { 
        RES_FAILED = 0,
        RES_SUCCESS = 1,
        RES_NOT_SIGNED_IN = 2,
        RES_NOT_REGISTERED = 3,
        RES_WRONG_PASSWORD = 4,
        RES_NEW_PSWD = 5,
        RES_USERNAME_MISSING = 6
    }

    enum AppletOperation
    {
        RESET_MEMORY = 0,
        GET_PASSWORD = 1,
        GET_USERNAME = 2,
        SIGN_IN = 3,
        REGISTER = 4
    }

}
