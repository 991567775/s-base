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
@ConfigurationProperties(prefix = "edp.redis")
public class Redis  extends ConfigPojo {
    private String ip;
    private int port;
    private String pwd;
    private int db;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }
}
