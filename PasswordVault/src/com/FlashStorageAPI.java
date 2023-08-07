package com;

import com.intel.langutil.Iterator;
import com.intel.langutil.LinkedList;
import com.intel.util.DebugPrint;
import com.intel.util.FlashStorage;

public class FlashStorageAPI {
	// Constants for the each flashStorage documents
	final static int DATA_CODE = 0;
	final static int PASSWORD_CODE = 1;
	
	// Local variables
	LinkedList<Byte[]> passwords = LinkedList.create();
	LinkedList<Byte[]> urls = LinkedList.create();
	LinkedList<Byte[]> usernames = LinkedList.create();
	int size = 0; //number of urls, passwords and usernames.
	
	//Constants values for char and bytes:
    final static int BYTE_CHAR_RATIO = 1;
    final static int NUM_CHAR = 20;
    final static int NUM_BYTE = BYTE_CHAR_RATIO * NUM_CHAR;
    final int SLOT_SIZE = NUM_BYTE * 3;
	
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
    	{
    		DebugPrint.printString("Loading data");
    		loadData();
    	}
    	else
    	{
    		DebugPrint.printString("data empty");
    	}
    }
    
    public void resetData()
    {
    	if(existsData()) {
            FlashStorage.eraseFlashData(DATA_CODE);
        }
    	
    	passwords = LinkedList.create();
    	urls = LinkedList.create();
    	usernames = LinkedList.create();
    	size = 0;
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
     * Check if user registered -> password is stored in the flash memory
     *
     * @return A boolean value.
     */
    public boolean isRegistered() {
        return 0 != FlashStorage.getFlashDataSize(PASSWORD_CODE);
    }
    
    public void loadData() {
    	byte[] data = new byte[FlashStorage.getFlashDataSize(DATA_CODE)];
        FlashStorage.readFlashData(DATA_CODE, data, 0);

        byte[] url;
        byte[] username;
        byte[] pass;

        final int slot_size = SLOT_SIZE;
        for (int i = 0; i < data.length; i += slot_size) {
            // for every group of: url, username, password
            url = Utils.sliceArray(data, i, i + NUM_BYTE);
            username = Utils.sliceArray(data, i + NUM_BYTE, i + NUM_BYTE * 2);
            pass = Utils.sliceArray(data, i + NUM_BYTE * 2, i + slot_size);
            
            urls.add(Utils.convertByte(url));
            usernames.add(Utils.convertByte(username));
            passwords.add(Utils.convertByte(pass));
            size++;
        }
    }
    
    public void saveData() {
    	Iterator<Byte[]> urlIter = urls.getIterator();
    	Iterator<Byte[]> usernameIter = usernames.getIterator();
        Iterator<Byte[]> passIter = passwords.getIterator();

        Byte[] dataToSave = new Byte[size * SLOT_SIZE];

        int i = 0;

        while (urlIter.hasNext()) {
            Utils.place(dataToSave, urlIter.getNext(), i * SLOT_SIZE);
            Utils.place(dataToSave, usernameIter.getNext(), i * SLOT_SIZE + NUM_BYTE);
            Utils.place(dataToSave, passIter.getNext(), i * SLOT_SIZE + NUM_BYTE * 2);
            i++;
        }

        FlashStorage.writeFlashData(DATA_CODE, Utils.convertByte(dataToSave), 0, dataToSave.length);
    }

    public void addData(byte[] url, byte[] username, byte[] password)
    {
    	byte[] fixedUrl = Utils.padZeros(url, NUM_BYTE);
    	byte[] fixedUsername = Utils.padZeros(username, NUM_BYTE);
    	byte[] fixedPassword = Utils.padZeros(password, NUM_BYTE);

    	urls.add(Utils.convertByte(fixedUrl));
    	usernames.add(Utils.convertByte(fixedUsername));
        passwords.add(Utils.convertByte(fixedPassword));
        size++;

        saveData();
    }
    
    public boolean isUrlExistsForUsername(byte[] currentUrl)
    {
    	Byte[] fixedUrl = Utils.convertByte(Utils.padZeros(currentUrl, NUM_BYTE));
    	Iterator<Byte[]> urlIter = urls.getIterator();
    	Byte[] url;
    	
    	while (urlIter.hasNext()) {
            url = urlIter.getNext();
            if(Utils.equals(url, fixedUrl))
                return true;
        }
    	
    	return false;
    }
    
    public byte[] getPassword(byte[] currentUrl) 
    {
    	Byte[] fixedUrl = Utils.convertByte(Utils.padZeros(currentUrl, NUM_BYTE));
    	
    	Iterator<Byte[]> urlIter = urls.getIterator();
        Iterator<Byte[]> passIter = passwords.getIterator();
        
        Byte[] url;
        Byte[] pass;
        
        while (urlIter.hasNext()) {
            url = urlIter.getNext();
            pass = passIter.getNext();
            if(Utils.equals(url, fixedUrl))
                return Utils.convertByte(pass);
        }

        return null;  // if not found
    }
    
    public byte[] getUsername(byte[] currentUrl)
    {
    	Byte[] fixedUrl = Utils.convertByte(Utils.padZeros(currentUrl, NUM_BYTE));
    	
    	Iterator<Byte[]> urlIter = urls.getIterator();
        Iterator<Byte[]> usernameIter = usernames.getIterator();
        
        Byte[] url;
        Byte[] username;
        
        while (urlIter.hasNext()) {
            url = urlIter.getNext();
            username = usernameIter.getNext();
            if(Utils.equals(url, fixedUrl))
                return Utils.convertByte(username);
        }

        return null;  // if not found
    }
    
    public boolean isValidPass(byte[] pass2check)
    {
    	byte[] realPass = new byte[FlashStorage.getFlashDataSize(PASSWORD_CODE)];
    	FlashStorage.readFlashData(PASSWORD_CODE, realPass, 0);
    	return Utils.equals(pass2check, realPass);
    }
    
    public void setPassword(byte[] pass) 
    {
    	// make sure data password is clean.
    	if(isRegistered())
    	{
        	FlashStorage.eraseFlashData(PASSWORD_CODE);
    	}
        // write to the FlaseStorage the access password, the master key
        FlashStorage.writeFlashData(PASSWORD_CODE, pass, 0, pass.length);
    }
}