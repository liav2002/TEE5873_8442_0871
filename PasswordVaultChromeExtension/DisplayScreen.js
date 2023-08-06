export class DisplayScreen {
    constructor(urlScreenTextElement, usernameScreenTextElement, passwordScreenTextElement, msgScreenTextElement) {
        this.urlScreenTextElement = urlScreenTextElement;
        this.usernameScreenTextElement = usernameScreenTextElement;
        this.passwordScreenTextElement = passwordScreenTextElement;
        this.msgScreenTextElement = msgScreenTextElement;

        chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
            const currentTab = tabs[0];
            const currentUrl = currentTab.url;
            const urlObject = new URL(currentUrl);
            const domain = urlObject.hostname;
            const domainWithoutWww = domain.replace(/^www\./, '');
            this.urlScreenTextElement.textContent = domainWithoutWww;
        });

        this.clear();
    }

    displayText(msg, username = "", password = "") {
        this.usernameScreenTextElement.textContent = username;
        this.passwordScreenTextElement.textContent = password;
        this.msgScreenTextElement.textContent = msg;
    }

    clear() {
        this.usernameScreenTextElement.textContent = '';
        this.passwordScreenTextElement.textContent = '';
        this.msgScreenTextElement.textContent = '';
    }
}