/**
 * @(#)RSACoderHelper.java
 * 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号: 
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-8-13
 * 描　述：创建
 */

package com.fardo.common.util.security.rsa;

import com.fardo.common.util.security.Base64;
import com.fardo.common.util.security.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @作者:李木泉
 * @文件名:RSACoderHelper
 * @版本号:1.0
 * @创建日期:2016-8-13
 * @描述:
 */
public class RSACoderHelper {
	
	private static Logger logger = LoggerFactory.getLogger(RSACoderHelper.class);
	
	public static final String KEY_ALGORITHM = "RSA";
	public static final String PUBLIC_KEY = "RSAPublicKey";
	public static final String PRIVATE_KEY = "RSAPrivateKey";
	//public static final String CHAR_ENCODING = "utf-8";
	/*RSA密钥长度 必须为64的倍数 默认为1024*/
	private static int KEY_SIZE = 1024;
	
	/**
	 * 获取实例
	 * @param 密钥长度(默认为1024)
	 * @return
	 * @throws Exception
	 */
	public static RSACoderHelper getInstance(){
		return new RSACoderHelper();
	}
	
	/**
	 * 密钥初始化(系统自动生成)
	 * @return 密钥对
	 * @throws Exception
	 */
	public Map<String,Object> initKey()throws Exception{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(KEY_SIZE);
		KeyPair keypair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey)keypair.getPublic();//公钥
		RSAPrivateKey privateKey = (RSAPrivateKey)keypair.getPrivate(); //私钥
		Map<String,Object> keyMap = new HashMap<String,Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
	
	/**
	 * 获取公钥
	 * @param map 密钥对Map
	 * @return 公钥byte[]
	 */
	public byte[] getPublicKey(Map<String,Object> map){
		Key key = (Key)map.get(PUBLIC_KEY);
		return key.getEncoded();
	}
	
	/**
	 * 获取公钥
	 * @param map 密钥对Map
	 * @return String 公钥
	 */
	public String getStringPublicKey(Map<String,Object> map){
		return Base64.encodeBase64String(getPublicKey(map));
	}
	
	/**
	 * 获取私钥
	 * @param map 密钥对Map
	 * @return 私钥byte[]
	 */
	public byte[] getPrivateKey(Map<String,Object> map){
		Key key = (Key)map.get(PRIVATE_KEY);
		return key.getEncoded();
	}
	
	/**
	 * 获取私钥
	 * @param map 密钥对Map
	 * @return String 私钥
	 */
	public String getStringPrivateKey(Map<String,Object> map){
		return Base64.encodeBase64String(getPrivateKey(map));
	}
	
	/**
	 * 私钥加密
	 * @param data 原文byte[]
	 * @param key 密钥byte[]
	 * @return 密文
	 * @throws Exception
	 */
	private byte[] encryptByPrivateKey(byte[] data,byte[] key) throws Exception{
		PKCS8EncodedKeySpec pkcs8ks = new PKCS8EncodedKeySpec(key);
		KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey pk = kf.generatePrivate(pkcs8ks);
		Cipher cipher = Cipher.getInstance(kf.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, pk);
		return cipher.doFinal(data);
	}
	
	/**
	 * 私钥加密
	 * @param data 原文
	 * @param key 密钥
	 * @return 密文
	 * @throws Exception
	 */
	public String encryptByPrivateKeyString(String data,String key) throws Exception{
		return Base64.encodeBase64String(encryptByPrivateKey(data.getBytes(CharEncoding.UTF_8),Base64.decodeBase64(key)));
	}
	
	/**
	 * 私钥解密
	 * @param data 密文byte[]
	 * @param key 密钥byte[]
	 * @return 原文byte[]
	 * @throws Exception
	 */
	public byte[] decryptByPrivateKey(byte[] data,byte[] key) throws Exception{
		PKCS8EncodedKeySpec pkcs8ks = new PKCS8EncodedKeySpec(key);
		KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey pk = kf.generatePrivate(pkcs8ks);
		Cipher cipher = Cipher.getInstance(kf.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, pk);
		return cipher.doFinal(data);
	}
	
	/**
	 * 私钥解密
	 * @param data 密文
	 * @param key 密钥
	 * @return 原文
	 * @throws Exception
	 */
	public String decryptByPrivateKey(String data,String key) throws Exception{
		return new String(decryptByPrivateKey(Base64.decodeBase64(data),Base64.decodeBase64(key)));
	}
	
	/**
	 * 公钥解密
	 * @param data 密文byte[]
	 * @param key 密钥byte[]
	 * @return 原文byte[]
	 * @throws Exception
	 */
	private byte[] decryptByPublicKey(byte[] data,byte[] key) throws Exception{
		X509EncodedKeySpec x509ks = new X509EncodedKeySpec(key);
		KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pk = kf.generatePublic(x509ks);
		Cipher cipher = Cipher.getInstance(kf.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, pk);
		return cipher.doFinal(data);
	}
	
	/**
	 * 公钥解密，用于授权文件，专用于RSA/ECB/PKCS1Padding协议
	 * @更新时间:2017年6月28日
	 * @更新作者:linfeng
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private byte[] decryptByPublicKeyRSA_EBC_PK(byte[] data,byte[] key)  throws Exception{
		X509EncodedKeySpec x509ks = new X509EncodedKeySpec(key);
		KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pk = kf.generatePublic(x509ks);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, pk);
		return cipher.doFinal(data);
	}
	
	/**
	 * 公钥解密，用于授权文件，专用于RSA/ECB/PKCS1Padding协议
	 * @更新时间:2017年6月28日
	 * @更新作者:linfeng
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String decryptByPublicKeyRSA_EBC_PK(String data,String key) throws Exception{
		return new String(decryptByPublicKeyRSA_EBC_PK(Base64.decodeBase64(data),Base64.decodeBase64(key)),CharEncoding.UTF_8);
	}
	
	/**
	 * 公钥解密
	 * @param data 密文
	 * @param key 密钥
	 * @return 原文
	 * @throws Exception
	 */
	public String decryptByPublicKey(String data,String key) throws Exception{
		return new String(decryptByPublicKey(Base64.decodeBase64(data),Base64.decodeBase64(key)),CharEncoding.UTF_8);
	}
	
	/**
	 * 公钥加密
	 * @param data 原文byte[]
	 * @param key 密钥byte[]
	 * @return 密文byte[]
	 * @throws Exception
	 */
	public byte[] encryptByPublicKey(byte[] data,byte[] key) throws Exception{
		X509EncodedKeySpec x509ks = new X509EncodedKeySpec(key);
		KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pk = kf.generatePublic(x509ks);
		Cipher cipher = Cipher.getInstance(kf.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, pk);
		return cipher.doFinal(data);
	}
	
	/**
	 * 公钥加密
	 * @param data 原文byte[]
	 * @param key 密钥byte[]
	 * @return 密文
	 * @throws Exception
	 */
	public String encryptByPublicKeyString(byte[] data,byte[] key) throws Exception{
		return Base64.encodeBase64String(encryptByPublicKey(data,key));
	} 
	
	/**
	 * 公钥加密
	 * @param data 原文
	 * @param key 密钥
	 * @return 密文
	 * @throws Exception
	 */
	public String encryptByPublicKeyString(String data,String key) throws Exception{
		return encryptByPublicKeyString(data.getBytes(CharEncoding.UTF_8),Base64.decodeBase64(key));
	}
	
	/**
	 * 加密(java与c#互通)
	 * java通过c#传来的公钥进行加密
	 * @param data 原文
	 * @param module 模块
	 * @param exponent 指数
	 * @return 密文
	 * @throws Exception
	 */
	public String encryptNet(String data,String module,String exponent) throws Exception {
		return Base64.encodeBase64String(encryptNet(data.getBytes(CharEncoding.UTF_8), Base64.decodeBase64(module), Base64.decodeBase64(exponent)));
	}
	
	/**
	 * 加密(java与c#互通)
	 * java通过c#传来的公钥进行加密
	 * @param data 原文byte[]
	 * @param module 模块byte[]
	 * @param exponent 指数byte[]
	 * @return 密文byte[]
	 * @throws Exception
	 */
    private byte[] encryptNet(byte[] data,byte[] module,byte[] exponent) throws Exception {
        BigInteger modulus = new BigInteger(1, module);  
        BigInteger exponents = new BigInteger(1, exponent);  

        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponents);
        KeyFactory fact = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey pubKey = fact.generatePublic(rsaPubKey);  

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data); 
    }  
    
    /**
     * 解密(java与c#互通)
     * java通过c#传来的私钥进行解密
     * @param data 密文
     * @param delement 专用指数
     * @param module 模块
     * @return 原文
     * @throws Exception
     */
    public String dencryptNet(String data,String delement,String module) throws Exception{ 
        return new String(dencryptNet(Base64.decodeBase64(data), Base64.decodeBase64(delement), Base64.decodeBase64((module))));  
    }
    
    /**
     * 解密(java与c#互通)
     * java通过c#传来私钥进行解密
     * @param data 密文byte[]
     * @param delement 专用指数
     * @param module 模块byte[]
     * @return 原文byte[]
     * @throws Exception
     */
    public byte[] dencryptNet(byte[] data,byte[] delement,byte[] module) throws Exception {  
    	BigInteger modules = new BigInteger(1, module);  
        BigInteger d = new BigInteger(1, delement);  

        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);  

        RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, d);  
        PrivateKey privKey = factory.generatePrivate(privSpec);  
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        return cipher.doFinal(data); 
    }
	
    /**
	 * java 私钥转换为c#私钥
	 * @param encodedPrivatekey java私钥
	 * @return
	 */
    public String getRSAPrivateKeyAsNetFormat(String encodedPrivatekey){
    	return getRSAPrivateKeyAsNetFormat(Base64.decodeBase64(encodedPrivatekey));
    }
    
    /**
	 * java 私钥转换为c#私钥
	 * @param key java私钥
	 * @return
	 */
	private String getRSAPrivateKeyAsNetFormat(byte[] key) {
		try {
	    	StringBuffer buff = new StringBuffer(1024);
	    	RSAPrivateCrtKey pvkKey = getRSAPrivateCrtKey(key);
	        buff.append("<RSAKeyValue>");
	        buff.append("<Modulus>"+ b64encode(removeMSZero(pvkKey.getModulus().toByteArray()))+ "</Modulus>");
	        buff.append("<Exponent>"+ b64encode(removeMSZero(pvkKey.getPublicExponent().toByteArray())) + "</Exponent>");//公用指数
	        buff.append("<P>"+ b64encode(removeMSZero(pvkKey.getPrimeP().toByteArray()))+ "</P>");
	        buff.append("<Q>"+ b64encode(removeMSZero(pvkKey.getPrimeQ().toByteArray()))+ "</Q>");
	        buff.append("<DP>"+ b64encode(removeMSZero(pvkKey.getPrimeExponentP().toByteArray())) + "</DP>");
	        buff.append("<DQ>"+ b64encode(removeMSZero(pvkKey.getPrimeExponentQ().toByteArray())) + "</DQ>");
	        buff.append("<InverseQ>"+ b64encode(removeMSZero(pvkKey.getCrtCoefficient().toByteArray())) + "</InverseQ>");
	        buff.append("<D>"+ b64encode(removeMSZero(pvkKey.getPrivateExponent().toByteArray())) + "</D>");
	        buff.append("</RSAKeyValue>");
	        return buff.toString().replaceAll("[\t\n\r]", "");
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	        return null;
	    }
	}
	
	/**
	 * java 私钥转换为c#公钥
	 * @param encodedPrivatekey java私钥
	 * @return
	 */
	public String getRSAPublicKeyAsNetFormat(String encodedPrivatekey){
		return getRSAPublicKeyAsNetFormat(Base64.decodeBase64(encodedPrivatekey));
	}
	public String getRSAPublicKeyAsJavaFormat(String encodedPrivatekey){
		return getRSAPublicKeyAsJavaFormat(Base64.decodeBase64(encodedPrivatekey));
	}
	
	/**
	 * java私钥转换为c#公钥Modulus系数
	 * @param key java端生成的私钥
	 * @return
	 * @throws Exception 
	 */
	private String getRSAPublicKeyModulus(String key) throws Exception{
		return b64encode(removeMSZero(getRSAPrivateCrtKey(Base64.decodeBase64(key)).getModulus().toByteArray()));
	}
	
	/**
	 * java私钥转换为c#公钥Exponent系数
	 * @param key java端生成的私钥
	 * @return
	 * @throws Exception 
	 */
	private String getRSAPublicKeyExponent(String key) throws Exception{
		return b64encode(removeMSZero(getRSAPrivateCrtKey(Base64.decodeBase64(key)).getPublicExponent().toByteArray()));
	}
	
	/**
	 * 获取私钥 RSAPrivateCrtKey
	 * @param key 私钥
	 * @return RSAPrivateCrtKey
	 * @throws Exception
	 */
	public RSAPrivateCrtKey getRSAPrivateCrtKey(byte [] key) throws Exception{
		PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(pvkKeySpec);
		return pvkKey;
	}
	public PublicKey getRSAPublicKey(byte [] key) throws Exception{
		PKCS8EncodedKeySpec pvkKeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateCrtKey pvkKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(pvkKeySpec);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(pvkKey.getModulus(), pvkKey.getPublicExponent());
		PublicKey pubKey = (PublicKey) keyFactory.generatePublic(keySpec);
		return pubKey;
	}
	/**
	 * java 私钥转换为c#公钥
	 * @param key java私钥byte[]
	 * @return
	 */
	private String getRSAPublicKeyAsNetFormat(byte[] key) {
		try {
			StringBuffer buff = new StringBuffer(1024);
			RSAPrivateCrtKey pvkKey = getRSAPrivateCrtKey(key);
			buff.append("<RSAKeyValue>");
			buff.append("<Modulus>"+ b64encode(removeMSZero(pvkKey.getModulus().toByteArray()))+ "</Modulus>");
			buff.append("<Exponent>"+ b64encode(removeMSZero(pvkKey.getPublicExponent().toByteArray())) + "</Exponent>");
			buff.append("</RSAKeyValue>");
			return buff.toString().replaceAll("[ \t\n\r]", "");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	private String getRSAPublicKeyAsJavaFormat(byte[] key) {
		try {			
			PublicKey pubKey = getRSAPublicKey(key);			
			return b64encode(pubKey.getEncoded());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
    
	/**
	 * Base64 加密
	 * @param data 原文byte[]
	 * @return 密文
	 */
	private String b64encode(byte[] data) {
		return Base64.encodeBase64String(data);
	}
	
	private byte[] removeMSZero(byte[] data) {
		byte[] dataNew;
	    int len = data.length;
	    if (data[0] == 0) {
	    	dataNew = new byte[data.length - 1];
	        System.arraycopy(data, 1, dataNew, 0, len - 1);
	    } else{
	    	dataNew = data;
	    }
	    return dataNew;
	}

	/**
	 * 根据RSA公钥进行密码加密
	 *
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM, new org.bouncycastle.jce.provider.BouncyCastleProvider());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长
		int key_len = publicKey.getModulus().bitLength() / 8;
		// 加密数据长度 <= 模长-11
		String[] datas = splitString(data, key_len - 11);
		String mi = "";
		// 如果明文长度大于模长-11则要分组加密
		for (String s : datas) {
			mi += bcd2Str(cipher.doFinal(s.getBytes()));
		}
		return mi;
	}
	/**
	 * 根据RSA私钥进行密码解密
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM, new org.bouncycastle.jce.provider.BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		// System.err.println(bcd.length);
		// 如果密文长度大于模长则要分组解密
		String ming = "";
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming += new String(cipher.doFinal(arr));
		}
		return ming;
	}
	/**
	 * ASCII码转BCD码
	 *
	 */
	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	/**
	 *BCD码值转换
	 * @param asc
	 * @return
	 */
	public static byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	/**
	 * BCD转字符串
	 */
	public static String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	/**
	 * 拆分字符串
	 */
	public static String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if (i == x + z - 1 && y != 0) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}
	/**
	 * 拆分数组
	 */
	public static byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[len];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}
}
