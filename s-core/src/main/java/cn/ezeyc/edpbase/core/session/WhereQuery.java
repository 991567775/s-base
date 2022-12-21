package cn.ezeyc.edpbase.core.session;


import cn.ezeyc.edpcommon.pojo.Page;

import java.util.List;

/**
 * query条件构造器返回值实体
 * @author wz
 */
public class WhereQuery {
    private  String whereSql;
    private List<Object> whereParam;
    private Page pageLimit;

    public WhereQuery(String whereSql, List<Object> whereParam, Page pageLimit) {
        this.whereSql = whereSql;
        this.whereParam = whereParam;
        this.pageLimit = pageLimit;
    }
    public String getWhereSql() {
        return whereSql;
    }

    public void setWhereSql(String whereSql) {
        this.whereSql = whereSql;
    }

    public List<Object> getWhereParam() {
        return whereParam;
    }

    public void setWhereParam(List<Object> whereParam) {
        this.whereParam = whereParam;
    }

    public Page getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(Page pageLimit) {
        this.pageLimit = pageLimit;
    }
}
