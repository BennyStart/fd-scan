package com.fardo.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：Limq
 * 时　间：2017/12/7
 * 描　述：创建
 */
public class GZIPUtils {
    /**
     *
     * @param str
     * @return
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return "";
        }

        byte[] tArray;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        try {
            gzip.write(str.getBytes("UTF-8"));
            gzip.flush();
        } finally {
            gzip.close();
        }

        tArray = out.toByteArray();
        out.close();

        BASE64Encoder tBase64Encoder = new BASE64Encoder();
        return tBase64Encoder.encode(tArray);
    }

    /**
     *
     * @param str
     * @return
     * @throws IOException
     */
    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return "";
        }

        BASE64Decoder tBase64Decoder = new BASE64Decoder();
        byte[] t = tBase64Decoder.decodeBuffer(str);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(t);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        try {
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        }finally{
            gunzip.close();
        }
        in.close();
        out.close();

        return out.toString("UTF-8");
    }

}
