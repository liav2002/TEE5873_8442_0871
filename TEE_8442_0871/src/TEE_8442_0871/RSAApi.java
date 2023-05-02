package TEE_8442_0871;
import com.intel.crypto.RsaAlg;
import com.intel.util.DebugPrint;

public abstract class RSAApi {
	private static FlashStorageAPI fsInstance = FlashStorageAPI.getInstance();

	public static final short PRIVATE_KEY_LENGTH = 256;
	public static final short PUBLIC_KEY_LENGTH = 4;
	
	private static RsaAlg Rsa = RsaAlg.create();
	
	/**
	 * Generate a public and private key pair if they yet exist, 
	 * and store them in the FlashStorage
	 *
	 * @return The keys.
	 */
	public static byte[] generateKeys() {
		Rsa.setHashAlg(RsaAlg.HASH_TYPE_SHA256);
		Rsa.setPaddingScheme(RsaAlg.PAD_TYPE_PKCS1);
		byte[] nArray;
		byte[] eArray;
		byte[] dArray;

		if(fsInstance.existsKey()) {
			nArray = new byte[PRIVATE_KEY_LENGTH];
			eArray = new byte[PUBLIC_KEY_LENGTH];
			dArray = new byte[PRIVATE_KEY_LENGTH];

			fsInstance.getKeys(nArray, eArray, dArray);
			Rsa.setKey(
					nArray, (short)0, (short)nArray.length,
					eArray, (short)0, (short)eArray.length,
					dArray, (short)0, (short)dArray.length
			);

		}
		else {
			Rsa.generateKeys(PRIVATE_KEY_LENGTH);
			nArray=new byte[Rsa.getModulusSize()];
			eArray=new byte[Rsa.getPublicExponentSize()];
			dArray=new byte[Rsa.getPrivateExponentSize()];
			Rsa.getKey(nArray, (short)0, eArray, (short)0, dArray, (short)0);
			fsInstance.setKey(nArray, eArray, dArray);
			DebugPrint.printString("Generate keys");

			DebugPrint.printString("N is:");
			DebugPrint.printBuffer(nArray);
			DebugPrint.printString("E is:");
			DebugPrint.printBuffer(eArray);
			DebugPrint.printString("D is:");
			DebugPrint.printBuffer(dArray);
		}
		return Utils.concatArrays(nArray, eArray, dArray);
	}
	
	public static byte[] signature(byte[] data) {
		byte[] signature=new byte[256];
		Rsa.signComplete(data, (short)0, (short)data.length, signature, (short)0);

		return signature;
	}
}
