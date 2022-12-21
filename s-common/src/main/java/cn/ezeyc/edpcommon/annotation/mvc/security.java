package cn.ezeyc.edpcommon.annotation.mvc;
import java.lang.annotation.*;
/**
 * @author wz
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface security {
    String value() default "";
}
