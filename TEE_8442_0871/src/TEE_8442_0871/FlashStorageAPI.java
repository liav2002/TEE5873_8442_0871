package TEE_8442_0871;

import com.intel.util.FlashStorage;

public class FlashStorageAPI {
	// This is a singleton pattern.
    private static FlashStorageAPI instance = null;

    private final int SEED_CODE = 1;
    
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
    
    private FlashStorageAPI() { 
    	
    }
    
    public void resetSeed() {
        if(existsSeed())
            FlashStorage.eraseFlashData(SEED_CODE);   
    }
    
    /**
     * Check if the seed is stored in the flash memory
     *
     * @return A boolean value.
     */
    public boolean existsSeed() {
        return 0 != FlashStorage.getFlashDataSize(SEED_CODE);
    }
    
    // getters and setters
    public byte[] getSeed() {
        byte[] res = new byte[FlashStorage.getFlashDataSize(SEED_CODE)];
        FlashStorage.readFlashData(SEED_CODE, res, 0);
        return res;
    }
    
    /**
     * It sets the seed to the data passed in.
     *
     * @param data The data to be written to the flash storage.
     */
    public void setSeed(byte[] data) {

        // write to the FlaseStorage
        FlashStorage.writeFlashData(SEED_CODE, data, 0, data.length);
    }
}
