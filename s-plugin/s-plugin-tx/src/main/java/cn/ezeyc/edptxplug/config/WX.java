package cn.ezeyc.edptxplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信相关配置
 */
@Configuration
@ConfigurationProperties(prefix = "edp.wx")
public class WX     extends ConfigPojo {
    /**
     * 小程序相关配置
     */
    public List<wxMp> mp =new ArrayList<>();
    /**
     * 公众号服务号相关配置
     */
    public List<wxPub> pub =new ArrayList<>();

}
