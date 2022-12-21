package cn.ezeyc.edpbase.http;

import cn.ezeyc.edpcommon.pojo.ZdConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;
import java.util.Locale;

/**
 * WebMvcConfig：
 * url参数转换,返回类型转换
 *
 * @author: Administrator
 * @date: 2020年12月4日, 0004 11:10:15
 */
@Configuration
@EnableWebMvc
@Import({MethodResolver.class,MethodArgsResolver.class, PostFilter.class})
public class WebConfigFilter implements WebMvcConfigurer, EnvironmentAware  {
    private ConfigurableEnvironment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }

    @Autowired
    private  MethodResolver methodResolver;
    @Autowired
    private  MethodArgsResolver methodArgsResolver;

    //异步处理支持
    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(120 * 1000L);//设置响应时间 120s
        configurer.registerCallableInterceptors(timeoutInterceptor());
        configurer.setTaskExecutor(threadPoolTaskExecutor());
    }
    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(10);
        t.setMaxPoolSize(100);
        t.setQueueCapacity(20);
        t.setThreadNamePrefix("WYF-Thread-");
        return t;
    }

    /**
     * 请求参数拦截器
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(methodArgsResolver);
    }

    /**
     * 请求拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个，这里选择拦截所有请求地址，进入后判断是否有加注解即可
        registry.addInterceptor(methodResolver);
    }

    /**
     * 跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("POST","GET","OPTIONS","PUT","DELETE","PATCH","HEAD")
                .allowedOrigins("*")
                .maxAge(3600);
    }

    /**
     * 资源映射处理
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //获得系统属性集
        String props = System.getProperties().getProperty("os.name");
        String url= ZdConst.UPLOAD_URL;
        if(props.toLowerCase(Locale.ROOT).contains(ZdConst.windows)){
            //Windows下
            String winPath=environment.getProperty(ZdConst.uploadWinPath);
            if(winPath==null||"".equals(winPath)){
                winPath= ZdConst.WIN_UPLOAD;
            }
            registry.addResourceHandler(url).addResourceLocations("file:"+ winPath+"/");

        }else {
            //Mac或Linux下(没有CDEF盘符)
            String path=environment.getProperty(ZdConst.uploadPath);
            if(path==null||"".equals(path)){
                path= ZdConst.UPLOAD;
            }
            registry.addResourceHandler(url).addResourceLocations("file:"+path+"/");
        }
        //配置拦截器访问swapper2静态资源
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
