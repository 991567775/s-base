package cn.ezeyc.edpbase.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RdpConfig：
 *
 * @author Administrator
 * @date 2020年7月31日, 0031 14:42:28
 */
@Component
@ConfigurationProperties(prefix = "edp.db")
public class Db extends ConfigPojo {
    private String className;
    private String url;
    private String user;
    private String password;
    /**
     * 打印sql
     */
    private Boolean showSql;
    /**
     * 显示连接池监控
     */
    private Boolean showMonitor;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getShowSql() {
        return showSql;
    }

    public void setShowSql(Boolean showSql) {
        this.showSql = showSql;
    }

    public Boolean getShowMonitor() {
        return showMonitor;
    }

    public void setShowMonitor(Boolean showMonitor) {
        this.showMonitor = showMonitor;
    }
}
