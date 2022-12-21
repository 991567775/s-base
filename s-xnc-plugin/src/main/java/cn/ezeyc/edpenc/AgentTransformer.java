package cn.ezeyc.edpenc;

import cn.ezeyc.edpenc.util.Const;
import cn.ezeyc.edpenc.util.JarUtils;
import cn.ezeyc.edpenc.util.StrUtils;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
/**
 * AgentTransformer
 * jvm加载class时回调
 *
 * @author roseboy
 */
public class AgentTransformer implements ClassFileTransformer {

    /**
     * 密码
     */
    private final char[] pwd;
    /**
     * 构造方法
     *
     * @param pwd 密码
     */
    public AgentTransformer(char[] pwd) {
        this.pwd = pwd;
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain domain, byte[] classBuffer) {
        if (className == null || domain == null || loader == null) {
            return null;
        }
        //获取类所在的项目运行路径
        String projectPath = domain.getCodeSource().getLocation().getPath();
        projectPath = JarUtils.getRootPath(projectPath);
        if (StrUtils.isEmpty(projectPath)) {
            return null;
        }
        className = className.replace("/", ".").replace("\\", ".");
        if(className.startsWith(Const.PACKAGES)){
            byte[] bytes = JarDecryptor.getInstance().doDecrypt(projectPath, className, this.pwd);
            //CAFEBABE,表示解密成功
            int index0 = 0;
            int index1 = 1;
            int index2 = 2;
            int index3 = 3;
            int size0 = -54;
            int size1 = -2;
            int size2 = -70;
            int size3 = -66;
            if (bytes != null && bytes[index0] ==size0 && bytes[index1] == size1 && bytes[index2] == size2 && bytes[index3] == size3) {
                System.out.println(className+" decrypt  success");
                return bytes;
            }
        }
        return null;
    }


}
