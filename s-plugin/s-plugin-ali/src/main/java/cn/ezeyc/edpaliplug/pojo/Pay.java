package cn.ezeyc.edpaliplug.pojo;

import java.math.BigDecimal;

/**
 * 支付信息实体
 */
public class Pay {

    /**
     * id
     */
    private  String id;
    /**
     * 交易号支付宝支付回调返回
     */
    private  String tradeNo;
    /**
     * 系统订单号[自己生成]
     */
    private  String outTradeNo;
    /**
     * 支付金额
     */
    private BigDecimal totalAmount;
    /**
     * 商品信息
     */
    private  String subject;
    /**
     * 支付人
     */
    private  String buyerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
}
