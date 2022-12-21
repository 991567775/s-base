package cn.ezeyc.edpenc.util;

/**
 * @author wz
 */
public class Const {

    /**
     * 版本
     */
    public static final String VERSION = "v1.0.0";

    /**
     * 加密出来的文件名
     */
    public static final String FILE_NAME = ".classes";

    /**
     * lib下的jar解压的目录名后缀
     */
    public static final String LIB_JAR_DIR = "__temp__";

    /**
     * 默认加密方式
     */
    public static final int ENCRYPT_TYPE = 1;

    /**
     * 密码标记
     */
    public static final String CONFIG_PASS = "pass";
    /**
     * 机器码标记
     */
    public static final String CONFIG_CODE = "code";
    /**
     * 加密密码的hash
     */
    public static final String CONFIG_PASS_HASH = "passHash";

    public static final String WIN="Windows";

    public static final int SIZE_1=47;
    public static final int SIZE_0=46;
    public static final String LIB="lib";
    public static final String DOU=",";
    public static final String PACKAGES="cn.ezeyc.edpbase";
    public static final String CLASSES=".class";
    public static final String  JAR=".jar";

    public static final String CLASS_G="/classes/";
    /**
     * 本项目需要打包的代码
     */
    public static final String[] CLASS_FINAL_FILES = {"CoreAgent.class",
            "JarDecryptor.class", "AgentTransformer.class", "Const.class",
            "EncryptUtils.class", "IoUtils.class", "JarUtils.class", "StrUtils.class",
            "SysUtils.class"};
    public static final String MAC = "Mac";
    public static final String LINUX="Linux";
    public static final String DAO = ".";


    public static void info() {
        String sysName = System.getProperty("os.name");
        if (sysName.contains(WIN)) {
            System.out.println();
            System.out.println("===============================================");
            System.out.println("=                                             =");
            System.out.println("=  e  -core with java class enc " + VERSION + " by wz  =");
            System.out.println("=                                             =");
            System.out.println("===============================================");
            System.out.println();
            return;
        }
        String[] color = {"\033[31m", "\033[32m", "\033[33m", "\033[34m", "\033[35m", "\033[36m",
                "\033[90m", "\033[92m", "\033[93m", "\033[94m", "\033[95m", "\033[96m"};
        System.out.println();
        for (int i = 0; i < SIZE_1; i++) {
            System.out.print(color[i % color.length] + "=\033[0m");
        }
        System.out.println();
        System.out.println("\033[34m=                                             \033[92m=");
        System.out.println("\033[35m=  \033[96me  -core \033[94mwith \033[31mjava \033[92mclass \033[95mnec \033[37m"
                + VERSION + "\033[0m by \033[91mwz \033[0m \033[93m=");
        System.out.println("\033[36m=                                             \033[94m=");
        for (int i = SIZE_0; i >= 0; i--) {
            System.out.print(color[i % color.length] + "=\033[0m");
        }
        System.out.println();
        System.out.println();
    }
}
