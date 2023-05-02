package TEE_8442_0871;

import com.intel.util.FlashStorage;

public class FlashStorageAPI {
	// This is a singleton pattern.
    private static FlashStorageAPI instance = null;

    private final int KEY_CODE = 1;
    private final int PASSWORD_CODE = 0;
    
    /**
     * This function is used to get the singleton instance of the FlashStorageService
     *
     * @return The instance of the FlashStorageService.
     */
    public static FlashStorageAPI getInstance() {
        if (instance == null)
            instance = new FlashStorageAPI();

        // This is a singleton pattern. It is returning the instance of the class.
        return instance;
    }
    
    private FlashStorageAPI() { //private CTOR:
        resetKeyAndRegister(); //this function makes rest of CTOR irrelevant..
    }
    
    private void resetKeyAndRegister() {
        if(existsKey())
            FlashStorage.eraseFlashData(KEY_CODE);
        if(isRegistered())
            FlashStorage.eraseFlashData(PASSWORD_CODE);
    }
    
    /**
     * Check if the password is stored in the flash memory
     *
     * @return A boolean value.
     */
    public boolean isRegistered() {
        return 0 != FlashStorage.getFlashDataSize(PASSWORD_CODE);
    }
    
    /**
     * Check if the key is stored in the flash memory
     *
     * @return A boolean value.
     */
    public boolean existsKey() {
        return 0 != FlashStorage.getFlashDataSize(KEY_CODE);
    }
    
    // getters and setters
    public byte[] getPassword() {
        byte[] res = new byte[FlashStorage.getFlashDataSize(PASSWORD_CODE)];
        FlashStorage.readFlashData(PASSWORD_CODE, res, 0);
        return res;
    }

    public void getKeys(byte[] nArray, byte[] eArray, byte[] dArray) {
        byte[] res = new byte[FlashStorage.getFlashDataSize(KEY_CODE)];
        FlashStorage.readFlashData(KEY_CODE, res, 0);
        Utils.splitArray(res, nArray, eArray, dArray);
    }
    
    /**
     * It sets the password to the data passed in.
     *
     * @param data The data to be written to the flash storage.
     */
    public void setPassword(byte[] data) {

        // write to the FlaseStorage
        FlashStorage.writeFlashData(PASSWORD_CODE, data, 0, data.length);
    }
    
    public void setKey(byte[] data) {
        // write to the FlaseStorage
        FlashStorage.writeFlashData(KEY_CODE, data, 0, data.length);
    }

    public void setKey(byte[] a, byte[] b, byte[] c) {
        // write to the FlaseStorage
        byte[] data = Utils.concatArrays(a, b, c);
        FlashStorage.writeFlashData(KEY_CODE, data, 0, data.length);
    }
}
