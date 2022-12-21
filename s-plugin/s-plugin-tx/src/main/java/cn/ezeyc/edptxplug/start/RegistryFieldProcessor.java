package cn.ezeyc.edptxplug.start;

import cn.ezeyc.edpcommon.error.ExRuntimeException;
import cn.ezeyc.edptxplug.msg.TxMessage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;


/**
 * @author wz
 * rdp框架bean属性值 注入到spring 容器
 */
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



    /**
     * bean前置处理器
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }
    /**
     * bean后置处理器
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass()== TxMessage.class){
            try {
                ((TxMessage) bean).init();
            } catch (Exception e) {
                throw new ExRuntimeException("阿里云消息客户端初始化失败");
            }
        }

        return bean;
    }


}
