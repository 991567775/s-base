package cn.ezeyc.edpenc;

import cn.ezeyc.edpenc.util.Const;
import cn.ezeyc.edpenc.util.EncryptUtils;
import cn.ezeyc.edpenc.util.JarUtils;
import cn.ezeyc.edpenc.util.StrUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;

/**
 * 监听类加载
 *
 * @author roseboy
 */
public class CoreAgent {
    /**
     * man方法执行前调用
     *
     * @param args 参数
     * @param inst inst
     */
    public static void premain(String args, Instrumentation inst) {
        //打印
        Const.info();
        //验证密钥

        String rootPath = JarUtils.getRootPath(null);
        if(rootPath!=null){
            //jar中获取密码
            char[] pwd= JarDecryptor.readPassFromJar(new File(rootPath));
            //还是没有获取密码，退出
            if (StrUtils.isEmpty(pwd)) {
                System.exit(0);
            }
            //验证密码,jar包是才验证
            byte[] passHash = JarDecryptor.readEncryptedFile(new File(rootPath), Const.CONFIG_PASS_HASH);
            if (passHash != null) {
                char[] p1 = StrUtils.toChars(passHash);
                char[] p2 = EncryptUtils.md5(StrUtils.merger(pwd, EncryptUtils.SALT));
                p2 = EncryptUtils.md5(StrUtils.merger(EncryptUtils.SALT, p2));
                if (!StrUtils.equal(p1, p2)) {
                    System.exit(0);
                }
            }
            //GO
            if (inst != null) {
                AgentTransformer tran = new AgentTransformer(pwd);
                inst.addTransformer(tran);

            }
        }else {
            System.exit(0);
        }

    }
}