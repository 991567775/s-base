package cn.ezeyc.edpbase.interfaces;


import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * 查询数据库封装接口
 * @param <T> 实体
 * @author wz
 */
public interface SqlSession<T> {
    /**
     * 查询接口
     * @param returnClass  返回类型
     * @param model   映射表实体
     * @param method  方法
     * @param t      返回集合类型
     * @param args   参数
     * @return
     * @throws SQLException
     */
    Object select(Class returnClass,Class model,Class t,Method method, Object[] args) throws SQLException;
    /**
     * 更新接口
     * @param t 实体class
     * @param method 方法
     * @param args 参数
     * @return
     * @throws SQLException
     */
    Integer update(Class t, Method method,Object[] args) throws SQLException;
    /**
     * 删除接口
     * @param t 实体class
     * @param method 方法
     * @param args 参数
     * @return
     * @throws SQLException
     */
    Integer delete(Class t, Method method,Object[] args) throws SQLException;
    /**
     * 插入接口
     * @param t 实体class
     * @param method 方法
     * @param args 参数
     * @return
     * @throws SQLException
     */
    Object insert(Class t, Method method, Object[] args) throws SQLException;

    /**
     * 执行sql
     * @param args 参数
     * @param returnType 返回类型class
     * @param model 实体class
     * @return
     * @throws SQLException
     */
    Object executeSql(Object[] args,Class returnType, Class model) throws SQLException;

}
