package cn.ezeyc.edpbase.core.session;



import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 应癫
 *
 * 事务管理器类：负责手动事务的开启、提交、回滚
 */

@configuration
public class TransactionManager {
    private final Logger logger= LoggerFactory.getLogger(TransactionManager.class);
    public static ThreadLocal<Connection> localTx = new ThreadLocal<Connection>();
    public static ThreadLocal<Connection> local = new ThreadLocal<Connection>();
    public static ThreadLocal<Boolean> is = new ThreadLocal<Boolean>();
    @autowired
    private DataSource dataSource;
    /**
     * 事务连接
     */
    public Connection  transaction(){
        try {
            Connection conn =  localTx.get();
            if(conn==null){
                conn = dataSource.getConnection();
                conn.setAutoCommit(false);
                is.set(true);
                localTx.set(conn);
            }
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 普通连接
     * @return
     * @throws SQLException
     */
    public Connection getCon() throws SQLException {
        Connection connection = local.get();
        if(connection == null) {
            // 从连接池拿连接并绑定到线程
            connection = dataSource.getConnection();
            // 绑定到当前线程
            local.set(connection);
            logger.info("获取新链接");
        }
        return connection;
    }

    /**
     * 操作日志获取连接
     * @return
     * @throws SQLException
     */
    public Connection getLogCon() throws SQLException {
           return   dataSource.getConnection();
    }

    /**
     * 关闭操作日志连接
     * @param conn
     * @throws SQLException
     */
    public void closeLogCon(Connection conn) throws SQLException {
           conn.close();
    }
    public boolean getTx()  {
        Boolean tx=is.get();
        return tx != null && tx;

    }

    /**
     * 事务提交
     */
    public  void commit() throws SQLException {
        Connection conn = localTx.get();
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     *	事务回滚
     */
    public  void rollback() throws SQLException {
        Connection conn = localTx.get();
        if (conn != null) {
            conn.rollback();
        }
    }
    /**
     * 关闭事务，并且调用数据库关闭的动作
     * @throws SQLException
     */
    public  void close() throws SQLException {
        Connection conn = local.get();
        if (conn != null) {
            conn.close();
        }
        local.remove();
    }

    public void closeTx() throws SQLException {
        Connection conn=localTx.get();
        if(conn != null){
            conn.close();
        }
        localTx.remove();
        is.remove();
    }
}
