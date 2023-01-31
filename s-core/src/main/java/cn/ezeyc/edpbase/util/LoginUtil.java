package cn.ezeyc.edpbase.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import cn.ezeyc.edpbase.security.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 获取request
 * @author wz
 */
public class LoginUtil {
    /**
     * 获取用户id
     * @return
     */
    public static String getCurrentLoginId(){
         try{
             HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
             String token = request.getHeader("Authorization");
             if(StringUtils.isNotBlank(token)){
                 DecodedJWT verifier = JwtUtils.verifier(token.replace("bearer ",""));
                 if(verifier!=null){
                     return  verifier.getClaim("id").asString();
                 }
             }
         }catch (Exception e){
           return  null;
         }

        return  null;
    }
    public static String getCurrentToken(){
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");
        if(StringUtils.isNotBlank(token)){
           return token;
        }
        return  null;
    }
    /**
     * 获取request
     * @return
     */
    public static HttpServletRequest getRequest(){
       return  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }
    /**
     * 获取response
     * @return
     */
    public static HttpServletResponse getResponse(){
        return  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }
}
