package cn.ezeyc.edptxplug.pojo;


import com.alibaba.fastjson2.JSONObject;

/**
 * 微信消息推送实体
 */
public class wxSendMsg {
    /**
     * token
     */
    private String accessToken;
    private String touser;
    private String templateId;
    private String page;
    private String lang="zh_CN";
    private String miniprogramState="formal";
    private JSONObject data;//推送文字

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getMiniprogramState() {
        return miniprogramState;
    }

    public void setMiniprogramState(String miniprogramState) {
        this.miniprogramState = miniprogramState;
    }
}
