package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpbase.constant.SqlConst;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpbase.enums.SqlType;
import cn.ezeyc.edpbase.interfaces.Executor;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpbase.pojo.session.SqlParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * sql执行器
 * @author wz
 */
@configuration
public class SimpleExecutor implements Executor {
    private final Logger logger= LoggerFactory.getLogger(SimpleExecutor.class);
//    @Autowired
//    private  TransactionManager transactionManager;

    @Autowired
    private  SetResult setResult;
    @autowired
    private DataSource dataSource;

    @value("edp.db.showSql")
    private Boolean showSql;

    @Override
    public Object doSql(SqlParam obj, SqlType type, Class returnType, Class model) throws SQLException {
        PreparedStatement preparedStatement=null;
         ResultSet resultSet=null;
        Connection        currentThreadConn=null;
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try {
            if(transactionManager.getTx()){
                currentThreadConn = transactionManager.transaction();
            }else{
                currentThreadConn = transactionManager.getCon();
            }
            if(returnType== Page.class){
                if(obj.getPage()==null){
                    throw new ExRuntimeException("返回分页集合请传递继承ModelBase的实体作为参数或自己写入分页条件，否则无法接收分页信息");
                }
                preparedStatement =currentThreadConn.prepareStatement(obj.getSql()+obj.getPage().getPageSql());
               if(showSql!=null&&showSql){
                   logger.info(obj.getSql()+obj.getPage().getPageSql());
               }
            }else{
                preparedStatement =currentThreadConn.prepareStatement(obj.getSql());
                if(showSql!=null&&showSql){
                    logger.info(obj.getSql());
                }
            }
            //参数赋值
            if(obj.getParams()!=null){
                preparedStatement=setValue(preparedStatement,obj.getParams());
            }
            if(type == SqlType.SELECT){
                if(showSql!=null&&showSql&&obj.getParams()!=null){
                    logger.info(obj.getParams().size()+ StringUtils.join(obj.getParams()));
                }
                resultSet = preparedStatement.executeQuery();
                //实体转换器//查询单个必须有条件，否则直接返回null
                if(returnType == ModelBase.class &&obj.getParams()!=null&&obj.getParams().size()==0){
                    logger.error("查询单个对象必须传递参数");
                    throw new ExRuntimeException("查询单个对象必须传递参数");
                }
                 Object o = setResult.toResult(resultSet, returnType, model);
                //获取总条数
                if(returnType== Page.class){
                    Long total=0L;
                    preparedStatement =currentThreadConn.prepareStatement("SELECT count(*) AS total from ( "+obj.getSql() +") a");
                    preparedStatement=setValue(preparedStatement,obj.getParams());
                    final ResultSet result = preparedStatement.executeQuery();
                    while (result.next()) {
                        total= result.getLong(ZdConst.total);
                    }
                    obj.getPage().setResults((List) o);
                    obj.getPage().setTotalCount(total);
                    result.close();
                    return obj.getPage();
                }
                return o;
            }else {
                if(showSql!=null&&showSql&&obj.getParams()!=null) {
                    logger.info("param:" + obj.getParams().size());
                }
                return preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            logger.error("数据库操作错误:"+throwables.getMessage());
            throw throwables;
        }finally {
            try {
                if(!transactionManager.getTx()){
                    if(resultSet!=null){
                        resultSet.close();
                    }
                    if(preparedStatement!=null){
                        preparedStatement.close();
                    }
                    transactionManager.close();
                }
            } catch (SQLException throwables) {
                logger.error("连接关闭错误:"+throwables.getMessage());
                throw throwables;
            }
        }
    }

    @Override
    public Object doSqlBatch(List<SqlParam> list, Class model) throws SQLException {
        PreparedStatement preparedStatement=null;
        Connection        currentThreadConn=null;
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try {
            if(transactionManager.getTx()){
                currentThreadConn = transactionManager.transaction();
            }else{
                currentThreadConn = transactionManager.getCon();
            }
            int sizes=0;
            if(list.size()>0){
                if(showSql!=null&&showSql) {
                    logger.info(list.get(0).getSql());
                }
                preparedStatement =currentThreadConn.prepareStatement(list.get(0).getSql());
                for(int x=0;x<list.size();x++){
                    preparedStatement=setValue(preparedStatement,list.get(x).getParams());
                    if(showSql!=null&&showSql&&list.get(x).getParams()!=null) {
                        logger.info("param:"+list.get(x).getParams().size());
                    }
                    //1. "攒" sql
                    preparedStatement.addBatch();
                        if(x % 500 == 0){
                            //2.  攒够500,执行一次batch
                            int[] size=  preparedStatement.executeBatch();
                            sizes+=size.length;
                            //3. 清空batch
                            preparedStatement.clearBatch();
                        }

                }
                //不足部分执行
                int[] size= preparedStatement.executeBatch();
                sizes+=size.length;
                preparedStatement.clearBatch();
                return sizes;
            }
        } catch (SQLException throwables) {
            logger.error("批量操作错误:"+throwables.getMessage());
            throw throwables;
        }finally {
            try {
                if(!transactionManager.getTx()){
                    if(preparedStatement!=null){
                        preparedStatement.close();
                    }
                    transactionManager.close();
                }
            } catch (SQLException throwables) {
                logger.error("连接关闭错误:"+throwables.getMessage());
                throw throwables;
            }
        }
        return -1;
    }

    @Override
    public Object executeSql(String sql, List<Object> params,Class returnType, Class model) throws SQLException {
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        Connection        currentThreadConn=null;
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try  {
            if(transactionManager.getTx()){
                currentThreadConn = transactionManager.transaction();
            }else{
                currentThreadConn = transactionManager.getCon();
            }
            preparedStatement =currentThreadConn.prepareStatement(sql);
            //参数赋值
            if(params!=null){
                preparedStatement=setValue(preparedStatement,params);
            }
            //日志
            if(showSql!=null&&showSql&&params!=null) {
                logger.info(sql);
                logger.info("param:"+params.size());
            }
            if(sql.trim().startsWith(SqlConst.select)){
                resultSet = preparedStatement.executeQuery();
                return setResult.toResult(resultSet,returnType,model);
            }else {
                return preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            logger.error("sql操作错误:"+throwables.getMessage());
            throw throwables;
        }finally {
            try {
                if(!transactionManager.getTx()){
                    if(resultSet!=null){
                        resultSet.close();
                    }
                    if(preparedStatement!=null){
                        preparedStatement.close();
                    }
                    transactionManager.close();
                }
            } catch (SQLException throwables) {
                logger.error("连接关闭错误:"+throwables.getMessage());
                throw throwables;
            }
        }

    }

    @Override
    public void executeSql(String sql, List<Object> params) throws SQLException {
        PreparedStatement preparedStatement=null;
        Connection        currentThreadConn=null;
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try  {
            if(transactionManager.getTx()){
                currentThreadConn = transactionManager.transaction();
            }else{
                currentThreadConn = transactionManager.getCon();
            }
            preparedStatement =currentThreadConn.prepareStatement(sql);
            //参数赋值
            if(params!=null){
                preparedStatement=setValue(preparedStatement,params);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            logger.error("sql操作错误:"+throwables.getMessage());
            throw throwables;
        }finally {
            try {
                //非事务关闭
                if(!transactionManager.getTx()){
                    if(preparedStatement!=null){
                        preparedStatement.close();
                    }
                    transactionManager.close();
                }
            } catch (SQLException throwables) {
                logger.error("连接关闭错误:"+throwables.getMessage());
                throw throwables;
            }
        }
    }

    @Override
    public void executeSql(String sql) throws SQLException {
        PreparedStatement preparedStatement=null;
        Connection        currentThreadConn=null;
        TransactionManager transactionManager = new TransactionManager(dataSource);
        try  {
            if(transactionManager.getTx()){
                currentThreadConn = transactionManager.transaction();
            }else{
                currentThreadConn = transactionManager.getCon();
            }
            preparedStatement =currentThreadConn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            logger.error("sql操作错误:"+throwables.getMessage());
            throw throwables;
        }finally {
            try {
                if(!transactionManager.getTx()){
                    if(preparedStatement!=null){
                        preparedStatement.close();
                    }
                    transactionManager.close();
                }
            } catch (SQLException throwables) {
                logger.error("连接关闭错误:"+throwables.getMessage());
                throw throwables;
            }
        }
    }

    /**
     * 赋值
     * @param preparedStatement
     * @param list
     * @return
     */
    private   PreparedStatement setValue(PreparedStatement preparedStatement, List<Object> list){
        for(int x=0; x<list.size();x++){
            try {
                preparedStatement.setObject(x+1,list.get(x));
            } catch (SQLException throwables) {
            }
        }
        return  preparedStatement;
    }
}
