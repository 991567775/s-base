package cn.ezeyc.edpbase.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassNameUtil：
 *
 * @author: Administrator
 * @date: 2020年11月19日, 0019 10:55:36
 */
public class StringUtil {

    /**
     * 判断是否是ip
     */
    public  static boolean ip(String ip){
        String regex = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
        return ip.matches(regex);
    }
    /**
     * 判断是否是ip
     */
    public  static boolean number(String num){
        String regex = "^[1-9]+[0-9]*$";
        return num.matches(regex);
    }
    /**
     * 转数组
     */
    public static Object[] convertArray(Class c,String value){
        if(c==Long.class){
            Long[] intArr = new Long[0];
            if(StringUtils.isBlank(value)){
                intArr = new Long[0];
            }else{
                String[] valueArr = value.split(",");
                intArr = new Long[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    intArr[i] = Long.parseLong(valueArr[i]);
                }
            }
            return intArr;
        }else if(c==Integer.class){
            Integer[] intArr = new Integer[0];
            if(StringUtils.isBlank(value)){
                intArr = new Integer[0];
            }else{
                String[] valueArr = value.split(",");
                intArr = new Integer[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    intArr[i] = Integer.parseInt(valueArr[i]);
                }
            }
            return intArr;
        }else if(c==Double.class){
            Double[] intArr = new Double[0];
            if(StringUtils.isBlank(value)){
                intArr = new Double[0];
            }else{
                String[] valueArr = value.split(",");
                intArr = new Double[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    intArr[i] = Double.parseDouble(valueArr[i]);
                }
            }
            return intArr;
        }else if(c==Float.class){
            Float[] intArr = new Float[0];
            if(StringUtils.isBlank(value)){
                intArr = new Float[0];
            }else{
                String[] valueArr = value.split(",");
                intArr = new Float[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    intArr[i] = Float.parseFloat(valueArr[i]);
                }
            }
            return intArr;
        }else {
            String[] valueArr = value.split(",");
            return  valueArr;
        }
    }



    public static Object[] jsonArrayConvert(Class c,Object object){
        if(c==Long.class){
            if(object!=null&&!"".equals(object)){
                List list =new ArrayList();
                if(object instanceof Long||object instanceof Integer){
                    list.add(object);
                }else if(object!=""){
                    list = JSONArray.parseArray(JSONObject.toJSONString(object));
                }
                Long[] ids = new Long[list.size()];
                for(int i = 0;i<list.size();i++){
                    ids[i] = Long.parseLong(list.get(i).toString());
                }
                return ids;
            }else{
                return null;
            }
        }else if(c==Integer.class){
            List list =new ArrayList();
            if(object instanceof Integer){
                list.add(object);
            }else if(object!=""){
                list = JSONArray.parseArray(JSONObject.toJSONString(object));
            }
            Integer[] ids = new Integer[list.size()];
            for(int i = 0;i<list.size();i++){
                ids[i] = Integer.parseInt(list.get(i).toString()) ;
            }
            return ids;
        }else if(c==Double.class){
            List list =new ArrayList();
            if(object instanceof Double){
                list.add(object);
            }else if(object!=""){
                list = JSONArray.parseArray(JSONObject.toJSONString(object));
            }
            Double[] ids = new Double[list.size()];
            for(int i = 0;i<list.size();i++){
                ids[i] = Double.parseDouble(list.get(i).toString()) ;
            }
            return ids;
        }else if(c==Float.class){
            List list =new ArrayList();
            if(object instanceof Float){
                list.add(object);
            }else if(object!=""){
                list = JSONArray.parseArray(JSONObject.toJSONString(object));
            }
            Float[] ids = new Float[list.size()];
            for(int i = 0;i<list.size();i++){
                ids[i] = Float.parseFloat(list.get(i).toString()) ;
            }
            return ids;
        }else {
            List list =new ArrayList();
            if(object instanceof String){
                list.add(object);
            }else if(object!=""){
                list = JSONArray.parseArray(JSONObject.toJSONString(object));
            }
            String[] ids = new String[list.size()];
            for(int i = 0;i<list.size();i++){
                if(list.get(i)!=null){
                    try {
                        ids[i] =  URLDecoder.decode(String.valueOf(list.get(i).toString()),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new ExRuntimeException(e.getMessage());
                    }
                }
            }
            return ids;
        }
    }




    /**
     * 非字符串数组转字符串
     * @param arr
     * @return
     */
    public static String arrayToString(Object[] arr) {
        //定义一个内容为"["的StringBuffer的缓冲区
        StringBuffer sb = new StringBuffer();
        //进行数组的遍历，以及转换为StringBuffer缓冲区
        for(int x=0;x<arr.length;x++){
            if(x == arr.length-1){
                sb.append(arr[x]);
            }else{
                sb.append(arr[x]).append(",");
            }
        }
        return sb.toString();
    }


    /**
     * 首字母转小写
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 首字母转大写
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 下划线转驼峰
     */
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    /**
     * 驼峰转下划线,效率比上面高
     */
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取文件类型后缀
     * @param file
     * @return
     */
    public static String getFileType(File file) {
        return file.getName().substring(file.getName().indexOf("."),file.getName().length());
    }

    /**
     * 去除横杠
     * @param str
     * @return
     */
    public static String  rmLine(String str){
        return  str.replaceAll("-","");
    }

    /**
     * 判断是否是json格式
     * @param str
     * @return
     */
    public static boolean isJson(String str) {
        boolean result = false;
        try {
              JSON.parseObject(str);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 验证是否是定时任务表达式
     * @param cron
     * @return
     */
    public static boolean isTaskCron(String cron){
        if(StringUtils.isNotBlank(cron)){
            // cron表达式格式验证
            String str = "0 * * * * ?";
            String regMiao = "([0-9]{1,2}|[0-9]{1,2}\\-[0-9]{1,2}|\\*|[0-9]{1,2}\\/[0-9]{1,2}|[0-9]{1,2}\\,[0-9]{1,2})";
            String regFen = "\\s([0-9]{1,2}|[0-9]{1,2}\\-[0-9]{1,2}|\\*|[0-9]{1,2}\\/[0-9]{1,2}|[0-9]{1,2}\\,[0-9]{1,2})";
            String regShi = "\\s([0-9]{1,2}|[0-9]{1,2}\\-[0-9]{1,2}|\\*|[0-9]{1,2}\\/[0-9]{1,2}|[0-9]{1,2}\\,[0-9]{1,2})";
            String regRi = "\\s([0-9]{1,2}|[0-9]{1,2}\\-[0-9]{1,2}|\\*|[0-9]{1,2}\\/[0-9]{1,2}|[0-9]{1,2}\\,[0-9]{1,2}|\\?|L|W|C)";
            String regYue = "\\s([0-9]{1,2}|[0-9]{1,2}\\-[0-9]{1,2}|\\*|[0-9]{1,2}\\/[0-9]{1,2}|[0-9]{1,2}\\,[0-9]{1,2}|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)";
            String regZhou = "\\s([1-7]{1}|[1-7]{1}\\-[1-7]{1}|[1-7]{1}\\#[1-7]{1}|\\*|[1-7]{1}\\/[1-7]{1}|[1-7]{1}\\,[1-7]{1}|MON|TUES|WED|THUR|FRI|SAT|SUN|\\?|L|C)";
            String regNian = "(\\s([0-9]{4}|[0-9]{4}\\-[0-9]{4}|\\*|[0-9]{4}\\/[0-9]{4}|[0-9]{4}\\,[0-9]{4})){0,1}";
            String regEx = regMiao + regFen + regShi + regRi + regYue + regZhou + regNian;
            // 忽略大小写的写法
             Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pat.matcher(str);
            // 字符串是否与正则表达式相匹配
            return matcher.matches();
        }
        return false;
    }

}
