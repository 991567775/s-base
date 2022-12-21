package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpbase.core.client.ClientRequest;
import cn.ezeyc.edpcommon.annotation.framework.logRecord;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.enums.ResultEnum;
import cn.ezeyc.edpbase.interfaces.Executor;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import cn.ezeyc.edpbase.pojo.session.TokenBody;
import cn.ezeyc.edpbase.util.LoginUtil;
import cn.ezeyc.edpbase.util.ThreadPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * control 记录远程调用日志
 * 代理对象工厂
 * @author wz
 */
public class ControlFactory implements MethodInterceptor {
    private static final Logger logger=  LoggerFactory.getLogger(ControlFactory.class);
    /**
     * 需要代理的目标对象
     */
    private Object target;
    /**
     * 是否开启记录日志
     */
    private Boolean enable;
    /**
     * 服务名
     */
    private String service;
    /**
     * 请求地址
     */
    private String url;
    /**
     * sql执行
     */
    private Executor executor;
    /**
     * 远程请求
     */
    private ClientRequest clientRequest;

    public ControlFactory(Object target, Boolean enable, String service, String url, ClientRequest clientRequest) {
        this.target = target;
        this.enable=enable;
        this.service=service;
        this.url=url;
        this.clientRequest=clientRequest;
    }


    /**
     * 代理方法
     * @param obj
     * @param method
     * @param arr
     * @param proxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] arr, MethodProxy proxy) throws Throwable {
        //原生方法调用toString等
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this,arr);
        }
        Object invoke=proxy.invoke(target,arr);
        String currentToken = LoginUtil.getCurrentToken();
        if(enable!=null&&service!=null&&url!=null&&enable&&method.isAnnotationPresent(logRecord.class)){
            //使用线程并发执行日志记录
            ThreadPool.pool.schedule(new TimerTask() {
                @Override
                public void run() {
                    String simpleName = target.getClass().getSimpleName();
                    Map map = new HashMap(9);
                    map.put("path", ZdConst.slanting + simpleName.replace(ZdConst.end_with_control, "") + ZdConst.slanting + method.getName());
                    map.put("actions", method.getAnnotation(logRecord.class).value());
                    map.put("lei", simpleName);
                    if (invoke instanceof ResultBody) {
                        if (((ResultBody<?>) invoke).getCode() == ResultEnum.OK.getCode()) {
                            map.put("level", "info");
                            map.put("result", "success");

                        } else {
                            map.put("level", "error");
                            map.put("result", ((ResultBody<?>) invoke).getMessage());
                        }
                    } else if (invoke instanceof TokenBody) {
                        if (StringUtils.isNotBlank(((TokenBody) invoke).getAccess_token())) {
                            map.put("level", "info");
                            map.put("result", "success");
                        } else {
                            map.put("level", "error");
                            map.put("result", "失败");
                        }
                    } else if (invoke instanceof String) {
                        map.put("level", "info");
                        map.put("result", invoke);
                    }
                    map.put("remove", 0);
                    ResultBody post = clientRequest.post(currentToken,service, url, map);
                    if (ResultEnum.OK.getCode() != post.getCode()) {
                        logger.error("日志插入失败");
                    }

                }
            },1, TimeUnit.SECONDS);
        }

        return invoke;
    }


    /**
     * 定义获取代理对象方法
     * @return
     */
    public Object getCglibProxy(){
        //为目标对象target赋值
        Enhancer enhancer = new Enhancer();
        //设置父类,因为Cglib是针对指定的类生成一个子类，所以需要指定父类
        enhancer.setSuperclass(target.getClass());
        // 设置回调
        enhancer.setCallback(this);
        //创建并返回代理对象
        Object result = enhancer.create();
        return result;
    }

}
