/**
 * @(#)SignUtils.java
 * 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号: 
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-7-29
 * 描　述：创建
 */

package com.fardo.common.util.security;

import com.faduit.security.cipher.SM3;
import com.fardo.common.constant.RequestConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;


/**
 * @作者:李木泉
 * @文件名:SignUtils
 * @版本号:1.0
 * @创建日期:2016-7-29
 * @描述:
 */
public class SignUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SignUtils.class);

	/**
	 * 获取签名原串
	 * 规则：appSecrect+function+timestamp+appSecrect
	 * @param appSecrect
	 * @param function
	 * @param timestamp
	 * @param appSecrect
	 * @return
	 */
	public static String getSignInitStr(String appSecrect, String function, String params, String timestamp){
		String initStr = appSecrect + function+ params + timestamp + appSecrect;
		return initStr;
	}
	
	/**
	 * 计算字符串的md5值，编码默认使用utf-8
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static String encrptMD5ToString(String data) throws IOException{
		// 创建具有指定算法名称的信息摘要  
	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			 // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算  
		    byte[] results = md.digest(data.getBytes(StandardCharsets.UTF_8));
		    // 将得到的字节数组变成字符串返回
			String str = byte2hex(results);
		    return str;
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException：data={}",data);
		}  
		return null;
	}
	
	/**
	 * 签名验证
	 * @param params
	 * @param secret
	 * @return
	 * @throws IOException
	 */
	public static String signRequest(Map<String, String> params, String secret) throws 	IOException {
		//剔除sign参数
		if(params.containsKey("sign")){
			params.remove("sign");
		}
	    // 第一步：检查参数是否已经排序
	    String[] keys = params.keySet().toArray(new String[0]);
	    Arrays.sort(keys);
	 
	    // 第二步：把所有参数名和参数值串在一起
	    StringBuilder query = new StringBuilder();
	    query.append(secret);	    
	    for (String key : keys) {
	        String value = params.get(key);
	       
	        if (key!=null && !"".equals(key.trim()) && value!=null && !"".equals(value.trim())) {
	            query.append(key).append(value);
	        }
	    }
	    // 第三步：使用MD5
        query.append(secret);
        LOG.debug("签名之前的原文：" + query.toString());
        String encryptType = params.get("encryptType");
        if (RequestConstant.DOMESTIC_ENCRYPT_TYPE.equals(encryptType)) {
            return SM3.cipher(query.toString());
        }else{
            //System.out.println("str:"+query.toString());
            byte[]  bytes = encryptMD5(query.toString());
            // 第四步：把二进制转化为大写的十六进制
            return byte2hex(bytes);
        }
	}
	 
	public static byte[] encryptMD5(String data) throws IOException {	  
	 // 创建具有指定算法名称的信息摘要  
	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			 // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算  
		    byte[] results = md.digest(data.getBytes(StandardCharsets.UTF_8));
		    // 将得到的字节数组变成字符串返回  	    
		    return results;  
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return null;
	   
	}
	 
	public static String byte2hex(byte[] bytes) {
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

    /**
     * 签名验证
     * @param params
     * @param secret
     * @return
     * @throws IOException
     */
    public static String signZfbaRequest(Map<String, String> params, String secret) throws IOException {
        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append(secret);
        for (String key : keys) {
            String value = params.get(key);
            if (key!=null && !"".equals(key.trim()) && value!=null && !"".equals(value.trim())) {
                query.append(key).append(value);
            }
        }
        // 第三步：使用MD5
        query.append(secret);
        byte[] bytes = encryptMD5(query.toString());
        // 第四步：把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

}
