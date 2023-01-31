package cn.ezeyc.edpbase.http;

import cn.ezeyc.edpbase.core.utils.reflect;
import cn.ezeyc.edpbase.util.MultipartFileToFile;
import cn.ezeyc.edpbase.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpbase.interfaces.Interceptor;
import cn.ezeyc.edpcommon.annotation.dao.pojo;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.mvc.body;
import cn.ezeyc.edpcommon.annotation.valid.verify;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import org.apache.commons.fileupload.RequestContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ObjHandlerMethodArgumentResolver：
 * 请求参数拦截
 * post 请求组装为对象
 * @author: Administrator
 * @date: 2020年12月4日, 0004 10:41:48
 */
public class  MethodArgsResolver implements HandlerMethodArgumentResolver {
    /**
     * 范型
     */
    private static final String T="T";
    /**
     * 范型
     */
    private static final String Z="Z";
    @autowired
    private Interceptor interceptor;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        RequestWrapper request = new RequestWrapper(webRequest.getNativeRequest(HttpServletRequest.class));

        if(ZdConst.post.equals(request.getMethod())){
            if( request.getRequest().getClass().getSimpleName().toLowerCase().contains("multipart") ){
                //multipartRequest附件上传请求
                MultipartHttpServletRequest multipartRequest =
                        WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
                MultipartFile file = multipartRequest.getFile("file");
                File toFile = null;
                if (!"".equals(file) && file.getSize() > 0) {
                    InputStream ins = null;
                    ins = file.getInputStream();
                    toFile = new File(file.getOriginalFilename());
                    MultipartFileToFile.inputStreamToFile(ins, toFile);
                    ins.close();
                }
                return toFile;
            }else  {
                //json请求
                String sb = request.getBody();
                //判断字符串是否json格式
                JSONObject jsonObject=null;
                if(StringUtil.isJson(sb)){
                    jsonObject = JSONObject.parseObject(sb);
                }else if(sb.contains(ZdConst.eq)){
                    //非json,名称=值?名称=值
                    String s="{'"+sb.replaceAll(ZdConst.eq,"':'").replaceAll("&","','")+"'}";
                    jsonObject = JSONObject.parseObject(s);
                }
                //实体参数
                if(parameter.getParameterType().isAnnotationPresent(pojo.class)
                        ||parameter.hasParameterAnnotation(body.class)) {
                    //注解实体拼装
                    if(parameter.hasParameterAnnotation(verify.class)){
                        return setObject(parameter.getParameterType(),jsonObject,true);
                    }else{
                        return setObject(parameter.getParameterType(),jsonObject,false);
                    }

                }else{
                    //其他类型参数
                    //参数验证
                    if(parameter.getParameterType().isAnnotationPresent(verify.class)){
                        verify annotation = parameter.getParameterType().getAnnotation(verify.class);
                        //判断是否为空//
                        if(annotation.notEmpty()){
                            if(jsonObject.get(parameter.getParameterName())==null||"".equals(jsonObject.get(parameter.getParameterName()))){
                                throw new ExRuntimeException(parameter.getParameterName()+annotation.msg());
                            }
                        }
                        //判断长度
                        if(annotation.length()!=-1){
                            if(jsonObject.get(parameter.getParameterName()).toString().length()>annotation.length()){
                                throw new ExRuntimeException(parameter.getParameterName()+annotation.msg());
                            }
                        }
                        //正则验证
                        if(annotation.regexp().length()>0){
                            if(!Pattern.matches(annotation.regexp(), jsonObject.get(parameter.getParameterName()).toString())){
                                throw new ExRuntimeException(parameter.getParameterName()+annotation.msg());
                            }
                        }
                    }
                    if(jsonObject!=null&&jsonObject.get(parameter.getParameterName())!=null&&!"".equals(jsonObject.get(parameter.getParameterName()))){
                        //list类型 参数
                        if(parameter.getParameterType()==List.class){
                            Object o = jsonObject.get(parameter.getParameterName());
                            if(o instanceof    JSONArray){
                                Type type = parameter.getGenericParameterType();
                                if(type instanceof ParameterizedType){
                                    String typeName = ((ParameterizedType) type).getActualTypeArguments()[0].getTypeName();
                                    return ((JSONArray) o).toJavaList(Class.forName(typeName));
                                }
                            }
                        }
                        //数组、 基本类型参数
                        return   reflect.setNormal(null,parameter.getParameterType(),null,jsonObject.get(parameter.getParameterName()));
                    }

                }
            }
        }
        return request.getParameter(parameter.getParameterName());
    }
    private    Object setObject(Class param, JSONObject jsonObject,Boolean isVerify) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            //参数实列
            Object o=param.getDeclaredConstructor().newInstance();
            if(jsonObject==null){
                return o;
            }
            //本身实体属性
            Field[] declaredFields = param.getDeclaredFields();
            for(Field f:declaredFields){
                //参数验证
                if(isVerify&&f.isAnnotationPresent(verify.class)){
                    verify annotation = f.getAnnotation(verify.class);
                    //判断是否为空//
                    if(annotation.notEmpty()){
                        if(jsonObject.get(f.getName())==null||"".equals(jsonObject.get(f.getName()))){
                            throw new ExRuntimeException(f.getName()+annotation.msg());
                        }
                    }
                     //判断长度
                    if(annotation.length()!=-1){
                        if(jsonObject.get(f.getName()).toString().length()>annotation.length()){
                            throw new ExRuntimeException(f.getName()+annotation.msg());
                        }
                    }
                    //正则验证
                    if(annotation.regexp().length()>0){
                        if(!Pattern.matches(annotation.regexp(), jsonObject.get(f.getName()).toString())){
                            throw new ExRuntimeException(f.getName()+annotation.msg());
                        }
                    }
                }
                if(jsonObject.get(f.getName())!=null&&o!=null){
                    setField(o,f,jsonObject,isVerify);
                }
            }
            //父类通用属性
            Field[] parents = param.getSuperclass().getDeclaredFields();
            for(Field f:parents){
                if(f.getName().equals(ZdConst.page)){
                    //设置分页
                    Object page = f.getType().getDeclaredConstructor().newInstance();
                    Field[] pageFileds = f.getType().getDeclaredFields();
                    for(Field pf:pageFileds){
                        if(jsonObject.get(pf.getName())!=null&&page!=null){
                            //设置其他通用值
                            pf.setAccessible(true);
                            pf.set(page,jsonObject.get(pf.getName()));
                        }
                    }
                    f.setAccessible(true);
                    f.set(o,page);
                }

                if(jsonObject.get(f.getName())!=null&&o!=null){
                    //设置父类属性
                    setField(o,f,jsonObject,isVerify);
                }
            }
        return  o;
    }


    private  void setField(Object o,Field f,JSONObject jsonObject,Boolean isVerify) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        f.setAccessible(true);
        //对象中list<对象> 解析
        if (f.getType()==List.class) {
            Type t = f.getGenericType();
            if (t instanceof ParameterizedType pt) {
                //得到对象list中实例的类型
                Class clazz = (Class) pt.getActualTypeArguments()[0];
                List list = new ArrayList();
                if(null!=jsonObject.getJSONArray(f.getName())){
                    for (int i = 0; i < jsonObject.getJSONArray(f.getName()).size(); i++) {
                        //实体拼装
                        if(clazz.isAnnotationPresent(pojo.class)||clazz.isAnnotationPresent(body.class)){
                            JSONObject jsonObj =jsonObject.getJSONArray(f.getName()).getJSONObject(i);
                            list.add(setObject(clazz,jsonObj,isVerify));
                        }else{
                            //list<String>
                            list.add(jsonObject.getJSONArray(f.getName()).get(i));
                        }
                    }
                    f.set(o,list);
                }
            }
        }else if(f.isAnnotationPresent(body.class)|| ModelBase.class==f.getType().getSuperclass()||f.getType()== Page.class){
            //实体
            f.set(o, setObject(f.getType(), jsonObject.getJSONObject(f.getName()),isVerify));
        } else if(f.getType()==BigDecimal.class){
            //BigDecimal
            if(jsonObject.get(f.getName())!=null&&!"".equals(jsonObject.get(f.getName()))){
                f.set(o,new BigDecimal(jsonObject.get(f.getName()).toString()));
            }
        }else{
            //数组 基本类型 参数
            reflect.setNormal(f,f.getType(),o,jsonObject.get(f.getName()));
        }

    }

}
