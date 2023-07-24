using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using WebSocketSharp;
using WebSocketSharp.Server;

namespace PasswordVaultHost
{
    class Program
    {
        static void Main(string[] args)
        {
            string portNumber = "5789"; // TODO: Makesure the port suitable to chrome extention.
            WebSocketServer ws_server = new WebSocketServer("ws://127.0.0.1:" + portNumber);
        }
    }
}
