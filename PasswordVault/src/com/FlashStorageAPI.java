package com;

import com.intel.langutil.Iterator;
import com.intel.langutil.LinkedList;
import com.intel.util.FlashStorage;

public class FlashStorageAPI {
	final static int DATA_CODE = 0;
	final static int PASSWORD_CODE = 1;
	
	// Local variables
	LinkedList<Byte[]> passwords = LinkedList.create();
	LinkedList<Byte[]> urls = LinkedList.create();
	LinkedList<Byte[]> usernames = LinkedList.create();
	byte size = 0; //num pairs of urls, passwords and usernames.
	
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
    	
    	int ptr = 0;
    	size = data[ptr++];
    	
    	byte elementSize = 0;
    	for (int i = 0; i < size; i++) {
    		elementSize = data[ptr++];
    		urls.add(Utils.convertByte(Utils.sliceArray(data, ptr, ptr+elementSize)));
    		ptr += elementSize;
    		
    		elementSize = data[ptr++];
    		usernames.add(Utils.convertByte(Utils.sliceArray(data, ptr, ptr+elementSize)));
    		ptr += elementSize;

    		elementSize = data[ptr++];
    		passwords.add(Utils.convertByte(Utils.sliceArray(data, ptr, ptr+elementSize)));
    		ptr += elementSize;
    	}
    }

    public void addData(byte[] password, byte[] url, byte[] username)
    {
    	// add data to local linked list.
        passwords.add(Utils.convertByte(password));
        urls.add(Utils.convertByte(url));
        usernames.add(Utils.convertByte(username));
        size++;
        
        // add data to flash storage.
        int ptr = FlashStorage.getFlashDataSize(DATA_CODE) - 1;
        
    	FlashStorage.writeFlashData(DATA_CODE, Utils.convertIntToByteArr(size), 0, 1);
    
    	FlashStorage.writeFlashData(DATA_CODE, Utils.convertIntToByteArr(url.length), ptr++, ptr);
    	FlashStorage.writeFlashData(DATA_CODE, url, ptr, ptr + url.length);
    	ptr += url.length;
    	
    	FlashStorage.writeFlashData(DATA_CODE, Utils.convertIntToByteArr(username.length), ptr++, ptr);
    	FlashStorage.writeFlashData(DATA_CODE, username, ptr, ptr + username.length);
    	ptr += username.length;
    	
    	FlashStorage.writeFlashData(DATA_CODE, Utils.convertIntToByteArr(password.length), ptr++, ptr);
    	FlashStorage.writeFlashData(DATA_CODE, password, ptr, ptr + password.length);
    	ptr += password.length;
    }
    
    public byte[] getPassword(byte[] currentUrl) 
    {
        Iterator<Byte[]> passIter = passwords.getIterator();
        Iterator<Byte[]> urlIter = urls.getIterator();
        
        Byte[] pass;
        Byte[] url;
        
        while (passIter.hasNext()) {
            url = urlIter.getNext();
            pass = passIter.getNext();
            if(Utils.equals(url, Utils.convertByte(currentUrl)))
                return Utils.convertByte(pass);
        }

        return null;  // if not found
    }
    
    public byte[] getUsername(byte[] currentUrl)
    {
        Iterator<Byte[]> usernameIter = usernames.getIterator();
        Iterator<Byte[]> urlIter = urls.getIterator();
        
        Byte[] username;
        Byte[] url;
        
        while (usernameIter.hasNext()) {
            url = urlIter.getNext();
            username = usernameIter.getNext();
            if(Utils.equals(url, Utils.convertByte(currentUrl)))
                return Utils.convertByte(username);
        }

        return null;  // if not found
    }
    
    public LinkedList<Byte[]> getPasswords()
    {
        return passwords;
    }
    
    public int getNumOfPasswords()
    {
        return size;
    }
    
    public boolean isValidPass(byte[] pass2check)
    {
    	byte[] realPass = new byte[FlashStorage.getFlashDataSize(PASSWORD_CODE)];
    	FlashStorage.readFlashData(PASSWORD_CODE, realPass, 0);
    	return Utils.equals(pass2check, realPass);
    }
    
    public void setPassword(byte[] pass) {

        // write to the FlaseStorage the access password, the master key
        FlashStorage.writeFlashData(PASSWORD_CODE, pass, 0, pass.length);
    } 
}