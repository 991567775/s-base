package cn.ezeyc.edpbase.core.starter;

import cn.ezeyc.edpbase.core.client.ClientProxyFactory;
import cn.ezeyc.edpbase.core.license.create.LicenseCreator;
import cn.ezeyc.edpbase.core.session.DaoProxyFactory;
import cn.ezeyc.edpbase.config.Config;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.enums.ResultEnum;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edpcommon.util.ClassUtil;
import cn.ezeyc.edpcommon.pojo.ConfigPojo;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
/**
 * @author wz
 */
@EnableConfigurationProperties(Config.class)
public class RegistryBeanProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware, EnvironmentAware {
    private static final Logger logger= LoggerFactory.getLogger(RegistryBeanProcessor.class);
    private volatile Environment environment;
    private volatile BeanFactory beanFactory;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public ConfigPojo configPojo (){
        ConfigPojo configPojo=new ConfigPojo();
        Yaml yaml =  new Yaml();
        try {
            String active="";
            if(environment.getActiveProfiles().length>0){
                 active= "-"+environment.getActiveProfiles()[0];
            }
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("application"+active+".yml");
            configPojo.setData(yaml.load(inputStream));
        } catch (Exception e){
            e.printStackTrace();
        }
        return configPojo;
    };
    /**
     * 负载均衡
     * @return
     */
    @Bean
    public RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(155000);
        factory.setOutputStreaming(false);
        restTemplate.setRequestFactory(factory);
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() != ResultEnum.noAuth.getCode()){
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }
    class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public WxMappingJackson2HttpMessageConverter(){
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            mediaTypes.add(MediaType.TEXT_HTML);
            setSupportedMediaTypes(mediaTypes);
        }
    }


    /**
     * 注册bean
     * @param fieldClass
     * @return
     */
    private BeanDefinitionHolder createBeanDefinition(Class<?> fieldClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
        String className = fieldClass.getName();
        // bean的name首字母小写，spring通过它来注入
        String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf('.')+1));
        // 给ProxyFactoryBean字段赋值
        builder.addPropertyValue("interfaceClass", fieldClass);
        return new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
    }
    /**
     * 注册bean
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerDataSource(registry);
         Set<Class<?>> classes= ClassUtil.getClasses("cn.ezeyc");
         if(StringUtils.hasLength(environment.getProperty("edp.config.scan"))){
             Set<Class<?>> classes1 = ClassUtil.getClasses(environment.getProperty("edp.config.scan"));
             classes.addAll(classes1);
         }
        BeanDefinitionBuilder builder=null;
        GenericBeanDefinition definition=null;
        List<String> packages = AutoConfigurationPackages.get(beanFactory);
        if(packages!=null&&packages.size()>0){
            for(String pk:packages){
                classes.addAll(ClassUtil.getClasses(pk));
            }
        }
        for(Class c:classes){
             builder = BeanDefinitionBuilder.genericBeanDefinition(c);
            definition= (GenericBeanDefinition) builder.getRawBeanDefinition();
            if(c.getSimpleName().endsWith(ZdConst.end_with_Dao)){
                //dao接口代理注入
                definition.getConstructorArgumentValues().addGenericArgumentValue(c);
                definition.setBeanClass(DaoProxyFactory.class);
            }else if(c.getSimpleName().endsWith(ZdConst.client)){
                //远程调用
                definition.getConstructorArgumentValues().addGenericArgumentValue(c);
                definition.setBeanClass(ClientProxyFactory.class);
            }
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(c.getSimpleName(), definition);
        }
        //证书初始化
//        installLicense();
    }
    /**
     * 注册数据源
     */
    private void registerDataSource(BeanDefinitionRegistry registry)  {
        boolean containsBean = beanFactory.containsBean("dataSource");
        //配置url没写时则不需要数据库
        logger.info("数据库链接url:"+environment.getProperty("edp.db.url"));
        if(environment.getProperty("edp.db.url")!=null){
            if(containsBean){
                registry.removeBeanDefinition("dataSource");
            }
            BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(HikariDataSource.class).setPrimary(true)
                    .addPropertyValue("registerMbeans", true)
                    .addPropertyValue("jdbcUrl", environment.getProperty("edp.db.url"))
                    .addPropertyValue("username", environment.getProperty("edp.db.user"))
                    .addPropertyValue("password", environment.getProperty("edp.db.password"))
                    .addPropertyValue("driverClassName", environment.getProperty("edp.db.className")!=null?environment.getProperty("edp.db.className"):"com.mysql.cj.jdbc.Driver")
                    //等待连接池分配连接的最大时长（毫秒）
                    .addPropertyValue("connectionTimeout", environment.getProperty("edp.db.ConnectionTimeout") != null ? environment.getProperty("edp.db.ConnectionTimeout") : 30000)

                    .addPropertyValue("maximumPoolSize", environment.getProperty("edp.db.maximumPoolSize") != null ? environment.getProperty("edp.db.maximumPoolSize") : 10)

                    .addPropertyValue("minimumIdle", environment.getProperty("edp.db.minimumIdle") != null ? environment.getProperty("edp.db.minimumIdle") : 10)
                    //此属性控制允许连接在池中闲置的最长时间,此设置仅适用于minimumIdle设置为小于maximumPoolSize的情况
                    .addPropertyValue("idleTimeout", environment.getProperty("edp.db.idleTimeout") != null ? environment.getProperty("edp.db.idleTimeout") : 60000)
                    .addPropertyValue("validationTimeout", environment.getProperty("edp.db.validationTimeout") != null ? environment.getProperty("edp.db.validationTimeout") : 5 * 1000L)
                    .addPropertyValue("connectionTimeout", environment.getProperty("edp.db.connectionTimeout") != null ? environment.getProperty("edp.db.connectionTimeout") : 60000)

                    //一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired）
                    .addPropertyValue("maxLifetime", environment.getProperty("edp.db.maxLifetime") != null ? environment.getProperty("edp.db.maxLifetime") : 60000)
                    .addPropertyValue("loginTimeout", environment.getProperty("edp.db.loginTimeout") != null ? environment.getProperty("edp.db.loginTimeout") : 5)
//                    .addPropertyValue("keepaliveTime", environment.getProperty("edp.db.keepaliveTime") != null ? environment.getProperty("edp.db.keepaliveTime") : 5 * 1000L)
                    .addPropertyValue("connectionTestQuery", environment.getProperty("edp.db.connectionTestQuery") != null ? environment.getProperty("edp.db.connectionTestQuery") : "SELECT 1")
                    .addPropertyValue("poolName", environment.getProperty("edp.db.poolName") != null ? environment.getProperty("edp.db.poolName") : "mysql");
            registry.registerBeanDefinition("dataSource",builder.getBeanDefinition());
            //设置监控
            HikariDataSource dataSource = beanFactory.getBean(HikariDataSource.class);
            try {
                Connection connection = dataSource.getConnection();
                connection.close();
            } catch (SQLException e) {
                ExRuntimeException.getExceptionInfo(e);
            }
        }

    }
    /**
     * 证书安装
     */
    private void  installLicense(){
        String licensePath = environment.getProperty("edp.config.licensePath");
        if(licensePath==null){
            logger.error("证书地址未配置");
        }else{
            //安装证书
            try {
                LicenseCreator.licenseInstall(licensePath);
            } catch (Exception e) {
                throw new ExRuntimeException("证书安装失败");
            }
        }
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
