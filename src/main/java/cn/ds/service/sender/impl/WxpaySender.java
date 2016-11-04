package cn.ds.service.sender.impl;

import cn.ds.commons.PaymentExceptionFactor;
import cn.ds.commons.config.WxConfig;
import cn.ds.commons.utils.CommonDateParseUtil;
import cn.ds.commons.utils.MD5;
import cn.ds.commons.utils.NumberUtil;
import cn.ds.commons.utils.PrepayUtil;
import cn.ds.model.WxpayReq;
import cn.ds.model.enums.PaymentStatus;
import cn.ds.service.sender.PaymentSender;
import cn.zb.commons.util.ApiLogger;
import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * WxpaySender
 *
 * @author zj
 * @date 2016/1/27
 */
@Service
public class WxpaySender implements PaymentSender<WxpayReq> {

    /**
     * 微信支付
     *
     * @param transId       支付ID
     * @param title         支付标题
     * @param desc          支付描述
     * @param totalFee      支付金额
     * @param expireTime    支付过期时间
     * @param ip            支付ip
     * @return
     */
    @Override
    public WxpayReq pay(long transId, String title, String desc, long totalFee, long expireTime, String ip) {
        WxpayReq payReq = new WxpayReq();
        String nonceStr = genNonceStr();//随机串
        String expire = CommonDateParseUtil.date2string(new Date(expireTime), CommonDateParseUtil.YYYYMMDDHHMMSS);
        String entity = genProductArgs(transId, nonceStr, ip, title, totalFee, expire);
        if (null == entity || entity.length() <= 0) {
            throw PaymentExceptionFactor.PAYMENT_DATA_ERROR;
        }
        ApiLogger.info("entity:"+entity);
        String prepayId = PrepayUtil.getPrepayId(WxConfig.prepayIdUrl, entity);
        if (null == prepayId || "".equals(prepayId)) {
            throw PaymentExceptionFactor.PAYMENT_PREPAYID_ERROR;
        }
        payReq.setAppid(WxConfig.appId);
        payReq.setPartnerid(WxConfig.mchId);
        payReq.setPrepayId(prepayId);
        payReq.setPackageValue("Sign=WXPay");
        payReq.setNonceStr(nonceStr);
        payReq.setTimeStamp(String.valueOf(genTimeStamp()));

        //拼接签名数据包
        StringBuilder sb = new StringBuilder();
        sb.append("appid=" + payReq.getAppid());
        sb.append("&noncestr=" + payReq.getNonceStr());
        sb.append("&package=" + payReq.getPackageValue());
        sb.append("&partnerid=" + payReq.getPartnerid());
        sb.append("&prepayid=" + payReq.getPrepayId());
        sb.append("&timestamp=" + payReq.getTimeStamp());
        sb.append("&key=");
        sb.append(WxConfig.apiKey);

        //生成签名
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        if (null == appSign || appSign.length() <= 0) {
            throw PaymentExceptionFactor.PAYMENT_SIGN_ERROR;
        }
        payReq.setSign(appSign);
        return payReq;
    }

    /**
     * 异步回调处理--微信
     *
     * @param params    异步通知数据
     * @return
     */
    @Override
    public String asynNotify(Map<String, String> params) {
        String result = "fail";
        if (!compareSign(params)) {
            ApiLogger.error("sign compare error! params:" + JSON.toJSONString(params));
            return result;
        }
        if (params.get("return_code").equals("SUCCESS") && params.get("result_code").equals("SUCCESS")) {
            result = "success";
            //TODO 支付成功业务逻辑处理

        } else {
            ApiLogger.error("支付失败!return_msg:" + params.get("return_msg"));
        }

        return result;
    }

    /**
     * 查询交易结果--微信
     *
     * @param transId
     * @return
     */
    @Override
    public boolean tradeResult(long transId) {
        String args = genProductArgs(transId);
        if (null == args || args.isEmpty()) {
            return false;
        }
        Map map = PrepayUtil.getTrade(WxConfig.tradeResutlUrl, args);
        ApiLogger.info("调用微信订单查询接口！args:" + args + ",result:" + JSON.toJSONString(map));
        if (null == map) {
            return false;
        }
        if (!compareSign(map)) {
            ApiLogger.error("sign compare error! map:" + JSON.toJSONString(map));
            return false;
        }

        boolean result = false;
        if ("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))) {
            String tradeState = String.valueOf(map.get("trade_state"));
            switch (tradeState) {
                case "SUCCESS":
                case "REFUND":
                    result = true;
                    //TODO 支付成功业务逻辑处理
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }

    /**
     * 获取支付默认状态
     *
     * @return
     */
    @Override
    public PaymentStatus getDefaultPaymentStatus() {
        return PaymentStatus.PaymentUnpaid;
    }

    /**
     * 生成随即字符串，用于签名
     *
     * @return
     */
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成预支付ID请求数据
     *
     * @param transId
     * @param nonceStr
     * @param ip
     * @param body
     * @param total_fee
     * @param expireTime
     * @return
     */

    private String genProductArgs(long transId, String nonceStr, String ip, String body, long total_fee, String expireTime) {
        try {

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new NameValuePair("appid", WxConfig.appId));
            packageParams.add(new NameValuePair("body", body));
            packageParams.add(new NameValuePair("mch_id", WxConfig.mchId));
            packageParams.add(new NameValuePair("nonce_str", nonceStr));
            packageParams.add(new NameValuePair("notify_url", WxConfig.notifyUrl));
            packageParams.add(new NameValuePair("out_trade_no", String.valueOf(transId)));
            packageParams.add(new NameValuePair("spbill_create_ip", ip));
            packageParams.add(new NameValuePair("time_expire", expireTime));
            packageParams.add(new NameValuePair("total_fee", String.valueOf(NumberUtil.myTfen(total_fee))));//精确到分
            packageParams.add(new NameValuePair("trade_type", "APP"));

            String sign = genPackageSign(packageParams);
            if (null == sign) {
                ApiLogger.error("生成签名包失败！" + JSON.toJSONString(packageParams));
                return null;
            }
            packageParams.add(new NameValuePair("sign", sign));
            String xmlstring = toXml(packageParams);
            return xmlstring;

        } catch (Exception e) {
            ApiLogger.error("genProductArgs fail" + e);
            return null;
        }
    }

    /**
     * 生成交易结果查询请求数据
     *
     * @param transId
     * @return
     */
    private String genProductArgs(long transId) {
        try {
            List<NameValuePair> packageParams = new LinkedList<>();
            packageParams.add(new NameValuePair("appid", WxConfig.appId));
            packageParams.add(new NameValuePair("mch_id", WxConfig.mchId));
            packageParams.add(new NameValuePair("nonce_str", genNonceStr()));
            packageParams.add(new NameValuePair("out_trade_no", String.valueOf(transId)));
            String sign = genPackageSign(packageParams);
            packageParams.add(new NameValuePair("sign", sign));
            String xmlstring = toXml(packageParams);
            return xmlstring;

        } catch (Exception e) {
            ApiLogger.error("genProductArgs fail" + e);
            return null;
        }
    }

    /**
     * 生成Package签名
     *
     * @param params
     * @return
     */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WxConfig.apiKey);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    /**
     * 转xml
     *
     * @param params
     * @return
     */
    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            String name = params.get(i).getName();
            String value = params.get(i).getValue();
            if ("attach".equalsIgnoreCase(name) || "body".equalsIgnoreCase(name)
                    || "sign".equalsIgnoreCase(name)) {
                sb.append("<" + name + ">" + "<![CDATA[" + value + "]]></" + name + ">");
            } else {
                sb.append("<" + name + ">" + value + "</" + name + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 生成时间戳
     *
     * @return
     */
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 签名认证
     *
     * @param params
     * @return
     */
    private boolean compareSign(Map params) {
        ApiLogger.info("params:"+JSON.toJSONString(params));
        List<String> list = new ArrayList();
        for (Object entry : params.entrySet()) {
            Map.Entry en = (Map.Entry) entry;
            if (en.getValue() != null && !en.getValue().equals("") && !en.getKey().equals("sign")) {
                list.add(String.valueOf(en.getKey()));
            }
        }
        Collections.sort(list);
        StringBuffer string1 = new StringBuffer("");
        String stringSignTemp;
        for (String str : list) {
            string1.append(str + "=" + params.get(str) + "&");
        }
        stringSignTemp = string1 + "key=" + WxConfig.apiKey;
        String sign = MD5.getMessageDigest(stringSignTemp.getBytes()).toUpperCase();
        try{
            return params.get("sign").equals(sign);
        }catch (Exception e){
            ApiLogger.error("数据格式有问题！params：" + JSON.toJSONString(params));
        }
        return false;
    }
}
