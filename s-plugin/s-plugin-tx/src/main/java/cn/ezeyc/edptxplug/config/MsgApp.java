package cn.ezeyc.edptxplug.config;


import cn.ezeyc.edpcommon.pojo.ConfigPojo;

/**
 * @author zewang
 */
public  class MsgApp extends ConfigPojo {
    private  int type;
    private  String sdkAppId;

    private  String signName;

    private  String templateId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
