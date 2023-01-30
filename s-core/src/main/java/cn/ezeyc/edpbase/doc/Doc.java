package cn.ezeyc.edpbase.doc;


import cn.ezeyc.edpbase.util.StringUtil;
import cn.ezeyc.edpcommon.enums.ResultEnum;
import cn.ezeyc.edpcommon.pojo.Page;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.torna.sdk.client.OpenClient;
import cn.torna.sdk.common.Booleans;
import cn.torna.sdk.param.*;
import cn.torna.sdk.request.DocPushRequest;
import cn.torna.sdk.response.DocPushResponse;
import com.alibaba.fastjson2.JSON;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.bcel.Const;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Doc {

    /**
     * 配置
     */
    private static DocConfig config; // 推送客户端
    /**
     * 客户端
     */
    private  static OpenClient client = null;

    /**
     * 路径
     */
    private static  String root=null;
    /**
     * java文件构造
     */
    JavaProjectBuilder builder = new JavaProjectBuilder();
    /**
     * doc文档集合
     */
    private static final List<DocItem> list=new ArrayList();
    /**
     * 实体集合
     */
    private final List<JavaClass> models=new ArrayList<>();
    /**
     * 作者
     */
    private  String author="none";
    /**
     * 数据库链接
     */
    HikariDataSource dataSource = new HikariDataSource();
    /**
     * 排序
     */
    private int index=100;
    /**
     * 创建文档
     */
    public  void create(){
        config = getJson();
        //数据链接
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(config.getSqlUrl());
        dataSource.setUsername(config.getUser());
        dataSource.setPassword(config.getPwd());
        //初始化api
        client=new OpenClient(config.getOpenUrl());
        root=System.getProperty("user.dir");
        if(!"".equals(config.getModelPath())){
            root+=File.separator+config.getModelPath();
        }
        root+=File.separator+"src"+File.separator+"main"+File.separator+"java";
        //推送
        try {
            testDocPush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 推送接口文档
     */
    private void testDocPush() throws ClassNotFoundException, SQLException {
        DocPushRequest request = new DocPushRequest(config.getAppToken());
        //创建分类
        //扫描类并获取list
        loadJavaSource();
        // 创建调试环境
        DebugEnv debugEnv = new DebugEnv(config.getDebugEnvName(), config.getDebugEnvUrl());
        // 设置请求参数
        request.setApis(list);
        request.setDebugEnvs(List.of(debugEnv));
        request.setAuthor(author);
        // 是否替换文档，true：替换，false：不替换（追加）。默认：true
        request.setIsReplace(Booleans.TRUE);
        // 发送请求
        DocPushResponse response = client.execute(request);
        if (response.isSuccess()) {
            // 返回结果
            System.out.println("=================请求成功=====================");
        } else {
            System.out.println("errorCode:" + response.getCode() + ",errorMsg:" + response.getMsg());
        }
    }

    /**
     * 获取json配置
     * @return
     */
    private DocConfig getJson()  {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("doc.json");
        String jsontext = null;
        try {
            jsontext = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 转为实体类
        return JSON.parseObject(jsontext, DocConfig.class);
    }
    /**
     * 获取接口文件
     * @throws IOException
     */
    private void loadJavaSource() throws ClassNotFoundException, SQLException {
        List<String> packages = config.getPackages();
        //扫描control
        if(packages!=null&&packages.size()>0){
            for (String path:packages){
                //扫描control路径
                DirectoryScanner scanner = new DirectoryScanner(new File(root+File.separator+path.replaceAll("\\.","/")));
                scanner.addFilter(new SuffixFilter(".java"));
                scanner.scan(currentFile -> {
                    try {
                        builder.addSource(currentFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Collection<JavaClass> classes = builder.getClasses();
                for (JavaClass aClass : classes) {
                    if(aClass.getSimpleName().endsWith(ZdConst.end_with_control)){
                        getDoc(aClass);
                    }else {
                        List<JavaAnnotation> annotations = aClass.getAnnotations();
                        for(JavaAnnotation an:annotations){
                            if("cn.ezeyc.edpcommon.annotation.dao.pojo".equals(an.getType().getFullyQualifiedName())
                                    ||"cn.ezeyc.edpcommon.annotation.mvc.body".equals(an.getType().getFullyQualifiedName())){
                                models.add(aClass);
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * 解析control
     * @args aClass
     */
    private void  getDoc(JavaClass aClass) throws ClassNotFoundException, SQLException {
        DocItem folder = new DocItem();
        folder.setIsFolder(Booleans.TRUE);
        //设置分类
        if("".equals(aClass.getComment())){
            folder.setName("default");
        }else{
            folder.setName(aClass.getComment());
        }
        folder.setIsShow(Booleans.TRUE);
        folder.setOrderIndex(index);
        index++;
        //设置作者
        List<DocletTag> tags = aClass.getTags();
        if(tags.size()>0){
            for(DocletTag d:tags){
                if("author".equals(d.getName())){
                    author=d.getValue();
                    folder.setAuthor(d.getValue());
                }else  if("index".equals(d.getName())){
                    folder.setOrderIndex(Integer.valueOf(d.getValue()));
                }
            }
        }
        folder.setUrl(StringUtil.toLowerCaseFirstOne(aClass.getSimpleName()).replaceAll("Control",""));
        //设置方法
        List<DocItem> items = new ArrayList<>(8);
        List<JavaMethod> methods = aClass.getMethods();
        if(methods.size()>0){
            for (int i = 0; i < methods.size(); i++) {
                DocItem docItem = buildDocItem(methods.get(i),folder);
                if(docItem!=null){
                    docItem.setOrderIndex(i);
                    items.add(docItem);
                }
            }
        }
        folder.setItems(items);
        list.add(folder);
    }

    /**
     * 解析函数
     * @args method
     * @args folder
     * @return
     */
    private  DocItem buildDocItem(JavaMethod method, DocItem folder) throws ClassNotFoundException, SQLException {
        DocItem item = new DocItem();
        item.setAuthor(folder.getAuthor());
        /* 设置默认基本信息 */
        item.setName(method.getComment());
        item.setDescription("描述信息" );
        item.setUrl("/"+ folder.getUrl()+"/"+method.getName());
        //默认post
        item.setHttpMethod("POST");
        List<JavaAnnotation> annotations = method.getAnnotations();
        for(JavaAnnotation an:annotations ){
            if("cn.ezeyc.edpcommon.annotation.mvc.get".equals(an.getType().getFullyQualifiedName())){
                item.setHttpMethod("GET");
            }
        }
        List<DocletTag> tags = method.getTags();
        //默认显示
        item.setIsShow(Booleans.TRUE);
        item.setContentType("application/json");
        List<JavaParameter> parameters = method.getParameters();
        for(JavaParameter p:parameters){
            if(p.getType().getFullyQualifiedName().equals("org.noear.solon.core.handle.UploadedFile")){
                item.setContentType("multipart/form-data");
                break;
            }
        }
        //设置tag标签
        boolean noApi=false;
        List<DocParamReq> paramReqs=new ArrayList<>();
        if(tags!=null&&tags.size()>0){
            for(DocletTag tag:tags){
                //判断是否是接口
                if("noApi".equals(tag.getName())){
                   return  null;
                }
                //设置排序
                if("index".equals(tag.getName())){
                   folder.setOrderIndex(Integer.valueOf(tag.getValue()));
                }
                //接口是否隐藏
                if("show".equals(tag.getName())&&"false".equals(tag.getValue())){
                    item.setIsShow(Booleans.FALSE);
                }
                //接口ContentType
                if("ContentType".equals(tag.getName())&&!"".equals(tag.getValue())){
                    item.setContentType(tag.getValue());
                }
                //接口描述
                if("description".equals(tag.getName())&&!"".equals(tag.getValue())){
                    item.setDescription(tag.getValue());
                }
                //作者
                if("author".equals(tag.getName())&&!"".equals(tag.getValue())){
                    folder.setAuthor(tag.getValue());
                }
                //
                if("List".equals(tag.getName())&&!"".equals(tag.getValue())){
                    String[] val =StringUtil.delEmpty(tag.getValue().split(" ")) ;
                    JavaParameter param = method.getParameterByName(val[0]);
                    DocParamReq  paramReq= new DocParamReq();
                    paramReq.setType(param.getValue());
                    paramReq.setExample("[对象值,对象值1]");
                    paramReq.setName(val[2]);
                    paramReq.setDescription(val[2]);
                    paramReq.setRequired(Booleans.TRUE);
                    paramReq.setChildren(setList(method,parameters,item,tags,val[0]));
                    paramReqs.add(paramReq);
                }
                //参数
                if("args".equals(tag.getName())&&!"".equals(tag.getValue())){
                    String[] val =StringUtil.delEmpty(tag.getValue().split(" ")) ;
                    //参数描述少于3个报错
                    if(val.length<3){
                        throw  new RuntimeException("请求"+item.getUrl()+"参数"+val[0]+"描述内容");
                    }
                    JavaParameter param = method.getParameterByName(val[0]);

                    DocParamReq  paramReq= new DocParamReq();
                    //设置参数名
                    paramReq.setName(val[0]);
                    if(val.length>=2&&!val[1].equals("必选")&&!val[1].equals("可选")){
                        throw  new RuntimeException("请求"+item.getUrl()+"参数"+val[0]+"缺少是否必选");
                    }else{
                        if(val[1].equals("必选")){
                            paramReq.setRequired(Booleans.TRUE);
                        }else {
                            paramReq.setRequired(Booleans.FALSE);
                        }
                    }
                    //设置描述
                    paramReq.setDescription(val[2]);
                    if(val.length>=4){
                        paramReq.setExample(val[3]);
                    }
                    if(param!=null){//基本类型
                        paramReq.setType(param.getValue());
                        //设置最大值
                        if("String".equals(param.getValue())&&val.length>=5&& val[4]!="-1"){
                            paramReq.setMaxLength(val[4]);
                        }
                        paramReqs.add(paramReq);
                    }else if(parameters!=null&&parameters.size()>0){//从实体找
                        for(JavaParameter p:parameters){
                            if(!isBase(p.getValue())){
                                paramReqs.addAll(setModels(Class.forName(p.getFullyQualifiedName()),item,tags,tag.getName()));
                            }
                        }
                    }
                }
            }
        }
        //设置参数
        item.setRequestParams(paramReqs);
        /* 设置header */
        item.setHeaderParams(setHeader());
        /* 设置返回参数 */
        item.setResponseParams(setResponse(method));
        /* 设置错误码 */
        item.setErrorCodeParams( setError());
        return item;
    }



    /**
     * 设置请求头
     * @return
     */
    private List<DocParamHeader> setHeader(){
        List<DocParamHeader> headers=new ArrayList<>();
        DocParamHeader header = new DocParamHeader();
        header.setName("token");
        header.setRequired(Booleans.TRUE);
        header.setDescription("请求token");
        header.setExample("Ag_tgRYK7t71mc2US_NcRHiVNPQctbyD3Y__");
        headers.add(header);
        header = new DocParamHeader();
        header.setName("SA_ID_TOKEN");
        header.setRequired(Booleans.FALSE);
        header.setDescription("网关token");
        header.setExample("Ag_tgRYK7t71mc2US_NcRHiVNPQctbyD3Y__");
        headers.add(header);
        return  headers;
    }
    /**
     * 设置返回参数
     * @args method
     * @return
     * @throws ClassNotFoundException
     */
    private   List<DocParamResp> setResponse(JavaMethod method) throws ClassNotFoundException, SQLException {
        List<DocParamResp> respList=new ArrayList<>();
        if(method.getReturnType()!=null){
            //返回基本类型
            if(isBase(method.getReturnType().getValue())){
                DocParamResp resp= new DocParamResp();
                resp.setType(method.getReturnType().getValue());
                resp.setDescription(method.getReturnType().getValue());
                resp.setExample("");
                respList.add(resp);
            }else if(!("void").equals(method.getReturnType().getValue())){
                //返回实体
                Class<?> aClass = Class.forName(method.getReturnType().getFullyQualifiedName());
                Field[] declaredFields = aClass.getDeclaredFields();
                DocParamResp resp=null;
                if(aClass == ResultBody.class){
                    for (Field f:declaredFields){
                        resp=new  DocParamResp();
                        resp.setName(f.getName());
                        resp.setType(f.getType().getSimpleName());
                        if("code".equals(f.getName())){
                            resp.setDescription("状态码");
                            resp.setExample("200");
                        } else if ("message".equals(f.getName())) {
                            resp.setDescription("返回消息");
                            resp.setExample("成功");
                        } else if ("extra".equals(f.getName())) {
                            resp.setDescription("附加数据");
                        } else if ("timestamp".equals(f.getName())) {
                            resp.setDescription("服务器时间");
                            resp.setExample("1212193992");
                        }
                        else if ("data".equals(f.getName())) {
                            //分页
                            if(method.getReturnType().getGenericFullyQualifiedName().contains(Page.class.getName())){
                                Class<?> page = Class.forName(Page.class.getName());
                                List< DocParamResp> d=new ArrayList<>();
                                for( Field pf:page.getDeclaredFields()){
                                    DocParamResp r=new  DocParamResp();
                                    r.setName(pf.getName());
                                    r.setType(pf.getType().getSimpleName());
                                    if("total".equals(pf.getName())){
                                        r.setDescription("总数");
                                        d.add(r);
                                    } else if ("size".equals(pf.getName())) {
                                        r.setDescription("每页数");
                                        d.add(r);
                                    }else if ("current".equals(pf.getName())) {
                                        r.setDescription("当前页");
                                        d.add(r);
                                    }else if ("records".equals(pf.getName())) {
                                        JavaType javaType = ((JavaParameterizedType) method.getReturnType()).getActualTypeArguments().get(0);
                                        JavaType javaType1 = ((DefaultJavaParameterizedType) javaType).getActualTypeArguments().get(0);
                                        r.setChildren(setModelP(javaType1.getFullyQualifiedName()));
                                        d.add(r);
                                    }
                                }
                                resp.setChildren(d);
                                //list
                            } else if (method.getReturnType().getGenericFullyQualifiedName().contains(List.class.getName())) {//model
                                resp.setType("List");
                                List<JavaType> all = ((JavaParameterizedType) method.getReturnType()).getActualTypeArguments();
                                if(all.size()>0){
                                    JavaType javaType = all.get(0);
                                    List<JavaType> alls = ((DefaultJavaParameterizedType) javaType).getActualTypeArguments();
                                    if(alls.size()>0){
                                        resp.setChildren(setModelP(alls .get(0).getFullyQualifiedName()));
                                    }
                                }
                            }else{//其他
                                List<JavaType> all = ((JavaParameterizedType) method.getReturnType()).getActualTypeArguments();
                                if(all.size()>0){
                                    resp.setType(all.get(0).getValue());
                                    resp.setChildren(setModelP(all.get(0).getFullyQualifiedName()));
                                }
                            }
                        }
                        respList.add(resp);
                    }
                }else{
                    respList.addAll(setModelP(aClass.getName()));
                }
            }
        }
        return  respList;
    }
    /**
     * 设置错误码
     * @return
     */
    private List<DocParamCode> setError(){
        List<DocParamCode> paramCodes=new ArrayList<>();
        DocParamCode code=null;
        for(int x = 0; x< ResultEnum.values().length; x++){
            code= new DocParamCode();
            code.setCode(String.valueOf(ResultEnum.values()[x].getCode()));
            code.setMsg(ResultEnum.values()[x].getMessage());
            code.setSolution(ResultEnum.values()[x].getMessage());
            paramCodes.add(code);
        }
        return  paramCodes;
    }

    /**
     * 设置响应实体
     * @return
     */
    private List<DocParamResp> setModelP(String name) throws SQLException {
        List<DocParamResp> reqs=new ArrayList<>();
        for(JavaClass javaClass:models){
            if(name.equals(javaClass.getFullyQualifiedName())){
                List<JavaField> fields = javaClass.getFields();
                for(JavaField field:fields){
                    DocParamResp r=new  DocParamResp();
                    r.setName(field.getName());
                    r.setDescription(field.getComment());
                    r.setType(field.getType().getSimpleName());
                    //设置字典
                    List<JavaAnnotation> annotations = field.getAnnotations();
                    for(JavaAnnotation j:annotations){
                        if("ezeyc.core.annotation.direct".equals(j.getType().getFullyQualifiedName())){
                            AnnotationValue value = j.getProperty("value");
                            r.setEnumInfo(setEnum(value));
                            break;
                        }
                    }
                    ////实体参数
                    if(!isBase(field.getType().getSimpleName())){
                        if("List".equals(field.getType().getSimpleName())){
                            List<JavaType> all = ((JavaParameterizedType) field.getType()).getActualTypeArguments();
                            if(all.size()>0&&!isBase(all.get(0).getValue())){
                                r.setChildren(setModelP(all.get(0).getFullyQualifiedName()));
                            }
                        }else{
                            r.setChildren(setModelP(field.getType().getFullyQualifiedName()));
                        }
                    }
                    reqs.add(r);
                }
                //添加父类
                if(("ezeyc.core.pojo.mybatis.ModelBase").equals(javaClass.getSuperClass().getFullyQualifiedName())){
                    reqs.addAll(parentModels());
                }
                break;
            }

        }
        return  reqs;
    }



    /**
     * 设置字典
     * @args name
     * @return
     */
    private EnumInfoParam  setEnum(AnnotationValue name) throws SQLException {
        EnumInfoParam enumInfoParam = new EnumInfoParam();
        if(name!=null){
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select d.label as name,d.code,v.label,v.val FROM sys_direct_value v LEFT JOIN sys_direct d on v.pid=d.id WHERE d.code="+name);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<EnumItemParam> list=new ArrayList<>();
            while(resultSet.next()){
                enumInfoParam.setName(resultSet.getString("name"));
                enumInfoParam.setDescription(resultSet.getString("code"));
                EnumItemParam itemParam = new EnumItemParam();
                itemParam.setValue(resultSet.getString("val"));
                itemParam.setType(Directory.getType(resultSet.getString("val")));
                itemParam.setName(resultSet.getString("label"));
                itemParam.setDescription(resultSet.getString("label"));
                list.add(itemParam);
            }
            enumInfoParam.setItems(list);
            connection.close();
        }
        return enumInfoParam;
    }

    /**
     * 父类属性设置
     * @return
     */
    private List<DocParamResp> parentModels(){
        List<DocParamResp> reqs=new ArrayList<>();
        DocParamResp r=new  DocParamResp();
        r.setName("id");
        r.setDescription("主键");
        r.setType("Long");
        reqs.add(r);
        r=new  DocParamResp();
        r.setName("createUser");
        r.setDescription("创建人");
        r.setType("Long");
        reqs.add(r);
        r=new  DocParamResp();
        r.setName("createDate");
        r.setDescription("创建时间");
        r.setType("LocalDateTime");
        reqs.add(r);
        r=new  DocParamResp();
        r.setName("updateUser");
        r.setDescription("更新人");
        r.setType("Long");
        reqs.add(r);
        r=new  DocParamResp();
        r.setName("updateDate");
        r.setDescription("更新时间");
        r.setType("LocalDateTime");
        reqs.add(r);
        return  reqs;
    }

    /**
     * list参数解析
     * @return
     */
    private List<DocParamReq>  setList(JavaMethod method, List<JavaParameter> parameters,DocItem item, List<DocletTag> tags,String name) throws ClassNotFoundException {
        List<DocParamReq> reqs=new ArrayList<>();
        for(DocletTag tag:tags){
            if(name.equals(tag.getName())&&!"".equals(tag.getValue())){
                String[] val =StringUtil.delEmpty(tag.getValue().split(" ")) ;
                //参数描述少于3个报错
                if(val.length<3){
                    throw  new RuntimeException("请求"+item.getUrl()+"参数"+val[0]+"描述内容");
                }
                for(JavaParameter p:parameters){
                    List<JavaType> all = ((JavaParameterizedType) p.getType()).getActualTypeArguments();
                    if(all.size()>0&&!isBase(all.get(0).getValue())){
                        Field[] fields = getAllFields(Class.forName(all.get(0).getFullyQualifiedName()));
                        for(Field f:fields){
                            if(val[0].equals(f.getName())){
                                DocParamReq  paramReq= new DocParamReq();
                                //设置参数名
                                paramReq.setName(val[0]);
                                if(val.length>=2&&!val[1].equals("必选")&&!val[1].equals("可选")){
                                    throw  new RuntimeException("请求"+item.getUrl()+"参数"+val[0]+"缺少是否必选");
                                }else{
                                    if(val[1].equals("必选")){
                                        paramReq.setRequired(Booleans.TRUE);
                                    }else {
                                        paramReq.setRequired(Booleans.FALSE);
                                    }
                                }
                                //设置描述
                                paramReq.setDescription(val[2]);
                                if(val.length>=4){
                                    paramReq.setExample(val[3]);
                                }
                                if(isBase(f.getType().getSimpleName())){//基本类型
                                    paramReq.setType(f.getType().getSimpleName());
                                    //设置最大值
                                    if("String".equals(f.getType().getSimpleName())&&val.length>=5&& val[4]!="-1"){
                                        paramReq.setMaxLength(val[4]);
                                    }
                                    reqs.add(paramReq);
                                }else{
                                    reqs.addAll(setModels(Class.forName(f.getType().getName()),item,tags,tag.getName()));
                                }
                            }
                        }
                    }
                }

            }
        }
        return reqs;
    }
    /**
     * 实体参数设置
     * @args aClass
     * @args item
     * @args tags
     * @args tag
     * @return
     */
    private  List<DocParamReq>  setModels(Class aClass ,DocItem item,List<DocletTag> tags,String tag) throws ClassNotFoundException {
        List<DocParamReq> reqs=new ArrayList<>();
        Field[] fields = getAllFields(aClass);
        for(DocletTag t:tags){
            if(tag.equals(t.getName())){
                for(Field f:fields){
                    String[] val =StringUtil.delEmpty(t.getValue().split(" ")) ;
                    //参数描述少于3个报错
                    if(val.length<3){
                        throw  new RuntimeException("请求"+item.getUrl()+"参数"+val[0]+"描述内容");
                    }
                    if(val[0].equals(f.getName())){
                        DocParamReq  paramReq= new DocParamReq();
                        //设置参数名
                        paramReq.setName(val[0]);
                        if(val.length>=2&&!val[1].equals("必选")&&!val[1].equals("可选")){
                            throw  new RuntimeException("请求"+item.getUrl()+"参数"+val[0]+"缺少是否必选");
                        }else{
                            if(val[1].equals("必选")){
                                paramReq.setRequired(Booleans.TRUE);
                            }else {
                                paramReq.setRequired(Booleans.FALSE);
                            }
                        }
                        //设置描述
                        paramReq.setDescription(val[2]);
                        if(val.length>=4){
                            paramReq.setExample(val[3]);
                        }
                        if(f.getType()==List.class&&f.getGenericType()!=null){
                            if (f.getGenericType() instanceof ParameterizedType pt) {
                                // 得到泛型里的class类型对象
                                Class<?> argument = (Class<?>)pt.getActualTypeArguments()[0];
                                //非普通范型
                                paramReq.setType(f.getType().getSimpleName());
                                if(!isBase(argument.getSimpleName())){
                                    paramReq.setExample("");
                                    paramReq.setChildren(setModels(argument,item,tags,val[3]));
                                }
                            }
                        }else if (!isBase(f.getType().getSimpleName())){
                            paramReq.setExample("");
                            paramReq.setChildren(setModels(Class.forName(f.getType().getName()),item,tags,val[3]));
                        }

                        else{
                            paramReq.setType(f.getType().getSimpleName());
                            if("String".equals(f.getType().getSimpleName())&&val.length>=5){
                                paramReq.setMaxLength(val[4]);
                            }
                        }
                        reqs.add(paramReq);
                        break;
                    }
                }
            }
        }
        return  reqs;
    }
    /**
     * 判断是否基本类型以及基本数组
     * @args s
     * @return
     */
    private static boolean isBase(String s){
        return "String[]".equals(s) || "String".equals(s)
                || "Long".equals(s) || "long".equals(s) || "Long[]".equals(s) || "long[]".equals(s)
                || "Double".equals(s) || "double".equals(s) || "Double[]".equals(s) || "double[]".equals(s)
                || "Float".equals(s) || "float".equals(s) || "Float[]".equals(s) || "float[]".equals(s)
                || "Integer".equals(s) || "int".equals(s) || "Integer[]".equals(s) || "int[]".equals(s)
                || "Boolean".equals(s) || "boolean".equals(s) || "Boolean[]".equals(s) || "boolean[]".equals(s)
                || "Byte".equals(s) || "byte".equals(s) || "Byte[]".equals(s) || "byte[]".equals(s)
                || "Short".equals(s) || "short".equals(s) || "Short[]".equals(s) || "short[]".equals(s)
                || "Char".equals(s) || "char".equals(s) || "Char[]".equals(s) || "char[]".equals(s)
                || "BigDecimal".equals(s) || "LocalDate".equals(s) || "LocalDateTime".equals(s) || "LocalTime".equals(s);
    }


    /**
     * 获取Field
     * @args clazz
     * @return
     */
    public static Field[] getAllFields(Class clazz){
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null){
            final Field[] declaredFields = clazz.getDeclaredFields();
            Collections.addAll(fieldList, declaredFields);
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }



}
