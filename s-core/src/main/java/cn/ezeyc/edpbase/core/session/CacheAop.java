package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpbase.util.RedisUtil;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.value;
import com.alibaba.fastjson2.JSONArray;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class CacheAop {


    @autowired
    private RedisUtil redisUtil;
    @value("edp.config.cache")
    private Boolean cache;
    /**
     * 选择切面的注解CustomTransaction
     */
    @Pointcut("@annotation(cn.ezeyc.edpcommon.annotation.dao.cache)")
    public void cachePointCut() {

    }

    /**
     * 方法增强@Arounbd
     * @param point
     */
    @Around("cachePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String simpleName = point.getTarget().getClass().getSimpleName();
        String name = point.getSignature().getName();
        JSONArray sub = JSONArray.of(point.getArgs());
        String key= simpleName+":"+name+":"+sub.toString();
        Object cacheResult = redisUtil.getByString(key);
        if(cacheResult!=null){
            return cacheResult;
        }
        Object result = point.proceed();
        redisUtil.setByString(key,result);
        return  result;
    }

}
