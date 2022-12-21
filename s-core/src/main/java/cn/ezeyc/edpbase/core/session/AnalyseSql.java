package cn.ezeyc.edpbase.core.session;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpbase.constant.SqlConst;
import cn.ezeyc.edpbase.core.utils.BoundSql;
import cn.ezeyc.edpbase.idgenerator.SnowflakeIdGenerator;
import cn.ezeyc.edpbase.interfaces.Interceptor;
import cn.ezeyc.edpbase.pojo.session.Query;
import cn.ezeyc.edpbase.pojo.session.SqlParam;
import cn.ezeyc.edpbase.util.BeanUtils;
import cn.ezeyc.edpbase.util.LoginUtil;
import cn.ezeyc.edpcommon.annotation.dao.col;
import cn.ezeyc.edpcommon.annotation.dao.param;
import cn.ezeyc.edpcommon.annotation.dao.pojo;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author wz
 */
public class AnalyseSql {
    private final static Logger logger= LoggerFactory.getLogger(AnalyseSql.class);
    private  static   Matcher m;
    private final static Pattern CHILDREN=Pattern.compile("\\(+?.*?select +?.*?from+?.*?where+?.*?\\)+\\s*(\\S+)\\s*");
    private final static Pattern CHILDREN_SEARCH=Pattern.compile("where?.*?\\(+?.*?select?.*?from?.*?where?.*?\\)");

    private static int limit=2;
    private static String limitParam="";
    private static boolean limitHasParam=true;

    private static List<Object> pages=new ArrayList<>();
    /**
     * 组装查询字段（默认查询）
     * @param t 实体class
     * @return
     */
     public static   String fieldSql(Class t){
        String field= SqlConst.select;
        pojo pojo= (pojo) t.getAnnotation(pojo.class);
        final Field[] fields=getAllFields(t);
        for(Field f:fields){
            if(f.isAnnotationPresent(col.class)){
                final col annotation = f.getAnnotation(col.class);
                if(annotation.select()){
                    field+=annotation.value();
                    if(StringUtils.isNotBlank(annotation.alias())){
                        field+= SqlConst.as+annotation.alias();
                    }
                    field+= SqlConst.comma;
                }
            }
        }
        field=field.substring(0,field.length()-1)+ SqlConst.from+pojo.value();
        return field;
    }
    /**
     * 自定义sql where解析
     * @param args
     * @param remove
     * @return
     */
    public  static WhereQuery whereSql(Parameter[] parameters, Object[] args, BoundSql boundSql, Boolean remove, Boolean isUser, Boolean isUpdate, Interceptor interceptor){
        //参数值对象 有顺序
        List<Object> params =new ArrayList<>();
        //分页对象
        Page pageLimit=null;
        //分组。排序
        StringBuffer orderSql=new StringBuffer();
        StringBuffer groupSql=new  StringBuffer();
        //sql 语句
        StringBuffer whereSql=new StringBuffer(boundSql.getSqlText());
        //是否自定义分页
        boolean isLimit=false;
        //判断是否定义分页limit
        if(whereSql.toString().replaceAll("\\s*", "").contains(SqlConst.limit)){
            pageLimit=new Page();
            String[] limits = whereSql.toString().split("limit");
            whereSql=new StringBuffer(limits[0]);
            if(limits[1].contains("?")){
                limit=limits[1].split("\\?").length+1;
            }else{
                limitParam=limits[1];
                limitHasParam=false;
            }
            isLimit=true;
        }
        //判断是否自己写where条件
        if(!hasWhere(whereSql)){
            whereSql.append(SqlConst.where);
        }
        //判断是否是join 查询
        boolean isJoin=false;
        if(whereSql.toString().toLowerCase().contains(SqlConst.join)){
            isJoin=true;
        }
        //当有 #{}时,参数值获取解析
        if(boundSql.getFieldList().size()>0){
            //当前args否被设置过。下次跳过
            int currentArgs=0;
            for (String fieldName:boundSql.getFieldList()){
                for(int x=0;x<args.length;x++){
                    Object o=args[x];
                    if(o!=null) {
                        //#{} 优先从query中获取
                        //根据名称获取参数值
                        if(o instanceof Query &&currentArgs<boundSql.getFieldList().size()){
                            final Integer index = ((Query) o).getParam(fieldName);
                            if(index!=-1){
                                final Object o1 = ((Query) o).getParam().get(index);
                                if(o1!=null){
                                    params .add(o1);
                                    currentArgs++;
                                    break;
                                }
                            }
                            //#{} 其次从实体中获取
                            //实体
                        } else  if (o instanceof ModelBase &&currentArgs<boundSql.getFieldList().size()) {
                            Field field = getFieldByName(o.getClass(),fieldName);
                            try {
                                if (field != null&&!field.getType().isArray()) {
                                    field.setAccessible(true);
                                    final Object o1 = field.get(o);
                                    pageLimit= ((ModelBase<?>) o).getPage();
                                    if(o1!=null&&StringUtils.isNotBlank(o1.toString())){
                                        params.add(o1);
                                        currentArgs++;
                                        break;
                                    }
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            //#{} 其次从数组中获取//数组
                        }else if(o.getClass().isArray()){
                            //todo

                            final param annotation = parameters[x].getAnnotation(param.class);
                            if(annotation!=null&&fieldName.equals(annotation.value())){
                                params.add(o);
                                currentArgs++;
                                break;
                            }
                            System.out.println(1);
                            //#{} 其次从基本类型中获取
                            //其他基本类型
                        }else  if(currentArgs<boundSql.getFieldList().size()){
                            final param annotation = parameters[x].getAnnotation(param.class);
                            if(annotation!=null&&fieldName.equals(annotation.value())){
                                params.add(o);
                                currentArgs++;
                                break;
                            }
                        }
                    }else {
                        logger.warn("参数传递的值为null");
                    }
                }
            }
        }
        if(params.size()!=boundSql.getFieldList().size()){
            logger.error("自定义sql#{field}参数值未设置");
            throw new ExRuntimeException("自定义sql#{field}参数值未设置");
        }
        //
        if(isLimit&&limitHasParam){
            //limit 前面的问号个数
            final Integer index = index( whereSql.toString().split("limit ")[0], "?");
            pages.add(Long.valueOf(params.get(index).toString()));
            params.remove(params.get(index));
            if(limit==4){
                pages.add(Long.valueOf(params.get(index).toString()));
                params.remove(params.get(index));
            }
        }
        //然后额外追加条件以及追加值解析
        pageLimit=queryAdd(args,whereSql,interceptor,remove,isJoin,params,pageLimit,orderSql,groupSql,isUser);
        //拼接
        whereSql.append(orderSql.toString()+groupSql.toString());
        //自定义分页
        limitPage(isLimit,whereSql,params,pageLimit);

        //重置分页

        return   new WhereQuery(whereSql.toString(),params,pageLimit);
    }

    /**
     * query 参数追加
     * @param args
     * @param whereSql
     * @param interceptor
     * @param remove
     * @param isJoin
     * @param params
     * @param pageLimit
     * @param orderSql
     * @param groupSql
     * @param isUser
     */
    private  static Page queryAdd(Object[] args, StringBuffer whereSql, Interceptor interceptor, Boolean remove, boolean isJoin, List params, Page pageLimit, StringBuffer orderSql, StringBuffer groupSql, Boolean isUser){
        for(Object o:args){
            if(o!=null){
                //query 类型参数追加
                if(o instanceof  Query) {
                    //追加是否删除条件
                    boolean re=true;
                    for(String s:interceptor.getNoRemoveTable()){
                        if(whereSql.toString().contains(s)){
                            re=false;  break;
                        }
                    }
                    boolean isRemove=re&&(remove==null||remove);
                    if(isRemove&&!((Query) o).UnRemove){
                        if(isJoin){
                            whereSql.append(SqlConst.and+ SqlConst.aliasJoin+((Query) o).setRemove()) ;
                        }else{
                            whereSql.append(SqlConst.and+((Query) o).setRemove()) ;
                        }
                    }

                    //数据权限添加
                    addAuth(whereSql,interceptor);
                    //关联查询追加参数别名
                    join(isJoin,o,whereSql,orderSql,groupSql,params);
                    break;
                }else if(o instanceof  ModelBase||o instanceof Page){
                    //分页
                    pageLimit= ((ModelBase<?>) o).getPage();
                    if(pageLimit==null){
                        pageLimit=new Page();
                    }
                    //数组类型参数追加
                }else if(o.getClass().isArray()){
                    //数组

                }else {//普通值
                    //默认方法
                    if(!isUser){
                        //根据ids in 删除
                        if(o instanceof Long){
                            whereSql.append("and id=?");
                            params.add(o);

                        }else{
                            //long 类型id
                            final String[] split = ((String) o).split(SqlConst.comma);
                            whereSql.append("and id in (");
                            for(int x=0; x<split.length;x++){
                                if(x==split.length-1){
                                    whereSql.append(SqlConst.val) ;
                                }else{
                                    whereSql.append(SqlConst.val+ SqlConst.comma) ;
                                }
                                params.add(split[x]);
                            }
                            whereSql.append(SqlConst.kuoRight);
                        }
                    }
                }
            }else {
                logger.warn("参数传递的值为null");
            }
        }
        return pageLimit;
    }
    private  static  String dataCode="data",dataType1="1",dataType2="2",dataType3="3",dataType4="4";
    /**
     * 数据权限sal 追加
     * @param whereSql
     * @param interceptor
     * @return
     */
    private static void addAuth(StringBuffer whereSql,Interceptor interceptor) {
        if(interceptor.getAllAuth()&&interceptor.isDataAuth()){
            JSONObject user = interceptor.getUser();
            if(user!=null&&user.getJSONArray(dataCode)!=null){
                JSONArray data = user.getJSONArray(dataCode);
                //通用数据放行
                boolean isAuth=true;
                for(String s:interceptor.getNoAuthTable()){
                    if(whereSql.toString().contains(s)){
                        isAuth=false;
                        break;
                    }
                }
                if(isAuth){
                    if(data.contains(dataType4)){
                        //个人
                        whereSql.append( SqlConst.and+"( create_user="+user.getLong("id")+" or data_code is null )");
                    }else{
                        JSONArray code=null;
                        if(data.contains(dataType1)){
                            //公司级别
                            code= user.getJSONArray("companyCode");

                        }else if(data.contains(dataType2)){
                            //部门
                            code= user.getJSONArray("departCode");

                        }else if(data.contains(dataType3)){
                            //岗位
                            code = user.getJSONArray("stationCode");
                        }
                        if(code!=null&&code.size()>0){
                            String qx= SqlConst.and+"( data_code is null ";
                            for(int x=0;x<code.size();x++){
                                qx+=" or find_in_set('"+code.get(x)+"', data_code)>0 ";
                            }
                            whereSql.append(qx+" ) ");
                        }
                    }
                }
            }
        }
    }

    private static void join(boolean isJoin,Object o,StringBuffer whereSql,StringBuffer orderSql,StringBuffer groupSql,List params){
        List<String> whereSqlQuery = ((Query) o).getWhereSql();
        if(isJoin){
            List<String> whereSqlQueryNew =new ArrayList<>();
            for(String q:whereSqlQuery){
                whereSqlQueryNew.add(q);
            }
            whereSqlQuery=whereSqlQueryNew;
        }
        List<Object> param = ((Query) o).getParam();
        List<String> outSql =new ArrayList<>();
        List<Object> outParam =new ArrayList<>();
        //上一次记录
        String backSql=whereSql.toString();
        int count=0;
        for(int x=0;x<whereSqlQuery.size();x++){
            //or判断
            if(whereSqlQuery.get(x).equals(SqlConst.or)){
                if(whereSqlQuery.size()>2){
                    backSql+=SqlConst.and+"("+whereSqlQuery.get(x-1)+ SqlConst.or+whereSqlQuery.get(x+1)+")";
                    //清除原来字符串
                    whereSql.delete(0,whereSql.length());
                    //重新append
                    whereSql.append(backSql);
                    //移除or值
                    param.remove("!"+whereSqlQuery.get(x).trim()+"!");
                    x++;
                }else{
                    throw new ExRuntimeException("or前后需要值");
                }
            }else{
                backSql=whereSql.toString();
                whereSql .append(SqlConst.and+ whereSqlQuery.get(x)) ;
                //判断该条件是否是子查询别名 ,如果是则记录
                if(StringUtils.isNotBlank(getChildrenSqlField(whereSql.toString()))){
                    outParam.add(param.get(x));
                    outSql.add(whereSqlQuery.get(x));
                    //记录值
                    count++;
                    //清除原来字符串
                    whereSql.delete(0,whereSql.length());
                    //重新append
                    whereSql.append(backSql);
                }
            }
        }
        //移除已使用的
        for(int x=0;x<count;x++){
            param.remove(0);
        }
        orderSql.append(((Query) o).getOrderSql());
        groupSql.append(((Query) o).getGroupSql());
        params .addAll(param);
        //子查询外部
        if(outSql.size()>0){
            //包裹原sql
            String old=whereSql.toString();
            //清除原来字符串
            whereSql.delete(0,whereSql.length());
            //重新append
            whereSql.append(SqlConst.select+ SqlConst.aliasXing+ SqlConst.from+ SqlConst.kuoLeft+old+ SqlConst.kuoRight+ SqlConst.alias+ SqlConst.where) ;
            backSql=whereSql.toString();
            for(int x=0;x<outSql.size();x++){
                //or判断
                if(outSql.get(x).equals(SqlConst.or)){
                    if(outSql.size()>2){
                        backSql+=SqlConst.and+"("+outSql.get(x-1)+ SqlConst.or+outSql.get(x+1)+")" ;
                        //清除原来字符串
                        whereSql.delete(0,whereSql.length());
                        whereSql.append(backSql);
                        //移除or值
                        outParam.remove(x);
                        x++;
                    }else{
                        throw new ExRuntimeException("or前后需要值");
                    }
                }else{
                    //清除原来字符串
                    whereSql.delete(0,whereSql.length());
                    whereSql.append(backSql);
                    whereSql .append(SqlConst.and+ outSql.get(x));
                    backSql= String.valueOf(whereSql);
                }
            }
        }
        params.addAll(outParam);
    }
    /**
     * 自定义分页处理
     * @param isLimit
     * @param whereSql
     * @param params
     * @param pageLimit
     * @return
     */
    private static  void limitPage(boolean isLimit,StringBuffer whereSql,List params,Page pageLimit){
        //自定义分页的处理
        if(isLimit){
            String old="";
            //分页传参数
            if(limitHasParam){
                if(limit==4){
                    whereSql.append(" limit "+pages.get(0)+","+pages.get(1));
                }else{
                    whereSql.append(" limit "+pages.get(0));
                }
            }else{ //分页不传参数
                whereSql.append(" limit "+limitParam);
            }
        }
    }

    /**
     * 组装更新字段默认更新
     * @return
     */
    public static   String updateSqlField(String sql,Object[] args,boolean isnull){
        Object t=null;
        for(Object o:args){
            if(o instanceof  ModelBase){
                t=o;
            }
        }
        if(StringUtils.isBlank(sql)){
            pojo pojo=  t.getClass().getAnnotation(pojo.class);
            sql= SqlConst.update+pojo.value()+ SqlConst.set;
        }else{
            if(!sql.contains(SqlConst.set)){
                sql+= SqlConst.set;
            }else{
                sql+= SqlConst.comma;
            }
        }

        final Field[] pFields =getAllFields(t.getClass());
        for(Field f:pFields){
            f.setAccessible(true);
            try {
                if(f.isAnnotationPresent(col.class)){
                    if(isnull){
                        final col annotation = f.getAnnotation(col.class);
                        sql+=annotation.value()+ SqlConst.eq+ SqlConst.val+ SqlConst.comma;
                    }else{
                        if(f.get(t)!=null&&StringUtils.isNotBlank(f.get(t).toString())){
                            final col annotation = f.getAnnotation(col.class);
                            sql+=annotation.value()+ SqlConst.eq+ SqlConst.val+ SqlConst.comma;
                        }
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return  sql.substring(0,sql.length()-1);
    }
    /**
     * 组装删除字段
     * @param t
     * @return
     */
    public static   String delSql(Class t){
        pojo pojo= (pojo) t.getAnnotation(pojo.class);
        return  SqlConst.delete+pojo.value()+ SqlConst.set +" remove=true ";

    }
    /**
     * 组装插入字段以及值
     * @param t
     * @return
     */
    private static SqlParam insertSql(Class t, Object args, BoundSql boundSql, Boolean isBatch){
        //拷贝参数对象
        Object o = BeanUtils.copy(args);
        pojo pojo= (pojo) t.getAnnotation(pojo.class);
        Long  id=null;
        String sql= boundSql.getSqlText();
        Boolean isDefault=false;
        if(StringUtils.isBlank(sql)){
            sql= SqlConst.insert+pojo.value()+ SqlConst.kuoLeft;
            isDefault=true;
        }else if(!sql.contains(SqlConst.id)){
            throw new ExRuntimeException("id为主键，必须要插入");
        }
        List<Object> list=new ArrayList<>();
        final Field[] pFields =getAllFields(t);
        StringBuffer field=new StringBuffer();
        StringBuffer  fieldVal=new StringBuffer();
        //自定义参数序列

        id=setField(pFields,boundSql,o,id,list,isBatch,field,fieldVal);
        ((ModelBase<?>) o).setId(id);
        if(isDefault){
            if(!isBatch){
                sql+=field.substring(0,field.length()-1)+ SqlConst.kuoRight+ SqlConst.values+fieldVal.substring(0,fieldVal.length()-1)+ SqlConst.kuoRight;
                return  new SqlParam(sql,list,o);
            } else {
                return  new SqlParam(null,list,o);
            }
        }else {
            if(!isBatch){
                return  new SqlParam(sql,list,o);
            } else {
                return  new SqlParam(null,list,o);
            }
        }
    }

    /**
     * 设置字段值
     * @param pFields
     * @param boundSql
     * @param o
     * @param id
     * @param list
     * @param isBatch
     * @param field
     * @param fieldVal
     * @return
     */
    private static Long  setField(Field[] pFields,BoundSql boundSql,Object o,Long id,List list,Boolean isBatch,StringBuffer field,StringBuffer fieldVal){
        int currentArgs=0;
        for(Field f:pFields){
            if(f.isAnnotationPresent(col.class)){
                final col annotation = f.getAnnotation(col.class);
                final Object fieldObj = getFieldObjNoRemove(f, o);
                if(fieldObj!=null){
                    //判断自定义sql 插入字段
                    if(boundSql.getFieldList().size()>0){
                        for(String fieldName:boundSql.getFieldList()){
                            if(fieldName.equals(SqlConst.id)&&currentArgs<boundSql.getFieldList().size()){
                                id= SnowflakeIdGenerator.nextId();
                                list.add(id);
                                currentArgs++;
                            }else    if(fieldName.equals(annotation.value())&&currentArgs<boundSql.getFieldList().size()){
                                //判断是否是数组存入
                                if(fieldObj.getClass().isArray()){
                                    list.add(ArrayUtils.toString(fieldObj, SqlConst.comma).replace("{","").replace("}",""));
                                }else {
                                    list.add(fieldObj);
                                }
                                currentArgs++;
                            }
                        }
                    }else{
                        if(!isBatch){
                            field.append(annotation.value()+ SqlConst.comma);
                            fieldVal.append(SqlConst.val+ SqlConst.comma) ;
                        }
                        //判断是否是数组存入
                        if(fieldObj.getClass().isArray()){
                            list.add(ArrayUtils.toString(fieldObj, SqlConst.comma).replace("{","").replace("}",""));
                        }else {
                            list.add(fieldObj);
                        }
                    }
                }else if(f.getName().equals(ZdConst.CREATE_DATE)){
                    //创建时间
                    if(!isBatch){
                        field.append(annotation.value()+ SqlConst.comma);
                        fieldVal.append(SqlConst.val+ SqlConst.comma) ;
                    }
                    list.add(LocalDateTime.now());
                }else if(f.getName().equals(ZdConst.CREATE_USER)){
                    //创建人
                    if(!isBatch){
                        field.append(annotation.value()+ SqlConst.comma);
                        fieldVal.append(SqlConst.val+ SqlConst.comma) ;
                    }
                    list.add(LoginUtil.getCurrentLoginId());
                }else if(annotation.value().equals(SqlConst.id)){
                    //自定义插入跳过id
                    if(boundSql.getFieldList().size()>0){
                        continue;
                    }
                    if(!isBatch){
                        field.append(annotation.value()+ SqlConst.comma);
                        fieldVal.append(SqlConst.val+ SqlConst.comma) ;
                    }
                    id=SnowflakeIdGenerator.nextId();
                    list.add(id);
                }
            }
        }
        return id;
    }
    /**
     * 是否批量插入
     * @param t
     * @param o
     * @return
     */
    public static List<SqlParam> insertSqlBatch(Class t,BoundSql boundSql, Object o){
        List<SqlParam> list=new ArrayList<>();
        if(o instanceof List){
            for(int x=0;x<((List<?>) o).size();x++){
                if(x==0){
                    list.add(insertSql(t,((List<?>) o).get(x),boundSql,false)) ;
                }else {
                    list.add(insertSql(t,((List<?>) o).get(x),boundSql,true)) ;
                }
            }
        }else{
         list.add(insertSql(t,o,boundSql,false)) ;
        }
        return  list;
    }

    /**
     * 更新参数转参数
     * @param args
     * @return
     */
    public static   List<Object> updateSqlParam(Object[] args,boolean isnull,Interceptor interceptor){
        List<Object> params =null;
        for(Object o:args){
            if(o!=null){
                if(o instanceof  ModelBase){
                    //获取参数
                    if(isnull){
                        params=modelToParam(o,interceptor);
                    }else{
                        params=modelToParamNotNull(o,interceptor);
                    }
                }
            }
        }
        return  params;
    }

    /**
     * 获取全部字段（包括父类）
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(Class clazz){
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            final Field[] declaredFields = clazz.getDeclaredFields();
            for(Field field:declaredFields){
                if (field.isAnnotationPresent(col.class)){
                    fieldList.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }


    /**
     * 获取字段值（包括父类）
     * @param f
     * @return
     */
    public static Object getFieldObjNoRemove(Field f,Object o){
        Object obj=null;
        try {
            f.setAccessible(true);
            obj=f.get(o);
            //设置删除默认值Boolean 为false
            if(f.getType()== Boolean.class&&obj==null&& SqlConst.remove.equals(f.getName())){
                obj=false;
            }
        } catch (IllegalAccessException e) { }
        return obj;
    }
    /**
     * 获取字段值（包括父类）
     * @param f
     * @return
     */
    public static Object getFieldObj(Field f,Object o){
        Object obj=null;
        try {
            f.setAccessible(true);
            obj=f.get(o);
        } catch (IllegalAccessException e) { }
        return obj;
    }

    /**
     * 根据名称获取field（包括父类）
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getFieldByName(Class clazz,String fieldName){
        Field field=null;
        while (clazz != null){
            try {
                field = clazz.getDeclaredField(fieldName);
                if(field!=null){
                    return field;
                }
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
    /**
     * 实体转参数（全部，可为空）
     * @param o
     * @return
     */
    private static   List<Object> modelToParam(Object o,Interceptor interceptor){
        List<Object> params =new ArrayList<>();
        final Field[] allFields = getAllFields(o.getClass());
        for(Field f:allFields){
            if(f.isAnnotationPresent(col.class)){
                final Object fieldObjNoRemove = getFieldObjNoRemove(f, o);
                //判断是否是数组
                if(fieldObjNoRemove!=null&&fieldObjNoRemove.getClass().isArray()){
                    params.add(ArrayUtils.toString(fieldObjNoRemove, SqlConst.comma).replace("{","").replace("}",""));
                }else if(f.getName().equals(ZdConst.UPDATE_DATE)){
                    //更新时间
                    params.add(LocalDateTime.now());
                }else if(f.getName().equals(ZdConst.UPDATE_USER)){
                    //更新人
                    params.add(LoginUtil.getCurrentLoginId());
                }else if(ZdConst.DATA_CODE.equals(f.getName())){
                    //权限设置
                    List<String> strings=new ArrayList<>();
                        JSONObject user = interceptor.getUser();
                        if(user!=null){
                            JSONArray companyCode = user.getJSONArray("companyCode");
                            if(companyCode!=null&&companyCode.size()>0){
                                strings= companyCode.toJavaList(String.class);
                                JSONArray departCode = user.getJSONArray("departCode");
                                if(departCode!=null&&departCode.size()>0){
                                    strings.addAll(departCode.toJavaList(String.class));
                                    JSONArray stationCode = user.getJSONArray("stationCode");
                                    if(stationCode!=null&&stationCode.size()>0){
                                        strings.addAll(stationCode.toJavaList(String.class));
                                    }
                                }
                            }
                        }
                    params.add(StringUtils.join(strings, ZdConst.comma));
                }else {
                    params.add(fieldObjNoRemove);
                }
            }

        }
        return  params;
    }

    /**
     * 实体转参数(不为空)
     * @param o
     * @return
     */
   private static   List<Object> modelToParamNotNull(Object o,Interceptor interceptor){
        List<Object> params =new ArrayList<>();
        final Field[] allFields = getAllFields(o.getClass());
        for(Field f:allFields){
            if(f.isAnnotationPresent(col.class)){
                final Object fieldObj = getFieldObj(f, o);
                if(fieldObj!=null&&StringUtils.isNotBlank(fieldObj.toString())){
                    //判断是否是数组
                    if(fieldObj.getClass().isArray()){
                        params.add(ArrayUtils.toString(fieldObj, SqlConst.comma).replace("{","").replace("}",""));
                    }else if(f.getName().equals(ZdConst.UPDATE_DATE)){
                        //更新时间
                        params.add(LocalDateTime.now());
                    }else if(f.getName().equals(ZdConst.UPDATE_USER)){
                        //更新人
                        params.add(LoginUtil.getCurrentLoginId());
                    }else if(ZdConst.DATA_CODE.equals(f.getName())){
                        //权限设置
                        List<String> strings=new ArrayList<>();
                        JSONObject user = interceptor.getUser();
                        if(user!=null){
                            JSONArray companyCode = user.getJSONArray("companyCode");
                            if(companyCode!=null&&companyCode.size()>0){
                                strings= companyCode.toJavaList(String.class);
                                JSONArray departCode = user.getJSONArray("departCode");
                                if(departCode!=null&&departCode.size()>0){
                                    strings.addAll(departCode.toJavaList(String.class));
                                    JSONArray stationCode = user.getJSONArray("stationCode");
                                    if(stationCode!=null&&stationCode.size()>0){
                                        strings.addAll(stationCode.toJavaList(String.class));
                                    }
                                }
                            }
                        }
                        params.add(StringUtils.join(strings, ZdConst.comma));
                    } else{
                        params.add(fieldObj);
                    }
                }
            }

        }
        return  params;
    }

    /**
     * 获取子查询别名
     * @return
     */

    public static  String getChildrenSqlField(String sql){
         m= CHILDREN.matcher(sql.trim().toLowerCase());
        String childrenSql="";
        while(m.find()){
            childrenSql=m.group().trim();
            break;
        }
        if(StringUtils.isNotBlank(childrenSql)){
            childrenSql=  childrenSql.substring(childrenSql.lastIndexOf(" "));
        }
        return childrenSql.trim();
    }

    /**
     * 获取子查询语句
     * @param sql
     * @return
     */

    public static  String getChildrenSql(String sql){
        m= CHILDREN.matcher(sql.trim().toLowerCase());
        String childrenSql="";
        while(m.find()){
            return  m.group().trim();
        }
        return "";
    }

    /**
     * 是否是字段 子查询
     * @param sql
     * @return
     */
    public static  boolean isChildrenSql(String sql){
         m= CHILDREN.matcher(sql.trim().toLowerCase());
        while(m.find()){
            return true;
        }
        return false;
    }

    /**
     * where 子查询
     * @param sql
     * @return
     */
    public static  String isWhereChildrenSql(String sql){
         m= CHILDREN_SEARCH.matcher(sql.trim().toLowerCase());
        String childrenSql="";
        while(m.find()){
            childrenSql=m.group().trim();
        }
        return childrenSql.trim();
    }

    /**
     * 判断用户是否写自己的where
     * @param sql
     * @return
     */
    public  static boolean hasWhere(StringBuffer sql){
        //where 条件子查询
        if(StringUtils.isNotBlank(isWhereChildrenSql(sql.toString().toLowerCase()))){
            return true;
        }
        //字段子查询
        while (isChildrenSql(sql.toString().toLowerCase())){
            sql=new StringBuffer(sql.toString().toLowerCase().replace(getChildrenSql(sql.toString().toLowerCase()),""));
        }
        //普通查询
        if(sql.toString().toLowerCase().contains(SqlConst.hasWhere)){
            return true;
        }
       return false;
    }
    private  static Integer index(String sql,String val){
        int count = 0;

        int origialLength = sql.length();
        sql = sql.replace(val, "");
        int newLength = sql.length();

        count = origialLength - newLength;

        return count;
    }




}
