package cn.ezeyc.edpbase.monitor;

import cn.ezeyc.edpcommon.pojo.ZdConst;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
/**
 * @author wz
 */
public class AutoMonitor implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        Boolean property = environment.getProperty(ZdConst.showMonitor, Boolean.class);
        if (property != null && property) {

            return true;
        }
        return false;
    }
}