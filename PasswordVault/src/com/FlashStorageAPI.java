package com;

import java.util.LinkedList;
import java.util.Iterator;
import com.intel.util.FlashStorage;

public class FlashStorageAPI {
	// CONSTANT VALUES FOR CHAR AND BYTES:
	final static int BYTE_CHAR_RATIO = 1;
	final static int NUM_CHAR_IN_PASSWORD = 20;
	final static int NUM_BYTE_IN_PASSWORD = BYTE_CHAR_RATIO * NUM_CHAR_IN_PASSWORD;
	final static int NUM_CHAR_IN_URL = 20;
	final static int NUM_BYTE_IN_URL = BYTE_CHAR_RATIO * NUM_CHAR_IN_URL;
	final static int NUM_CHAR_IN_USERNAME = 20;
	final static int NUM_BYTE_IN_USERNAME = BYTE_CHAR_RATIO * NUM_CHAR_IN_USERNAME;
	
	final static int DATA_CODE = 0;
	final static int PASSWORD_CODE = 1;
	final int SLOT_SIZE = NUM_BYTE_IN_PASSWORD + NUM_BYTE_IN_URL + NUM_BYTE_IN_USERNAME;
	
	// Local variables
	LinkedList<Byte[]> passwords = new LinkedList<Byte[]>();
	LinkedList<Byte[]> urls = new LinkedList<Byte[]>();
	LinkedList<Byte[]> usernames = new LinkedList<Byte[]>();
	int size = 0; //num pairs of urls and password.
	
	// This is a singleton pattern.
    private static FlashStorageAPI instance = null;
    
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
    	if(existsData())
    		loadData();
    }
    
    public void resetData()
    {
    	// TODO: Implement
    }
    
    /**
     * Check if there is data stored in the flash memory
     *
     * @return A boolean value.
     */
    public boolean existsData() {
        return 0 != FlashStorage.getFlashDataSize(DATA_CODE);
    }
    
    /**
     * Check if user registered aka password is stored in the flash memory
     *
     * @return A boolean value.
     */
    public boolean isRegistered() {
        return 0 != FlashStorage.getFlashDataSize(PASSWORD_CODE);
    }
    
    public void loadData() {
        byte[] data = new byte[FlashStorage.getFlashDataSize(DATA_CODE)];
        FlashStorage.readFlashData(DATA_CODE, data, 0);

        byte[] passData;
        byte[] urlsData;
        byte[] usernamesData;

        for (int i = 0; i < data.length; i += SLOT_SIZE) {
            // for every pair of: password, url
            passData = Utils.sliceArray(data, i, i + NUM_BYTE_IN_PASSWORD);
            urlsData = Utils.sliceArray(data, i + NUM_BYTE_IN_PASSWORD, i + NUM_BYTE_IN_PASSWORD + NUM_BYTE_IN_URL);
            usernamesData = Utils.sliceArray(data, i + NUM_BYTE_IN_PASSWORD + NUM_BYTE_IN_URL, i + SLOT_SIZE);
            
            passwords.add(Utils.convertByte(passData));
            urls.add(Utils.convertByte(urlsData));
            usernames.add(Utils.convertByte(usernamesData));
            size++;
        }
    }
    
    public void saveData()
    {
    	Iterator<Byte[]> passIter = passwords.iterator();
        Iterator<Byte[]> urlIter = urls.iterator();
        Iterator<Byte[]> usernamesIter = usernames.iterator();
        
        Byte[] data2Save = new Byte[size * SLOT_SIZE];
        
        int i = 0;
        
        while(passIter.hasNext())
        {
        	Utils.place(data2Save, passIter.next(), i * SLOT_SIZE);
        	Utils.place(data2Save, urlIter.next(), i * SLOT_SIZE + NUM_BYTE_IN_PASSWORD);
        	Utils.place(data2Save, usernamesIter.next(), i * SLOT_SIZE + NUM_BYTE_IN_PASSWORD + NUM_BYTE_IN_URL);
        	i++;
        }
        
        FlashStorage.writeFlashData(DATA_CODE, Utils.convertByte(data2Save), 0, data2Save.length);
    }
    
    public void addData(byte[] password, byte[] url, byte[] usernames)
    {
    	// TODO: Implement.
    }
    
    public byte[] getPassword(byte[] currentUrl) 
    {
    	// TODO: Implement.
    	return null;
    }
    
    public byte[] getUsername(byte[] currentUrl)
    {
    	// TODO: Implement.
    	return null;
    }
    
    public LinkedList<Byte[]> getPasswords()
    {
        return passwords;
    }
    
    public int getNumOfPasswords()
    {
        return passwords.size();
    }
    
    public byte[] getAllPasswords() 
    {
    	// TODO: Implement.
    	return null;
    }
    
    public boolean isValidPass(byte[] pass2check)
    {
    	byte[] realPass = new byte[FlashStorage.getFlashDataSize(PASSWORD_CODE)];
    	FlashStorage.readFlashData(PASSWORD_CODE, realPass, 0);
    	return Utils.equals(pass2check, realPass);
    }
    
    public void setPassword(byte[] pass) {

        // write to the FlaseStorage
        FlashStorage.writeFlashData(PASSWORD_CODE, pass, 0, pass.length);
    }
    
    
}