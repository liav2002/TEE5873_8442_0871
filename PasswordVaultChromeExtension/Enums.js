export class ServerOperation {
    static REGISTER = new ServerOperation("0");
    static SIGN_IN = new ServerOperation("1");
    static GET_PASSWORD = new ServerOperation("2");
    static GET_USERNAME = new ServerOperation("3");
    static RESET_MEMORY = new ServerOperation("4");

    constructor(name) {
        this.name = name;
    }
}

export class ServerResult {
    static RES_NOT_SIGNED_IN  = new FromServerOperation("0"); //when client wants to get a password from vault, but he's not signed in
    static RES_SUCCESS = new FromServerOperation("1");
    static RES_WRONG_PASSWORD = new FromServerOperation('2');
    static RES_PASSWORD_RETREIVED = new FromServerOperation("3"); //when client wants to enter his account
    static RES_NOT_REGISTERED = new FromServerOperation("4"); //when server sends password to client
    static RES_FAILED = new FromServerOperation("5");
    static RES_USERNAME_RETREIVED = new FromServerOperation("6");

    constructor(name) {
        this.name = name
    }
}