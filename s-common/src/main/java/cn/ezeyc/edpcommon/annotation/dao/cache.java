package cn.ezeyc.edpcommon.annotation.dao;
import java.lang.annotation.*;

/**
 * @author wz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)//由JVM 加载，包含在类文件中，在运行时可以被获取到
@Target({ElementType.METHOD})
//TYPE：接口、类 FIELD:属性 METHOD:方法 PARAMETER：方法形式参数 CONSTRUCTOR：构造方法
// LOCAL_VARIABLE：局部变量 ANNOTATION_TYPE：注解类型 PACKAGE：包
public @interface cache {
    String value() default "";
}
