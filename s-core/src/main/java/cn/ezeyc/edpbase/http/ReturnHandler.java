package cn.ezeyc.edpbase.http;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.annotation.Nullable;
/**
 * ReturnHandler：
 * 请求返回类型设置
 * @author: Administrator
 * @date: 2020年12月15日, 0015 11:09:09
 */
public class ReturnHandler implements HandlerMethodReturnValueHandler {
    private final HandlerMethodReturnValueHandler target;
    public ReturnHandler(HandlerMethodReturnValueHandler target) {
        this.target = target;
    }

    /**
     * 判断非基本类型则json
     * @param returnType 方法返回类型
     * @return 需要转换返回true，否则返回false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        //判断类型
        if("void".equals(returnType.getParameterType().getName())){
            return false;
        }else {
            return  true;

        }

    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        /**
         * 标识请求是否已经在该方法内完成处理
         */
        if (!void.class.isAssignableFrom(returnType.getParameterType())) {
            target.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        }
    }
}
