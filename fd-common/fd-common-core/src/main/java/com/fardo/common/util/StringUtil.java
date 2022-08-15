package com.fardo.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public StringUtil() {
    }

    public static String toLowerCaseFirstOne(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String toUpperCaseFirstOne(String s) {
        return Character.isUpperCase(s.charAt(0)) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isNotEmptyAndNoUndefind(CharSequence cs) {
        return isNotEmpty(cs) && !cs.equals("undefined");
    }

    public static String getMd5(String string) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(string.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(40);
            byte[] var4 = bs;
            int var5 = bs.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte x = var4[var6];
                if ((x & 255) >> 4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 255));
                } else {
                    sb.append(Integer.toHexString(x & 255));
                }
            }

            return sb.toString();
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return null;
    }

    public static List<String> strToStrs(String str, int num){
        List<String> strs = null;
        boolean flag = false;
        if(str != null && str.length() > 0){
            strs = new ArrayList<>();
            while(!flag){
                if(str.length() > num){
                    strs.add(str.substring(0, num));
                    str = str.substring(num);
                }else{
                    flag = true;
                    strs.add(str);
                }
            }
        }
        return strs;
    }

    public static String randomNumber(int length) {
        String retu = "";
        for (int i = 0; i < length; i++) {
            retu += (int) (Math.random() * 10);
        }
        return retu;
    }


    /**
     * 产生指定长度随机字符串（包括字母及数字）.
     *
     * @param length
     *            int 随机字符串长度
     * @return String 随机字符串
     */
    public static String random(int length) {
        String retu = "";
        int d1, d2;
        char[] letters = initLetters();
        for (int i = 0; i < length; i++) {
            d1 = ((int) (Math.random() * 10) % 2);
            if (d1 == 0) { // use a letter
                d2 = ((int) (Math.random() * 100) % 52);
                retu += letters[d2];
            } else if (d1 == 1) { // use a number
                retu += (int) (Math.random() * 10);
            }
        }
        return retu;
    }

    private static char[] initLetters() {
        char[] ca = new char[52];
        for (int i = 0; i < 26; i++) {
            ca[i] = (char) (65 + i);
        }
        for (int i = 26; i < 52; i++) {
            ca[i] = (char) (71 + i);
        }
        return ca;
    }

}
