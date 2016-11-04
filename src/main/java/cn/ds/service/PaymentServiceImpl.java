package cn.ds.service;

import cn.ds.commons.PaymentExceptionFactor;
import cn.ds.model.RequestData;
import cn.ds.model.enums.PaymentType;
import cn.ds.service.sender.PaymentSender;
import cn.ds.service.sender.impl.AlipaySender;
import cn.ds.service.sender.impl.WxpaySender;
import cn.zb.commons.util.ApiLogger;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

/**
 * PaymentServiceImpl
 *
 * @author zj
 * @date 2016/10/30
 */
@Service(value = "paymentService")
public class PaymentServiceImpl implements PaymentService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 根据具体的类型初始化实例
     *
     * @param type
     * @return
     */
    PaymentSender getInstance(PaymentType type) {
        PaymentSender paymentSender = null;
        switch (type) {
            case Alipay:
                paymentSender = applicationContext.getBean(AlipaySender.class);
                break;
            case Wxpay:
                paymentSender = applicationContext.getBean(WxpaySender.class);
                break;
            case NoAccount:
                break;
            default:
                ApiLogger.error("支付方式不存在!" + type);
                break;
        }
        return paymentSender;
    }

    @Override
    public Serializable initPay(PaymentType type, RequestData data) {
        ApiLogger.info("初始化支付请求数据！type:"+type+",data:"+ JSON.toJSONString(data));
        //检查支付请求数据的有效性
        checkRequestData(data);
        //实例化具体支付对象
        PaymentSender<Serializable> instance = getInstance(type);
        //简单暴力，订单号作为交易号
        long transId = data.getOrderId();
        //订单标题
        String subject = data.getSubject();
        //订单描述
        String body = data.getBody();
        //订单金额
        long totalFee = data.getTotalFee();
        //订单超时时间
        long expireTime = data.getExpireTime();
        //下单ip
        String ip = data.getIp();
        //组装唤起第三方支付平台请求数据
        Serializable result = instance.pay(transId, subject, body, totalFee, expireTime, ip);//获取支付拉起数据
        return result;
    }

    @Override
    public Serializable continuePay(PaymentType type, long transId, RequestData data) {
        ApiLogger.info("初始化支付请求数据！type:"+type+",data:"+ JSON.toJSONString(data));
        //检查支付请求数据的有效性
        checkRequestData(data);
        //实例化具体支付对象
        PaymentSender<Serializable> instance = getInstance(type);
        //订单标题
        String subject = data.getSubject();
        //订单描述
        String body = data.getBody();
        //订单金额
        long totalFee = data.getTotalFee();
        //订单超时时间
        long expireTime = data.getExpireTime();
        //下单ip
        String ip = data.getIp();
        //组装唤起第三方支付平台请求数据
        Serializable pay = instance.pay(transId, subject, body, totalFee, expireTime, ip);
        return pay;
    }

    @Override
    public String asynNotify(PaymentType type, Map<String, String> params) {
        PaymentSender instance = getInstance(type);
        return instance.asynNotify(params);
    }

    @Override
    public boolean paymentResult(long transId) {
        ApiLogger.info("查询订单支付结果!transId:" + transId);
        boolean result = false;
        //TODO 业务逻辑
        PaymentType paymentType = PaymentType.Alipay;
        PaymentSender instance = getInstance(paymentType);
        result = instance.tradeResult(transId);
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     *
     * 支付请求数据有效性检查
     *
     * @param data
     * @return
     */
    private void checkRequestData(RequestData data) {
        if(null == data){
            throw PaymentExceptionFactor.INVALID_PARAMETER_EXCEPTION;
        }
        if(null == data.getTotalFee() || data.getTotalFee() <= 0){
            throw PaymentExceptionFactor.INVALID_PAYMENT_MONEY;
        }
        String subject = data.getSubject();
        if(null == subject || "".equals(subject) || subject.length() > 30){
            throw PaymentExceptionFactor.INVALID_PAYMENT_TITLE;
        }
        String body = data.getBody();
        if(null == body || "".equals(body) || body.length() > 30){
            throw PaymentExceptionFactor.INVALID_PAYMENT_DESC;
        }
        Long expireTime = data.getExpireTime();
        long millis = System.currentTimeMillis();
        if(null == expireTime || expireTime < millis + 1*60*1000){
            ApiLogger.error("支付超时时间过短，不能少于1分钟！data:"+JSON.toJSONString(data));
            throw PaymentExceptionFactor.INVALID_PAYMENT_EXPIRETIME;
        }
    }

}
