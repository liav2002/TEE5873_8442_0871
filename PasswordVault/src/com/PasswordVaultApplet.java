package com;

import com.intel.util.*;

//
// Implementation of DAL Trusted Application: PasswordVault 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class PasswordVaultApplet extends IntelApplet {
	FlashStorageAPI fs = FlashStorageAPI.getInstance();
	boolean isLoggedIn = false;
	
	// OPERATION REQUEST CODES, HOST -> APPLET:
	final int RESET_MEMORY = 0; // Applet will delete all flash sotrage data.
	final int GET_PASSWORD = 1; // Host sends URL, if Applet has no password save, generates password and return.
	final int GET_USERNAME = 2; // Host sends URL, Applet sent username.
	final int SIGN_IN = 3; // Sign in operation. if success -> isLoggedIn = True.
    final int REGISTER = 4; // Register operation. -> Master password will be set for the next sign in.
    final int ADD_DATA = 5; // Add data operation. -> User will provide url - username - password
    
    // OPERATION RESPONSE CODES, APPLET -> HOST:
    final int RES_FAILED = 0;
    final int RES_SUCCESS = 1;
    final int RES_NOT_SIGNED_IN = 2;
    final int RES_NOT_REGISTERED = 3;
    final int RES_WRONG_PASSWORD = 4;
    final int RES_NEW_PSWD = 5;
    final int RES_USERNAME_MISSING = 6;
    final int RES_MISSING_PARAMETERS = 7;
    final int RES_PASSWORD_MISSING = 8;
    final int RES_URL_EXISTS = 9;
    
    // Constant for random password
    final int DEFAULT_PASS_LEN = 10;
    
	/**
	 * This method will be called by the VM when a new session is opened to the Trusted Application 
	 * and this Trusted Application instance is being created to handle the new session.
	 * This method cannot provide response data and therefore calling
	 * setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @param	request	the input data sent to the Trusted Application during session creation
	 * 
	 * @return	APPLET_SUCCESS if the operation was processed successfully, 
	 * 		any other error status code otherwise (note that all error codes will be
	 * 		treated similarly by the VM by sending "cancel" error code to the SW application).
	 */
	public int onInit(byte[] request) {
		DebugPrint.printString("Hello, DAL!");
		return APPLET_SUCCESS;
	}
	
	/**
	 * This method will be called by the VM to handle a command sent to this
	 * Trusted Application instance.
	 * 
	 * @param	commandId	the command ID (Trusted Application specific) 
	 * @param	request		the input data for this command 
	 * @return	the return value should not be used by the applet
	 */
	public int invokeCommand(int commandId, byte[] request) {
		DebugPrint.printString("Received cmd Id: " + commandId + ".");
        
    	if (request != null) {
            DebugPrint.printString("Received buffer:");
            DebugPrint.printBuffer(request);

            //first number holds the command id....
            switch (commandId) {
	            case RESET_MEMORY:
	            {
	            	if (isLoggedIn)
	            	{
	            		fs.resetData();
	            		sendEmptyResponse(RES_SUCCESS);
	            	}
	            	
	            	else
	            	{
	            		sendEmptyResponse(RES_NOT_SIGNED_IN);
	            	}

            		break;
	            }
	            
	            case ADD_DATA:
	            {
	            	if(!isLoggedIn)
	            	{
	            		sendEmptyResponse(RES_NOT_SIGNED_IN);
	            	}
	            	
	            	else
	            	{
	            		byte[][] splitData = Utils.splitBySpace(request);
	            		
	            		if(splitData.length >= 2 && fs.isUrlExistsForUsername(splitData[0]))
	            		{
	            			sendEmptyResponse(RES_URL_EXISTS); // if url is already exists, handle this case.
	            		}
	            		
	            		else if (splitData.length == 3) // regular request
	            		{
	            			fs.addData(splitData[0], splitData[1], splitData[2]);
	            			sendEmptyResponse(RES_SUCCESS);
	            		}
	            		else if(splitData.length == 2) // want to use password generator
	            		{
	            			fs.addData(splitData[0], splitData[1], Utils.convertByte(PasswordGenerator.generateRandomPassword(DEFAULT_PASS_LEN)));
	            			sendEmptyResponse(RES_NEW_PSWD);
	            		}
	            		else
	            		{
	            			sendEmptyResponse(RES_MISSING_PARAMETERS);
	            		}
	            		
	            		
	            	}
	            	
	            	break;
	            }
	            
	            case GET_PASSWORD:
	            {
	            	if(!isLoggedIn)
	            	{
	            		sendEmptyResponse(RES_NOT_SIGNED_IN);
	            	}
	            	
	            	else
	            	{
	            		byte[] password = fs.getPassword(request); // the request should be the specific url.
	            		
	            		if(password == null)
	            		{
	            			sendEmptyResponse(RES_PASSWORD_MISSING);
	            		}
	            		
	            		else
	            		{
	            			sendResponse(RES_SUCCESS, password);
	            		}
	            	}
	            	
	            	break;
	            }
	            
	            case GET_USERNAME:
	            {
	            	if(!isLoggedIn)
	            	{
	            		sendEmptyResponse(RES_NOT_SIGNED_IN);
	            	}
	            	
	            	else
	            	{
	            		byte[] username = fs.getUsername(request); // the request should be the specific url.
	            		
	            		if(username == null)
	            		{
	            			sendEmptyResponse(RES_USERNAME_MISSING);
	            		}
	            		
	            		else
	            		{
	            			sendResponse(RES_SUCCESS, username);
	            		}
	            	}
	            	
	            	break;
	            }
	            
	            case SIGN_IN:
	            {
	            	if(!fs.isRegistered())
	            	{
	            		sendEmptyResponse(RES_NOT_REGISTERED);
	            	}
	            	
	            	else if(isLoggedIn)
	            	{
	            		sendEmptyResponse(RES_FAILED);
	            	}
	            	
	            	else if (fs.isValidPass(request))
	            	{
	            		isLoggedIn = true;
	            		sendEmptyResponse(RES_SUCCESS);
	            	}
	            	
	            	else
	            	{
	            		sendEmptyResponse(RES_WRONG_PASSWORD);
	            	}
	            	
	            	break;
	            }
	            	
	            case REGISTER:
	            {
	            	if(fs.isRegistered() && !isLoggedIn)
	            	{
	            		sendEmptyResponse(RES_FAILED);
	            	}
	            	else
	            	{
	            		fs.setPassword(request);
		            	isLoggedIn = true;
		            	sendEmptyResponse(RES_SUCCESS);
	            	}
	            	break;
	            }
	            
	            default:
	            	sendEmptyResponse(RES_FAILED);
            }
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
