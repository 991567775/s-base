package cn.ezeyc.edpcommon.annotation.dao;
import java.lang.annotation.*;

/**
 * @author wz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)//由JVM 加载，包含在类文件中，在运行时可以被获取到
@Target({ElementType.METHOD})
public @interface insert {
    String value() default "";
}
