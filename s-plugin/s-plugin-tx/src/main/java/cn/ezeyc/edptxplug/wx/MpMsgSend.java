package cn.ezeyc.edptxplug.wx;

import cn.ezeyc.edptxplug.pojo.wxSendMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpcommon.util.Http;
import cn.ezeyc.edptxplug.config.wxMp;

import java.security.MessageDigest;
import java.util.Map;

/**
 * 微信小程序订阅消息模板
 * 发送
 */
public class MpMsgSend {

    /**
     * 订阅消息模板token验证
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String token,String signature, String timestamp, String nonce) {
        String[] arr = new String[] { token, timestamp, nonce };
        //将token、timestamp、nonce三个参数进行字典序排序
//         Arrays.sort(arr);
        sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (Exception e) {
        }
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 订阅消息获取token
     * @param wxMp
     * @return
     */
    public  static String getToken(wxMp wxMp){
        //1.获取token
        String result = Http.sendGet(" https://api.weixin.qq.com/cgi-bin/token",
                "appid=" + wxMp.getAppId()+ "&secret=" + wxMp.getAppSecret() + "&grant_type=client_credential");
        Map<String,String> map =  JSON.parseObject(result, Map.class);
        //成功
        if(map.get("access_token")!=null){
            return  map.get("access_token");
        }else{
            throw new RuntimeException(map.get("errmsg"));
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
        param.put("page", wxSendMsg.getPage());
        param.put("data", wxSendMsg.getData());
        param.put("miniprogram_state", wxSendMsg.getMiniprogramState());
        param.put("lang", wxSendMsg.getLang());
        param.put("touser", wxSendMsg.getTouser());


        String result = Http.sendPost("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+ wxSendMsg.getAccessToken(),
                param);
        Map<String,String> map =  JSON.parseObject(result, Map.class);

        if(map.get("errcode")==null||"0".equals(map.get("errcode"))){
            throw new RuntimeException(map.get("errmsg"));
        }
       return  true;
    }
    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }
    private static void sort(String a[]) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].compareTo(a[i]) < 0) {
                    String temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }
}
