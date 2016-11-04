package cn.ds.service.sender.impl;

import cn.ds.commons.PaymentExceptionFactor;
import cn.ds.commons.utils.*;
import cn.ds.commons.config.AlipayConfig;
import cn.ds.model.AlipayReq;
import cn.ds.model.enums.PaymentStatus;
import cn.ds.service.sender.PaymentSender;
import cn.zb.commons.http.ApacheHttpClient;
import cn.zb.commons.util.ApiLogger;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AlipaySender
 *
 * @author zj
 * @date 2016/1/27
 */
@Service
public class AlipaySender implements PaymentSender<AlipayReq> {

    //请求httpclient
    private ApacheHttpClient httpClient = new ApacheHttpClient(3000, 5000);

    /**
     * 支付宝支付
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
    public AlipayReq pay(long transId, String title, String desc, long totalFee, long expireTime, String ip) {
        AlipayReq payReq = new AlipayReq();
        //时间格式化
        String expire = CommonDateParseUtil.date2string(new Date(expireTime), CommonDateParseUtil.YYYY_MM_DD_HH_MM_SS);
        //生成交易ID
        StringBuilder orderInfo = new StringBuilder();
        orderInfo.append("partner=" + "\"" + AlipayConfig.partner + "\"");//签约合作者身份ID
        orderInfo.append("&seller_id=" + "\"" + AlipayConfig.sellerEmail + "\"");//签约卖家支付宝账号
        orderInfo.append("&out_trade_no=" + "\"" + transId + "\"");//商户网站唯一订单号（交易ID）
        orderInfo.append("&subject=" + "\"" + title + "\"");//商品名称
        orderInfo.append("&body=" + "\"" + desc + "\"");//商品详情
        orderInfo.append("&total_fee=" + "\"" + NumberUtil.myToYuan(totalFee) + "\"");//待支付金额(将毫转元)
        orderInfo.append("&notify_url=" + "\"" + AlipayConfig.notifyUrl + "\"");//服务器异步通知页面路径
        orderInfo.append("&service=\"mobile.securitypay.pay\"");//服务接口名称， 固定值
        orderInfo.append("&payment_type=\"1\"");//支付类型，固定值
        orderInfo.append("&_input_charset=\"utf-8\"");//参数编码，固定值
        orderInfo.append("&it_b_pay=" + "\"" + expire + "\"");//设置未付款交易的超时时间
        //对订单信息进行签名
        String sign = RSA.sign(orderInfo.toString(), AlipayConfig.privatekey, AlipayConfig.inputCharset);
        try {
            //对sign做URL编码
            sign = URLEncoder.encode(sign, AlipayConfig.inputCharset);
            //完整的符合支付宝参数规范的订单信息
            StringBuilder payInfo = new StringBuilder();
            payInfo.append(orderInfo.toString());//订单信息
            payInfo.append("&sign=" + "\"" + sign + "\"");//签名信息
            payInfo.append("&sign_type=" + "\"" + AlipayConfig.signType + "\"");//签名类型
            payReq.setPayInfo(payInfo.toString());
        } catch (UnsupportedEncodingException e) {
            throw PaymentExceptionFactor.PAYMENT_SIGN_ERROR;
        }
        return payReq;
    }

    /**
     * 异步回调处理--支付宝
     *
     * @param params
     * @return
     */
    @Override
    public String asynNotify(Map<String, String> params) {
        ApiLogger.info("支付宝异步回调数据params:"+ JSON.toJSONString(params));
        String result = "fail";
        if (AlipayNotify.verify(params)) {//验证成功
            ApiLogger.info("sign验证成功!params:"+JSON.toJSONString(params));
            String tradeStatus = params.get("trade_status");
            switch (tradeStatus) {
                case "TRADE_FINISHED":
                case "TRADE_SUCCESS":
                    //TODO 支付成功业务逻辑处理
                    break;
                default:
                    break;
            }
        } else {
            ApiLogger.error("支付宝异步通知，签名认证失败！params:" + JSON.toJSONString(params));
        }
        return result;
    }

    /**
     * 查询交易结果--支付宝
     *
     * @param transId
     * @return
     */
    @Override
    public boolean tradeResult(long transId) {
        Map<String, Object> valuePairs = genTradeReq(transId);
        if (null == valuePairs) {
            return false;
        }
        String responseXml = httpClient.postAsync(AlipayConfig.alipayGatewayNew, valuePairs);
        return parseResonse(transId, responseXml);
    }

    /**
     * 获取支付默认状态 -- 支付宝（待支付）
     * @return
     */
    @Override
    public PaymentStatus getDefaultPaymentStatus() {
        return PaymentStatus.PaymentUnpaid;
    }

    /**
     * 解析返回的数据
     *
     * @param transId
     * @param responseXml
     * @return
     */
    private boolean parseResonse(long transId, String responseXml) {
        ApiLogger.info("解析支付宝主动查询数据！transId:"+transId+",responseXml:"+responseXml);
        Map<String, String> map = null;
        try {
            map = XmlUtil.doXMLParse(responseXml, "utf-8");
        } catch (Exception e) {
            ApiLogger.error("xml解析成map出错.", e);
        }
        if (map == null){
            return false;
        }
        ApiLogger.info("支付宝回调数据，生成map："+JSON.toJSONString(map));
        if (!"T".equals(map.get("is_success"))) {
            ApiLogger.info("此笔交易尚未支付！transId:" + transId + ",params:" + JSON.toJSONString(map));
            return false;
        }
        Map<String, String> params = null;
        try{
            params = XmlUtil.xml2Map(map.get("response"));
        }catch (Exception e){
            ApiLogger.error("xml to map error! map:" + JSON.toJSONString(map), e);
        }
        if (params == null){
            return false;
        }
        boolean result;
        String tradeStatus = params.get("trade_status");
        switch (tradeStatus) {
            case "TRADE_FINISHED":
            case "TRADE_SUCCESS":
            case "REFUND":
                result = true;
                //更新支付payment
                //TODO 支付成功业务逻辑处理
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * 生成交易结果请求数据
     *
     * @param transId 交易ID
     * @return
     */
    private Map<String, Object> genTradeReq(long transId) {
        Map<String, Object> packageParams = new LinkedHashMap<>();
        StringBuilder param = new StringBuilder();
        param.append("_input_charset=" + AlipayConfig.inputCharset);
        param.append("&out_trade_no=" + transId);
        param.append("&partner=" + AlipayConfig.partner);
        param.append("&service=single_trade_query");
        String mysign = MD5.sign(param.toString(), AlipayConfig.key, AlipayConfig.inputCharset);
        if (null == mysign || "".equals(mysign)) {
            ApiLogger.error("支付宝支付结果查询数据签名错误！params:" + param.toString());
            return null;
        }
        packageParams.put("_input_charset", AlipayConfig.inputCharset);
        packageParams.put("out_trade_no", String.valueOf(transId));
        packageParams.put("partner", AlipayConfig.partner);
        packageParams.put("service", "single_trade_query");
        packageParams.put("sign", mysign);
        packageParams.put("sign_type", "MD5");
        return packageParams;
    }
}
