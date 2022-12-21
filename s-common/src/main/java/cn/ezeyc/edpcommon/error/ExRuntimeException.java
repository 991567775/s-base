package cn.ezeyc.edpcommon.error;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 自定义异常
 * @author wz
 */
public class ExRuntimeException extends RuntimeException{
    private Logger logger=  LoggerFactory.getLogger(ExRuntimeException.class);
    public ExRuntimeException(String message) {
        super(message);
        logger.error(message);
    }

    /**
     * 打印异常信息
     * @param e
     * @return
     */
    public static String getExceptionInfo(Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(e!=null){
            e.printStackTrace(new PrintStream(baos));
        }
        return baos.toString();
    }

}
