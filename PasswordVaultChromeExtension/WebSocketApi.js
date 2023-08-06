export class WebSocketApi {
    constructor() {
        this.socketToServer;
        this.MsgFromServer = "";
        this.isConnected = false;
    }

    isInitialized() {
        return this.isConnected;
    }

    initialize(webSocketAddress) {
        const usernameScreenTextElement = document.getElementById('data-username-screen');
        const passwordScreenTextElement = document.getElementById('data-password-screen');
        const msgScreenTextElement = document.getElementById('data-msg-screen');

        usernameScreenTextElement.textContent = "";
        passwordScreenTextElement.textContent = "";
        msgScreenTextElement.textContent = "connecting...";

        this.socketToServer = new WebSocket(webSocketAddress);
        this.socketToServer.onmessage = this.onMessageCallback.bind(this);
    }

    //this function contains callbacks for receiving messages from the Server
    onMessageCallback(event) {
        class ServerResult {
            static RES_NOT_SIGNED_IN = new ServerResult("0");
            static RES_SUCCESS = new ServerResult("1");
            static RES_WRONG_PASSWORD = new ServerResult('2');
            static RES_PASSWORD_RETREIVED = new ServerResult("3");
            static RES_NOT_REGISTERED = new ServerResult("4");
            static RES_FAILED = new ServerResult("5");
            static RES_USERNAME_RETREIVEDR = new ServerResult("6");
            static RES_MISSING_PARAMETERS = new ServerResult("7");
            static RES_PASSWORD_MISSING = new ServerResult("8");
            static RES_URL_EXISTS = new ServerResult("9");

            constructor(name) {
                this.name = name;
            }
        }

        const dataLength = 30;
        const lengthOfCommandId = 1;

        switch (event.data[0]) { //command id is at beginning of the string..
            case (ServerResult.RES_NOT_SIGNED_IN.name):
            case ServerResult.RES_WRONG_PASSWORD.name:
            case ServerResult.RES_NOT_REGISTERED.name:
            case ServerResult.RES_FAILED.name:
            case ServerResult.RES_MISSING_PARAMETERS.name:
            case ServerResult.RES_PASSWORD_MISSING.name:
            case ServerResult.RES_URL_EXISTS.name: {
                const msgScreenTextElement = document.getElementById('data-msg-screen');
                msgScreenTextElement.textContent = event.data.substring(lengthOfCommandId, (event.data).length);
            }
                break;
            case ServerResult.RES_SUCCESS.name: {
                const msgScreenTextElement = document.getElementById('data-msg-screen');
                msgScreenTextElement.textContent = event.data.substring(lengthOfCommandId, (event.data).length);
                if (this.isConnected === false) {
                    this.isConnected = true;
                }
            }
                break;
            case ServerResult.RES_PASSWORD_RETREIVED.name: {
                const passwordFromServer = event.data.substring(lengthOfCommandId, dataLength + 1);
                const msgScreenTextElement = document.getElementById('data-msg-screen');
                const passwordScreenTextElement = document.getElementById('data-password-screen');

                msgScreenTextElement.textContent = event.data.substring(dataLength + 1);
                passwordScreenTextElement.textContent = passwordFromServer;
            }
                break;
            case ServerResult.RES_USERNAME_RETREIVEDR.name: {
                const usernameFromServer = event.data.substring(lengthOfCommandId, dataLength + 1);
                const msgScreenTextElement = document.getElementById('data-msg-screen');
                const usernameScreenTextElement = document.getElementById('data-username-screen');

                msgScreenTextElement.textContent = event.data.substring(dataLength + 1);
                usernameScreenTextElement.textContent = usernameFromServer;
            }
                break;

            default:
                break;
        }
    }

    sendMsgToServer(msg) {
        this.socketToServer.send(msg);
    }
}