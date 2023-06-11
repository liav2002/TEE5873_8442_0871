package com;
import com.intel.crypto.HashAlg;
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
     * @param length of byte[] required...
     */
    public static byte[] randomBytes(int length) {
        byte[] result = new byte[length];

        long numMiliSec = Calendar.getMillisFromStartup();
        byte[] input = convertLongToByteArr(numMiliSec);
        HashAlg hashObj = HashAlg.create(HashAlg.HASH_TYPE_SHA1);
        /** https://software.intel.com/sites/landingpage/dal/technology/api-reference/applet-apis/crypto/Hash-api_level6.html
         * short processComplete(byte[] input,
         short inputIndex,
         short inputLength,
         byte[] outputArray,
         short outputIndex)
         */
        hashObj.processComplete(input, (short)0, (short)input.length, result, (short)0);

        return result;
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