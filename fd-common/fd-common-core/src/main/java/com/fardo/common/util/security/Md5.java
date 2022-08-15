/**
 * @(#)Md5.java
 * 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号: 
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-6-28
 * 描　述：创建
 */
package com.fardo.common.util.security;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @作者:李木泉
 * @文件名:Md5
 * @版本号:1.0
 * @创建日期:2016-6-28
 * @描述:
 */
public class Md5 {
	private static final String CHARSET_UTF8 = "UTF-8";

	/**
	 * 默认使用utf-8编码
	 * @更新时间:2016-6-28
	 * @更新作者:李木泉
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String encryptMD5(String data) {
		return encryptMD5(data,CHARSET_UTF8);

	}
	
	public static String encryptMD5(byte[] data) {
		// 创建具有指定算法名称的信息摘要
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
			byte[] results = md.digest(data);
			// 将得到的字节数组变成字符串返回
			return byte2hex(results);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 
	 * @更新时间:2016-6-28
	 * @更新作者:李木泉
	 * @param data
	 * @param CHARSET
	 * @return
	 * @throws IOException
	 */
	public static String encryptMD5(String data,String CHARSET) {
		// 创建具有指定算法名称的信息摘要
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
			byte[] results = md.digest(data.getBytes(CHARSET));
			// 将得到的字节数组变成字符串返回
			return byte2hex(results);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 
	 * @更新时间:2016-6-29
	 * @更新作者:李木泉
	 * @param bytes
	 * @return
	 */
	private static String byte2hex(byte[] bytes) {
	    StringBuilder sign = new StringBuilder();
	    for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(bytes[i] & 0xFF);
	        if (hex.length() == 1) {
	            sign.append("0");
	        }
	        sign.append(hex.toUpperCase());
	    }
	    return sign.toString();
	}
}
