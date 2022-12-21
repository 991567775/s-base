package cn.ezeyc.edpaliplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;

/**
 * @author wz
 */
public class AliMp extends ConfigPojo {

    /**
     * 小程序appid
     */
    private String appId;
    /**
     * 小程序网关
     */
    private String gateWay;
    /**
     * 小程序公钥
     */
    private String publicKey;
    /**
     * 小程序私钥
     */
    private String PrivateKey;
    /**
     * 小程序⽀付宝UID\企业ID
     */
    private String companyId;
    /**
     * 付款回调地址
     */
    private  String payNotifyUrl;
    /**
     * 退款回调地址
     */
    private  String refundNotifyUrl;
    /**
     * 预授权冻结回调地址
     */
    private  String freezeNotify;
    /**
     * 预授权解冻回调地址
     */
    private  String unFreezeNotify;
    /**
     * 预授权转支付回调地址
     */
    private  String FreezePayNotify;
    /**
     * 小程序接口内容加密方式
     */
    private String aes;

    public  String format = "json";
    public  String charSet = "UTF-8";
    public   String singType = "RSA2";


    //证书方式
    private  String AppCertPath;

    private  String PublicCertPath;

    private  String RootCertPath;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return PrivateKey;
    }

    public void setPrivateKey(String privateKey) {
        PrivateKey = privateKey;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getAes() {
        return aes;
    }

    public void setAes(String aes) {
        this.aes = aes;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getSingType() {
        return singType;
    }

    public void setSingType(String singType) {
        this.singType = singType;
    }

    public String getAppCertPath() {
        return AppCertPath;
    }

    public void setAppCertPath(String appCertPath) {
        AppCertPath = appCertPath;
    }

    public String getPublicCertPath() {
        return PublicCertPath;
    }

    public void setPublicCertPath(String publicCertPath) {
        PublicCertPath = publicCertPath;
    }

    public String getRootCertPath() {
        return RootCertPath;
    }

    public void setRootCertPath(String rootCertPath) {
        RootCertPath = rootCertPath;
    }

    public String getPayNotifyUrl() {
        return payNotifyUrl;
    }

    public void setPayNotifyUrl(String payNotifyUrl) {
        this.payNotifyUrl = payNotifyUrl;
    }

    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl;
    }

    public String getUnFreezeNotify() {
        return unFreezeNotify;
    }

    public void setUnFreezeNotify(String unFreezeNotify) {
        this.unFreezeNotify = unFreezeNotify;
    }

    public String getFreezePayNotify() {
        return FreezePayNotify;
    }

    public void setFreezePayNotify(String freezePayNotify) {
        FreezePayNotify = freezePayNotify;
    }

    public String getFreezeNotify() {
        return freezeNotify;
    }

    public void setFreezeNotify(String freezeNotify) {
        this.freezeNotify = freezeNotify;
    }
}
