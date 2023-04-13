package TEE_8442_0871;

import com.intel.crypto.Random;
import com.intel.util.*;

//
// Implementation of DAL Trusted Application: TEE_8442_0871 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class TEE_8442_0871 extends IntelApplet {
	public boolean register = false;
    public boolean login = false;


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
		FlashStorage.eraseFlashData(0);
		DebugPrint.printString("Hello, DAL!");
		
		register = FlashStorage.getFlashDataSize(0) != 0;
		
		if (register) DebugPrint.printString("User is register!");
		else DebugPrint.printString("User is NOT register!");
		
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
		
		DebugPrint.printString("Received command Id: " + commandId + ".");
		if(request != null)
		{
			DebugPrint.printString("Received buffer:");
			DebugPrint.printBuffer(request);
		}
		
		
		switch (commandId) {
			case 1: // register
			{
				if (register) {
					// user is already registered
					sendFailedResp();
				}
				
				else {
					// save the password
					byte[] password = new byte[FlashStorage.getFlashDataSize(0)];
					
					FlashStorage.writeFlashData(0,  request, 0, request.length);
					register = true;
					
					int code = 1;
					byte[] resp = new byte[0];
					sendResp(code, resp);
				}
				
				break;
			}
			
			case 2: // login
			{
				if(!register) 
					sendFailedResp(); 
				
				else {
					byte[] password = new byte[FlashStorage.getFlashDataSize(0)];
					FlashStorage.readFlashData(0, password, 0);
					
					// check password
					if (cmp_bytes(password, request)) {
						login = true;
						int code = 1;
						byte[] resp = new byte[0];
						sendResp(code, resp);
					}
					
					else
						sendFailedResp();
				}
				
				break;
			}
			
			case 3: // reset password
			{
				if (login) {
					login = false;
					FlashStorage.writeFlashData(0, request, 0, request.length);
					int code = 1;
					byte[] resp = new byte[0];
					sendResp(code, resp);
				}
				
				else
					sendFailedResp();
				
				break;
			}
			
			case 4: // get random
			{
				if (login) {
					int length = getIntegers(request)[0]; 
					byte[] random = new byte[length];
					Random.getRandomBytes(random, (short)0, (short)length);
					
					int code = 1;
					sendResp(code, random);
				}
				
				else
					sendFailedResp();
				
				break;
			}
			
			default:
				break;
		}

		/*
		 * The return value of the invokeCommand method is not guaranteed to be
		 * delivered to the SW application, and therefore should not be used for
		 * this purpose. Trusted Application is expected to return APPLET_SUCCESS code 
		 * from this method and use the setResposeCode method instead.
		 */
		return APPLET_SUCCESS;
	}
	
	public void sendFailedResp() {
		int code = 0;
		byte[] resp = new byte[0];
		sendResp(code, resp);
	}
	
	public void sendResp(int code, byte[] resp) {
		DebugPrint.printString("Sending code: " + code + ", message: ");
		DebugPrint.printBuffer(resp);
		
		/*
         * To return the response data to the command, call the setResponse
         * method before returning from this method.
         * Note that calling this method more than once will
         * reset the response data previously set.
         */
        setResponse(resp, 0, resp.length);

        /*
         * In order to provide a return value for the command, which will be
         * delivered to the SW application communicating with the Trusted Application,
         * setResponseCode method should be called.
         * Note that calling this method more than once will reset the code previously set.
         * If not set, the default response code that will be returned to SW application is 0.
         */
        setResponseCode(code);
	}
	
	public int[] getIntegers(byte[] data) {
		final int integerBytes = 4;
		int arraySize = (int) (data.length / integerBytes);
		int[] numbersArray = new int[arraySize];
		
		// Casting bytes value to integers.
		for (int i = 0; i < arraySize; ++i)
		{
			for (int j = 0; j < integerBytes; ++j)
			{
				numbersArray[i] = (numbersArray[i] << 8) + (data[j + i * integerBytes] & 0xFF);
			}
		}
		
		// Print result for debug
		System.out.println("nums: ");
		for (int n: numbersArray)
			System.out.println(n);
		
		return numbersArray;
	}
	
	public boolean cmp_bytes(byte[] a, byte[] b) {
		if (a.length != b.length)
			return false;
		else
			for(int i = 0; i < a.length; ++i)
				if (a[i] != b[i])
					return false;
		
		return true;
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
