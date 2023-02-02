package cn.ezeyc.edpbase.core.session;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Component
public class TransactionUtils {


    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;


    /**
     *初始化创建TransactionStatus对象
     * @return
     */
    public TransactionStatus init(){
        System.out.println("创建事务了...");
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionAttribute());
        return transactionStatus;
    }

    /**
     * 提交事务
     * @param transactionStatus
     */
    public void commit(TransactionStatus transactionStatus){
        System.out.println("提交事务...");
        dataSourceTransactionManager.commit(transactionStatus);
    }


    public void rollback(TransactionStatus transactionStatus){
        System.out.println("事务回滚了....");
        dataSourceTransactionManager.rollback(transactionStatus);
    }
}
