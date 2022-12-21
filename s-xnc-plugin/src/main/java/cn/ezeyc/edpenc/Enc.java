package cn.ezeyc.edpenc;


import cn.ezeyc.edpenc.util.Const;
import cn.ezeyc.edpenc.util.StrUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wz
 */
@Mojo(name = "enc")
public class Enc extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    /**
     * 要加密的包
     */
    @Parameter
    private String packages;
    /**
     * 依赖jar
     */
    @Parameter
    private String jars;
    /**
     * 机器码
     */
    @Parameter
    private String code;

    @Override
    public void execute()  {
        Build build = project.getBuild();
        //依赖jar添加
        List<String> newJars=new ArrayList<>();
        //lib中的jar【打包依赖打入lib】
        if(new File(build.getDirectory() + File.separator + Const.LIB).exists()){
            String[] list = new File(build.getDirectory() + File.separator + Const.LIB).list();
            if(jars!=null&&jars.split(Const.DOU).length>0&&list!=null){
                for(String s:list){
                    for(String j:jars.split(Const.DOU)){
                        if(s.startsWith(j)){
                            newJars.add(build.getDirectory() + File.separator + Const.LIB+File.separator+s);
                        }
                    }
                }
            }else {
                if(list!=null){
                    for(String s:list){
                        newJars.add(build.getDirectory() + File.separator + Const.LIB+File.separator+s);
                    }
                }

            }
        }
        //目标jar
        String targetJar = build.getDirectory() + File.separator + build.getFinalName()  + Const.DAO + project.getPackaging();
        System.out.println(targetJar);
        //加密包
        List<String> packageList =new ArrayList<>();
        if(packages!=null){
            packageList = Arrays.stream(packages.split(Const.DOU)).toList();
        }
        //加密
        Encryptor encryptor = new Encryptor(build.getDirectory(),targetJar);
        encryptor.setCode(StrUtils.isEmpty(code) ? null : code.trim().toCharArray());
        encryptor.setPackages(packageList);
        encryptor.setJars(newJars);
        encryptor.doEncryptJar();
    }
}
