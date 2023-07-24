export class DisplayScreen {
    constructor(urlScreenTextElement, usernameScreenTextElement, passwordScreenTextElement, msgScreenTextElement) {
        this.urlScreenTextElement = urlScreenTextElement
        this.usernameScreenTextElement = usernameScreenTextElement
        this.passwordScreenTextElement = passwordScreenTextElement
        this.msgScreenTextElement = msgScreenTextElement
        this.clear()
    }

    displayText(msg, url = "", username = "", password = "",) {
        this.urlScreenTextElement.innerText = url
        this.usernameScreenTextElement.innerText = username
        this.passwordScreenTextElement.innerText = password
        this.msgScreenTextElement.innerText = msg
    }

    clear() {
        this.urlScreen = ''
        this.usernameScreen = ''
        this.passwordScreen = ''
        this.msgScreen = ''
    }
}