package TEE_8442_0871;

public class Utils {
	private static final int INT_SIZE = 4;
	
	/**
     * Convert a byte array to an array of integers
     *
     * @param data the byte array to convert
     * @return An array of integers.
     */
    public static int[] convertByteArrToIntArr(byte[] data) {
        int numOfInts = data.length / 4;
        int[] numbers = new int[numOfInts];
        for (int i = 0; i < numOfInts; i++) { //for every in
            for (int j = INT_SIZE - 1; j >= 0; j--) { //for every byte
                numbers[i] = (numbers[i] << 8) + (data[j + i * INT_SIZE] & 0xFF);
            }
        }
        return numbers;
    }
    
    /**
     * Convert an array of integers to an array of bytes
     *
     * @param data The array of integers to be converted to a byte array.
     * @return The byte array of the int array.
     */
    public static byte[] convertIntArrayToByteArr(int[] data) {


        byte[] bytes = new byte[data.length * INT_SIZE];
        for (int i = 0; i < data.length; i++) {
            byte[] intAsByteArr = convertIntToByteArr(data[i]);
            for (int j = 0; j < INT_SIZE; j++)
                bytes[(i * 4) + j] = intAsByteArr[j];
        }
        return bytes;
    }
    
    public static byte[] convertIntegerArrayToByteArray(Integer[] data) {
        int[] nums = convertIntArray(data);
        return convertIntArrayToByteArr(nums);
    }
    
    /**
     * Convert an integer to a byte array
     *
     * @param num The integer to convert to a byte array.
     * @return The byte array representation of the integer.
     */
    public static byte[] convertIntToByteArr(int num) {
        byte[] res = new byte[INT_SIZE]; //4 bytes in an integer
        int length = res.length;
        for (int i = 0; i < length; i++) {
            res[length - i - 1] = (byte) (num & 0xFF);
            num >>= 8;
        }
        return res;
    }
    
    /**
     * Convert a short to a byte array
     *
     * @param num The number to convert to a byte array.
     * @return The byte array of the short.
     */
    public static byte[] convertShortToByteArr(short num) {
        byte[] res = new byte[2];
        res[0] = (byte) (num & 0xff);
        res[1] = (byte) ((num >> 8) & 0xff);
        return res;
    }
    
    /**
     * If the data is too long, it will be sliced. If it's too short, it will be padded with zeros
     *
     * @param data   the data to be padded
     * @param length the length of the data to be returned.
     * @return The data that is being passed in.
     */
    public static byte[] setSize(byte[] data, int length) {
        if (data.length < length) {
            byte[] dataPadding = new byte[data.length];
            int i;
            for (i = 0; i < data.length; i++)
                dataPadding[i] = data[i]; // copy data to dataPadding
            for (i = data.length; i < length; i++)
                dataPadding[i] = 0; // pad with 00

            return dataPadding;

        } else if (data.length > length) {
            byte[] dataSliced = new byte[length];
            for (int i = 0; i < length; i++) {
                dataSliced[i] = data[i];
            }
            return dataSliced;
        }
        return data;
    }
    
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
    
    /**
     * @param oldArray The array to convert.
     * @return An array of integers.
     */
    public static Integer[] convertIntArray(int[] oldArray) {
        Integer[] newArray = new Integer[oldArray.length];
        int i = 0;
        for (int value : oldArray) {
            newArray[i++] = Integer.valueOf(value);
        }
        return newArray;
    }
    
    /**
     * @param oldArray The array to convert.
     * @return An array of integers.
     */
    public static int[] convertIntArray(Integer[] oldArray) {
        int[] newArray = new int[oldArray.length];
        int i = 0;
        for (int value : oldArray) {
            newArray[i++] = Integer.valueOf(value);
        }
        return newArray;
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
    
    /**
     * split arrays
     */
    public static void splitArray(byte[] fullArray, byte[] a, byte[] b, byte[] c) {
    	// assume that a.length + b.length + c.length equal to fullArray.length
    	int j = 0;
        for(int i = 0; i < a.length; i++) a[i] = fullArray[j++];
        for(int i = 0; i < b.length; i++) b[i] = fullArray[j++];
        for(int i = 0; i < c.length; i++) c[i] = fullArray[j++];
    }
}
