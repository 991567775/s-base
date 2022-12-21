package cn.ezeyc.edpbase.pojo.session;

import cn.ezeyc.edpbase.constant.SqlConst;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Query：
 *
 * @author: Administrator
 * @date: 2020年11月23日, 0023 13:37:03
 */
public class Query {
    private Logger logger= LoggerFactory.getLogger(Query.class);
    private final List<Object> param=new ArrayList<>();
    private final List<String> paramName=new ArrayList<>();
    private  List<String> whereSql=new ArrayList<>();


    private String orderSql="";
    private String groupSql="";
    public   List<String> getWhereSql(){
        return whereSql;
    }
    public   List<Object> getParam(){
        return param;
    }
    public   List<String> getParamName(){
        return paramName;
    }
    public    String getGroupSql(){
        return groupSql;
    }
    public    String getOrderSql(){
        return orderSql;
    }

    public  Boolean UnRemove=false;
    public    void setUnRemove(){
        UnRemove=true;
    }
    /**
     //     * 获取对应参数名位置
     //     * @param name
     //     * @return
     //     */
    public  Integer getParam(String name){
        for(int x=0;x<paramName.size();x++){
            if(name.equals(paramName.get(x))){
                return  x;
            }
        }
        return  -1;
    }
    public String  setRemove(){
      return   "remove=0";
    }
    /**
     * 等于
     * @param field
     * @param value
     * @return
     */
    public Query    eq(String field, Object value){
        return  setValue(field,value, SqlConst.eq);
    }
    /**
     * 不等于
     * @param field
     * @param value
     */
    public Query    ne(String field, Object value){
        return  setValue(field,value, SqlConst.ne);
    }
    /**
     * 大于
     * @param field
     * @param value
     */
    public Query    gt(String field, Object value){
        return  setValue(field,value, SqlConst.gt);
    }
    /**
     * 大于等于
     * @param field
     * @param value
     */
    public Query    ge(String field, Object value){
        return  setValue(field,value, SqlConst.ge);
    }
    /**
     * 小于
     * @param field
     * @param value
     */
    public Query    lt(String field, Object value){
        return  setValue(field,value, SqlConst.lt);
    }
    /**
     * 小于等于
     * @param field
     * @param value
     */
    public Query    le(String field, Object value){
        return  setValue(field,value, SqlConst.le);
    }
    /**
     *  like
     * @param field
     * @param value
     */
    public Query   like(String field, Object value){
        whereSql.add(field+ SqlConst.like+ SqlConst.val);
        param.add(SqlConst.likeS+value+ SqlConst.likeE);
        paramName.add(field);
        return this;
    }
    /**
     *  不like
     * @param field
     * @param value
     */
    public Query   notLike(String field, Object value){
        whereSql.add(field+ SqlConst.noLike+ SqlConst.val);
        param.add(SqlConst.likeS+value+ SqlConst.likeE);
        paramName.add(field);
        return this;

    }

    /**
     * 左like
     * @param field
     * @param value
     * @return
     */
    public Query   likeLeft(String field, Object value){
        whereSql.add(field+ SqlConst.like+ SqlConst.val);
        param.add(SqlConst.likeSl+value+ SqlConst.likeEl);
        paramName.add(field);
        return this;
    }

    /**
     * 右like
     * @param field
     * @param value
     * @return
     */
    public Query   likeRight(String field, Object value){
        whereSql.add(field+ SqlConst.like+ SqlConst.val);
        param.add(SqlConst.likeSr+value+ SqlConst.likeEr);
        paramName.add(field);
        return this;
    }
    /**
     * between
     * @param field
     * @param value
     * @param value1
     */
    public Query   between(String field, Object value, Object value1){
        whereSql.add(field+ SqlConst.between+ SqlConst.val+ SqlConst.and+ SqlConst.val);
        param.add(value);
        param.add(value1);
        paramName.add(field);
        return this;
    }
    /**
     * not between
     * @param field
     * @param value
     * @param value1
     */
    public Query   notBetween(String field, Object value, Object value1){
        whereSql.add(field+ SqlConst.noBetween+ SqlConst.val+ SqlConst.and+ SqlConst.val);
        param.add(value);
        param.add(value1);
        paramName.add(field);
        return this;
    }
    /**
     * in
     * @param field
     * @param value
     * @return
     */
    public Query   in(String field,Object ...value){
        String sql=field+" in (";
        return inAll(sql,field,value);
    }
    public Query   in(String field,List value){
        String sql=field+" in (";
        return inAll(sql,field,value);
    }

    /**
     * not in
     * @param field
     * @param value
     * @return
     */
    public Query   notIn(String field,Object ...value){
        String sql=field+" not in (";
        return inAll(sql,field,value);
    }
    public Query   notIn(String field,List value){
        String sql=field+" not in (";
        return inAll(sql,field,value);
    }

    /**
     * is null
     * @param field
     * @return
     */
    public Query   isNull(String field){
        whereSql.add(field+ SqlConst.isNull);
        return this;
    }

    /**
     * isNullOrEmpty
     * @param field
     * @return
     */
    public Query   isNullOrEmpty(String field){
        whereSql.add(SqlConst.kuoLeft+field+ SqlConst.isNull+ SqlConst.or+field + SqlConst.isEmpty+ SqlConst.kuoRight);
        return this;
    }

    /**
     * isNotNull
     * @param field
     * @return
     */
    public Query   isNotNull(String field){
        whereSql.add(field+ SqlConst.isNoNull);
        return this;
    }

    /**
     * v
     * @param field
     * @return
     */
    public Query   isEmpty(String field){
        whereSql.add(field+ SqlConst.isEmpty);
        return  this;
    }

    /**
     * isNotEmpty
     * @param field
     * @return
     */
    public Query   isNotEmpty(String field){
        whereSql.add(field+ SqlConst.isNoEmpty);
        return this;
    }

    /**
     * or
     * @return
     */
    public Query   or(){
        whereSql.add(SqlConst.or);
        param.add("!or!");
        paramName.add(SqlConst.or);
        return this;
    }

    public Query   orderByDesc(String ...field){
        if(orderSql.contains(SqlConst.orderBy)){
            orderSql+=","+ StringUtils.join(field, ZdConst.comma)+" desc";
        }else{
            orderSql+=" order by "+ StringUtils.join(field, ZdConst.comma)+" desc";
        }
        return this;
    }
    public Query   orderByAsc(String ...field){
        if(orderSql.contains(SqlConst.orderBy)){
            orderSql+=","+ StringUtils.join(field, ZdConst.comma)+" asc";
        }else{
            orderSql=" order by "+ StringUtils.join(field, ZdConst.comma)+" asc";
        }
        return this;
    }
    public Query   groupBy(String... field){
        groupSql=" group by "+ StringUtils.join(field, ZdConst.comma);
        return this;
    }
    public Query   having(String field){
        groupSql+=" having "+field;
        return this;
    }

    /**
     * 赋值
     * @param field
     * @param value
     * @param type
     * @return
     */
    private Query setValue(String field, Object value,String type){
        whereSql.add(field+ type+ SqlConst.val);
        param.add(value);
        paramName.add(field);
        return this;
    }

    /**
     * in 公用
     * @param sql
     * @param field
     * @param value
     * @return
     */
    private Query   inAll(String sql,String field,Object ...value){
        for(int x=0;x<value.length;x++){
            param.add(value[x]);
            if(x== value.length-1){
                sql+= SqlConst.val;
            }else{
                sql+= SqlConst.val+",";
            }
        }
        sql+=")";
        whereSql.add(sql);
        paramName.add(field);
        return this;
    }
    private Query   inAll(String sql,String field,List value){
        for(int x=0;x<value.size();x++){
            param.add(value.get(x));
            if(x== value.size()-1){
                sql+= SqlConst.val;
            }else{
                sql+= SqlConst.val+",";
            }
        }
        sql+=")";
        whereSql.add(sql);
        paramName.add(field);
        return this;
    }

}
