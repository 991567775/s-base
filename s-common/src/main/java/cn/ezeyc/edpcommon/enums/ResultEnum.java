package cn.ezeyc.edpcommon.enums;

/**
 * @author wz
 */
public enum ResultEnum {


    error(001,"系统错误,请联系管理员"),

    license(002,"证书错误,请联系管理员"),
    OperateField(100,"操作失败"),
    OK(200, "操作成功"),
    noAuth(401,"没有访问权限"),
    forbidden(403,"权限不足,禁止访问"),

    //登录
    loginError(1001,"用户认证错误"),
    loginPwdError(1002,"密码错误"),
    loginNameError(1003,"用户不存在"),
    Authorization(1004,"缺少token"),
    tokenError(1005,"token失效"),

    picNull(2001,"验证码为空"),
    picError(2002,"验证码错误"),
    picTimeOut(2003,"验证码失效"),

    verify(3001,"参数校验错误");

    private int code;
    private String message;

    ResultEnum() {
    }

    private ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultEnum getResultEnum(int code) {
        for (ResultEnum type : ResultEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return error;
    }

    public static ResultEnum getResultEnum(String message) {
        for (ResultEnum type : ResultEnum.values()) {
            if (type.getMessage().equals(message)) {
                return type;
            }
        }
        return error;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}