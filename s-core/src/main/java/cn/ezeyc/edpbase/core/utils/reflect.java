package cn.ezeyc.edpbase.core.utils;

import com.alibaba.fastjson.JSONArray;
import cn.ezeyc.edpbase.util.StringUtil;
import cn.ezeyc.edpcommon.enums.BaseType;
import cn.ezeyc.edpcommon.error.ExRuntimeException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设置值
 */
public class reflect {
    /**
     * 范型
     */
    private static String T="T";
    /**
     * 范型
     */
    private static String Z="Z";
    /**
     * 基本属性设置
     * @param value
     * @param o
     * @param f
     * @throws IllegalAccessException
     */
    public static Object setNormal(Field f,Class c,Object o,Object value) throws IllegalAccessException {
        if(f!=null) f.setAccessible(true);
        if(c.isArray()&&value != null&&!"".equals(value)){
            if(value.getClass()==JSONArray.class){
                return setNormal(f,c.getComponentType(),o, StringUtil.jsonArrayConvert(c.getComponentType(),value));
            }else{
                return setNormal(f,c.getComponentType(),o, StringUtil.convertArray(c.getComponentType(),String.valueOf(value)));
            }
        }
        if(c==Integer.class|| BaseType.INTEGER.getValue().equals(c.getSimpleName())){
            if(value != null&&!"".equals(value)){
                if(value.getClass().isArray()){
                    if(f!=null) f.set(o,value);
                    return  value;
                }else{
                    if(f!=null) f.set(o,Integer.valueOf(String.valueOf(value)));
                    return Integer.valueOf(String.valueOf(value));
                }
            }else{
                if(f!=null) f.set(o,null);
            }
        }else  if(c==Long.class||BaseType.LONG.getValue().equals(c.getSimpleName())){
            if(value != null&&!"".equals(value)){
                if(value.getClass().isArray()){
                    if(f!=null)   f.set(o,value);
                    return  value;
                }else{
                    if(f!=null)  f.set(o,Long.valueOf(String.valueOf(value)));
                    return  Long.valueOf(String.valueOf(value));
                }
            }else{
                if(f!=null) f.set(o,null);
            }
        }else  if(c==Double.class||BaseType.DOUBLE.getValue().equals(c.getSimpleName())){
            if(value != null&&!"".equals(value)){
                if(value.getClass().isArray()){
                    if(f!=null)   f.set(o,value);
                    return  value;
                }else{
                    if(f!=null)   f.set(o,Double.valueOf(String.valueOf(value)));
                    return  Double.valueOf(String.valueOf(value));
                }
            }else{
                if(f!=null) f.set(o,null);
            }
        }else  if(c==Float.class||BaseType.FLOAT.getValue().equals(c.getSimpleName())){
            if(value != null&&!"".equals(value)){
                if(value.getClass().isArray()){
                    if(f!=null)  f.set(o,value);
                    return  value;
                }else{
                    if(f!=null)  f.set(o,Float.valueOf(String.valueOf(value)));
                    return  Float.valueOf(String.valueOf(value));
                }
            }else{
                if(f!=null) f.set(o,null);
            }
        }else if(c==Boolean.class||BaseType.BOOLEAN.getValue().equals(c.getSimpleName())){
            if(value != null&&!"".equals(value)){
                if(value.getClass().isArray()){
                    if(f!=null)   f.set(o,value);
                    return  value;
                }else{
                    if(f!=null)  f.set(o, Boolean.valueOf(String.valueOf(value)));
                    return  Boolean.valueOf(String.valueOf(value));
                }
            }else{
                if(f!=null)   f.set(o,null);
            }
        } else if(value != null&&!"".equals(value)&&c== Date.class){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date utcDate = null;
            try {
                utcDate = sdf.parse(String.valueOf(value));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(f!=null)  f.set(o, utcDate);
            return utcDate;
        }else if(value != null&&!"".equals(value)&&c==LocalDate.class){
            LocalDate localDate = setDate(f.getName(), "yyyy-MM-dd", value).toLocalDate();
            if(f!=null) f.set(o, localDate);
            return localDate;
        }else if(value != null&&!"".equals(value)&&c==LocalDateTime.class){
            if(String.valueOf(value).contains(Z)){
                LocalDateTime localDateTime = setDate(f.getName(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", value);
                if(f!=null)   f.set(o, localDateTime );
                return localDateTime;
            }else if(String.valueOf(value).contains(T)){
                DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime localDateTime = setDate(f.getName(), "yyyy-MM-dd'T'HH:mm:ss", dateTimeFormatter.format(LocalDateTime.parse(value.toString())));
                if(f!=null)   f.set(o,localDateTime);
                return localDateTime;
            }else {
                LocalDateTime localDateTime = setDate(f.getName(), "yyyy-MM-dd HH:mm:ss", value);
                if(f!=null) f.set(o,localDateTime);
                return localDateTime;
            }
        }else if(value != null&&!"".equals(value)&&c==LocalTime.class){
            LocalDateTime localDateTime = setDate(f.getName(),"HH:mm:ss",value);
            if(f!=null)  f.set(o,localDateTime.toLocalTime());
            return localDateTime.toLocalTime();
        }else  if(c== BigDecimal.class){
            if(value != null&&!"".equals(value)){
                if(value.getClass().isArray()){
                    if(f!=null)   f.set(o,value);
                    return  value;
                }else{
                    if(f!=null)  f.set(o, (BigDecimal)value);
                    return   (BigDecimal)value;
                }
            }else{
                if(f!=null) f.set(o,null);
            }
        }
        else if(c==String.class){
            try {
                if(value != null&&!"".equals(value)){
                    if(value.getClass().isArray()){
                        if(f!=null)  f.set(o,value);
                        return  value;
                    }else{
                        if(f!=null)  f.set(o, URLDecoder.decode(String.valueOf(value),"UTF-8"));
                        return  URLDecoder.decode(String.valueOf(value),"UTF-8");
                    }
                }else {
                    if(f!=null)  f.set(o,"");
                }
            } catch (UnsupportedEncodingException e) {
                throw  new ExRuntimeException(e.getMessage());
            }
        }
        return  null;
    }

    /**
     * 设置返回数组类型
     * @param list
     * @param returnType
     * @return
     */
    public static Object[] setArray(List list, Class returnType) {

        if(returnType.getComponentType()==Long.class|| BaseType.LONG.getValue().equals(returnType.getComponentType().getSimpleName())){
            //long
            List<Long> data= (List<Long>) list.stream().map(l -> Long.valueOf(l.toString())).collect(Collectors.toList());
            return data.toArray(new Long[data.size()]);
        }else if(returnType.getComponentType()==Double.class||BaseType.DOUBLE.getValue().equals(returnType.getComponentType().getSimpleName())){
            //Double
            List<Double> data= (List<Double>) list.stream().map(l -> Double.valueOf(l.toString())).collect(Collectors.toList());
            return data.toArray(new Double[data.size()]);
        }else if(returnType.getComponentType()==Integer.class||BaseType.INTEGER.getValue().equals(returnType.getComponentType().getSimpleName())){
            //Integer
            List<Integer> data= (List<Integer>) list.stream().map(l -> Integer.valueOf(l.toString())).collect(Collectors.toList());
            return data.toArray(new Integer[data.size()]);
        }else if(returnType.getComponentType()==Float.class||BaseType.FLOAT.getValue().equals(returnType.getComponentType().getSimpleName())){
            //Float
            List<Float> data= (List<Float>) list.stream().map(l -> Float.valueOf(l.toString())).collect(Collectors.toList());
            return data.toArray(new Float[data.size()]);
        }else if(returnType.getComponentType()==Boolean.class||BaseType.BOOLEAN.getValue().equals(returnType.getComponentType().getSimpleName())){
            //boolean
            List<Boolean> data= (List<Boolean>) list.stream().map(l -> Boolean.valueOf(l.toString())).collect(Collectors.toList());
            return data.toArray(new Float[data.size()]);
        }
        else {
            return list.toArray(new String[list.size()]);
        }
    }

    private static LocalDateTime setDate( String field, String format,Object value){
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Date utcDate = null;
        try {
            utcDate = sdf.parse(String.valueOf(value));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Instant instant = utcDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return  instant.atZone(zoneId).toLocalDateTime();
    }


}
