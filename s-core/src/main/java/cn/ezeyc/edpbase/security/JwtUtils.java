package cn.ezeyc.edpbase.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;
/**
 * @author wz
 */
public class JwtUtils {
    /**
     * 签名随意写，但复杂性越高，安全性越高
     */
    public static final String SING = "rdp_991567775_wangze";
    /**
     * 生成token
     */
    public static String getToken(Map<String,String> map){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,2);
        //创建JWT builder
        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        //指定令牌的过期时间//签名
        String token =  builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SING));
        return token;
    }
    /**
     * 验证token合法性
     */
    public static DecodedJWT  verifier(String token){
        JWTVerifier build = JWT.require(Algorithm.HMAC256(SING)).build();
        DecodedJWT verify=null;
        try{
             verify = build.verify(token);
        }catch (Exception e){

        }
        return verify;
    }
    /**
     * 获取token信息
     */
    public static DecodedJWT getTokenInfo(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
        return verify;
    }


//    public static void main(String[] args) {
//        Map map=new HashMap(3);
//        map.put("id","1234");
//        map.put("password","12346");
//        String token = JwtUtils.getToken(map);
//        System.out.println(token);
//        DecodedJWT tokenInfo = JwtUtils.getTokenInfo(token);
//        String id = tokenInfo.getId();
//        String signature = tokenInfo.getSignature();
//        String payload = tokenInfo.getPayload();
//        String contentType = tokenInfo.getContentType();
//        DecodedJWT verifier = JwtUtils.verifier(token);
//        Date expiresAt = verifier.getExpiresAt();
//        String password = verifier.getClaim("password").asString();
//        System.out.println(tokenInfo);
//    }
}

