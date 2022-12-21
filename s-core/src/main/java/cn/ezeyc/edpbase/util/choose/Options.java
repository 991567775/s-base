package cn.ezeyc.edpbase.util.choose;

import cn.ezeyc.edpcommon.annotation.dao.col;
import cn.ezeyc.edpcommon.annotation.dao.pojo;
import cn.ezeyc.edpcommon.pojo.ModelBase;
/**
 * @author wz
 */
@pojo("sys_direct_value")
public class Options  extends ModelBase<Options> {
    @col("label")
    private String label;
    @col("val")
    private Object val;
    private Boolean checked;
    private Boolean readOnly;
    private Boolean display;
    private String code;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
