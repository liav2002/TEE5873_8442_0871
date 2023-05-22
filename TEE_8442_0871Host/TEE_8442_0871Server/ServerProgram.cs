using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace TEE_8442_0871Server
{
    public class DataBlock
    {
        private int randomSeed;
        private int userId;

        public DataBlock(int _userId, int _randomSeed)
        {
            userId = _userId;
            randomSeed = _randomSeed;
        }
    }

    public class ServerProgram
    {
        private static readonly string END_OF_MESSAGE = "<EOF>";
        private static readonly List<DataBlock> lst = new List<DataBlock>();

        public static int Main(string[] args)
        {
            Console.WriteLine("SERVER'S CONSOLE:");
            StartServer();
            return 0;
        }

        public static void StartServer()
        {
            // Get Host IP Address that is used to establish a connection
            // In this case, we get one IP address of localhost that is IP : 127.0.0.1
            // If a host has multiple addresses, you will get a list of addresses
            var host = Dns.GetHostEntry("localhost");
            var ipAddress = host.AddressList[0];
            var localEndPoint = new IPEndPoint(ipAddress, 12000);

            try
            {
                // Create a Socket that will use Tcp protocol
                var listener = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
                // A Socket must be associated with an endpoint using the Bind method
                listener.Bind(localEndPoint);
                // Specify how many requests a Socket can listen before it gives Server busy response.
                // We will listen 10 requests at a time
                listener.Listen(10);

                Console.WriteLine("Waiting for a connection...");
                var handler = listener.Accept(); //STOPS HERE - until the Host sends something
                Console.WriteLine("Message came thru! processing...");

                // Incoming data from the client.
                string data = null;
                byte[] recvdBytes = null; //received bytes

                while (true)
                {
                    recvdBytes = new byte[1024];
                    var numBytesRec = handler.Receive(recvdBytes);
                    data += Encoding.ASCII.GetString(recvdBytes, 0, numBytesRec);

                    //if we get to "<EOF>", then end of transmission... we leave the while() loop
                    if (data.IndexOf(END_OF_MESSAGE) > -1) break;
                    //otherwise, we continue Receiving bits from the handler
                }

                Console.WriteLine("Text received : {0}", data);

                var sizeOfData = data.Length - END_OF_MESSAGE.Length;
                var userIdString = "";
                for (var i = 0; i < sizeOfData; i++) userIdString += data[i];

                var userId = int.Parse(userIdString);

                var rnd = new Random();
                var randomSeed = rnd.Next(1, 100); // we can make the size bigger... just dont want issues..

                //add to table..
                lst.Add(new DataBlock(userId, randomSeed));

                //to send - convert everything to string
                var msgToSend = Encoding.ASCII.GetBytes(
                    userId + " " + randomSeed + END_OF_MESSAGE);
                //use a "_" to differ btw the numbers..
                handler.Send(msgToSend);
                handler.Shutdown(SocketShutdown.Both);
                handler.Close();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }

            Console.WriteLine("\n Press any key to continue...");
            Console.ReadKey();
        }
    }
}
