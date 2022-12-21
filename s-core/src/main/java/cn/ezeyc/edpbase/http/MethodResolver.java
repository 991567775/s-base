package cn.ezeyc.edpbase.http;

import cn.ezeyc.edpbase.security.JwtUtils;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import cn.ezeyc.edpbase.interfaces.Interceptor;
import cn.ezeyc.edpcommon.annotation.framework.autowired;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpcommon.annotation.mvc.security;
import cn.ezeyc.edpcommon.enums.ResultEnum;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import cn.ezeyc.edpcommon.pojo.ZdConst;
import cn.ezeyc.edpcommon.util.Http;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 请求拦截
 * @author wz
 */
public class MethodResolver implements HandlerInterceptor {

    private static  int time=60;
    @autowired
    private Interceptor interceptor;
    @value("edp.config.ignore")
    private List<String> ignore;
    @value("edp.config.security")
    private Boolean security;
    @autowired
    private PasswordEncoder passwordEncoder;
    private  static  AntPathMatcher antPathMatcher = new AntPathMatcher();
    public MethodResolver(){
    }
    private final Logger logger = LoggerFactory.getLogger(MethodResolver.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info(request.getRequestURI());
        Long userId=null;
        //无需校验证书请求
        boolean isLicenseVerify=true;
        //1.0默认免除证书验证
        for (String s : ZdConst.licenseIgnore) {
            if(antPathMatcher.match(s,request.getRequestURI())){
                isLicenseVerify=false;
                break;
            }
        }
        //2检测
//        if(isLicenseVerify){
//            //校验证书是否有效
//            LicenseVerify licenseVerify = new LicenseVerify();
//            boolean verifyResult = licenseVerify.verify();
//            if(!verifyResult){
//                returnJson(response,ResultBody.failed(ResultEnum.license));
//                return false;
//            }
//        }
        //免验证token匹配
        boolean isVerifier=true;
        //1.0默认免除
        for (String s : ZdConst.tokenIgnore) {
            if(antPathMatcher.match(s,request.getRequestURI())){
                isVerifier=false;
                break;
            }
        }
        //1.1项目配置免除
        if(ignore!=null&&ignore.size()>0){
            for (String s : ignore) {
                if(antPathMatcher.match(s,request.getRequestURI())){
                    isVerifier=false;
                    break;
                }
            }
        }

        //1.2判断方法是否有security注解
        security v =null;
        try {
             v = ((HandlerMethod) handler).getMethod().getAnnotation(security.class);
        }catch (Exception e){
            return  true;
        }
        //检测token
        boolean enableSecurity=security==null||security;
        //验证用户权限
        if(isVerifier&&enableSecurity&& v!=null&& StringUtils.isNotBlank(v.value())){
            String token = request.getHeader("Authorization");
            if(StringUtils.isNotBlank(token)){
                token=token.replace("bearer ","");
                DecodedJWT verifier = JwtUtils.verifier(token);
                if(verifier==null){
                    Http.returnJson(response,ResultBody.failed(ResultEnum.tokenError));
                    return  false;
                }
                if(new Date(System.currentTimeMillis() - time).after(verifier.getExpiresAt())){
                    //TODO
                }
                if(verifier.getClaim(ZdConst.id)!=null){
                    //设置到线程中
                    String s = verifier.getClaim(ZdConst.id).asString();
                    if(!"".equals(s)){
                        userId=Long.valueOf(s);
                        interceptor.setUser(userId);
                    }
                }
                //检测用户是否具有security的权限标识
                if(userId!=null){
                    //内置超级管理员
                    if(userId==1L){
                        return  true;
                    }
                    //查询用户请求权限
                    JSONObject json =interceptor.getUser();
                    if(json!=null&&!json.isEmpty()){
                        final boolean contains =json.getJSONArray("permissions").contains(v.value().trim());
                        if(!contains){
                            Http.returnJson(response,ResultBody.failed(ResultEnum.forbidden));
                            return false;
                        }
                    }else{
                        Http.returnJson(response,ResultBody.failed(ResultEnum.forbidden));
                        return  false;
                    }
                }
            }else{
                Http.returnJson(response,ResultBody.failed(ResultEnum.Authorization));
                return  false;
            }
        }
        if(!(handler instanceof  HandlerMethod) ){
            return true;
        }

        interceptor.removeUserId();
        return true;
    }

    /**
     * 返回客户端数据
     * @param response
     * @param resultBody
     */


}
