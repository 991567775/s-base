package cn.ezeyc.edpbase.core.utils;

import cn.ezeyc.edpbase.interfaces.SqlTxtHandler;

import java.util.ArrayList;
import java.util.List;
/**
 * @author wz
 */
public class ParamSqlTxtHandler implements SqlTxtHandler {
    private List<String> fieldList = new ArrayList();

    /**
     * context是参数名称 #{id} #{username}
     * @param field
     * @return
     */
    @Override
    public String handleToken(String field) {
        fieldList.add(field);
        return "?";
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }
}
