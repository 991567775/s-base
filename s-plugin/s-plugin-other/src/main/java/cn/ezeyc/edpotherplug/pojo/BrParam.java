package cn.ezeyc.edpotherplug.pojo;

/**
 * 百融查询参数
 */
public class BrParam {
    /**
     * 手机号
     */
    private  String phone;
    /**
     * 身份证
     */
    private  String card;
    /**
     * 姓名
     */
    private  String name;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
