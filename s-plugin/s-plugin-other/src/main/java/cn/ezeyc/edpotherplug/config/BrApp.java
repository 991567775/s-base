package cn.ezeyc.edpotherplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;

/**
 * 百融api
 * @author wz
 */
public class BrApp extends ConfigPojo {

    /**
     * 百融请求域名
     */
    private  String host;
    /**
     * 贷前策略API
     */
    private String strategyUrl;
    /**
     * 信息验证API
     */
    private String verifyUrl;

    /**
     * 加密方式
     */
    private String encryType="md5";
    /**
     * 应用名称
     */
    private String apiName;
    /**
     * 策略编号
     */
    private String strategyId;
    /**
     * ApiCode账号
     */
    private String apiCode;
    /**
     * ApiCode密钥
     */
    private String appKey;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStrategyUrl() {
        return strategyUrl;
    }

    public void setStrategyUrl(String strategyUrl) {
        this.strategyUrl = strategyUrl;
    }

    public String getVerifyUrl() {
        return verifyUrl;
    }

    public void setVerifyUrl(String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }

    public String getEncryType() {
        return encryType;
    }

    public void setEncryType(String encryType) {
        this.encryType = encryType;
    }
}
