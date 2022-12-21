package cn.ezeyc.edpbase.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Config：
 *
 * @author Administrator
 * @date 2020年7月31日, 0031 14:42:28
 */
@ConfigurationProperties(prefix = "edp.config")
public class Config extends ConfigPojo {
    private  String scan;
    /**
     * 是否开启缓存
     */
    private Boolean cache;
    /**
     * 是否开启虚拟删除
     */
    private Boolean remove;
    /**
     * 是的开启数据权限验证
     */
    private Boolean dataAuth;
    /**
     * 是否开启权限验证
     */
    private Boolean security;
    /**
     * 是否记录操作日志到数据库
     */
    private Boolean logRecord;
    /**
     * 上传路径
     */
    private String uploadPath;
    /**
     * win上传路径
     */
    private String uploadWinPath;
    /**
     * 证书路径
     */
    private  String licensePath;

    /**
     * 免token验证地址
     */
    private List<String> ignore;

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getUploadWinPath() {
        return uploadWinPath;
    }

    public void setUploadWinPath(String uploadWinPath) {
        this.uploadWinPath = uploadWinPath;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public Boolean getRemove() {
        return remove;
    }

    public void setRemove(Boolean remove) {
        this.remove = remove;
    }

    public Boolean getDataAuth() {
        return dataAuth;
    }

    public void setDataAuth(Boolean dataAuth) {
        this.dataAuth = dataAuth;
    }
    public Boolean getSecurity() {
        return security;
    }

    public void setSecurity(Boolean security) {
        this.security = security;
    }


    public List<String> getIgnore() {
        return ignore;
    }

    public void setIgnore(List<String> ignore) {
        this.ignore = ignore;
    }


    public Boolean getLogRecord() {
        return logRecord;
    }

    public void setLogRecord(Boolean logRecord) {
        this.logRecord = logRecord;
    }

    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }
}
