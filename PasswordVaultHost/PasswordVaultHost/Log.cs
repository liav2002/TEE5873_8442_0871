using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PasswordVaultHost
{
    class Log
    {
        public static void Default_LOG(string msg)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.Write("SYS_LOG: ");
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine(msg);
            Console.ForegroundColor = ConsoleColor.White;
        }

        public static void Error_LOG(string msg)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.Write("SYS_ERROR: ");
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine(msg);
            Console.ForegroundColor = ConsoleColor.White;
        }
    }
}
