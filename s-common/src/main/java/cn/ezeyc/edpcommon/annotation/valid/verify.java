package cn.ezeyc.edpcommon.annotation.valid;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)//由JVM 加载，包含在类文件中，在运行时可以被获取到
@Target({ ElementType.FIELD,ElementType.PARAMETER})
public @interface verify {


    /**
     * 是否不为空
     */
    boolean notEmpty() default  false;
    /**
     * 字符长度
     * @return
     */
    int length() default -1;

    /**
     * 正则验证
     * @return
     */
    String regexp() default "";

    /**
     * 提示
     */
    String msg() default "参数值不符合验证";



}
