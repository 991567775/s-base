package cn.ezeyc.edpcommon.util;


import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author moyuan
 * @since 2019/12/28
 */
public class MD5Util {
    private static String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String md5(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] b = data.getBytes("UTF8");
        md5.update(b, 0, b.length);
        return byteArrayToHexString(md5.digest());
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < b.length; ++i) {
            sb.append(byteToHexString(b[i]));
        }

        return sb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (b < 0) {
            n = 256 + b;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String genMd5(String msg) {
        String messageDigest = null;

        try {
            messageDigest = md5(msg);
            return messageDigest;
        } catch (Exception var3) {
            throw new RuntimeException("Md5 Error. Cause: ", var3);
        }
    }
    /**
     * 获取一个文件的md5值，转base64(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8092];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            // 取摘要的base64
            return Base64.encodeBase64String(MD5.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            File pic = new File("/Users/wuxb/Downloads/模板文件测试.pdf");
            System.out.println(getMD5(pic));
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }
}
