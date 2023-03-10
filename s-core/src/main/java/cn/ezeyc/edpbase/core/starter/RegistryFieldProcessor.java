package cn.ezeyc.edpbase.core.starter;

import cn.ezeyc.edpbase.core.client.ClientRequest;
import cn.ezeyc.edpbase.core.session.ControlFactory;
import cn.ezeyc.edpbase.core.session.ServiceProxyFactory;
import cn.ezeyc.edpbase.util.RedisUtil;
import cn.ezeyc.edpbase.util.RedissonUtil;
import cn.ezeyc.edpbase.util.StringUtil;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.primary;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpcommon.annotation.mvc.get;
import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.util.BeanUtil;
import cn.ezeyc.edpcommon.util.ClassUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wz
 * rdp??????bean????????? ?????????spring ??????
 */
@Import(ClientRequest.class)
public class RegistryFieldProcessor implements BeanPostProcessor, EnvironmentAware, BeanFactoryAware {
    private volatile BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
    private ConfigurableEnvironment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }
    @Autowired
    private volatile ApplicationContext applicationContext;
    @Autowired
    private  ConfigPojo configPojo;
    private  RequestMappingHandlerMapping mapping;
    private final RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    private  String pkg;
    /**
     * ????????????
     */
    private final Map<String,Object> map=new HashMap();
    /**
     * bean???????????????
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
       if(pkg==null){
           List<String> packages = AutoConfigurationPackages.get(beanFactory);
           pkg=packages.get(0);
       }
       //com.ezeyc?????????dao???bean
       boolean s=(bean.getClass().getPackage().getName().startsWith(pkg)||bean.getClass().getPackage().getName().startsWith(ZdConst.package_path))&&!beanName.endsWith(ZdConst.end_with_Dao);
        if(s){
            //???dao?????????????????????
            //??????
            try {

                registerAutowired(bean);
            } catch (Exception e) {
                e.printStackTrace();

            }
            //????????????
            if(beanName.endsWith(ZdConst.end_with_control)){

                if(mapping==null){
                    mapping = applicationContext.getBean("requestMappingHandlerMapping",RequestMappingHandlerMapping.class);
                    this.config.setTrailingSlashMatch(mapping.useTrailingSlashMatch());
                    this.config.setContentNegotiationManager(mapping.getContentNegotiationManager());
                    if (mapping.getPatternParser() != null) {
                        this.config.setPatternParser(mapping.getPatternParser());
                        Assert.isTrue(!mapping.useSuffixPatternMatch() && !mapping.useRegisteredSuffixPatternMatch(),
                                "Suffix pattern matching not supported with PathPatternParser.");
                    } else {
                        this.config.setSuffixPatternMatch(mapping.useSuffixPatternMatch());
                        this.config.setRegisteredSuffixPatternMatch(mapping.useRegisteredSuffixPatternMatch());
                        this.config.setPathMatcher(mapping.getPathMatcher());
                    }
                }
                //??????url????????????control?????????
                registerUrlMapping(bean,new ControlFactory(bean,environment.getProperty(ZdConst.enableLog, Boolean.class),
                        environment.getProperty(ZdConst.logService),environment.getProperty(ZdConst.logUrl),applicationContext.getBean(ClientRequest.class)).getCglibProxy());
            }
        }
        return bean;
    }
    /**
     * ??????
     * @param pattern
     * @param method
     */

    /**
     * ????????????
     * @param bean
     * @return
     * @throws IllegalAccessException
     */
    private Object registerAutowired(Object bean) throws Exception {
        Field[] declaredFields = ClassUtil.getAllFields(bean.getClass(),value.class,autowired.class);
         for(Field f:declaredFields){

             if(f.isAnnotationPresent(value.class)){
                 //??????????????????
                 registerField(f,bean);
             }else if(f.isAnnotationPresent(autowired.class)){
                 //????????????

                 registerFieldObj(f,bean);
//                 if(!lazy(bean.getClass(),f)){
//                     if(!f.getType().isAnnotationPresent(Lazy.class)){
//                         throw new RuntimeException(bean.getClass().getSimpleName()+"???"+f.getType().getSimpleName()+"????????????????????????@Autowired???@Lazy??????");
//                     }else{
//                         registerFieldObj(f,bean);
//                     }
//                 }else{
//
//                 }
             }
         }
         return  bean;
    }

    /**
     * ???????????? 2?????????(??????)
     * @param bean
     * @param field
     * @return
     */
    private boolean lazy(Class bean,Field field){
        if(field.getType().isInterface()){
            Reflections reflections = new Reflections(field.getType().getPackage().getName());
            Set<Class<? extends Object>> set = reflections.getSubTypesOf((Class<Object>) field.getType());
            if(set.size()==1){
                final Class o = set.iterator().next();
                Field[] declaredFields = ClassUtil.getAllFields(o,Autowired.class,autowired.class);
                for(Field f:declaredFields){
                    if(f.getType()==bean){
                        return false;
                    }
                }
            } else if (set.size()>1) {
                Object obj=null;
                for (Iterator it = set.iterator(); it.hasNext(); ) {
                    Object o = it.next();
                    if(o.getClass().isAnnotationPresent(primary.class)||o.getClass()==field.getType()){
                        obj=o;
                        break;
                    }
                }
                if(obj!=null){
                    Field[] declaredFields = ClassUtil.getAllFields(obj.getClass(),Autowired.class,autowired.class);
                    for(Field f:declaredFields){
                        if(f.getType()==bean){
                            return false;
                        }
                    }
                }

            }
        }else{
            Field[] declaredFields = ClassUtil.getAllFields(field.getType(),Autowired.class,autowired.class);
            for(Field f:declaredFields){
                if(bean.getInterfaces().length>0){
                    for(Class face: bean.getInterfaces()){
                        if(face==f.getType()){
                            return false;
                        }
                    }
                }else    if(f.getType()==bean){
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * ??????????????????
     * @param f
     * @param bean
     * @throws IllegalAccessException
     */
    private  void registerField(Field f,Object bean) throws IllegalAccessException, ClassNotFoundException {
        String value = f.getAnnotation(value.class).value();
        if(StringUtils.isNotBlank(value)){
            if(f.getType()==List.class){
                List list=null;
                Type actualTypeArgument = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
                if(BeanUtil.isBaseType(actualTypeArgument)){//????????????
                    list=environment.getProperty(value,List.class);
                }else{//???????????????
                    list= (List) configPojo.getValue(value,Class.forName(actualTypeArgument.getTypeName()));
                }
                if(list!=null&&list.size()>0){
                    f.setAccessible(true);
                    f.set(bean,list);
                }
            }else if(f.getType().getSuperclass()==ConfigPojo.class){
                Object o = configPojo.getValue(value);
                if(o instanceof Map<?,?>){
                    Map map= (Map) o;
                    if(map!=null){
                        f.setAccessible(true);
                        f.set(bean,JSON.parseObject(JSON.toJSONString(map), f.getType()));
                    }
                }else if(o instanceof List<?>){
                    if(o!=null){
                        f.setAccessible(true);
                        f.set(bean,o);
                    }
                }

            } else{
                final String property = environment.getProperty(value);
                if(property!=null){
                    f.setAccessible(true);
                    if(f.getType()==Integer.class){
                        f.set(bean,Integer.valueOf(property));
                    }else if(f.getType()==Boolean.class){
                        f.set(bean,Boolean.valueOf(property));
                    }else if(f.getType()==Long.class){
                        f.set(bean,Long.valueOf(property));
                    }else if(f.getType()==Float.class){
                        f.set(bean,Float.valueOf(property));
                    }else if(f.getType()==Double.class){
                        f.set(bean,Double.valueOf(property));
                    }  else if(f.getType()==String.class){
                        f.set(bean,property);
                    }
                }
            }
        }


    }
    /**
     * ??????????????????
     * @param f ??????
     * @param bean ??????
     * @throws IllegalAccessException
     */
    private  void registerFieldObj(Field f,Object bean) throws IllegalAccessException {
        if(f.getType()== DataSource.class&&environment.getProperty("edp.db.url")==null){
            return;
        }
        if(f.getType().getSimpleName().endsWith(ZdConst.client)||f.getType().getSimpleName().endsWith(ZdConst.end_with_Dao)){
            //dao ????????????
            f.setAccessible(true);
            f.set(bean,applicationContext.getBean(f.getType()));
        }else if(f.getType().getSimpleName().endsWith(ZdConst.end_with_service)
                &&f.getType().isInterface()){
            //service??????
            f.setAccessible(true);
            interfaceAutowired(f,bean,false);
        }
        else{
            //????????????
            Object obj=null;
            final Map<String, ?> count = applicationContext.getBeansOfType(f.getType());
            if(count.size()>1){
                for(Object o:count.values()){
                    if(o.getClass().isAnnotationPresent(primary.class)||o.getClass()==f.getType()){
                        obj=o;
                        break;
                    }
                }
            }else{
               obj = applicationContext.getBean(f.getType());
            }
            if(obj!=null){
                f.setAccessible(true);
                f.set(bean,obj);

            }
        }
    }

    private void   interfaceAutowired(Field f,Object bean,boolean isProxy) throws IllegalAccessException {
        final Map<String, ?> count = applicationContext.getBeansOfType(f.getType());
        if(count!=null){
            if(count.size()==1){
                final Object o = count.values().stream().findFirst().get();
                if(isProxy){
                    f.set(bean,applicationContext.getBean(ServiceProxyFactory.class).getJdkProxy(o));
                }else{
                    f.set(bean,o);
                }
            }else if(count.size()>1){
                if(f.getAnnotation(autowired.class).value()!=Exception.class){
                    if(isProxy){
                        f.set(bean,applicationContext.getBean(ServiceProxyFactory.class).getJdkProxy( applicationContext.getBean(f.getAnnotation(autowired.class).value())));
                    }else{
                        f.set(bean,applicationContext.getBean(f.getAnnotation(autowired.class).value()));
                    }
                }else{
                    throw new  RuntimeException(bean.getClass().getSimpleName()+"???"+f.getType().getSimpleName()+"???????????????????????????????????????");
                }
            }else if(count.size()==0){
                //????????????
                map.put(bean.getClass().getSimpleName()+"-"+f.getType().getSimpleName(),bean);
            }
        }else{
            throw new  RuntimeException(bean.getClass().getSimpleName()+"???"+f.getType().getSimpleName()+"?????????");

        }
    }

    /**
     * ??????????????????
     */
    private void registerUrlMapping(Object bean,Object proxy)  {

        //????????????
        String baseUrl = StringUtil.toLowerCaseFirstOne(bean.getClass().getSimpleName().split(ZdConst.end_with_control)[0]) ;
        //??????control??????
        final Method[] methods = bean.getClass().getDeclaredMethods();
        RequestMappingInfo mappingInfo=null;
        //????????????
        for(Method m:methods){
            //??????post
            mappingInfo = RequestMappingInfo.paths(baseUrl + ZdConst.slanting+m.getName())
                    .methods(RequestMethod.POST)
                    .options(this.config)
                    .build();
            if(m.isAnnotationPresent(get.class)||m.isAnnotationPresent(GetMapping.class)){
                mappingInfo = RequestMappingInfo.paths(baseUrl + ZdConst.slanting+m.getName())
                        .methods(RequestMethod.GET)
                        .options(this.config)
                        .build();
            }
            if(!m.isAnnotationPresent(RequestMapping.class)){
                mapping.registerMapping(mappingInfo,proxy,m);
            }else{
                RequestMethod[] method = m.getAnnotation(RequestMapping.class).method();
                if(method!=null&&method.length>0&&method[0].name().equals(RequestMethod.GET.name())){
                    mappingInfo = RequestMappingInfo.paths(baseUrl + ZdConst.slanting+m.getName())
                            .methods(RequestMethod.GET)
                            .options(this.config)
                            .build();
                }else{
                    mappingInfo = RequestMappingInfo.paths(baseUrl + ZdConst.slanting+m.getName())
                            .methods(RequestMethod.POST)
                            .options(this.config)
                            .build();
                }
                mapping.registerMapping(mappingInfo,proxy,m);
            }
        }
    }

    /**
     * bean???????????????
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //?????????redis ??????????????????redis ip????????????
        if(bean.getClass()== RedisUtil.class&&environment.getProperty("edp.redis.ip")!=null){

            ((RedisUtil) bean).init();
        }
        //?????????redis????????????
        if(bean.getClass()== RedissonUtil.class&&environment.getProperty("edp.redis.ip")!=null){
            ((RedissonUtil) bean).init();
        }
        if(bean.getClass().getSimpleName().contains("rderService")
        ||bean.getClass().getSimpleName().contains("InfoService")){
            System.out.print("1");
        }
        //?????????????????????bean

        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().getKey();
            List<String> collect = Arrays.stream(applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());

            if (key.contains(bean.getClass().getSimpleName().replace(ZdConst.end_with_service_impl,""))
            &&collect.contains(key.split("-")[1].replace(ZdConst.end_with_service,"")+ZdConst.end_with_service_impl) ) {
                try {
                    registerAutowired(map.get(key));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                iterator.remove();
            }
        }
        //??????????????????
        return bean;
    }


}
