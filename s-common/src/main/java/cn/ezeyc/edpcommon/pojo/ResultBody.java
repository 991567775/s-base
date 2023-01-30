package cn.ezeyc.edpcommon.pojo;


import cn.ezeyc.edpcommon.enums.ResultEnum;
import com.alibaba.fastjson2.JSONObject;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应
 * @author admin
 */
public class ResultBody<T> implements Serializable {
    private static final long serialVersionUID = -6190689122701100762L;
    /**
     * 消息码
     */
    private int code = 0;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 附加数据
     */
    private Map<String, Object> extra;

    /**
     * 服务器时间
     */
    private long timestamp = System.currentTimeMillis();


    public ResultBody() {
        super();
    }

    public static <T> ResultBody success() {
        return new ResultBody().setMessage(ResultEnum.OK.getMessage()).setCode(ResultEnum.OK.getCode());
    }

    public static <T> ResultBody success(T data) {
        return new ResultBody().setData(data).setMessage(ResultEnum.OK.getMessage()).setCode(ResultEnum.OK.getCode());
    }

    public static <T> ResultBody success(String msg, T result) {
        return new ResultBody().setMessage(msg).setData(result).setCode(ResultEnum.OK.getCode());
    }

    public static ResultBody failed(String msg) {
        if(msg==null){
            return new ResultBody().setCode(ResultEnum.error.getCode()).setMessage(ResultEnum.error.getMessage());
        }else{
            return new ResultBody().setCode(ResultEnum.OperateField.getCode()).setMessage(msg);
        }
    }

    public static ResultBody failed() {
        return new ResultBody().setCode(ResultEnum.OperateField.getCode()).setMessage(ResultEnum.OperateField.getMessage());
    }

    public static ResultBody failed(Integer code, String msg) {
        return new ResultBody().setCode(code).setMessage(msg);
    }
    public static ResultBody failed(ResultEnum code) {
        return failed(code.getCode(), code.getMessage());
    }

    public int getCode() {
        return code;
    }

    public ResultBody setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultBody setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResultBody setData(T data) {
        this.data = data;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ResultBody setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public ResultBody setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    public ResultBody putExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>(1);
        }
        this.extra.put(key, value);
        return this;
    }
    public ResultBody put(String key, Object value) {
        Map map=null;
        if (this.data == null) {
            map = new HashMap<>(1);
            map.put(key, value);
            this.setData((T) map);
        }else if(this.getData() instanceof Map){
            ((Map) this.getData()).put(key,value);
        }
        return this;
    }





    @Override
    public String toString() {
        return "{code:" + code + ",message:'" + message + "',timestamp:" + timestamp + ",extra:'" + JSONObject.toJSONString(extra) + "'}";
    }
}