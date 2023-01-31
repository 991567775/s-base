package cn.ezeyc.edpbase.core.client;


import cn.ezeyc.edpcommon.annotation.client.client;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpbase.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * 微服务接口代理类实现以及调用
 * @author wz
 */
@SuppressWarnings("unchecked")
public class ClientProxyFactory<T> implements FactoryBean<T> {
    private final Logger logger= LoggerFactory.getLogger(ClientProxyFactory.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient client;
    private final Class<T> interfaceType;
    public ClientProxyFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }
    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType},
                new ClientProxy<>(interfaceType,restTemplate));
    }
    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
     class ClientProxy<T>  implements InvocationHandler {
        private final Class type;
        private final RestTemplate restTemplate;
        public ClientProxy(Class type, RestTemplate restTemplate) {
            this.type = type;
            this.restTemplate =restTemplate;
        }

        @Override
        public String toString() {
            return "ClientProxy{" +
                    "type=" + type +
                    ", restTemplate=" + restTemplate +
                    '}';
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)  {
            //代理类显示名称【查看代理本质调用的是toString方法】
            if(method.getName().equals(ZdConst.toString)){
                return ZdConst.proxy;
            }
            if (type.isAnnotationPresent(client.class)) {
                client getAnnotation = (client) type.getAnnotation(client.class);
                //参数转换
                // 计算方法的参数位置信息  // query(HttpServletRequest request, HttpServletResponse response,String name)
                Parameter[] parameters = method.getParameters();

                Map m = new HashMap((int) ((parameters.length/0.75)+1));
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
                    if (parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        // 如果是request和response对象，那么参数名称写HttpServletRequest和HttpServletResponse
                        m.put(parameter.getType().getSimpleName(), args[j]);
                    } else {
                        m.put(parameter.getName(), args[j]);
                    }

                }

                return post(getAnnotation.value(), ZdConst.slanting + StringUtil.toLowerCaseFirstOne(type.getSimpleName().split(ZdConst.client)[0]) + ZdConst.slanting + method.getName(), m, method.getReturnType());
            }
            return null;
        }
        public Object   post(String service, String url, Object o, Class obj){

            HttpServletRequest request = getHttpServletRequest();
            HttpHeaders httpHeaders = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            httpHeaders.setContentType(type);
            httpHeaders.add("Accept", MediaType.APPLICATION_JSON.toString());
            //传递请求头(原参数不传递)
            if (!Objects.isNull(request)) {
                Map<String, String> headers = getHeaders(request);
                if (headers.size() > 0) {
                    httpHeaders.add("authorization",headers.get("authorization"));
                }
            }
            HttpEntity httpEntity = new HttpEntity(o, httpHeaders);
            //判断是否直接ip访问
            if(!service.contains(":")||!StringUtil.ip(service.split(":")[0])||!StringUtil.number(service.split(":")[1])){
                ServiceInstance choose = client.choose(service);
                service=choose.getHost()+":"+choose.getPort();
            }
            logger.info("远程服务地址:http://"+service+url);
            ResponseEntity<Object> s=restTemplate.exchange("http://"+service+url, HttpMethod.POST,httpEntity,obj);
            return s.getBody();

        }
         private HttpServletRequest getHttpServletRequest() {
             try {
                 // 这种方式获取的HttpServletRequest是线程安全的
                 return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
             } catch (Exception e) {

                 return null;
             }
         }

         private Map<String, String> getHeaders(HttpServletRequest request) {
             Map<String, String> map = new LinkedHashMap<>();
             Enumeration<String> enums = request.getHeaderNames();
             while (enums.hasMoreElements()) {
                 String key = enums.nextElement();
                 String value = request.getHeader(key);
                 map.put(key, value);
             }
             return map;

         }

    }



}
