package cn.ezeyc.edpbase.pojo.base;

import cn.ezeyc.edpcommon.annotation.dao.*;
import cn.ezeyc.edpbase.pojo.session.Query;
import cn.ezeyc.edpcommon.pojo.ModelBase;
import cn.ezeyc.edpcommon.pojo.Page;

import java.util.List;

/**
 * BaseDao：
 *
 * @author: Administrator
 * @date: 2020年11月23日, 0023 14:59:10
 */
public interface DaoBase<T extends ModelBase> {
    /**
     * 分页查询
     * @param t 实体
     * @param q 查询条件
     * @return
     */
    @select
    Page<T> select(@param T t, @param Query q);

    /**
     * 集合查询
     * @param q 查询条件
     * @return
     */
    @select
    List<T> list(@param Query q);

    /**
     * 根据id获取对象
     * @param id 主键
     * @return
     */
    @select
    T getById(@param Long id);

    /**
     * 根据条件查找某个对象
     * @param q 查询条件
     * @return
     */
    @select
    T selectOne(@param Query q);
    /**
     * 保存数据
     * @param t 实体
     * @return
     */
    @insert
    T insert(@param T t);

    /**
     * 批量插入
     * @param list 实体集合
     * @return
     */
    @insert
    List<T> insert(@param List<T> list);

    /**
     * 更新(更新全部)
     * @param t 实体
     * @param q 查询条件
     * @return
     */
    @update
    Integer update(@param T t,@param Query q);

    /**
     * 根据id更新(更新全部)
     * @param t 实体
     * @return
     */
    @update
    Integer updateById(@param T t);

    /**
     * 更新(只更新有值)
     * @param t 实体
     * @param q 查询条件
     * @return
     */
    @update
    Integer updateNotNull(@param T t,@param Query q);

    /**
     * 根据id更新(只更新有值)
     * @param t 实体
     * @return
     */
    @update
    Integer updateNotNullById(@param T t);

    /**
     * 根据id删除
     * @param id 主键
     * @return
     */
    @delete
    Integer deleteById(@param Long id);

    /**
     * 根据条件删除
     * @param q 查询条件
     * @return
     */
    @delete
    Integer delete(@param Query q);

    /**
     * 根据id集合删除
     * @param ids 主键
     * @return
     */
    @delete
    Integer deleteByIds(@param String ids);

    /**
     * sql执行
     * @param sql 语句
     * @param params 参数
     * @return
     */
    @sql
    Object executeSql(String sql,List<Object> params);

    /**
     * sql执行
     * @param sql 语句
     * @param params 参数
     * @return
     */
    @sql
    Object executeSql(String sql,Object ... params);
}
