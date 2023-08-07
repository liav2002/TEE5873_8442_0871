package com;
import com.intel.crypto.HashAlg;
import com.intel.langutil.Iterator;
import com.intel.langutil.LinkedList;
import com.intel.util.Calendar;

public class Utils {
    /**
     * Given two arrays of bytes, return true if they are equal
     *
     * @param a The first byte array to compare.
     * @param b The byte array to compare to a.
     * @return The boolean value of the expression.
     */
    public static boolean equals(byte[] a, byte[] b) {
        if (a.length != b.length)
            return false;
        else
            for (int i = 0; i < a.length; i++)
                if (a[i] != b[i])
                    return false;

        return true;
    }

    public static boolean equals(Byte[] a, Byte[] b) {
        return equals(convertByte(a), convertByte(b));
    }

    /**
     * concatenates arrays
     */
    public static byte[] concatArrays(byte[] a, byte[] b, byte[] c) {
        byte[] abc = new byte[a.length + b.length + c.length];
        int j = 0;
        for (int i = 0; i < a.length; i++) abc[j++] = a[i];
        for (int i = 0; i < b.length; i++) abc[j++] = b[i];
        for (int i = 0; i < c.length; i++) abc[j++] = c[i];
        return abc;
    }
    
    public static byte[] concatArrays(byte[] a, byte[] b) {
        //MUST FIX TO A PARAMS.... FUNCTION! this is crazy
        byte[] ab = new byte[a.length + b.length];
        int j = 0;
        for (int i = 0; i < a.length; i++) ab[j++] = a[i];
        for (int i = 0; i < b.length; i++) ab[j++] = b[i];
        return ab;
    }


    /**
     * split arrays
     */
    public static byte[] sliceArray(byte[] array, int start, int end) {
        // include start, not include end
        byte[] slice = new byte[end - start];
        for(int i = start; i < end; i++) slice[i - start] = array[i];

        return slice;
    }

    public static Byte[] convertByte(byte[]a) {
        Byte[] b = new Byte[a.length];

        for(int i = 0; i < a.length; i++) b[i] = a[i];
        return b;
    }

    public static byte[] convertByte(Byte[]a) {
        byte[] b = new byte[a.length];

        for(int i = 0; i < a.length; i++) b[i] = a[i];
        return b;
    }

    public static byte[] padZeros(byte[] a, int size) {
        // assume a.length is not greater than size
        byte[] b = new byte[size];

        for(int i = 0; i < a.length; i++) b[i] = a[i];
        for(int i = a.length; i < size; i++) b[i] = 0;

        return b;
    }

    public static void place(Byte[] array, Byte[] subArray, int index) {
        for(int i = index; i < index + subArray.length; i++)
            array[i] = subArray[i - index];
    }

    /**
     *
     * @param length of byte[] required... (length < 20)
     */
    public static byte[] randomBytes(int length) {
        byte[] result = new byte[20];
        long numMiliSec = Calendar.getMillisFromStartup();
        byte[] input = convertLongToByteArr(numMiliSec);
       
        HashAlg hashObj = HashAlg.create(HashAlg.HASH_TYPE_SHA1);
        hashObj.processComplete(input, (short)0, (short)input.length, result, (short)0);
       
        return Utils.sliceArray(result, 0, length);
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
    
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value += (bytes[i] & 0xFF) << (8 * (bytes.length - 1 - i));
        }
        return value;
    }
    

    /**
     * Convert an integer to a byte array
     *
     * @param num The integer to convert to a byte array.
     * @return The byte array representation of the integer.
     */
    public static byte[] convertIntToByteArr(int num) {
        int intSize = 4;
        byte[] res = new byte[intSize]; //4 bytes in an integer
        int length = res.length;
        for (int i = 0; i < length; i++) {
            res[length - i - 1] = (byte) (num & 0xFF);
            num >>= 8;
        }
        return res;
    }
    
    /**
     * Convert an integer to a 3 bytes array, the byte will represent ascci of digits.
     *
     * @param num The integer to convert to a byte array.
     * @return The byte array representation of the integer.
     */
    public static byte[] convertIntTo3BytesArray(int a) {
        byte[] result = new byte[3];
        int digit1 = (a / 100) % 10;
        int digit2 = (a / 10) % 10;
        int digit3 = a % 10;
        
        result[0] = (byte) (digit1 + '0');
        result[1] = (byte) (digit2 + '0');
        result[2] = (byte) (digit3 + '0');
        
        return result;
    }
    
    /**
     * Converts a 3-byte array representing ASCII digits to an integer.
     *
     * This function takes a byte array containing three ASCII digits (0-9)
     * and converts it into an integer. The resulting integer represents the
     * value of the ASCII digits in the given byte array.
     *
     * @param a The byte array containing three ASCII digits (0-9).
     * @return An integer representation of the value of the ASCII digits in the array.
     * @throws ArrayIndexOutOfBoundsException If the input array does not contain exactly three elements.
     * @throws NumberFormatException If any element in the input array is not a valid ASCII digit.
     */
    public static int convert3BytesArrayToInt(byte[] a) {
        int digit1 = a[0] - '0';
        int digit2 = a[1] - '0';
        int digit3 = a[2] - '0';
        
        int result = (digit1 * 100) + (digit2 * 10) + digit3;
        
        return result;
    }
    
    /**
     * Split data according spaces
     *
     * @param data The data to split.
     * @return 2-d byte array represent the splitData.
     */
    public static byte[][] splitBySpace(byte[] data) {
        int wordCount = 0;

        // Count the number of words in the data
        for (int i = 0; i < data.length; i++) {
            if (data[i] == ' ') {
                wordCount++;
            }
        }
        // Account for the last word
        wordCount++;

        byte[][] result = new byte[wordCount][];

        int startIndex = 0;
        int wordIndex = 0;

        // Split the data into words
        for (int i = 0; i < data.length; i++) {
            if (data[i] == ' ') {
                int wordLength = i - startIndex;
                result[wordIndex] = new byte[wordLength];
                result[wordIndex] = Utils.sliceArray(data, startIndex, startIndex + wordLength);
                startIndex = i + 1;
                wordIndex++;
            }
        }

        // Handle the last word
        int lastWordLength = data.length - startIndex;
        result[wordIndex] = new byte[lastWordLength];
        result[wordIndex] = Utils.sliceArray(data, startIndex, startIndex + lastWordLength);

        return result;
    }
    
    
    /**
     * Convert a long to a byte array
     * @param num The long to convert to a byte array.
     * @return The byte array representation of the integer.
     */
    public static byte[] convertLongToByteArr(long num) {
        int longSize = 8;
        byte[] res = new byte[longSize]; //8 bytes in a long...
        int length = res.length;
        for (int i = 0; i < length; i++) {
            res[length - i - 1] = (byte) (num & 0xFF);
            num >>= 8;
        }
        return res;
    }

    public static byte[] cutEndOfByteArr(byte[] input, int lengthOfOutput)
    {
        byte[] res = new byte[lengthOfOutput];
        for (int i = 0; i < res.length; i++) {
            res[i] = input[i];
        }
        return res;
    }
}