package cn.ezeyc.edpaliplug.pojo;

import java.math.BigDecimal;

/**
 * 支付信息实体
 */
public class PayNotify {
    /**
     * 交易号支付宝支付回调返回
     */
    private  String TradeNo;
    /**
     * 系统订单号[自己生成]
     */
    private  String outTradeNo;
    /**
     * 支付金额
     */
    private BigDecimal totalAmount;
    /**
     *
     */
    private  String tradeStatus;
    /**
     *
     */
    private  String gmtPayment;

    public String getTradeNo() {
        return TradeNo;
    }

    public void setTradeNo(String tradeNo) {
        TradeNo = tradeNo;
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

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(String gmtPayment) {
        this.gmtPayment = gmtPayment;
    }
}
