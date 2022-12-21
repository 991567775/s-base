package cn.ezeyc.edpbase.pojo.session;



import java.io.Serializable;

/**
 * token返回响应数据
 * @author admin
 */
public class TokenBody implements Serializable {
    private static final long serialVersionUID = -6190689122701100762L;
    /**
     * token值
     */
    private String access_token;
    /**
     *权限
     */
    private String scope="all";
    /**
     *类型
     */
    private String token_type="bearer";
    /**
     *过期时间
     */
    private Long expires_in;
    /**
     *信息
     */
    private String tip;

    public TokenBody(String accessToken, Long expiresIn) {
        this.access_token = accessToken;
        this.expires_in= expiresIn;
    }

    public TokenBody(String access_token, Long expires_in, String tip) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


}