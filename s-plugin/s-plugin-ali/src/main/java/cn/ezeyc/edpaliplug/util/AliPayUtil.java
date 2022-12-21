package cn.ezeyc.edpaliplug.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipayFundAuthOrderAppFreezeModel;
import com.alipay.api.domain.AlipayFundAuthOrderUnfreezeModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import cn.ezeyc.edpaliplug.config.AliMp;
import cn.ezeyc.edpaliplug.pojo.Pay;

/**
 * 支付宝支付、预授权接口
 */
public class AliPayUtil {

    /**
     * 订单创建接口
     * @param mp
     * @param pay
     * @return
     */
    //
    public static AlipayTradeCreateResponse pay(AliMp mp, Pay pay)  {
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest ();
        //参数设置
        JSONObject bizContent = new JSONObject();
        bizContent.put("total_amount", pay.getTotalAmount());//支付成功后回调返回的
        bizContent.put("out_trade_no", pay.getOutTradeNo());
        bizContent.put("subject", pay.getSubject());
        bizContent.put("buyer_id", pay.getBuyerId());
        request.setBizContent(bizContent.toString());
        request.setNotifyUrl(mp.getPayNotifyUrl());
        return (AlipayTradeCreateResponse) AliUtil.request(request,mp);
    }

    /**
     * 退款
     * @param mp
     * @param pay
     * @return
     */
    public static AlipayTradeRefundResponse refund(AliMp mp, Pay pay) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        //参数设置
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", pay.getTradeNo());//支付成功后回调返回的
        bizContent.put("out_trade_no", pay.getOutTradeNo());
        bizContent.put("refund_amount", pay.getTotalAmount());
        request.setBizContent(bizContent.toString());
        request.setNotifyUrl(mp.getRefundNotifyUrl());
        return (AlipayTradeRefundResponse) AliUtil.request(request,mp);
    }

    /**
     * 资金授权冻结接口  		2.0
     *
     */
    public static AlipayFundAuthOrderAppFreezeResponse freeze(AliMp mp, Pay pay) {
        AlipayFundAuthOrderAppFreezeRequest request = new AlipayFundAuthOrderAppFreezeRequest();
        //参数设置
        AlipayFundAuthOrderAppFreezeModel model = new AlipayFundAuthOrderAppFreezeModel();
        model.setOrderTitle("尺素预授权");
        model.setOutOrderNo(pay.getOutTradeNo());//替换为实际订单号
//        model.setOutRequestNo(outRequestNo);//替换为实际请求单号，保证每次请求都是唯一的
        model.setProductCode("PRE_AUTH_ONLINE");//PRE_AUTH_ONLINE为固定值，不要替换
        model.setAmount(String.valueOf(pay.getTotalAmount()));
        model.setPayTimeout("30m");
//        if (!StringUtil.isEmpty(payChannels)) {
//            model.setExtraParam(payChannels);
//        }
        //选填字段
        // model.setEnablePayChannels(payChannels);
        request.setBizModel(model);
        request.setNotifyUrl(mp.getFreezeNotify());//异步通知地址，必填，该接口只通过该参数进行异步通知
        return (AlipayFundAuthOrderAppFreezeResponse) AliUtil.request(request,mp);
    }

    /**
     * 冻结转支付
     */
    public static AlipayTradePayResponse freezePay(AliMp mp, Pay pay) {
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        //参数设置
        AlipayTradePayModel model = new AlipayTradePayModel();
        model.setOutTradeNo(pay.getOutTradeNo()); // 预授权转支付商户订单号，为新的商户交易流水号
        model.setProductCode("PRE_AUTH_ONLINE"); // 固定值PRE_AUTH_ONLINE
//        model.setAuthNo(authNo); // 填写预授权冻结交易号
        model.setSubject("尺素：预授权转支付"); // 解冻转支付标题，用于展示在支付宝账单中
        model.setTotalAmount(String.valueOf(pay.getTotalAmount())); // 结算支付金额
        model.setSellerId(mp.getCompanyId()); // 填写卖家支付宝账户pid
        model.setBuyerId(pay.getBuyerId()); // 填写预授权用户uid，通过预授权冻结接口返回的payer_user_id字段获取
        // model.setStoreId("test_store_id"); // 填写实际交易发生的终端编号，与预授权的outStoreCode保持一致即可
        model.setBody("预授权冻结转支付:" + pay.getOutTradeNo()); // 可填写备注信息
//        model.setAuthConfirmMode("");//必须使用COMPLETE,传入该值用户剩余金额会自动解冻
        request.setBizModel(model);
        request.setNotifyUrl(mp.getFreezePayNotify());
        return (AlipayTradePayResponse) AliUtil.request(request,mp);

    }
    /**
     * 预授权解冻  2.0
     *
     */
    public static AlipayFundAuthOrderUnfreezeResponse unFreeze(AliMp mp, Pay pay) {
        AlipayFundAuthOrderUnfreezeRequest request = new AlipayFundAuthOrderUnfreezeRequest();
        //参数设置
        AlipayFundAuthOrderUnfreezeModel model = new AlipayFundAuthOrderUnfreezeModel();
//        model.setAuthNo(authNo); // 支付宝资金授权订单号，在授权冻结成功时返回需要入库保存
//        model.setOutRequestNo(outRequestNo);//同一商户每次不同的资金操作请求，商户请求流水号不能重复,且与冻结流水号不同
//        model.setAmount(amount); // 本次操作解冻的金额，单位为：元（人民币），精确到小数点后两位
//        model.setRemark("尺素：预授权解冻"); // 商户对本次解冻操作的附言描述，长度不超过100个字母或50个汉字
        //选填字段，信用授权订单，针对0元订单，传入该值完结信用订单，形成芝麻履约记录
//        model.setExtraParam("{\"unfreezeBizInfo\":\"{\\\"bizComplete\\\":\\\"true\\\"}\"}");
        request.setBizModel(model);
        request.setNotifyUrl(mp.getUnFreezeNotify());
        return (AlipayFundAuthOrderUnfreezeResponse) AliUtil.request(request,mp);
    }



}
