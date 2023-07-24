using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using WebSocketSharp.Server;
using WebSocketSharp;

namespace PasswordVaultHost
{
    class WS2Applet : WebSocketBehavior
    {
        AppletAPI itsAppletAPI = new AppletAPI();
        const int CMD_ID_LENGTH = 1;

        protected override void OnOpen()
        {
            base.OnOpen();
            PasswordVaultHost.Log.Default_LOG("Client opened a socket.");
            send2Client(ServerResult.RES_SUCCESS, "Successfullt connected to Server!");
        }

        protected override void OnMessage(MessageEventArgs e)
        {
            string receivedMsg = e.Data;
            string data = receivedMsg.Substring(CMD_ID_LENGTH);
            int commandId = getCommandId(receivedMsg);

            PasswordVaultHost.Log.Default_LOG("<== Received message from Echo client: " + (ServerOperation)commandId + " " + data);

            switch (commandId)
            {
                case (int)ServerOperation.REGISTER:
                {
                    itsAppletAPI.ResetMemory();
                    int responseCode = itsAppletAPI.Register(data);
                    if (responseCode == (int)AppletResult.RES_SUCCESS)
                        send2Client(ServerResult.RES_SUCCESS, "successfully registered.");
                    break;
                }

                case (int)ServerOperation.SIGN_IN:
                {
                    int responseCode = itsAppletAPI.SignIn(data);
                    if (responseCode == (int)AppletResult.RES_WRONG_PASSWORD)
                        send2Client(ServerResult.RES_WRONG_PASSWORD, "wrong password");
                    else if (responseCode == (int)AppletResult.RES_NOT_REGISTERED)
                        send2Client(ServerResult.RES_NOT_REGISTERED, "user not registered.");
                    else if (responseCode == (int)AppletResult.RES_SUCCESS)
                        send2Client(ServerResult.RES_SUCCESS, "successfully signed in.");
                    else
                        send2Client(ServerResult.RES_FAILED, "unknown error.");
                    break;
                }

                case (int)ServerOperation.GET_PASSWORD:
                {
                    try
                    {
                        string msg = itsAppletAPI.GetPassword(data);
                        send2Client(ServerResult.RES_PASSWORD_RETREIVED, msg);
                    }
                    catch (ERROR_NotSignedIn ex)
                    {
                        send2Client(ServerResult.RES_NOT_SIGNED_IN, ex.msg);
                    }
                    break;
                }

                case (int)ServerOperation.GET_USERNAME:
                {
                    try
                    {
                        string msg = itsAppletAPI.GetUsername(data);
                        send2Client(ServerResult.RES_USERNAME_RETREIVED, msg);
                    }
                    catch (ERROR_NotSignedIn ex)
                    {
                        send2Client(ServerResult.RES_NOT_SIGNED_IN, ex.msg);
                    }
                    break;
                }

                case (int)ServerOperation.RESET_MEMORY:
                {
                    itsAppletAPI.ResetMemory();
                    send2Client(ServerResult.RES_SUCCESS, "memory reset.");
                    break;
                }

                default:
                {
                    send2Client(ServerResult.RES_FAILED, "unknown server operaion.");
                    break;
                }
            }
        }

        protected override void OnClose(CloseEventArgs e)
        {
            base.OnClose(e);
            itsAppletAPI.Close();
        }

        private void send2Client(ServerResult cmd, string msg)
        {
            string msgToSend = ((int)cmd + msg);
            PasswordVaultHost.Log.Default_LOG("Try send message to client.");
            Send(msgToSend);
            PasswordVaultHost.Log.Default_LOG("==> Message sent to client: " + cmd + " " + msg);
        }

        private int getCommandId(string msg)
        {
            int res = (int)Symbols.NOT_INITIATED;
            bool success = Int32.TryParse(msg.Substring(0, CMD_ID_LENGTH), out res);
            if (!success)
            {
                PasswordVaultHost.Log.Error_LOG("getCommandId Failed (WS2Applet.cs).");
                throw new ERROR_CantGetCmdId();
            }
                
            return res;
        }

        public static string GetPortName()
        {
            return typeof(WS2Applet).Name;
        }
    }
}
