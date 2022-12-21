package cn.ezeyc.edptxplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;

/**
 * @author wz
 */
public class wxMp extends ConfigPojo {

    /**
     * 小程序appid
     */
    private String appId;
    /**
     * 小程序秘钥
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
