package cn.ezeyc.edpbase.doc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocConfig {
    /**
     * 模块路径
     */
    private  String modelPath;
    /**
     * torna环境名称
     */
    private  String debugEnvName;
    /**
     * 测试环境地址
     */
    private  String debugEnvUrl;
    /**
     * torna平台地址
     */
    private  String openUrl;
    /**
     * torna平台appToken
     */
    private  String appToken;
    /**
     * 扫描包路径
     */
    private List<String> packages;

    /**
     * 数据库链接
     */
    private String sqlUrl;
    /**
     * 用户名
     */
    private String user;
    /**
     * 密码
     */
    private String pwd;
}
