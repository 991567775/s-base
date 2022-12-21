package cn.ezeyc.edptxplug.msg;

/**
 * @author wz
 */
public enum MsgTypeEnum {
    /**
     * 验证码类型
     */
    YZM(1),
    OTHER(0);

    private int type;

    MsgTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}