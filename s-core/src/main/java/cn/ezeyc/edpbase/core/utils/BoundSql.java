package cn.ezeyc.edpbase.core.utils;

import java.util.ArrayList;
import java.util.List;
/**
 * @author wz
 */
public class BoundSql {
    /**
     * 解析过后的sql
     */
    private String sqlText;

    private List<String> fieldList = new ArrayList<>();

    public BoundSql(String sqlText, List<String> fieldList) {
        this.sqlText = sqlText;
        this.fieldList = fieldList;
    }

    public BoundSql(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<String> getFieldList() {
        return fieldList;
    }
    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }
}
