package cn.ezeyc.edpcommon.annotation.client;

import java.lang.annotation.*;

/**
 * @author wz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)//由JVM 加载，包含在类文件中，在运行时可以被获取到
@Target({ElementType.TYPE})
public @interface client {
    String value() ;
}

