package cn.ezeyc.edpbase.enums;
/**
 * @author wz
 */
public enum SqlType {
    /**
     * 语句类型
     */
    SELECT("select"),
    UPDATE("update");
    private String val;
    SqlType(String val) {
        this.val = val;
    }
    public String getValue() {
        return val;
    }
}
