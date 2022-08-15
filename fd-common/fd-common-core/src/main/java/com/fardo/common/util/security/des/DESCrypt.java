package com.fardo.common.util.security.des;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;

public class DESCrypt {
	private static String key = "87F78F1A";

	public static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}
		return hexString.toString();
	}

	public static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	public static String encryptByEncoding(String message, String encoding) {
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(encoding));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes(encoding));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] buf = cipher.doFinal(message.getBytes(encoding));
			String a = toHexString(buf);
			return a;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String encrypt(String message, String encoding) {
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(encoding));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes(encoding));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] buf = cipher.doFinal(message.getBytes("gb2312"));
			String a = toHexString(buf);
			return a;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String message, String encoding)throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec dks = new DESKeySpec(key.getBytes(encoding));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);
		AlgorithmParameterSpec iv = new IvParameterSpec(key.getBytes(encoding));
		cipher.init(Cipher.DECRYPT_MODE, securekey, iv);
		byte[] pasByte = cipher.doFinal(convertHexString(message));
		return new String(pasByte, encoding);

		/*
		 * return toHexString(cipher.doFinal(message.getBytes("gb2312")));
		 */
	}

	public static void main(String agrs[]) throws Exception {
		System.out.println(decrypt("0E7E04AACD1A789A28B27787056D292B7B1A749C4EB6296AB1777A0799F5000855346BE4FDD37A9E35F61FB6B29F6909D035DD9EFB156D3300F37E26BF87B814BF5ECF522E168A50DC074C857B826CE29C510A156D9113E7EAECBFD73AAC71CDCD1A811F4B090704F08B3D21B8FB79E4A1E772726239DA618C0F28F3B7760C28F2A1D54E22D9B87AF47F09C042AF47E4CBE8C02E3C25727CCB6B2896393E13E4AA2E68CE3A60EE310255A3874035F6F27A7287A73E9F2F6935C846002A67C26D".toUpperCase(), "gb2312"));
	}
}
