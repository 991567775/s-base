package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpcommon.annotation.framework.autowired;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Aspect
@Component
public class TransactionAop {


    @autowired
    private DataSource dataSource;


    /**
     * 选择切面的注解CustomTransaction
     */
    @Pointcut("@annotation(cn.ezeyc.edpcommon.annotation.framework.tx)")
    public void transactionPointCut() {

    }

    /**
     * 方法增强@Arounbd
     * @param point
     */
    @Around("transactionPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //开启事务
        Object proceed=null;
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try {
            //执行方法
            transactionManager.transaction();
             proceed = point.proceed();
            //执行成功提交事务
            transactionManager.commit();
        } catch (Throwable throwable) {
            //执行方法出错则回滚事务
            transactionManager.rollback();
            throw  throwable;
        }finally {
            transactionManager.closeTx();
            return proceed;
        }

    }

}
