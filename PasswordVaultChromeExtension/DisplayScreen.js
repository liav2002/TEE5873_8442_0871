export class DisplayScreen {
    constructor(urlScreenTextElement, usernameScreenTextElement, passwordScreenTextElement, msgScreenTextElement) {
        this.urlScreenTextElement = urlScreenTextElement;
        this.usernameScreenTextElement = usernameScreenTextElement;
        this.passwordScreenTextElement = passwordScreenTextElement;
        this.msgScreenTextElement = msgScreenTextElement;

        chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
            const currentTab = tabs[0];
            const currentUrl = currentTab.url;
            this.urlScreenTextElement.innerText = currentUrl;
        });

        this.clear();
    }

    displayText(msg, username = "", password = "") {
        this.usernameScreenTextElement.innerText = username;
        this.passwordScreenTextElement.innerText = password;
        this.msgScreenTextElement.innerText = msg;
    }

    clear() {
        this.usernameScreenTextElement.innerText = '';
        this.passwordScreenTextElement.innerText = '';
        this.msgScreenTextElement.innerText = '';
    }
}