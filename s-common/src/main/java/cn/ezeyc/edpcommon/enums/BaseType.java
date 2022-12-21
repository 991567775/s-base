package cn.ezeyc.edpcommon.enums;

/**
 * @author wz
 */
public enum BaseType {
    /**
     * 数据类型
     */
    FLOAT("float"),
    DOUBLE("double"),
    INTEGER("int"),
    LONG("long"),

    STRING("string"),
    BOOLEAN("boolean"),

    LOCAL_DATETIME("LocalDateTime"),

    ARRAY("array");

    private String val;


    BaseType(String val) {
        this.val = val;
    }


    public String getValue() {
        return val;
    }


}
