package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpcommon.annotation.dao.cache;
import cn.ezeyc.edpcommon.annotation.dao.clearCache;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.annotation.framework.tx;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpbase.util.RedisUtil;
import cn.ezeyc.edpbase.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事务处理封装
 * 代理对象工厂
 * @author wz
 */
@configuration
public class ServiceProxyFactory {
    private final Logger logger=  LoggerFactory.getLogger(ServiceProxyFactory.class);

    @autowired
    private TransactionManager transactionManager;
    @autowired
    private RedisUtil redisUtil;
    @value("edp.config.cache")
    private Boolean cache;

    /**
     * Jdk动态代理
     * @param obj  委托对象
     * @return   代理对象
     */
    public Object getJdkProxy(Object obj) {

        // 获取代理对象
        return  Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        try{
                            boolean isCache= (cache==null|| cache)  &&findAnnotationFromProxy(cache.class,obj,method);
                            boolean isClearCache= (cache==null|| cache)  &&findAnnotationFromProxy(clearCache.class,obj,method);
                            //是否需要清除缓存
                            if(isClearCache){
                                final Class[] classes = findAnnotationValueFromProxy(clearCache.class, obj, method);
                                final String[] s = findAnnotationValueFromProxyString(clearCache.class, obj, method);
                                for(Class c:classes){
                                    redisUtil.delBySuffix(c.getSimpleName());
                                }
                                for(String c:s){
                                    redisUtil.del(c);
                                }
                                redisUtil.delBySuffix(obj.getClass().getSimpleName());
                            }
                            //是否需要缓存---获取缓存值
                            if(isCache){
                                result = redisUtil.getByString(obj.getClass().getSimpleName()+":"+method.getReturnType().getSimpleName()+":"+ SerializeUtil.serializeString(args));
                            }
                            if(result==null){
                                // 开启事务(关闭事务的自动提交)
                                if(findAnnotationFromProxy(tx.class, obj, method)) {
                                    //打开事务链接
                                    if(!transactionManager.getTx()){
                                        transactionManager.transaction();
                                    }
                                    result = method.invoke(obj,args);
                                    // 提交事务
                                    transactionManager.commit();
                                }else {

                                    result = method.invoke(obj,args);
                                }
                                //是否需要缓存---设置缓存值
                                if(isCache){
                                    redisUtil.setByString(obj.getClass().getSimpleName()+":"+method.getReturnType().getSimpleName()+":"+SerializeUtil.serializeString(args),result);
                                }
                            }
                        }catch (Exception e) {
                            System.out.println(e);
                            // 回滚事务
                            if(findAnnotationFromProxy(tx.class, obj, method)) {
                                transactionManager.rollback();
                            }
                            if(!(e.getCause() instanceof ExRuntimeException)){
                                logger.error(ExRuntimeException.getExceptionInfo(e.getCause()));
                            }
                            throw  e.getCause();
                        }finally {
                            // 事务关闭
                            if(findAnnotationFromProxy(tx.class, obj, method)) {
                                transactionManager.closeTx();
                            }

                        }
                        return result;
                    }
                });

    }

    private static boolean findAnnotationFromProxy(Class<?> cusAnnotation, Object target, Method method) throws NoSuchMethodException {
        Annotation[] annotations = target.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotations();
        boolean hasAnnotation = Arrays.stream(annotations).filter(annotation -> {
            return annotation.annotationType().isAssignableFrom(cusAnnotation);
        }).count() > 0;
        return hasAnnotation;
    }
    private static Class[] findAnnotationValueFromProxy(Class<?> cusAnnotation, Object target, Method method) throws NoSuchMethodException {
        Annotation[] annotations = target.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotations();
        final List<Annotation> collect = Arrays.stream(annotations).filter(annotation -> {
            return annotation.annotationType().isAssignableFrom(cusAnnotation);
        }).collect(Collectors.toList());
        if(collect.size()>0){
            final clearCache annotation =(clearCache) collect.get(0);
            if(annotation.value()!=null){
              return annotation.value();
            }
        }
        return  null;
    }
    private static String[] findAnnotationValueFromProxyString(Class<?> cusAnnotation, Object target, Method method) throws NoSuchMethodException {
        Annotation[] annotations = target.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotations();
        final List<Annotation> collect = Arrays.stream(annotations).filter(annotation -> {
            return annotation.annotationType().isAssignableFrom(cusAnnotation);
        }).collect(Collectors.toList());
        if(collect.size()>0){
            final clearCache annotation =(clearCache) collect.get(0);
            if(annotation.value()!=null){
                return annotation.values();
            }
        }
        return  null;
    }

}
