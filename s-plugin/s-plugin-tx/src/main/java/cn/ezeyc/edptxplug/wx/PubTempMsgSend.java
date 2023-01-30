package cn.ezeyc.edptxplug.wx;

import cn.ezeyc.edpcommon.util.Http;
import cn.ezeyc.edptxplug.config.wxPub;
import cn.ezeyc.edptxplug.pojo.wxSendMsg;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;


import java.util.Map;

/**
 * 微信服务号消息模板
 * 发送
 */
public class PubTempMsgSend {


    /**
     * 获取token
     * @param wxPub
     * @return
     */
    public  static String getToken(wxPub wxPub){
        //1.获取token
        String result = Http.sendGet(" https://api.weixin.qq.com/cgi-bin/token",
                "appid=" + wxPub.getAppId() + "&secret=" + wxPub.getAppSecret() + "&grant_type=client_credential");
        Map<String,String> map =  JSON.parseObject(result, Map.class);
        //成功
        if(map.get("access_token")!=null){
            return  map.get("access_token");
        }else{
            throw new RuntimeException(map.get("errmsg"));
        }
    }

    /**
     * 获取粉丝openid集合
     * @param wxSendMsg
     * @return
     */
    public static String [] getUser(wxSendMsg wxSendMsg){
        //1.获取token
        String result = Http.sendGet("https://api.weixin.qq.com/cgi-bin/user/get","access_token="+ wxSendMsg.getAccessToken()+"&next_openid=");
        Map map =  JSON.parseObject(result, Map.class);
        if(map.get("data")!=null){
            return ((JSONArray)((JSONObject)map.get("data")).get("openid")).toArray(new String[]{});
        }else{
            throw new RuntimeException(map.get("errmsg").toString());
        }
    }
    public static Map getUserInfo(wxSendMsg wxSendMsg){
        //1.获取token
        String result = Http.sendGet("https://api.weixin.qq.com/cgi-bin/user/info","access_token="+ wxSendMsg.getAccessToken()+"&openid="+wxSendMsg.getTouser()+"&lang=zh_CN");
        Map map =  JSON.parseObject(result, Map.class);
        if(map.get("openid")!=null){
            return  map;
        }else{
            throw new RuntimeException(map.get("errmsg").toString());
        }
    }
    /**
     * 发送消息
     * @param wxSendMsg
     * @return
     */
    public  static boolean send( wxSendMsg wxSendMsg){
        JSONObject param=new JSONObject();
        param.put("template_id", wxSendMsg.getTemplateId());
        param.put("pagepath", wxSendMsg.getPage());
        param.put("data", wxSendMsg.getData());
        param.put("min iprogram_state", wxSendMsg.getMiniprogramState());
        param.put("appid","");
        param.put("touser", wxSendMsg.getTouser());


        String result = Http.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+ wxSendMsg.getAccessToken(),
                param);
        Map<String,String> map =  JSON.parseObject(result, Map.class);

        if(map.get("errcode")==null||"0".equals(map.get("errcode"))){
            throw new RuntimeException(map.get("errmsg"));
        }
       return  true;
    }


}
