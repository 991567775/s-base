package cn.ezeyc.edpbase.core.session;


import cn.ezeyc.edpbase.core.utils.reflect;
import cn.ezeyc.edpbase.util.StringUtil;
import cn.ezeyc.edpcommon.annotation.dao.col;
import cn.ezeyc.edpcommon.annotation.dao.pojo;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * ResultSetDao：
 * 数据库查询结果集封装
 * @author: wz
 * @date: 2020年8月10日, 0010 10:38:55
 */
@configuration
public class SetResult<T> {
    private final Logger logger= LoggerFactory.getLogger(SetResult.class);

    public <T> Object toResult(ResultSet rs,Class returnType,Class model) {
        if (rs != null) {
            ResultSetMetaData rsmd = null;
            try {
                rsmd = rs.getMetaData();
                //list 集合
                if (returnType==List.class||returnType== Page.class) {
                    List list=new ArrayList();
                    //list中为pojo实体
                    if (model.isAnnotationPresent(pojo.class)) {
                        list= (List) setPojo(rsmd,rs,model,list);
                    }else if(model ==Map.class ){
                        Map m=null;
                        while (rs.next()) {
                            m=new HashMap();
                            for (int x = 0; x < rsmd.getColumnCount(); x++) {
                                m.put(rsmd.getColumnLabel(x+1), rs.getObject(x + 1));
                            }
                            list.add( m);
                        }
                    } else {
                        //list中为其他类型：基本类型、数组等
                        while (rs.next()) {
                            for (int x = 0; x < rsmd.getColumnCount(); x++) {
                                Object columnValue = rs.getObject(x + 1);
                                list.add( columnValue);
                            }
                        }
                    }
                    return list;
                    //返回类型为pojo实体
                } else if (returnType == ModelBase.class ||returnType.isAnnotationPresent(pojo.class)) {
                    return setPojo(rsmd,rs,model,null);
                   //map类型
                }else if(returnType ==Map.class ){
                    Map m=null;
                    while (rs.next()) {
                        m=new HashMap();
                        for (int x = 0; x < rsmd.getColumnCount(); x++) {
                            m.put(rsmd.getColumnLabel(x+1), rs.getObject(x + 1));
                        }
                    }
                    return  m;
                    //数组类型
                }else if(returnType.isArray()){
                    List  list=new ArrayList();
                    while (rs.next()) {
                        for (int x = 0; x < rsmd.getColumnCount(); x++) {
                            Object columnValue = rs.getObject(x + 1);
                            if(columnValue!=null&&!"".equals(columnValue)){
                                List temp=new ArrayList(Arrays.asList(columnValue.toString().split(ZdConst.comma)));
                                list.addAll(temp);
                            }
                        }
                    }
                    return  reflect.setArray(list,returnType);

                }else{
                    //返回其他基本类型
                   return  setNormal(rs,rsmd,returnType);
                }
            } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error(e.getMessage());
            }
        }
        return  null;
    }

    /**
     * 设置返回值
     * @param rs
     * @param rsmd
     * @param returnType
     * @return
     * @throws SQLException
     */
    private static Object setNormal(ResultSet rs, ResultSetMetaData rsmd,Class returnType) throws SQLException, IllegalAccessException {
        while (rs.next()) {
            for (int x = 0;x< rsmd.getColumnCount(); x++) {
                return reflect.setNormal(null, returnType, null, rs.getObject(x + 1));
            }
        }
        return null;
    }

    /**
     * 设置返回实体或实体list
     * @param rsmd
     * @param rs
     * @param model
     * @return
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private  Object setPojo(ResultSetMetaData rsmd,ResultSet rs,Class model,List list) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Field[] fields = model.getDeclaredFields();
        Field[] fieldsP= model.getSuperclass().getDeclaredFields();
        while (rs.next()) {
            T bean = (T) model.getDeclaredConstructor().newInstance();
            for (int x = 0; x < rsmd.getColumnCount(); x++) {
                String columnName = rsmd.getColumnLabel(x + 1);
                Object columnValue = rs.getObject(x + 1);
                for (Field field : fields) {
                    //判断col数据库字符跟实体字段名称是否一致
                    if(field.getName().equals(columnName)||
                            (field.isAnnotationPresent(col.class)&&field.getAnnotation(col.class).value().equals(columnName))
                    ){
                        setField(bean,field, field.getName(),columnValue);
                    }
                }
                for (Field field : fieldsP) {
                    if(field.getName().equals(columnName)||
                        (field.isAnnotationPresent(col.class)&&field.getAnnotation(col.class).value().equals(columnName))
                    ){
                        setField(bean,field, field.getName(),columnValue);
                    }
                }
            }
            if(list==null){
                return  bean;
            }else{
                list.add(bean);
            }
        }
        return list;
    }
    /**
     * sql查询pojo实体赋值
     * @param bean
     * @param field
     * @param columnName
     * @param columnValue
     * @throws IllegalAccessException
     */
    private static void setField(Object bean,Field field, String columnName,Object columnValue) throws IllegalAccessException {
        if ( field.getName().equalsIgnoreCase(StringUtil.lineToHump(columnName)) ) {
                reflect.setNormal(field,field.getType(),bean,columnValue);
        }
    }
}

