package cn.ezeyc.edpbase.core.session;

import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.annotation.framework.value;
import cn.ezeyc.edpbase.interfaces.Interceptor;
import cn.ezeyc.edpbase.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限默认实现
 * @author wz
 */
@configuration
public class DataInterceptor implements Interceptor {
    @value("edp.config.dataAuth")
    private Boolean dataAuth;
    @Autowired
    private RedisUtil redisUtil;
    private static ThreadLocal<Long> id = new ThreadLocal<Long>();
    public List<String> noAuthTable=new ArrayList();
    public List<String> noRemoveTable=new ArrayList();
    public DataInterceptor(){
        noAuthTable();
        noRemoveTable();
    }


    /**
     * 获取用户信息
     * @return
     */
    @Override
    public JSONObject getUser(){
        if(id.get()!=null&& redisUtil.getByString(id.get().toString())!=null){
         return    (JSONObject) JSONObject.toJSON(redisUtil.getByString(id.get().toString()));
        }
        return  null;
    }

    @Override
    public void removeUserId() {
        id.remove();
    }

    @Override
    public Long getUserId() {
        return id.get();
    }

    @Override
    public void noAuthTable() {

    }

    @Override
    public List<String> getNoAuthTable() {
        return noAuthTable;
    }

    @Override
    public void noRemoveTable() {

    }

    @Override
    public List<String> getNoRemoveTable() {
        return  noRemoveTable;
    }

    @Override
    public boolean getAllAuth() {
        return dataAuth==null||dataAuth;
    }

    @Override
    public  void setUser(Long userId) {
        id.set(userId);
    }

    @Override
    public boolean isDataAuth() {
        return true;
    }

    @Override
    public void dataAuth( String field) {

    }
}
