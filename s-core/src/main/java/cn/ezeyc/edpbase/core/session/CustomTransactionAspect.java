package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpbase.core.client.ClientRequest;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;


@Aspect
@Component
@Import(TransactionUtils.class)
public class CustomTransactionAspect {

    @Autowired
    private TransactionUtils transactionUtils;

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
    public void around(ProceedingJoinPoint point) throws Throwable {
        //开启事务
        TransactionStatus transactionStatus = transactionUtils.init();
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try {
            //执行方法
            transactionManager.transaction();
            point.proceed();
            //执行成功提交事务
            transactionManager.commit();
        } catch (Throwable throwable) {
            //执行方法出错则回滚事务
            transactionManager.rollback();
            throw  throwable;
        }finally {
            transactionManager.commit();
        }

    }
}
