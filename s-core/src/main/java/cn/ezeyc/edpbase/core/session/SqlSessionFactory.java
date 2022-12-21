package cn.ezeyc.edpbase.core.session;

import cn.ezeyc.edpcommon.annotation.dao.delete;
import cn.ezeyc.edpcommon.annotation.dao.insert;
import cn.ezeyc.edpcommon.annotation.dao.select;
import cn.ezeyc.edpcommon.annotation.dao.update;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpbase.constant.SqlConst;
import cn.ezeyc.edpbase.core.utils.BoundSql;
import cn.ezeyc.edpbase.core.utils.GenericTokenParser;
import cn.ezeyc.edpbase.core.utils.ParamSqlTxtHandler;
import cn.ezeyc.edpbase.enums.SqlType;
import cn.ezeyc.edpbase.interfaces.Executor;
import cn.ezeyc.edpbase.interfaces.Interceptor;
import cn.ezeyc.edpbase.interfaces.SqlSession;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpbase.pojo.session.Query;
import cn.ezeyc.edpbase.pojo.session.SqlParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SqlSessionFactory：
 *查询数据库接口实现工厂
 * @author: Administrator
 * @date: 2020年8月10日, 0010 11:12:06
 */
@configuration
public class SqlSessionFactory implements SqlSession {
    @autowired
    private Executor executor;
    @autowired
    private Interceptor interceptor;
    @value("edp.config.remove")
    private Boolean remove;
    private Logger logger= LoggerFactory.getLogger(SqlSessionFactory.class);


    @Override
    public Object select(Class returnType,Class model,Class t, Method method, Object[] args) throws SQLException {
      return   executor.doSql(translateSelectSql(model,returnType== Page.class,method,args), SqlType.SELECT,returnType,t);
    }
    /**
     * select查询解析
     * @param t
     * @param method
     * @param args
     * @return
     */
    private SqlParam translateSelectSql(Class t, boolean isPage, Method method, Object[] args)   {
        String  sql=method.getAnnotation(select.class).value();
        List<Object> params=null;
        WhereQuery whereQuery=null;
         BoundSql boundSql=null;

        //默认查询
        if(StringUtils.isEmpty(sql)){
            //组装查询字段、表
            sql= AnalyseSql.fieldSql(t);
            if(args!=null){
                boundSql=getBoundSql(sql);
                whereQuery= AnalyseSql.whereSql(method.getParameters(),args,boundSql,remove,false,false,interceptor);
            }
        }else if(args!=null){
            //自定义sql查询
            //数据权限拦截
            boundSql = getBoundSql(sql);
            whereQuery= AnalyseSql.whereSql(method.getParameters(),args,boundSql,remove,true,false,interceptor);
        }
        //获取组装后sql
        if(args!=null){
            sql=whereQuery.getWhereSql();
            //设置where条件值
            params=whereQuery.getWhereParam();
        }

        if(whereQuery!=null){
            return new SqlParam(sql,params,whereQuery.getPageLimit());
        }else{
            return new SqlParam(sql,params,new Page<>());
        }
    }
    @Override
    public Integer update(Class t, Method method, Object[] args) throws SQLException {
        return (Integer) executor.doSql(translateUpdateSql(t,method,args), SqlType.UPDATE,null,null);
    }
    /**
     * update解析
     * @param t
     * @param method
     * @param args
     * @return
     */
    private   SqlParam   translateUpdateSql(Class t,Method method, Object[] args)  {
        String  sql=method.getAnnotation(update.class).value();
        List<Object> params=null;
        WhereQuery whereQuery=null;
        BoundSql boundSql=null;
        if(args!=null){
            //只有实体默认通过id更新
            if(args.length==1&&args[0] instanceof ModelBase){
                Query query=new Query();
                query.eq("id",((ModelBase<?>) args[0]).getId());
                args=new  Object[]{args[0],query};
            }
            //默认更新
            if(StringUtils.isEmpty(sql)){
                //判断是否更新全部or不为空的字段
                if(method.getName().contains(SqlConst.methodUpdateByIdNotNull)||method.getName().contains(SqlConst.methodUpdateNotNull)){
                    boundSql=new BoundSql(AnalyseSql.updateSqlField(sql,args,false));
                    //组装更新字段值
                    params= AnalyseSql.updateSqlParam(args,false,interceptor);
                }else{
                    boundSql=new BoundSql(AnalyseSql.updateSqlField(sql,args,true));
                    //组装更新字段值
                    params= AnalyseSql.updateSqlParam(args,true,interceptor);
                }
                whereQuery= AnalyseSql.whereSql(method.getParameters(),args,boundSql,remove,false,true,interceptor);
//            //组装更新字段 组装where条件
                sql=whereQuery.getWhereSql();
//            //组装where条件值
                params.addAll(whereQuery.getWhereParam());
            }else{
                //自定义sql查询
                //组装更新字段值
                String updateField=sql;
                String updateWhere="";
                if(sql.contains(SqlConst.hasWhere)){
                    final String[] split = sql.split(SqlConst.hasWhere);
                    updateField=split[0];
                    updateWhere= SqlConst.and+split[1];
                }
                //判断是否更新全部or不为空的字段(自定义方法名要包含此字段）
                if(method.getName().contains(SqlConst.methodUpdateByIdNotNull)||method.getName().contains(SqlConst.methodUpdateNotNull)){
                    boundSql=new BoundSql(AnalyseSql.updateSqlField(updateField,args,false));
                    params= AnalyseSql.updateSqlParam(args,false,interceptor);
                }else{
                    boundSql=new BoundSql(AnalyseSql.updateSqlField(updateField,args,true));
                    params= AnalyseSql.updateSqlParam(args,true,interceptor);
                }
                //组装更新字段
                whereQuery= AnalyseSql.whereSql(method.getParameters(),args,boundSql,remove,true,true,interceptor);
//            //组装更新字段 组装where条件
                sql=whereQuery.getWhereSql()+updateWhere;
//            //组装where条件值
                params.addAll(whereQuery.getWhereParam());

            }
        }

        return new SqlParam(sql,params);
    }
    @Override
    public Integer delete(Class t, Method method, Object[] args) throws SQLException {
        return (Integer) executor.doSql(translateDelSql(t,method,args), SqlType.UPDATE,null,null);
    }

    /**
     * 删除
     * @param t
     * @param method
     * @param args
     * @return
     */
    private   SqlParam   translateDelSql(Class t,Method method, Object[] args) {
        String  sql=method.getAnnotation(delete.class).value();
        List<Object> params=null;
        WhereQuery whereQuery=null;
         BoundSql boundSql=null;
         if(args!=null){
             //默认更新
             if(StringUtils.isEmpty(sql)){
                 boundSql=new BoundSql(AnalyseSql.delSql(t));
                 whereQuery= AnalyseSql.whereSql(method.getParameters(),args,boundSql,remove,false,false,interceptor);
             }else{
                 //自定义sql查询
                 boundSql= getBoundSql(sql);
                 whereQuery= AnalyseSql.whereSql(method.getParameters(),args,boundSql,remove,true,false,interceptor);
             }
             //获取组装后sql
             sql=whereQuery.getWhereSql();
             //设置where条件值
             params=whereQuery.getWhereParam();
         }
        return new SqlParam(sql,params);
    }
    @Override
    public Object insert(Class t, Method method, Object[] args) throws SQLException {
        final List<SqlParam> sqlParams = translateInsertSql(t, method, args);
        List<Object> models=new ArrayList<>();
         Integer count = (Integer) executor.doSqlBatch(sqlParams, t);
        if(count>0){
            boolean isList = args.length > 0 && (args[0].getClass() == ArrayList.class || args[0].getClass() == List.class);
            if(isList){
                for(SqlParam obj:sqlParams){
                    models.add(obj.getModel());
                }
            }else{
                return sqlParams.get(0).getModel();
            }
            return  models;
        }
        return null;
    }
    @Override
    public Object executeSql(Object[] args,Class returnType, Class model) throws SQLException {
        List<Object> list=new ArrayList();
        if(args[1]  instanceof  List){
            list= (List<Object>) args[1];
        }else{
            Object [] ss= (Object[]) args[1];
            for(int x=0;x<ss.length;x++){
                list.add(ss[x]);
            }
        }
        return executor.executeSql(args[0].toString(), list,returnType,model);
    }
    /**
     * insert查询解析
     * @param t
     * @param method
     * @param args
     * @return
     */
    private   List<SqlParam>   translateInsertSql(Class t,Method method, Object[] args) {
        String  sql=method.getAnnotation(insert.class).value();
        List<SqlParam> sqlParam=null;
        BoundSql boundSql=null;
        //默认插入
        if(StringUtils.isEmpty(sql)){
            //组装插入字段 组装where条件
            boundSql=new BoundSql("");
        }else{//自定义sql查询
             boundSql = getBoundSql(sql);
        }
        sqlParam = AnalyseSql.insertSqlBatch(t, boundSql,args[0]);
        return sqlParam;
    }
    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     *
     * @param sql
     * @return
     */
    public static BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParamSqlTxtHandler handler = new ParamSqlTxtHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", handler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<String> list = handler.getFieldList();

        BoundSql boundSql = new BoundSql(parseSql, list);
        return boundSql;
    }
}
