package cn.ezeyc.edpbase.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Log：
 *
 * @author Administrator
 * @date 2020年7月31日, 0031 14:42:28
 */
@Component
@ConfigurationProperties(prefix = "edp.log")
public class Log extends ConfigPojo {
    /**
     * 是否记录操作日志到数据库
     */
    private Boolean enable;
    /**
     * 日志服务名
     * 例如：edp-system
     */
    private String service;
    /**
     * 日志请求地址
     */
    private  String url;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
