package cn.ezeyc.edpbase.interfaces;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * sql 执行拦截器
 * @author wz
 */
public interface Interceptor {
    /**
     * 是否数据验证
     * @return
     */
    boolean  isDataAuth();

    /**
     * 验证字段 默认data_code
     * @param field
     */
    void   dataAuth(String field);

    /**
     * 获取当前用户
     * @return
     */
     JSONObject getUser();

    /**
     * 移除当前用户id
     */
    void  removeUserId();

    /**
     * 获取当前用户id
     * @return
     */
     Long getUserId();

    /**
     * 设置当前用户id
     * @param id
     */
    void setUser(Long id);

    /**
     * 免验证表
     */
     void  noAuthTable();

    /**
     * 获取免验证表
     * @return
     */
    List<String>  getNoAuthTable();

    /**
     * 无物理删除
     */
    void  noRemoveTable();

    /**
     * 获取无物理珊删除
     * @return
     */
    List<String> getNoRemoveTable();

    /**
     * 是否验证
     * @return
     */
    boolean getAllAuth();
}
