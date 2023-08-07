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
	int triosCount = 0; //number of urls, passwords and usernames.
	
	//Constants values for char and bytes:
    final static int BYTE_CHAR_RATIO = 1;
    final static int NUM_CHAR = 20;
    final static int NUM_BYTE = BYTE_CHAR_RATIO * NUM_CHAR;
    final int SLOT_SIZE = NUM_BYTE * 3;
    
    final static int LEN_BYTES = 3;
	
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
    	triosCount = 0;
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
    	// DATA_STRUCTURE: NUM_OF_TRIOS[3 Bytes] | URL_SIZE [3 Bytes] | URL |USERNAME_SIZE[3 Bytes] | USERNAME | PASSWORD_SIZE [3 Bytes] | PASSWORD ...
    	byte[] data = new byte[FlashStorage.getFlashDataSize(DATA_CODE)];
        FlashStorage.readFlashData(DATA_CODE, data, 0);  
        
        triosCount = Utils.convert3BytesArrayToInt((Utils.sliceArray(data, 0, LEN_BYTES)));
        int currentIndex = LEN_BYTES;
        
        for(int i = 0; i < triosCount; i++)
        {
        	int urlSize = Utils.convert3BytesArrayToInt(Utils.sliceArray(data, currentIndex, currentIndex + LEN_BYTES));
        	currentIndex += LEN_BYTES;
        	byte[] urlBytes = Utils.sliceArray(data, currentIndex, currentIndex + urlSize);
        	currentIndex += urlSize;
        	
        	int usernameSize = Utils.convert3BytesArrayToInt(Utils.sliceArray(data, currentIndex, currentIndex + LEN_BYTES));
        	currentIndex += LEN_BYTES;
        	byte[] usernameBytes = Utils.sliceArray(data, currentIndex, currentIndex + usernameSize);
        	currentIndex += usernameSize;
        	
        	int passwordSize = Utils.convert3BytesArrayToInt(Utils.sliceArray(data, currentIndex, currentIndex + LEN_BYTES));
        	currentIndex += LEN_BYTES;
        	byte[] passwordBytes = Utils.sliceArray(data, currentIndex, currentIndex + passwordSize);
        	currentIndex += passwordSize;
        	
        	// Convert bytes to Byte objects
            Byte[] url = Utils.convertByte(urlBytes);
            Byte[] username = Utils.convertByte(usernameBytes);
            Byte[] password = Utils.convertByte(passwordBytes);
            
            // Add to LinkedLists
            urls.add(url);
            usernames.add(username);
            passwords.add(password);
        }
    }
    
    public void saveData() {    	
    	// First, calculate number of bytes to write.
    	int totalDataSize = LEN_BYTES; // initialized for first 3 bytes of numberOfTrios.
    	
    	// Calculate the size for URL, Username, and Password data
        Iterator<Byte[]> urlIterator = urls.getIterator();
        Iterator<Byte[]> usernameIterator = usernames.getIterator();
        Iterator<Byte[]> passwordIterator = passwords.getIterator();
        
        while (urlIterator.hasNext() && usernameIterator.hasNext() && passwordIterator.hasNext()) {
            Byte[] url = urlIterator.getNext();
            Byte[] username = usernameIterator.getNext();
            Byte[] password = passwordIterator.getNext();

            totalDataSize += 3 + url.length + 3 + username.length + 3 + password.length;
        }
        
        // Then, create the byte array represent data to write.
        Byte[] data2save = new Byte[totalDataSize];
        int currentIndex = 0;
        
        // Write triosCount
        Byte[] triosCountBytes = Utils.convertByte(Utils.convertIntTo3BytesArray(triosCount));
        Utils.place(data2save, triosCountBytes, currentIndex);
        currentIndex += LEN_BYTES;
        
        // Write URL, Username, and Password data
        urlIterator = urls.getIterator();
        usernameIterator = usernames.getIterator();
        passwordIterator = passwords.getIterator();
        
        while (urlIterator.hasNext() && usernameIterator.hasNext() && passwordIterator.hasNext()) {
            Byte[] url = urlIterator.getNext();
            Byte[] username = usernameIterator.getNext();
            Byte[] password = passwordIterator.getNext();

            // Write URL
            Byte[] urlSizeBytes = Utils.convertByte(Utils.convertIntTo3BytesArray(url.length));
            Utils.place(data2save, urlSizeBytes, currentIndex);
            currentIndex += LEN_BYTES;
            Utils.place(data2save, url, currentIndex);
            currentIndex += url.length;

            // Write Username
            Byte[] usernameSizeBytes = Utils.convertByte(Utils.convertIntTo3BytesArray(username.length));
            Utils.place(data2save, usernameSizeBytes, currentIndex);
            currentIndex += LEN_BYTES;
            Utils.place(data2save, username, currentIndex);
            currentIndex += username.length;

            // Write Password
            Byte[] passwordSizeBytes = Utils.convertByte(Utils.convertIntTo3BytesArray(password.length));
            Utils.place(data2save, passwordSizeBytes, currentIndex);
            currentIndex += LEN_BYTES;
            Utils.place(data2save, password, currentIndex);
            currentIndex += password.length;
        }
        
        // Finally, write to flash storage data.
        FlashStorage.writeFlashData(DATA_CODE, Utils.convertByte(data2save), 0, data2save.length);
    }

    public void addData(byte[] url, byte[] username, byte[] password)
    {
    	byte[] fixedUrl = Utils.padZeros(url, NUM_BYTE);
    	byte[] fixedUsername = Utils.padZeros(username, NUM_BYTE);
    	byte[] fixedPassword = Utils.padZeros(password, NUM_BYTE);

    	urls.add(Utils.convertByte(fixedUrl));
    	usernames.add(Utils.convertByte(fixedUsername));
        passwords.add(Utils.convertByte(fixedPassword));
        triosCount++;

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