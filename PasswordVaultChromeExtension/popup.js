import { WebSocketApi } from './WebSocketApi.js'
import { DisplayScreen } from './DisplayScreen.js'

class ServerOperation {
    static REGISTER = new ToServerOperation("0");
    static SIGN_IN = new ToServerOperation("1");
    static GET_PASSWORD = new ToServerOperation("2");
    static GET_USERNAME = new ToServerOperation("3");
    static RESET_MEMORY = new ToServerOperation("4");
    static ADD_DATA = new ToServerOperation("5");

    constructor(name) {
        this.name = name;
    }
}

// buttons:
const registerButton = document.getElementById('button-register');
const signInButton = document.getElementById('button-sign-in');
const getPasswordButton = document.getElementById('button-get-password');
const getUsernameButton = document.getElementById('button-get-username');
const addDataButton = document.getElementById('button-add-data');
const resetMemoryButton = document.getElementById('button-reset-memory');
const connectToWebSocketButton = document.getElementById('button-connect-to-socket');
const submitButton = document.getElementById('submit');

// screen:
const urlScreenTextElement = document.getElementById('data-url-screen');
const usernameScreenTextElement = document.getElementById('data-username-screen');
const passwordScreenTextElement = document.getElementById('data-password-screen');
const msgScreenTextElement = document.getElementById('data-msg-screen');

// objects:
const popup = document.getElementById('popup');
const myDisplayScreen = new DisplayScreen(urlScreenTextElement, usernameScreenTextElement, passwordScreenTextElement, msgScreenTextElement);
const websockethandler = new WebSocketApi(myDisplayScreen);
const socketName = "ws://127.0.0.1:5789//WS2Applet";

// string messages:
const alreadyConnected = "already connected to the Server!";
const mustconnect = "you must first initialize the websocket!";

// websockethandler.initialize(socketName); //you can comment this out, and manually connect

// add events handlers to buttons:

registerButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }

    if (confirm("Registering will erase any memory. Are you sure that you want to Register anyway?")) {
        const textFromScreen = prompt("Please Enter a Password:");
        websockethandler.sendMsgToServer(ServerOperation.REGISTER.name + textFromScreen);
    }
});

signInButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }
    //must get text from screen!
    const textFromScreen = prompt("Please Enter a Password:");
    websockethandler.sendMsgToServer(ServerOperation.SIGN_IN.name + textFromScreen);
});

getPasswordButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }

    const domain_name = urlScreenTextElement.innerText;
    websockethandler.sendMsgToServer(ServerOperation.GET_PASSWORD.name + domain_name);
});

getUsernameButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }

    const domain_name = urlScreenTextElement.innerText;
    websockethandler.sendMsgToServer(ServerOperation.GET_USERNAME.name + domain_name);
});

addDataButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }

    showPopup();
});

submitButton.addEventListener('click', () => {
    const urlInput = urlScreenTextElement.innerText;
    const usernameInput = document.getElementById('username-input').value;
    const passwordInput = document.getElementById('password-input').value;

    // Processing the input values
    websockethandler.sendMsgToServer(ToServerOperation.ADD_DATA.name + urlInput + " " + usernameInput + " " + passwordInput);

    // Close the popup
    popup.style.display = 'none';
});

resetMemoryButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(mustconnect);
        return;
    }
    websockethandler.sendMsgToServer(ToServerOperation.RESET_MEMORY.name);
});

connectToWebSocketButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === true) {
        alert(alreadyConnected);
        return;
    }
    websockethandler.initialize(socketName);
})

// Show the popup when needed
function showPopup() {
    popup.style.display = 'flex';
}
