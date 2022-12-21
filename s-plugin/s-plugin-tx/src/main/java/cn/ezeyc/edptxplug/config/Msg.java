package cn.ezeyc.edptxplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author wz
 */
@Configuration
@ConfigurationProperties(prefix = "edp.tx.msg")
public class Msg extends ConfigPojo {


    private String secretId;

    private String secretKey;


    private  List<MsgApp> sdkApp;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public List<MsgApp> getSdkApp() {
        return sdkApp;
    }

    public void setSdkApp(List<MsgApp> sdkApp) {
        this.sdkApp = sdkApp;
    }


}
