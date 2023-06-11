package com;

import java.util.LinkedList;
import java.util.Random;

public class PasswordGenerator {
    public static Byte[] generateRandomPassword(int length) {
        LinkedList<Byte> password = new LinkedList<Byte>();

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
        
        Random random = new Random();
        // Generate the remaining characters randomly
        for (int i = 4; i < length; i++) {
        	int code = random.nextInt(4);
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
        Byte[] shuffledPassword = password.toArray(new Byte[0]);
        for (int i = shuffledPassword.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Byte temp = shuffledPassword[i];
            shuffledPassword[i] = shuffledPassword[j];
            shuffledPassword[j] = temp;
        }

        return shuffledPassword;
    }

    private static Byte getRandomElementFrom(Byte[] array) {
        Random random = new Random();
        int randomIndex = random.nextInt(array.length);
        Byte randomElement = array[randomIndex];
        return randomElement;
    }
}