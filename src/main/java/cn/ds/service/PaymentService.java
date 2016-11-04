package cn.ds.service;

import cn.ds.model.RequestData;
import cn.ds.model.enums.PaymentType;

import java.io.Serializable;
import java.util.Map;

/**
 * PaymentService
 *
 * @author zj
 * @date 2016/10/30
 */
public interface PaymentService {

    /**
     * 发起支付
     *
     * @param type 支付类型
     * @param data 支付相关数据
     * @return
     */
    Serializable initPay(PaymentType type, RequestData data);

    /**
     * 继续支付
     *
     * @param type 支付类型
     * @param transId 交易ID
     * @param data 继续支付数据
     * @return
     */
    Serializable continuePay(PaymentType type, long transId, RequestData data);

    /**
     * 第三方异步通知处理
     *
     * @param type   支付类型
     * @param params 异步数据
     * @return
     */
    String asynNotify(PaymentType type, Map<String, String> params);

    /**
     * 查询支付交易情况--第三方平台
     *
     * @param transId
     * @return
     */
    boolean paymentResult(long transId);
}
