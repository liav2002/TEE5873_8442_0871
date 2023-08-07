import { WebSocketApi } from './WebSocketApi.js'

class ServerOperation {
    static REGISTER = new ServerOperation("0");
    static SIGN_IN = new ServerOperation("1");
    static GET_PASSWORD = new ServerOperation("2");
    static GET_USERNAME = new ServerOperation("3");
    static RESET_MEMORY = new ServerOperation("4");
    static ADD_DATA = new ServerOperation("5");

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
const submitButton = document.getElementById('submit');

// screen:
const urlScreenTextElement = document.getElementById('data-url-screen');

chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    const currentTab = tabs[0];
    const currentUrl = currentTab.url;
    const urlObject = new URL(currentUrl);
    const domain = urlObject.hostname;
    const domainWithoutWww = domain.replace(/^www\./, '');
    urlScreenTextElement.textContent = domainWithoutWww;
});

// objects:
const popup = document.getElementById('popup');
const socketName = "ws://127.0.0.1:5789/WS2Applet";
const websockethandler = new WebSocketApi(socketName);

const failed2connect = "ERROR: Failed connect to data api.";

// websockethandler.initialize(socketName); //you can comment this out, and manually connect

// add events handlers to buttons:

registerButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(failed2connect);
        return;
    }

    if (confirm("Are you sure you want to change your master password?")) {
        const textFromScreen = prompt("Please enter a new password:");
        if (textFromScreen != null) {
            websockethandler.sendMsgToServer(ServerOperation.REGISTER.name + textFromScreen);

            const msgScreenTextElement = document.getElementById('data-msg-screen');
            const usernameScreenTextElement = document.getElementById('data-username-screen');
            const passwordScreenTextElement = document.getElementById('data-password-screen');

            msgScreenTextElement.textContent = "";
            usernameScreenTextElement.textContent = "";
            passwordScreenTextElement.textContent = "";
        }
    }
});

signInButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(failed2connect);
        return;
    }
    //must get text from screen!
    const textFromScreen = prompt("Please enter a password:");
    if (textFromScreen != null) {
        websockethandler.sendMsgToServer(ServerOperation.SIGN_IN.name + textFromScreen);
    }
});

getPasswordButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(failed2connect);
        return;
    }

    const domain_name = urlScreenTextElement.textContent;
    websockethandler.sendMsgToServer(ServerOperation.GET_PASSWORD.name + domain_name);
});

getUsernameButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(failed2connect);
        return;
    }

    const domain_name = urlScreenTextElement.textContent;
    websockethandler.sendMsgToServer(ServerOperation.GET_USERNAME.name + domain_name);
});

addDataButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(failed2connect);
        return;
    }

    showPopup();
});

submitButton.addEventListener('click', () => {
    const urlInput = urlScreenTextElement.textContent;
    const usernameInput = document.getElementById('username-input').value.trim();
    const passwordInput = document.getElementById('password-input').value.trim();

    if (usernameInput == '') {
        alert("Username is missing !!");
    }

    else {
        // Processing the input values
        if (passwordInput == '') {
            websockethandler.sendMsgToServer(ServerOperation.ADD_DATA.name + urlInput + " " + usernameInput);
        }

        else {
            websockethandler.sendMsgToServer(ServerOperation.ADD_DATA.name + urlInput + " " + usernameInput + " " + passwordInput);
        }

        // Close the popup
        popup.style.display = 'none';
    }
});

resetMemoryButton.addEventListener('click', button => {
    if (websockethandler.isInitialized() === false) {
        alert(failed2connect);
        return;
    }
    websockethandler.sendMsgToServer(ServerOperation.RESET_MEMORY.name);

    const msgScreenTextElement = document.getElementById('data-msg-screen');
    const usernameScreenTextElement = document.getElementById('data-username-screen');
    const passwordScreenTextElement = document.getElementById('data-password-screen');

    msgScreenTextElement.textContent = "";
    usernameScreenTextElement.textContent = "";
    passwordScreenTextElement.textContent = "";
});

// Show the popup when needed
function showPopup() {
    popup.style.display = 'flex';
}
