package com.fardo.common.util;

import com.faduit.security.cipher.SM3;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希加密算法类
 *
 * @Author Rechie
 * @Date 2020/9/9 0009 14:48
 */
public class HashAlgorithm {

    /**
     * 对字符串做hash加密，当未配置hash算法时，默认采用md5
     *
     * @param plainText: 需要做hash的字符串
     * @return java.lang.String
     * @Author Rechie
     * @Date 2020/8/27 0027 11:32
     */
    public static String stringToHash(String algorithm, String plainText) {
        switch (algorithm.toUpperCase()) {
            case "SM3":
                return stringToSM3(plainText).toUpperCase();
            case "SHA256":
                return stringToSHA256(plainText).toUpperCase();
            default:
                return stringToMD5(plainText).toUpperCase();
        }
    }

    /**
     * 国密SM3算法
     *
     * @param plainText:
     * @return java.lang.String
     * @Author Rechie
     * @Date 2020/8/27 0027 13:34
     */
    private static String stringToSM3(String plainText) {
        String str=SM3.cipher(plainText);
        return str;
    }

    /**
     * MD5算法对字符串加密
     *
     * @param plainText: 需要做MD5的字符串
     * @return java.lang.String 32字符串
     * @Author Rechie
     * @Date 2020/8/27 0027 10:31
     */
    private static String stringToMD5(String plainText) {
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * SHA-256算法
     *
     * @param plainText:
     * @return java.lang.String
     * @Author Rechie
     * @Date 2020/8/27 0027 13:34
     */
    private static String stringToSHA256(String plainText) {
        MessageDigest messageDigest;
        String encodeStr;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(plainText.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个SHA-256算法！");
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes:
     * @return java.lang.String
     * @Author Rechie
     * @Date 2020/8/27 0027 17:44
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
