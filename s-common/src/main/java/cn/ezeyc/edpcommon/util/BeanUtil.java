package cn.ezeyc.edpcommon.util;

import cn.ezeyc.edpcommon.pojo.Page;

import cn.ezeyc.edpcommon.annotation.dao.col;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
/**
 * @author wz
 */
public class BeanUtil {

    /**
     * 对象转map 值全部为字符串
     * @param obj
     * @return
     */
    public static Map<String, String> objectToMapString(Object obj){
        Class<?> clazz = obj.getClass();
        Field[] allFields = ClassUtil.getAllFields(clazz, col.class);
        Map<String, String> map = new HashMap<String,String>((int) ((allFields.length/0.75)+1));
        for (Field field :allFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object value = field.get(obj);
                if(value!=null){
                    if(value.getClass().isPrimitive()||value instanceof String||value instanceof Boolean||value instanceof Long){
                        map.put(fieldName+"",String.valueOf(value));
                    }else if(value.getClass().isArray()){
                        map.put(fieldName+"",  JSON.toJSON(value).toString());
                    }else if(value instanceof LocalDateTime){
                        map.put(fieldName+"",  ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }else if(value instanceof Page){
                        //不做处理
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }

    /**
     * 对象转map
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] allFields = ClassUtil.getAllFields(clazz, col.class);
        Map<String, Object> map = new HashMap<String,Object>((int) ((allFields.length/0.75)+1));
        for (Field field :allFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object value = field.get(obj);
                if(value!=null){
                    if(field.getType().isAssignableFrom(ModelBase.class)){
                        Map<String, Object> c = new HashMap<String,Object>((int) ((allFields.length/0.75)+1));
                        c.put(fieldName+"",objectToMapString(value));
                    }else{
                        map.put(fieldName+"",value);
                    }
                }else{
                    map.put(fieldName+"","");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }
    /**
     * 判断object是否为基本类型
     * @param c
     * @return
     */
    public static boolean isBaseType(Class c) {
        return c.isPrimitive() || c == String.class || c == Integer.class || c == Byte.class || c == Long.class || c == Double.class ||
                c == Float.class || c == Character.class || c == Short.class || c == Boolean.class;
    }
    /**
     * 判断object是否为基本类型
     * @param c
     * @return
     */
    public static boolean isBaseType(Type c) {
        return c == String.class || c == Integer.class || c == Byte.class || c == Long.class || c == Double.class ||
                c == Float.class || c == Character.class || c == Short.class || c == Boolean.class;
    }

    /**
     * map转实体
     * @param map
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        T t = null;
        JSONObject jsonObject = mapToJson(map);
        t = jsonObject.toJavaObject(clazz);
        return t;
    }

    /**
     * map转json
     * @param map
     * @return
     */
    public static JSONObject mapToJson(Map<String, Object> map) {
        String data = JSON.toJSONString(map);
        return JSON.parseObject(data);
    }

}
