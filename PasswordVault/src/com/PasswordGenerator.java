package com;

import com.intel.crypto.Random;
import com.intel.langutil.Iterator;
import com.intel.langutil.LinkedList;

public class PasswordGenerator {
    public static Byte[] generateRandomPassword(int length) {
        LinkedList<Byte> password =LinkedList.create();

        // Create a character pool combining all required character types
        Byte[] lowercaseChars = Utils.convertByte("abcdefghijklmnopqrstuvwxyz".getBytes());
        Byte[] uppercaseChars = Utils.convertByte("ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes());
        Byte[] numbers = Utils.convertByte("0123456789".getBytes());
        Byte[] specialChars = Utils.convertByte("!@#$%^&*()_-+=<>?".getBytes());

        // Add at least one character from each character type
        password.add(getRandomElementFrom(lowercaseChars));
        password.add(getRandomElementFrom(uppercaseChars));
        password.add(getRandomElementFrom(numbers));
        password.add(getRandomElementFrom(specialChars));
        
        // Generate the remaining characters randomly
        for (int i = 4; i < length; i++) {
        	int code = customNextInt(4);
        	switch (code)
        	{
	        	case 0:
	        	{
	        		password.add(getRandomElementFrom(lowercaseChars));
	        		break;
	        	}
	        	case 1:
	        	{
	        		password.add(getRandomElementFrom(uppercaseChars));
	        		break;
	        	}
	        	case 2:
	        	{
	        		password.add(getRandomElementFrom(numbers));
	        		break;
	        	}
	        	case 3:
	        	{
	        		password.add(getRandomElementFrom(specialChars));
	        		break;
	        	}
        	}
        }

        // Shuffle the password characters
        Byte[] shuffledPassword = linkedListToByteArray(password);
        for (int i = shuffledPassword.length - 1; i > 0; i--) {
            int j = customNextInt(i + 1);
            Byte temp = shuffledPassword[i];
            shuffledPassword[i] = shuffledPassword[j];
            shuffledPassword[j] = temp;
        }

        return shuffledPassword;
    }

    private static Byte getRandomElementFrom(Byte[] array) {
        int randomIndex = customNextInt(array.length);
        Byte randomElement = array[randomIndex];
        return randomElement;
    }
    
    private static int customNextInt(int n) {
        byte[] randomBytes = new byte[2];
        Random.getRandomBytes(randomBytes, (short) 0, (short) 2);
        
        int randomValue = ((randomBytes[0] & 0xFF) << 8) | (randomBytes[1] & 0xFF);
        
        return Math.abs(randomValue) % (n + 1);
    }
    
    public static Byte[] linkedListToByteArray(LinkedList<Byte> linkedList) {
        Byte[] byteArray = new Byte[linkedList.size()];
        int index = 0;
        
        Iterator<Byte> iter = linkedList.getIterator();
        while (iter.hasNext()) {
            byteArray[index++] = iter.getNext();
        }

        return byteArray;
    }
}