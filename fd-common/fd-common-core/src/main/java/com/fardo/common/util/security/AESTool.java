package com.fardo.common.util.security;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

/**
 * AES encryption and decryption tool.
 * 
 * @author Joshua zhong
 */
public class AESTool {

	private byte[] initVector = { 0x32, 0x37, 0x36, 0x35, 0x34, 0x33, 0x32, 0x31, 0x38, 0x27, 0x36, 0x35, 0x33, 0x23, 0x32, 0x31 };

	/**
	 * Encrypt the content with a given key using aes algorithm.
	 * 
	 * @param content
	 * @param key
	 *            must contain exactly 32 characters
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String content, String key) throws Exception {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null!");
		}
		String encrypted = null;
		byte[] keyBytes = key.getBytes();
		byte[] encryptedBytes = null;
		encryptedBytes = encrypt(content.getBytes("utf-8"), keyBytes, initVector);
		encrypted = new String(Hex.encode(encryptedBytes));
		return encrypted;
	}

	/**
	 * Decrypt the content with a given key using aes algorithm.
	 * 
	 * @param content
	 * @param key
	 *            must contain exactly 32 characters
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String content, String key) throws Exception {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null!");
		}
		String decrypted = null;
		byte[] encryptedContent = Hex.decode(content);
		byte[] keyBytes = key.getBytes();
		byte[] decryptedBytes = null;
		decryptedBytes = decrypt(encryptedContent, keyBytes, initVector);
		decrypted = new String(decryptedBytes, "utf-8");
		return decrypted;
	}

	/**
	 * Encrypt data.
	 * 
	 * @param plain
	 * @param key
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] plain, byte[] key, byte[] iv) throws Exception {
		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
		CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
		aes.init(true, ivAndKey);
		return cipherData(aes, plain);
	}

	/**
	 * Decrypt data.
	 * 
	 * @param cipher
	 * @param key
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] cipher, byte[] key, byte[] iv) throws Exception {
		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
		CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
		aes.init(false, ivAndKey);
		return cipherData(aes, cipher);
	}

	/**
	 * Encrypt or decrypt data.
	 * 
	 * @param cipher
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data) throws Exception {
		int minSize = cipher.getOutputSize(data.length);
		byte[] outBuf = new byte[minSize];
		int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
		int length2 = cipher.doFinal(outBuf, length1);
		int actualLength = length1 + length2;
		byte[] result = new byte[actualLength];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;
	}

	public static void main(String[] args) throws Exception {
		AESTool aesTool = new AESTool();
		String key = "a368ec459659f4f4a4747cc674b94447";
		String json = "我去你MIEDE";
		String encrypted = aesTool.encrypt(json, key);
		System.out.println("encrypted: \n" + encrypted);
		System.out.println("encrypted length: \n" + encrypted.length());
		String decrypted = aesTool.decrypt(encrypted, key);
		System.out.println("decrypted: \n" + decrypted);
		System.out.println("decrypted length: \n" + decrypted.length());
		boolean isSuccessful = StringUtils.equals(decrypted, json);
		System.out.println(isSuccessful);
	}
}