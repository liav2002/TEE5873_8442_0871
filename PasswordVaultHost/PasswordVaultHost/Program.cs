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
            ws_server.AddWebSocketService<WS2Applet>("/" + WS2Applet.GetPortName());
            ws_server.Start();

            Log.print_title("SERVER CONSOLE LOG");
            Log.Default_LOG("Enter q! to stop");
            Log.Default_LOG("WS server started on ws://127.0.0.1:" + portNumber + "/" + WS2Applet.GetPortName());

            //to keep console alive..
            string x = Console.ReadLine();
            while (x != "q!")
                x = Console.ReadLine();
            ws_server.Stop();
        }
    }
}
