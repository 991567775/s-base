package cn.ezeyc.edptxplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;


/**
 * 微信公众号/服务号 开发配置
 * @author wz
 */
public class wxPub extends ConfigPojo {

    /**
     * 微信公众号/服务号 appId
     */
    private String appId;
    /**
     * 微信公众号/服务号 秘钥
     */
    private String appSecret;

    private String code;

    private String grantType;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
