package cn.ezeyc.edpbase.pojo.PicCode;

/**
 * @author zewang
 */
public class PicCode {

    private String code;
    private String base64;

    public PicCode(String code, String base64) {
        this.code = code;
        this.base64 = base64;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
