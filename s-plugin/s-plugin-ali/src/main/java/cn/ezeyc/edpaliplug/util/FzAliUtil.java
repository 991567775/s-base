package cn.ezeyc.edpaliplug.util;

import cn.ezeyc.edpaliplug.config.AliMp;
import cn.ezeyc.edpaliplug.pojo.Pay;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.request.*;
import com.alipay.api.response.*;

/**
 * 支付宝商家分账 接口
 */
public class FzAliUtil {

    /**
     * 分账关系绑定
     * @return
     */
    public  static AlipayTradeRoyaltyRelationBindResponse fzRelationIn (AliMp mp, Pay pay){
        AlipayTradeRoyaltyRelationBindRequest request = new AlipayTradeRoyaltyRelationBindRequest();
        //参数设置
        request.setBizContent("{" +
                "  \"receiver_list\":[" +
                "    {" +
                "      \"type\":\"userId\"," +
                "      \"account\":\"2088xxxxx00\"," +
                "      \"name\":\"测试名称\"," +
                "      \"memo\":\"分账给测试商户\"" +
                "    }" +
                "  ]," +
                "  \"out_request_no\":\"2019032200000001\"" +
                "}");
        return (AlipayTradeRoyaltyRelationBindResponse) AliUtil.request(request,mp);

    }

    /**
     * 分账关系解绑
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeRoyaltyRelationUnbindResponse fzRelationOut(AliMp mp, Pay pay){
        AlipayTradeRoyaltyRelationUnbindRequest request = new AlipayTradeRoyaltyRelationUnbindRequest();
        request.setBizContent("{" +
                "      \"receiver_list\":[{" +
                "        \"type\":\"userId\"," +
                "\"account\":\"2088xxxxx00\"," +
                "\"name\":\"测试名称\"," +
                "\"memo\":\"分账给测试商户\"" +
                "        }]," +
                "\"out_request_no\":\"2019032200000001\"" +
                "  }");
        return (AlipayTradeRoyaltyRelationUnbindResponse) AliUtil.request(request,mp);
    }

    /**
     * 分账关系查询
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeRoyaltyRelationBatchqueryResponse fzRelationSearch(AliMp mp, Pay pay){
        AlipayTradeRoyaltyRelationBatchqueryRequest request = new AlipayTradeRoyaltyRelationBatchqueryRequest();
        request.setBizContent("{" +
                "\"page_num\":1," +
                "\"page_size\":20," +
                "\"out_request_no\":\"2019032200000001\"" +
                "  }");
        return (AlipayTradeRoyaltyRelationBatchqueryResponse) AliUtil.request(request,mp);
    }

    /**
     * 分账支付
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeOrderSettleResponse fzPay(AliMp mp, Pay pay){
        AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
        //参数
        request.setBizContent("{" +
                "  \"out_request_no\":\"20160727001\"," +
                "  \"trade_no\":\"2014030411001007850000672009\"," +
                "  \"royalty_parameters\":[" +
                "    {" +
                "      \"royalty_type\":\"transfer\"," +
                "      \"trans_out\":\"2088101126765726\"," +
                "      \"trans_out_type\":\"userId\"," +
                "      \"trans_in_type\":\"userId\"," +
                "      \"trans_in\":\"2088101126708402\"," +
                "      \"amount\":0.1," +
                "      \"amount_percentage\":100," +
                "      \"desc\":\"分账给2088101126708402\"," +
                "      \"royalty_scene\":\"达人佣金\"," +
                "      \"trans_in_name\":\"张三\"" +
                "    }" +
                "  ]," +
                "  \"operator_id\":\"A0001\"," +
                "  \"extend_params\":{" +
                "    \"royalty_finish\":\"true\"" +
                "  }," +
                "  \"royalty_mode\":\"async\"" +
                "}");
        return (AlipayTradeOrderSettleResponse) AliUtil.request(request,mp);
    }
    /**
     * 统一收单线下交易查询
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeQueryResponse fzPayAllSearch(AliMp mp, Pay pay){
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20150320010101001");
//bizContent.put("trade_no", "2014112611001004680073956707");
        request.setBizContent(bizContent.toString());

        return (AlipayTradeQueryResponse) AliUtil.request(request,mp);
    }


    /**
     * 分账交易查询
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeOrderSettleQueryResponse fzPaySearch(AliMp mp, Pay pay){
        AlipayTradeOrderSettleQueryRequest request = new AlipayTradeOrderSettleQueryRequest();
        request.setBizContent("{" +
                "\"settle_no\":\"20210706002530020036530021395831\"" +
                "  }");
        return (AlipayTradeOrderSettleQueryResponse) AliUtil.request(request,mp);
    }
    /**
     * 分账退款
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeRefundResponse fzRefund(AliMp mp, Pay pay){
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        //参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", "2021081722001419121412730660");
        bizContent.put("refund_amount", 0.01);
        bizContent.put("out_request_no", "HZ01RF001");
        //// 返回参数选项，按需传入
        //JSONArray queryOptions = new JSONArray();
        //queryOptions.add("refund_detail_item_list");
        //bizContent.put("query_options", queryOptions);

        request.setBizContent(bizContent.toString());
        return (AlipayTradeRefundResponse) AliUtil.request(request,mp);
    }
    /**
     * 分账退款查询
     * @param mp
     * @param pay
     * @return
     */
    public  static AlipayTradeFastpayRefundQueryResponse fzRefundSearch(AliMp mp, Pay pay){
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", "2021081722001419121412730660");
        bizContent.put("out_request_no", "HZ01RF001");
        //// 返回参数选项，按需传入
        //JSONArray queryOptions = new JSONArray();
        //queryOptions.add("refund_detail_item_list");
        //bizContent.put("query_options", queryOptions);
        request.setBizContent(bizContent.toString());
        return (AlipayTradeFastpayRefundQueryResponse) AliUtil.request(request,mp);
    }

}
