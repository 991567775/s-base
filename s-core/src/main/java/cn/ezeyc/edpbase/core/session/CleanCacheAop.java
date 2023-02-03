package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpbase.util.RedisUtil;
import cn.ezeyc.edpcommon.annotation.dao.clearCache;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
@Order(1)
public class CleanCacheAop {


    @autowired
    private RedisUtil redisUtil;

    /**
     * 选择切面的注解CustomTransaction
     */
    @Pointcut("@annotation(cn.ezeyc.edpcommon.annotation.dao.clearCache)")
    public void CleanCachePointCut() {

    }
    @Before("CleanCachePointCut()")
    public void before(JoinPoint point) throws Throwable {
        //删除当前对应表
        String simpleName = point.getTarget().getClass().getSimpleName();
        redisUtil.delBySuffix(simpleName);
        //删除指定类的表
        Class<?> targetCls=point.getTarget().getClass();
        //获取方法签名(通过此签名获取目标方法信息)
        MethodSignature ms=(MethodSignature)point.getSignature();
        //获取目标方法上的注解指定的操作名称
        Method targetMethod= targetCls.getDeclaredMethod(ms.getName(), ms.getParameterTypes());
        clearCache clearCache=  targetMethod.getAnnotation(clearCache.class);
        Class<?>[] value = clearCache.value();
        String[] values = clearCache.values();
        for(Class c:value){
            redisUtil.delBySuffix(c.getSimpleName());
        }
        for(String c:values){
            redisUtil.delBySuffix(c);
        }
    }
}
