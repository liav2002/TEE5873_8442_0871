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

        public static void print_title(string title)
        {
            const string separator = "*************************"; // You can customize the separator here

            // Set console colors
            ConsoleColor originalForeground = Console.ForegroundColor;
            ConsoleColor originalBackground = Console.BackgroundColor;
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.BackgroundColor = ConsoleColor.DarkBlue;

            // Print top separator
            Console.WriteLine(separator);

            // Center the title
            int titlePadding = (separator.Length - title.Length) / 2;
            string paddedTitle = title.PadLeft(title.Length + titlePadding).PadRight(separator.Length);

            // Print the title
            Console.WriteLine(paddedTitle);

            // Print bottom separator
            Console.WriteLine(separator);

            // Reset console colors to original
            Console.ForegroundColor = originalForeground;
            Console.BackgroundColor = originalBackground;
        }
    }
}
