package TEE_8442_0871;
import com.intel.util.*;

//
// Implementation of DAL Trusted Application: TEE_8442_0871 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class TEE_8442_0871 extends IntelApplet {
	FlashStorageAPI fsInstance = FlashStorageAPI.getInstance();



    //Response Codes:
    final int RES_FAIL_NOT_LOGGED_IN = 0; //tried to make enum, but didnt work...
    final int RESPONSE_SUCCESS = 1;
    final int RES_FAIL_NO_KEY_GENERATED = 2;
    final int RES_KEY_ALREADY_GENERATED = 3;


    /**
     * This method will be called by the VM when a new session is opened to the Trusted Application
     * and this Trusted Application instance is being created to handle the new session.
     * This method cannot provide response data and therefore calling
     * setResponse or setResponseCode methods from it will throw a NullPointerException.
     *
     * @param request the input data sent to the Trusted Application during session creation
     * @return APPLET_SUCCESS if the operation was processed successfully,
     * any other error status code otherwise (note that all error codes will be
     * treated similarly by the VM by sending "cancel" error code to the SW application).
     */
    public int onInit(byte[] request) {
        return APPLET_SUCCESS;
    }


    /**
     * This method will be called by the VM to handle a command sent to this
     * Trusted Application instance.
     *
     * @param commandId the command ID (Trusted Application specific)
     * @param request   the input data for this command
     * @return the return value should not be used by the applet
     */
    public int invokeCommand(int id, byte[] seed) {

        DebugPrint.printString("Received user Id: " + id + ".");
        if (seed != null) {
            DebugPrint.printString("Received buffer:");
            DebugPrint.printBuffer(seed);
        }

        /*
         * The return value of the invokeCommand method is not guaranteed to be
         * delivered to the SW application, and therefore should not be used for
         * this purpose. Trusted Application is expected to return APPLET_SUCCESS code
         * from this method and use the setResposeCode method instead.
         */
        return APPLET_SUCCESS;
    }


    //sends response code, with empty byte[]
    public void sendEmptyResponse(int responseCode) {
        byte[] response = new byte[0];
        sendResponse(responseCode, response);
    }

    public void sendResponse(int code, byte[] response) {
        DebugPrint.printString("Sending code: " + code + ", message: ");
        DebugPrint.printBuffer(response);

        /*
         * To return the response data to the command, call the setResponse
         * method before returning from this method.
         * Note that calling this method more than once will
         * reset the response data previously set.
         */
        setResponse(response, 0, response.length);

        /*
         * In order to provide a return value for the command, which will be
         * delivered to the SW application communicating with the Trusted Application,
         * setResponseCode method should be called.
         * Note that calling this method more than once will reset the code previously set.
         * If not set, the default response code that will be returned to SW application is 0.
         */
        setResponseCode(code);
    }

    /**
     * This method will be called by the VM when the session being handled by
     * this Trusted Application instance is being closed
     * and this Trusted Application instance is about to be removed.
     * This method cannot provide response data and therefore
     * calling setResponse or setResponseCode methods from it will throw a NullPointerException.
     *
     * @return APPLET_SUCCESS code (the status code is not used by the VM).
     */


    public int onClose() {
        DebugPrint.printString("Goodbye, DAL!");
        return APPLET_SUCCESS;
    }
}
