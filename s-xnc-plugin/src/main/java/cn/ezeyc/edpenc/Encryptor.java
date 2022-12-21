package cn.ezeyc.edpenc;

import cn.ezeyc.edpenc.util.*;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.File;
import java.util.*;

/**
 * @author wz
 */
public class Encryptor {
    /**
     * 要加密的jar
     */
    private final String jarPath ;

    private char[] password=null;
    /**
     * 要加密的包，多个用逗号隔开
     */
    private List<String> packages = null;
    /**
     * 涉及到的依赖jar
     */
    private  List<String> jars = null;
    /**
     * 机器码
     */
    private char[] code = null;
    /**
     * 根目录
     */
    private final String baseDir;
    /**
     * 工作目录
     */
    private File targetDir = null;
    /**
     * 存储解析出来的类名和路径
     */
    private final Map<String, String> resolveClassName = new HashMap<>();

    /**
     * 构造方法
     *
     * @param jarPath  要加密的jar或war
     */
    public Encryptor(String baseDir, String jarPath) {
        super();
        this.baseDir=baseDir;
        this.jarPath = jarPath;
    }

    /**
     * 加密jar的主要过程
     */
    public void doEncryptJar() {
        if (!jarPath.endsWith(Const.JAR)) {
            throw new RuntimeException("jar文件格式有误");
        }
        if (!new File(jarPath).exists()) {
            throw new RuntimeException("文件不存在:" + jarPath);
        }
        System.out.println("机器绑定："+(code!=null&&code.length>0?"是":"否"));
        //临时work目录
        this.targetDir = new File(jarPath.replace(Const.JAR , Const.LIB_JAR_DIR));
        //调试目录
//        this.debugDir=new File(baseDir+File.separator+"classes"+File.separator+Const.FILE_NAME);
//        if(!this.debugDir.exists()){this.debugDir.mkdirs();}
        //[1]释放目标jar所有文件
        List<String> allFile = JarUtils.unJar(jarPath, this.targetDir.getAbsolutePath());
        //[1.1]释放lib依赖jar
        List<String> libJarFiles = new ArrayList<>();
        jars.forEach(path -> {
            if (path.toLowerCase().endsWith(Const.JAR)) {
                //依赖jar文件位置
                List<String> files = JarUtils.unJar(path, this.targetDir.getAbsolutePath()+"/lib");
                libJarFiles.addAll(files);
            }
        });
        allFile.addAll(libJarFiles);
        //[2]提取所有需要加密的class文件
        List<File> classFiles = filterClasses(allFile);
        //[3]将本项目的代码添加至jar中
        addClassFinalAgent();
        //[4]将正常的class加密，压缩另存
        encryptClass(classFiles);
        //[5]清空class方法体，并保存文件
        clearClassMethod(classFiles);
        //[6]打包回去
        packageJar();
    }


    /**
     * 找出所有需要加密的class文件
     * @param allFile 所有文件
     * @return 待加密的class列表
     */
    public List<File> filterClasses(List<String> allFile) {
        List<File> classFiles = new ArrayList<>();
        allFile.forEach(file -> {
            if (!file.endsWith(Const.CLASSES)) {
                return;
            }
            //解析出类全名
            String className = resolveClassName(file, true);
            //找到对应加密package的class
            if (StrUtils.findClass(this.packages, className) ) {
                classFiles.add(new File(file));
            }
        });
        return classFiles;
    }

    /**
     * 加密class文件，放在META-INF/classes里
     * 以及target下 [idea启动使用]
     * @param classFiles jar下需要加密的class文件
     */
    private void encryptClass(List<File> classFiles) {
        List<String> encryptClasses = new ArrayList<>();
        //加密后存储的位置
        File metaDir = new File(this.targetDir, "META-INF" + File.separator + Const.FILE_NAME);
        if (!metaDir.exists()) {
            metaDir.mkdirs();
        }
        //无密码模式,自动生成一个密码
        char[] randChars = EncryptUtils.randChar(32);
        this.password = EncryptUtils.md5(randChars);
        File configPass = new File(metaDir, Const.CONFIG_PASS);
        IoUtils.writeFile(configPass, StrUtils.toBytes(randChars));
        //有机器码
        if (StrUtils.isNotEmpty(this.code)) {
            File configCode = new File(metaDir, Const.CONFIG_CODE);
            IoUtils.writeFile(configCode, StrUtils.toBytes(EncryptUtils.md5(this.code)));
        }
        //加密另存
        classFiles.forEach(classFile -> {
            String className = classFile.getName();
            if (className.endsWith(Const.CLASSES)) {
                className = resolveClassName(classFile.getAbsolutePath(), true);
            }
            byte[] bytes = IoUtils.readFileToByte(classFile);
            char[] pass = StrUtils.merger(this.password, className.toCharArray());
            bytes = EncryptUtils.en(bytes, pass, Const.ENCRYPT_TYPE);
            //有机器码，再用机器码加密一遍
            if (StrUtils.isNotEmpty(this.code)) {
                pass = StrUtils.merger(className.toCharArray(), this.code);
                bytes = EncryptUtils.en(bytes, pass, Const.ENCRYPT_TYPE);
            }
            File targetFile = new File(metaDir, className);
            IoUtils.writeFile(targetFile, bytes);
            encryptClasses.add(className);
        });
        char[] pchar = EncryptUtils.md5(StrUtils.merger(this.password, EncryptUtils.SALT));
        pchar = EncryptUtils.md5(StrUtils.merger(EncryptUtils.SALT, pchar));
        IoUtils.writeFile(new File(metaDir, Const.CONFIG_PASS_HASH), StrUtils.toBytes(pchar));
    }

    /**
     * 清空class文件的方法体，并保留参数信息
     *
     * @param classFiles jar/war 下需要加密的class文件
     */
    private void clearClassMethod(List<File> classFiles) {
        //初始化javassist
        ClassPool pool = ClassPool.getDefault();
        //[1]把所有涉及到的类加入到ClassPool的classpath[包括依赖的jar]
        ClassUtils.loadClassPath(pool, new File(baseDir + File.separator+"lib"));
        List<String> classPaths = new ArrayList<>();
        classFiles.forEach(classFile -> {
            String classPath = resolveClassName(classFile.getAbsolutePath(), false);
            if (classPaths.contains(classPath)) {
                return;
            }
            try {
                pool.insertClassPath(classPath);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
            }
            classPaths.add(classPath);
        });
        //[2]修改class方法体，并保存文件
        classFiles.forEach(classFile -> {
            //解析出类全名
            String className = resolveClassName(classFile.getAbsolutePath(), true);
            byte[] bts = null;
            try {
                bts = ClassUtils.rewriteAllMethods(pool, className);
                System.out.println("clean method: " + className);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (bts != null) {
                IoUtils.writeFile(classFile, bts);
            }
        });
    }
    /**
     * 向jar文件中添加classfinal的代码
     */
    public void addClassFinalAgent() {
        List<String> thisJarPaths = new ArrayList<>();
        thisJarPaths.add(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        //把本项目的class文件打包进去
        thisJarPaths.forEach(thisJar -> {
            File thisJarFile = new File(thisJar);
            if ( thisJar.endsWith(Const.JAR)) {
                List<String> includeFiles = Arrays.asList(Const.CLASS_FINAL_FILES);
                JarUtils.unJar(thisJar, this.targetDir.getAbsolutePath(), includeFiles);
            }
            //本项目开发环境中未打包
            else if (thisJar.endsWith(Const.CLASS_G)) {
                List<File> files = new ArrayList<>();
                IoUtils.listFile(files, new File(thisJar));
                files.forEach(file -> {
                    String className = file.getAbsolutePath().substring(thisJarFile.getAbsolutePath().length());
                    File targetFile =  this.targetDir ;
                    targetFile = new File(targetFile, className);
                    if (file.isDirectory()) {
                        targetFile.mkdirs();
                    } else if (StrUtils.containsArray(file.getAbsolutePath(), Const.CLASS_FINAL_FILES)) {
                        byte[] bytes = IoUtils.readFileToByte(file);
                        IoUtils.writeFile(targetFile, bytes);
                    }
                });
            }
        });
        //把javaagent信息加入到MANIFEST.MF
        File manifest = new File(this.targetDir, "META-INF/MANIFEST.MF");
        String preMain = "Premain-Class: " + CoreAgent.class.getName();
        String[] txts = {};
        if (manifest.exists()) {
            txts = IoUtils.readTxtFile(manifest).split("\r\n");
        }
        if(txts!=null&&txts.length>0&&!Arrays.asList(txts).contains(preMain)){
            String str = StrUtils.insertStringArray(txts, preMain, "Main-Class:");
            IoUtils.writeTxtFile(manifest, str + "\r\n\r\n");
        }
    }
    /**
     * 压缩成jar
     */
    private void packageJar() {
        //[2]再打包jar
        String targetJar = jarPath.replace(".JAR" , "-encrypted.jar");
        JarUtils.doJar(this.targetDir.getAbsolutePath(), targetJar);
        IoUtils.delete(this.targetDir);
        System.out.println("package: " + targetJar);
    }

    /**
     * 根据class的绝对路径解析出class名称或class包所在的路径
     *
     * @param fileName    class绝对路径
     * @param classOrPath true|false
     * @return class名称|包所在的路径
     */
    private String resolveClassName(String fileName, boolean classOrPath) {
        String result = resolveClassName.get(fileName + classOrPath);
        if (result != null) {
            return result;
        }
        String file = fileName.substring(0, fileName.length() - 6);
        String kClasses = File.separator + "classes" + File.separator;
        String kLib = File.separator + "lib" + File.separator;

        String clsPath;
        String clsName;
        //lib内的的jar包
        if (file.contains(kLib)) {
            clsName = file.substring(file.indexOf(Const.LIB_JAR_DIR, file.indexOf(kLib))
                    + Const.LIB_JAR_DIR.length() + 1);
            clsPath = file.substring(0, file.length() - clsName.length() - 1);
        }
        //jar/war包-INF/classes下的class文件
        else if (file.contains(kClasses)) {
            clsName = file.substring(file.indexOf(kClasses) + kClasses.length());
            clsPath = file.substring(0, file.length() - clsName.length() - 1);

        }
        //jar包下的class文件
        else {
            clsName = file.substring(file.indexOf(Const.LIB_JAR_DIR) + Const.LIB_JAR_DIR.length() + 1);
            clsPath = file.substring(0, file.length() - clsName.length() - 1);
        }
        result = classOrPath ? clsName.replace(File.separator, ".") : clsPath;
        resolveClassName.put(fileName + classOrPath, result);
        return result;
    }
    public void setPackages(List<String> packages) {
        this.packages = packages;
    }


    public void setJars(List<String> jars) {
        this.jars = jars;
    }

    public void setCode(char[] code) {
        this.code = code;
    }
}
