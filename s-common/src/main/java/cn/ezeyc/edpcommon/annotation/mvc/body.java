package cn.ezeyc.edpcommon.annotation.mvc;
import java.lang.annotation.*;
/**
 * @author wz
 */
@Target({ElementType.PARAMETER,  ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface body {

}
