package cn.ezeyc.edpenc;


import cn.ezeyc.edpenc.util.*;

import java.io.File;

/**
 * java class解密
 *
 * @author roseboy
 */
public class JarDecryptor {
    /**
     * 单例
     */
    private static final JarDecryptor SINGLE = new JarDecryptor();
    /**
     * 机器码
     */
    private char[] code;
    /**
     * 加密后文件存放位置
     */
    private static final String ENCRYPT_PATH = "META-INF/" + Const.FILE_NAME + "/";

    /**
     * 单例
     *
     * @return 单例
     */
    public static JarDecryptor getInstance() {
        return SINGLE;
    }

    /**
     * 构造
     */
    public JarDecryptor() {
        this.code = SysUtils.makeMarchCode();
    }

    /**
     * 根据名称解密出一个文件
     *
     * @param projectPath 项目所在的路径
     * @param fileName    文件名
     * @param password    密码
     * @return 解密后的字节
     */
    public byte[] doDecrypt(String projectPath, String fileName, char[] password) {
        File workDir = new File(projectPath);
        byte[] bytes = readEncryptedFile(workDir, fileName);
        if (bytes == null) {
            return null;
        }

        //读取机器码，有机器码，先用机器码解密
        byte[] codeBytes = readEncryptedFile(workDir, Const.CONFIG_CODE);
        if (codeBytes != null) {
            //本机器码和打包的机器码不匹配
            if (!StrUtils.equal(EncryptUtils.md5(this.code), StrUtils.toChars(codeBytes))) {
                System.out.println("该项目不可在此机器上运行!\n");
                System.exit(-1);
            }

            //用机器码解密
            char[] pass = StrUtils.merger(fileName.toCharArray(), code);
            bytes = EncryptUtils.de(bytes, pass, Const.ENCRYPT_TYPE);
        }
        //无密码启动,读取隐藏的密码
        password = readPassFromJar(workDir);
        //密码解密
        char[] pass = StrUtils.merger(password, fileName.toCharArray());
        bytes = EncryptUtils.de(bytes, pass, Const.ENCRYPT_TYPE);
        return bytes;

    }
    /**
     * 在jar文件或目录中读取文件字节
     *
     * @param workDir jar文件或目录
     * @param name    文件名
     * @return 文件字节数组
     */
    public static byte[] readEncryptedFile(File workDir, String name) {
        byte[] bytes = null;
        //jar文件
        if (workDir.isFile()) {
            bytes = JarUtils.getFileFromJar(workDir, ENCRYPT_PATH + name);
        } else {//目录
            File file = new File(workDir, name);
            if (file.exists()) {
                bytes = IoUtils.readFileToByte(file);
            }
        }
        return bytes;
    }
    /**
     * 读取隐藏在jar的密码
     *
     * @param workDir jar路径
     * @return 密码char
     */
    public static char[] readPassFromJar(File workDir) {
        byte[] passbyte = readEncryptedFile(workDir, Const.CONFIG_PASS);
        if (passbyte != null) {
            char[] pass = StrUtils.toChars(passbyte);
            return EncryptUtils.md5(pass);
        }
        return null;
    }
}
