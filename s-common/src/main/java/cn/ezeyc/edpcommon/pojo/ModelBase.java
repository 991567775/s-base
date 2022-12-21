package cn.ezeyc.edpcommon.pojo;


import cn.ezeyc.edpcommon.annotation.dao.col;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseModel：有默认字段
 *
 * @author: Administrator
 * @date: 2020年11月23日, 0023 17:31:44
 */
public class ModelBase<T>  implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     * 主键
     */
    @col("id")
    private Long id;
    /**
     *创建人
     */
    @col("create_user")
    private Long createUser;
    /**
     *更新人
     */
    @col("update_user")
    private Long updateUser;
    /**
     *创建时间
     */
    @col("create_date")
    private  LocalDateTime createDate;
    /**
     *更新时间
     */
    @col("update_date")
    private  LocalDateTime updateDate;
    /**
     *是否删除
     */
    @col("remove")
    private Boolean remove;
    /**
     *数据级别编码
     */
    @col("data_code")
    private String [] dataCode;
    /**
     *当前用户id
     */
    private Long currentUserId;
    /**
     *分页信息
     */
    private Page<T> page;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getRemove() {
        return remove;
    }

    public void setRemove(Boolean remove) {
        this.remove = remove;
    }
    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }

    public String[] getDataCode() {
        return dataCode;
    }

    public void setDataCode(String[] dataCode) {
        this.dataCode = dataCode;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }
}
