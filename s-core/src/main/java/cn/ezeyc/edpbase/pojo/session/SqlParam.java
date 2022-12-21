package cn.ezeyc.edpbase.pojo.session;

import cn.ezeyc.edpcommon.pojo.Page;

import java.util.List;
/**
 * @author wz
 */
public class SqlParam<T> {
    private  String sql;
    private List<Object> params;
    private T model;
    private Page page;
    public SqlParam() {
    }



    public SqlParam(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public SqlParam(String sql, List<Object> list, T o) {
        this.sql=sql;
        this.params=list;
        this.model=o;
    }

    public SqlParam(String sql, List<Object> params, Page page) {
        this.sql = sql;
        this.params = params;
        this.page = page;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
