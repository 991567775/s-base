package cn.ezeyc.edpenc.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wz
 */
public class EncryptUtils {
    /**
     * 盐
     */
    public static final char[] SALT = {'w', 'h', 'o', 'i', 's', 'y', 'o', 'u', 'r', 'd', 'a', 'd', 'd', 'y', '#', '$', '@', '#', '@'};


    /**
     * 加密
     *
     * @param msg  内容
     * @param key  密钥
     * @param type 类型
     * @return 密文
     */
    public static byte[] en(byte[] msg, char[] key, int type) {
        if (type == 1) {
            return enAes(msg, md5(StrUtils.merger(key, SALT), true));
        }
        return enSimple(msg, key);
    }

    /**
     * 解密
     *
     * @param msg  密文
     * @param key  密钥
     * @param type 类型
     * @return 明文
     */
    public static byte[] de(byte[] msg, char[] key, int type) {
        if (type == 1) {
            return deAes(msg, md5(StrUtils.merger(key, SALT), true));
        }
        return deSimple(msg, key);
    }

    /**
     * md5加密
     *
     * @param str 字符串
     * @return md5字串
     */
    public static byte[] md5byte(char[] str) {
        byte[] b = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = StrUtils.toBytes(str);
            md.update(buffer);
            b = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * md5
     *
     * @param str 字串
     * @return 32位md5
     */
    public static char[] md5(char[] str) {
        return md5(str, false);
    }

    /**
     * md5
     *
     * @param str   字串
     * @param sh0rt 是否16位
     * @return 32位/16位md5
     */
    public static char[] md5(char[] str, boolean sh0rt) {
        byte[] s = md5byte(str);
        if (s == null) {
            return null;
        }
        int begin = 0;
        int end = s.length;
        if (sh0rt) {
            begin = 8;
            end = 16;
        }
        char[] result = new char[0];
        for (int i = begin; i < end; i++) {
            result = StrUtils.merger(result, Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6).toCharArray());
        }
        return result;
    }


    /**
     * 加密
     *
     * @param msg   加密报文
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return 加密后的字节
     */
    public static byte[] enSimple(byte[] msg, int start, int end, char[] key) {
        byte[] keys = IoUtils.merger(md5byte(StrUtils.merger(key, SALT)), md5byte(StrUtils.merger(SALT, key)));
        for (int i = start; i <= end; i++) {
            msg[i] = (byte) (msg[i] ^ keys[i % keys.length]);
        }
        return msg;
    }

    /**
     * 解密
     *
     * @param msg   加密报文
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return 解密后的字节
     */
    public static byte[] deSimple(byte[] msg, int start, int end, char[] key) {
        byte[] keys = IoUtils.merger(md5byte(StrUtils.merger(key, SALT)), md5byte(StrUtils.merger(SALT, key)));
        for (int i = start; i <= end; i++) {
            msg[i] = (byte) (msg[i] ^ keys[i % keys.length]);
        }
        return msg;
    }

    /**
     * 加密
     *
     * @param msg 加密报文
     * @param key 密钥
     * @return 加密后的字节
     */
    public static byte[] enSimple(byte[] msg, char[] key) {
        return enSimple(msg, 0, msg.length - 1, key);
    }

    /**
     * 解密
     *
     * @param msg 加密报文
     * @param key 密钥
     * @return 解密后的字节
     */
    public static byte[] deSimple(byte[] msg, char[] key) {
        return deSimple(msg, 0, msg.length - 1, key);
    }













    /**
     * AES加密字节
     *
     * @param msg 字节数组
     * @param key 密钥
     * @return 加密后的字节
     */
    public static byte[] enAes(byte[] msg, char[] key) {
        byte[] encrypted = null;
        try {
            byte[] raw = StrUtils.toBytes(key);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            //"算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }



    /**
     * AES解密
     *
     * @param msg 要解密的字节
     * @param key 密钥
     * @return 明文字节
     */
    public static byte[] deAes(byte[] msg, char[] key) {
        byte[] original = null;
        try {
            byte[] raw = StrUtils.toBytes(key);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            original = cipher.doFinal(msg);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return original;
    }

    /**
     * 随机字串
     *
     * @param lenght 长度
     * @return 字符数组
     */
    public static char[] randChar(int lenght) {
        char[] result = new char[lenght];
        Character[] chars = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '=', '_', '+', '.'};

        List<Character> list = Arrays.asList(chars);
        Collections.shuffle(list);
        for (int i = 0; i < lenght; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
