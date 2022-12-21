package cn.ezeyc.edptxplug.wx;

import com.alibaba.fastjson.JSON;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpcommon.util.Http;
import cn.ezeyc.edptxplug.config.wxMp;
import cn.ezeyc.edptxplug.config.wxPub;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 微信相关内容登录
 */
public class WxLogin {

    /**
     * 获取小程序 用户openid
     * @param wxMp
     * @return
     */
    public  static Map<String,String> loginMp(wxMp wxMp){
        Map<String,String> map = null;
        if(wxMp !=null&& StringUtils.isNotEmpty(wxMp.getAppId()) && StringUtils.isNotEmpty(wxMp.getAppSecret())){
            String result = Http.sendGet("https://api.weixin.qq.com/sns/jscode2session",
                    "appid=" + wxMp.getAppId() + "&secret=" + wxMp.getAppSecret() + "&js_code=" + wxMp.getCode() + "&grant_type=authorization_code");
            map = JSON.parseObject(result, Map.class);
            if (map.get("openid") == null) {
                throw new ExRuntimeException(map.get("errMsg"));
            }
        }else {
            throw new ExRuntimeException("获取微信小程序配置信息失败");
        }

        return  map;
    }
    /**
     * 获取微信公众号用户openid
     * @param wxPub
     * @return
     */
    public  static Map<String,String> loginPub(wxPub wxPub){
        Map<String,String> map = null;
        if(wxPub !=null&& StringUtils.isNotEmpty(wxPub.getAppId()) && StringUtils.isNotEmpty(wxPub.getAppSecret())){
            String result = Http.sendGet("https://api.weixin.qq.com/sns/jscode2session",
                    "appid=" + wxPub.getAppId() + "&secret=" + wxPub.getAppSecret() + "&js_code=" + wxPub.getCode() + "&grant_type=authorization_code");
            map = JSON.parseObject(result, Map.class);
            if (map.get("openid") == null) {
                throw new ExRuntimeException(map.get("errMsg"));
            }
        }else {
            throw new ExRuntimeException("获取微信公众号/服务号配置信息失败");
        }

        return  map;
    }
}
