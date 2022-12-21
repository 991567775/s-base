package cn.ezeyc.edpbase.core.client;

import com.alibaba.fastjson.JSON;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import cn.ezeyc.edpbase.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author wz
 */
public class ClientRequest {

    /**
     * 负载均衡
     */
    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient client;
    /**
     * 请求
     * @param token  token验证
     * @param service 服务名称
     * @param url 请求地址
     * @param o 参数
     * @return
     */
    public ResultBody post(String token,String service, String url, Object o) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if(StringUtils.isNotBlank(token)){
            httpHeaders.add("Authorization",token);
        }
       return   basePost(service,url,o,httpHeaders);
    }

    /**
     *
     * @param service
     * @param url
     * @param o
     * @return
     */
    public ResultBody post(String service, String url, Object o) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return  basePost(service,url,o,httpHeaders);
    }

    /**
     * 请求
     * @param service
     * @param url
     * @param o
     * @param httpHeaders
     * @return
     */
    private ResultBody basePost(String service, String url, Object o,HttpHeaders httpHeaders){
        //json字符串转换
        if(o instanceof String&& StringUtil.isJson((String) o)){
            o= JSON.parseObject(o.toString());
        }
        HttpEntity httpEntity = new HttpEntity(o, httpHeaders);
        if(!service.contains(":")||!StringUtil.ip(service.split(":")[0])||!StringUtil.number(service.split(":")[1])){
            ServiceInstance choose = client.choose(service);
            service=choose.getHost()+":"+choose.getPort();
        }
        ResponseEntity<ResultBody> s = restTemplate.exchange("http://" + service + url, HttpMethod.POST, httpEntity, ResultBody.class);
        return s.getBody();
    }



}
