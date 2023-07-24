import { ServerResult } from "./Enums";

export class WebSocketAPI {
    constructor(displayScreenElement) {
        this.socket2Server;
        this.MsgFromServer = "";
        this.myDisplayScreen = displayScreenElement;
    }

    isInitialized() {
        return document.querySelector('#ckbox_connected_to_server').checked;
    }

    initialize(webSocketAddress) {
        this.socket2Server = new WebSocket(webSocketAddress);
        this.myDisplayScreen.displayText("trying to connect..")
        this.socket2Server.onmessage = this.onMessageCallback;
    }

    onMessageCallback(event) {
        const lengthOfCommandId = 1;
        const urlScreenTextElement = document.querySelector('[data-url-screen]')
        const usernameScreenTextElement = document.querySelector('[data-username-screen]')
        const passwordScreenTextElement = document.querySelector('[data-password-screen]')
        const msgScreenTextElement = document.querySelector('[data-msg-screen]')

        switch (event.data[0]) { //command id is at beginning of the string..
            case (ServerResult.SUCCESS.name):
                {
                    document.querySelector('#ckbox_connected_to_Wserver').checked = true;
                    msgScreenTextElement.innerText = event.data.substring(lengthOfCommandId, (event.data).length);
                    urlScreenTextElement.innerText = "";
                    usernameScreenTextElement.innerText = "";
                    passwordScreenTextElement.innerText = "";
                }
                break;
            case ServerResult.RES_NOT_SIGNED_IN.name:
                {
                    msgScreenTextElement.innerText = event.data.substring(lengthOfCommandId, (event.data).length);
                    urlScreenTextElement.innerText = "";
                    usernameScreenTextElement.innerText = "";
                    passwordScreenTextElement.innerText = "";
                }
                break;
            case ServerResult.RES_WRONG_PASSWORD.name:
                {
                    msgScreenTextElement.innerText = event.data.substring(lengthOfCommandId, (event.data).length);
                    urlScreenTextElement.innerText = "";
                    usernameScreenTextElement.innerText = "";
                    passwordScreenTextElement.innerText = "";
                }
                break;
            case ServerResult.RES_PASSWORD_RETREIVED.name:
                {
                    const passwordFromServer = event.data.slice(1);
                    passwordScreenTextElement.innerText = passwordFromServer;
                    msgScreenTextElement.innerText = "password retrieved.";

                    //if running in chrome browser - inject password to appropriate text box
                    const checkBox = document.querySelector('#ckbox_running_chrome');
                    if (checkBox.checked === true) {
                        chrome.tabs.query({ 'active': true, 'lastFocusedWindow': true }, function (tabs) { 
                            const msg613 = passwordFromServer;

                            chrome.tabs.executeScript(null, {
                                code:
                                    "var inputs = document.getElementsByTagName('input'); "
                                    + "for (var i=0; i<inputs.length; i++) { "
                                    + " if (inputs[i].type.toLowerCase() === 'password') { "
                                    + "  inputs[i].value = '" + msg613 + "'"
                                    + "}"
                                    + " }"
                            });

                        });
                    }
                }
                break;
            case ServerResult.RES_USERNAME_RETREIVED.name:
                {
                    const usernameFromServer = event.data.slice(1);
                    usernameScreenTextElement.innerText = usernameFromServer;
                    msgScreenTextElement.innerText = "username retrieved.";

                    //if running in chrome browser - inject password to appropriate text box
                    const checkBox = document.querySelector('#ckbox_running_chrome');
                    if (checkBox.checked === true) {
                        chrome.tabs.query({ 'active': true, 'lastFocusedWindow': true }, function (tabs) {
                            const msg613 = passwordFromServer;

                            chrome.tabs.executeScript(null, {
                                code:
                                    "var inputs = document.getElementsByTagName('input'); "
                                    + "for (var i=0; i<inputs.length; i++) { "
                                    + " if (inputs[i].type.toLowerCase() === 'username') { "
                                    + "  inputs[i].value = '" + msg613 + "'"
                                    + "}"
                                    + " }"
                            });

                        });
                    }
                }
                break;
            case ServerResult.RES_NOT_REGISTERED.name:
                {
                    usernameScreenTextElement.innerText = "";
                    passwordScreenTextElement.innerText = "";
                    msgScreenTextElement.innerText = "You must first register.";
                }
                break;
            case ServerResult.RES_FAILED.name:
                {
                    msgScreenTextElement.innerText = "unknown error.";
                }
                break;

            default:
                break;
        }
    }

    send2Server(msg) {
        this.socket2Server.send(msg)
    }
}