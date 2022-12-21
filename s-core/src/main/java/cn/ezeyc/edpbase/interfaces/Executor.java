package cn.ezeyc.edpbase.interfaces;


import cn.ezeyc.edpbase.enums.SqlType;
import cn.ezeyc.edpbase.pojo.session.SqlParam;

import java.sql.SQLException;
import java.util.List;

/**
 * 查询器接口
 * @author wz
 */
public interface Executor {
    /**
     * sql执行
     * @param obj 参数对象
     * @param sqlType sql类型
     * @param returnType 返回类型
     * @param model 实体class
     * @return
     * @throws SQLException
     */
    Object doSql(SqlParam obj, SqlType sqlType, Class returnType, Class model) throws SQLException;

    /**
     * 批量执行
     * @param obj 参数
     * @param model 实体class
     * @return
     * @throws SQLException
     */
    Object doSqlBatch(List<SqlParam> obj,  Class model) throws SQLException;

    /**
     * sql执行
     * @param sql 孙侨潞语句
     * @param params 参数
     * @param returnType 返回类型
     * @param model 实体class
     * @return
     * @throws SQLException
     */
    Object executeSql(String sql,  List<Object> params,Class returnType, Class model) throws SQLException;

    /**
     * sql执行
     * @param sql sql语句
     * @param params 参数
     * @throws SQLException
     */
    void executeSql(String sql,  List<Object> params) throws SQLException;

    /**
     * sql执行
     * @param sql sql语句
     * @throws SQLException
     */
    void executeSql(String sql) throws SQLException;
}
