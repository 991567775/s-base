package cn.ezeyc.edpbase.pojo.base;



import cn.ezeyc.edpcommon.pojo.Page;

import java.io.Serializable;

/**
 * BaseModel：无默认字段
 *
 * @author: Administrator
 * @date: 2020年11月23日, 0023 17:31:44
 */
public class DefaultModelBase<T>  implements Serializable {
    private static final long serialVersionUID=1L;

    private Page<T> page;

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }
}
