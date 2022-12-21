package cn.ezeyc.edpbase.pojo.base;


import cn.ezeyc.edpcommon.annotation.dao.delete;
import cn.ezeyc.edpcommon.annotation.dao.param;
import cn.ezeyc.edpcommon.annotation.dao.select;
import cn.ezeyc.edpcommon.annotation.dao.update;
import cn.ezeyc.edpcommon.pojo.Page;

import java.util.List;

/**
 * BaseDao：
 *
 * @author: Administrator
 * @date: 2020年11月23日, 0023 14:59:10
 */
public interface ServiceBase<T> {
    /**
     * 查询分页列表
     * @param o 实体
     * @return
     */
    @select
    Page<T> list(@param T o);

    /**
     * 查询列表
     * @param o 实体
     * @return
     */
    @select
    List<T> listAll(@param T o);

    /**
     * 查询单个
     * @param id 主键
     * @return
     */
    @select
    T getById(@param Long id);

    /**
     * 更新
     * @param o 对象
     * @return
     */
    @update
    T save(@param T o);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @delete
    Integer deleteById(@param String id);

    /**
     * 根据条件删除
     * @param o 对象
     * @return
     */
    @delete
    Integer delete(@param T o);




}
