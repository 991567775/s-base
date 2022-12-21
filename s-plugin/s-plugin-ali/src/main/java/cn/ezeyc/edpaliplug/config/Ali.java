package cn.ezeyc.edpaliplug.config;

import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信相关配置
 */
@Configuration
@ConfigurationProperties(prefix = "edp.ali")
public class Ali extends ConfigPojo {
    /**
     * 小程序相关配置
     */
    public List<AliMp> mp =new ArrayList<>();

}
