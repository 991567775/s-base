package cn.ezeyc.edpaliplug.util;

import cn.ezeyc.edpaliplug.config.AliMp;
import com.alipay.api.*;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付宝获取客户端以及请求
 */
public class AliUtil {
    private  static  AlipayClient client=null;
    private  static AlipayClient getClient(AliMp mp){
        client=  new DefaultAlipayClient(mp.getGateWay().trim(), mp.getAppId().trim(), mp.getPrivateKey().trim(), mp.format, mp.charSet, mp.getPublicKey().trim(), mp.singType);
        return client;
    }
    /**
     * 证书客户端获取
     * @param mp
     * @return
     */
    private  static  AlipayClient getClientWithCert(AliMp mp){
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(mp.getGateWay());
        alipayConfig.setAppId(mp.getAppId());
        alipayConfig.setPrivateKey(mp.getPrivateKey());
        alipayConfig.setAppCertPath(mp.getAppCertPath());
        alipayConfig.setAlipayPublicCertPath(mp.getPublicCertPath());
        alipayConfig.setRootCertPath(mp.getRootCertPath());
        alipayConfig.setFormat(mp.format);
        alipayConfig.setCharset(mp.charSet);
        alipayConfig.setSignType(mp.singType);
        try {
            return  new DefaultAlipayClient(alipayConfig);
        } catch (AlipayApiException e) {
            throw new ExRuntimeException(e.getMessage());
        }

    }
    /**
     * 根据用户授权码获取支付宝用户信息
     * @param authCode 授权码
     * @return
     */
    public static AlipaySystemOauthTokenResponse getUserByCode(AliMp mp, String authCode) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode.trim());
        return (AlipaySystemOauthTokenResponse) AliUtil.request(request,mp);
    }
    /**
     * 支付宝请求
     * @param request
     * @param mp
     * @return
     */
    public  static AlipayResponse request(AlipayRequest request, AliMp mp){
        if(StringUtils.isNotBlank(mp.getAppCertPath())){
            client=getClientWithCert(mp);
        }else{
            client=getClient(mp);
        }
        try {
            if(StringUtils.isNotBlank(mp.getAppCertPath())){
                return client.certificateExecute(request);
            }
            return client.execute(request);
        } catch (Exception e) {
            throw new ExRuntimeException(e.getMessage());
        }
    }
}
