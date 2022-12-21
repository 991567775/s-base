package cn.ezeyc.edpbase.core.listener;

import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.error.ExRuntimeException;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 启动监听器
 * @author wz
 */
public class StartListener  implements ApplicationListener<ApplicationEvent>, Ordered {
    @Override
    public int getOrder() {
        // 当前监听器的启动顺序需要在日志配置监听器的前面，所以此处减 1
        return LoggingApplicationListener.DEFAULT_ORDER -1;
    }
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) applicationEvent).getEnvironment();
            if (environment.containsProperty(ZdConst.applicationName)) {
                System.setProperty("log4fFile", environment.getProperty(ZdConst.applicationName));
                //设置为nacoslog日志
                System.setProperty("JM.LOG.PATH",System.getProperty("user.dir"));
                //设置为nacos快照日志
                System.setProperty("JM.SNAPSHOT.PATH",System.getProperty("user.dir"));
            }else{
                throw new ExRuntimeException("未配置项目名称属性spring.application.name");
            }
        }

    }
}
