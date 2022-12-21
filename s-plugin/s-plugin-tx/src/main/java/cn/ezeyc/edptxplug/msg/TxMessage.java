package cn.ezeyc.edptxplug.msg;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edptxplug.config.Msg;
import cn.ezeyc.edptxplug.config.MsgApp;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
// 导入对应SMS模块的client
import com.tencentcloudapi.sms.v20210111.SmsClient;
// 导入要请求接口对应的request response类
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 腾讯短信
 * @author zewang
 * @date 2021.09.15
 */
@configuration
public class TxMessage {
    @Autowired
    private Msg accessKey;

    private SmsClient client;
    private SendSmsRequest req = new SendSmsRequest();
    private MsgApp app;
    public void  init(){
        Credential cred = new Credential(accessKey.getSecretId(), accessKey.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setConnTimeout(60);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
         client = new SmsClient(cred, "ap-nanjing",clientProfile);

    }
    public  void send(MsgTypeEnum type,String [] phones,String [] params) throws TencentCloudSDKException {
        List<MsgApp> sdkApp = accessKey.getSdkApp();
        if(sdkApp!=null&&sdkApp.size()>0){
            for(MsgApp a:sdkApp){
                if(type.getType()==a.getType()){
                    app=a;
                }
            }
            if(app==null){
                throw new ExRuntimeException("sdkApp中id配置错误");
            }
            //设置应用id
            req.setSmsSdkAppId(app.getSdkAppId());
            //设置签名
            req.setSignName(app.getSignName());
            //设置模版id
            req.setTemplateId(app.getTemplateId());
            //发送手机号
            req.setPhoneNumberSet(phones);
            //模版参数【按照顺序】
            req.setTemplateParamSet(params);
            SendSmsResponse res = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(res));
        }else {
            throw new ExRuntimeException("sdkApp未配置");
        }

    }
}
