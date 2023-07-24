import { WebSocketAPI } from './WebSocketAPI.js'
import { DisplayScreen } from './DisplayScreen.js'
import { ServerOperaion } from './Enums.js'

// buttons:
const registerButton = document.querySelector('[data-register]');
const signInButton = document.querySelector('[data-sign-in]');
const getPasswordButton = document.querySelector('[data-get-password]');
const getUsernameButton = document.querySelector('[data-get-username]');
const resetMemoryButton = document.querySelector('[data-reset-memory]');
const connectToSocketButton = document.querySelector('[data-connect-to-socket]');

// checkboxes:
const checkBoxRunningChrome = document.querySelector('#ckbox_running_chrome');
checkBoxRunningChrome.checked = true;
const checkBoxConnectedServer = document.querySelector('#ckbox_connected_to_server');
checkBoxConnectedServer.checked = false;
checkBoxConnectedServer.disabled = true;

// screen
const urlScreenTextElement = document.querySelector('[data-url-screen]');
const usernameScreenTextElement = document.querySelector('[data-username-screen]');
const passwordScreenTextElement = docment.querySelector('[data-password-screen]');
const msgScreenTextElement = document.querySelector('[data-msg-screen]');

// objects
const myDisplayScreen = new DisplayScreen(urlScreenTextElement, usernameScreenTextElement, passwordScreenTextElement, msgScreenTextElement);
const websockethandler = new WebSocketAPI(myDisplayScreen);
const socketName = "ws://127.0.0.1:5789/WS2Applet";

// string messages
const alreadyConnected = "you already connected to the server."; 
const need2Connect = "you must first intialize the websocket.";

websockethandler.intialize(socketName)

registerButton.addEventListener('click', button => {
    alert("test");
    if (websockethandler.isInitialized() == false) {
        alert(need2Connect);
        return;
    }

    const textFromScreen = prompt("Enter new password:");
    websockethandler.send2Server(ServerOperaion.REGISTER.name + textFromScreen);
})

signInButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() == false) {
        alert(need2Connect);
        return;
    }

    const textFromScreen = prompt("Enter Password:");
    websockethandler.send2Server(ServerOperation.SIGN_IN.name + textFromScreen);
})

getPasswordButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(need2Connect);
        return;
    }

    const checkBox = document.querySelector('#ckbox_running_chrome');

    if (checkBox.checked === true) //then get the URL directly from the browser
    {
        chrome.tabs.query({ 'active': true, 'lastFocusedWindow': true }, function (tabs) {
            const url = tabs[0].url;
            let domain_name = (new URL(url)).hostname;
            if (domain_name.substring(0, 4) === "www.") {
                domain_name = domain_name.substring(4);
            }

            myDisplayScreen.displayText("", "", domain_name);
            websockethandler.send2Server(ServerOperation.GET_PASSWORD.name + domain_name)
        });
    }

    else if (checkBox.checked === false) { //if program run from debugger
        const url = prompt("Enter url:");
        let domain_name = (new URL(url)).hostname;
        if (domain_name.substring(0, 4) === "www.") {
            domain_name = domain_name.substring(4);
        }

        myDisplayScreen.displayText("", "", domain_name);
        websockethandler.send2Server(ToServerOperation.GET_PASSWORD.name + domain_name)
    }
})

getUsernameButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(need2Connect);
        return;
    }

    const checkBox = document.querySelector('#ckbox_running_chrome');

    if (checkBox.checked === true) //then get the URL directly from the browser
    {
        chrome.tabs.query({ 'active': true, 'lastFocusedWindow': true }, function (tabs) {
            const url = tabs[0].url;
            let domain_name = (new URL(url)).hostname;
            if (domain_name.substring(0, 4) === "www.") {
                domain_name = domain_name.substring(4);
            }

            myDisplayScreen.displayText("", "", domain_name);
            websockethandler.send2Server(ServerOperation.GET_USERNAME.name + domain_name)
        });
    }

    else if (checkBox.checked === false) { //if program run from debugger
        const url = prompt("Enter url:");
        let domain_name = (new URL(url)).hostname;
        if (domain_name.substring(0, 4) === "www.") {
            domain_name = domain_name.substring(4);
        }

        myDisplayScreen.displayText("", "", domain_name);
        websockethandler.send2Server(ToServerOperation.GET_USERNAME.name + domain_name)
    }
})

resetMemoryButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }
    websockethandler.send2Server(ToServerOperation.RESET_MEMORY.name)
})

connectToSocketButton.addEventListener('click', button => {
    msgScreenTextElement.innerText = "Try to connect"
    if (websockethandler.isInitialized() === true) {
        alert(alreadyConnected);
        return;
    }
    websockethandler.initialize(socketName);
})