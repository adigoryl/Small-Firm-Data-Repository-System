package yuconz23d;

import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.math.BigInteger;
//import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Used for encrypting and decrypting passwords with AES
 * The key will be generated from their personal details
 * To avoid a key being stored and hardcoded
 * @author Tom
 *
 */

public class Encryption {

	Cipher ecipher;
	Cipher dcipher;
	SecretKey key;
	String basekey;

	public Encryption() throws Exception {

		ecipher = Cipher.getInstance("AES");
		dcipher = Cipher.getInstance("AES");
	}

	/**
	 * Creates a SecretKey to be used in encrypting and decrypting
	 * based off a provided String
	 * @param basekey the String to be used to generate the SecretKey
	 * @return the generated SecretKey 
	 */
	public SecretKey generateSecret(String basekey) {

		SecretKey key = new SecretKeySpec(basekey.getBytes(), "AES");
		return key;
	}

	/**
	 * Encrypts a String using AES and then returns it
	 * @param str the String to be encrypted
	 * @param key the SecretKey to be used for encryption
	 * @return the encrypted String
	 * @throws Exception
	 */
	public String encrypt(String str, SecretKey key) throws Exception {
		
		
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		
		// Encode the string
		byte[] utf8 = str.getBytes("UTF8");

		// Encrypt
		byte[] enc = ecipher.doFinal(utf8);

		// Encode
		String encrypted = new sun.misc.BASE64Encoder().encode(enc);

		System.out.println(encrypted);
		System.out.println(key);
		return encrypted;

	}
	
	/**
	 * Decrypts a String using AES and then returns it
	 * @param str the String to be decrypted
	 * @param key the SecretKey to be used for decryption
	 * @return the decrypted String
	 * @throws Exception
	 */
	public String decrypt(String str, SecretKey key) throws Exception {
		
		dcipher.init(Cipher.DECRYPT_MODE, key);
		// Decode
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

		// Decrypt
		byte[] utf8 = dcipher.doFinal(dec);

		// Decode using utf-8
		return new String(utf8, "UTF8");
	}

	/**
	 * Generates a 16 character long String from a variable length String 
	 * This will be used to generate a unique key for each user for encrypting and
	 * de-crypting their password
	 * @param text the text to be hashed to 16 characters
	 * @return the text hashed into 16 characters
	 */
	public String hashString(String text) throws Exception {

		MessageDigest msg = MessageDigest.getInstance("MD5");
		msg.update(text.getBytes(), 0, text.length());
		String digest1 = new BigInteger(1, msg.digest()).toString(16);
		digest1 = digest1.substring(0, 16);
		return digest1;

	}

}
