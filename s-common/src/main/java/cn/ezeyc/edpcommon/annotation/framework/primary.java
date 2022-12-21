package cn.ezeyc.edpcommon.annotation.framework;

import java.lang.annotation.*;
/**
 * @author wz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)//由JVM 加载，包含在类文件中，在运行时可以被获取到
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface primary {
}
