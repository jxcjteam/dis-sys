package cn.ds.service.sender;

import cn.ds.model.enums.PaymentStatus;
import java.util.Map;

/**
 * PaymentSender
 *
 * @author zj
 * @date 2016/1/27
 */
public interface PaymentSender<T> {

    /**
     * 首次支付/微信或支付宝/银联
     *
     * @param transId       支付ID
     * @param title         支付标题
     * @param desc          支付描述
     * @param totalFee      支付金额
     * @param expireTime    支付过期时间
     * @param ip            支付ip
     * @return
     */
    T pay(long transId, String title, String desc, long totalFee, long expireTime, String ip);

    /**
     * 异步回调处理--支付宝/微信
     *
     * @param params    异步通知数据
     * @return
     */
    String asynNotify(Map<String, String> params);

    /**
     * 查询交易结果--支付宝/微信
     *
     * @param transId
     * @return
     */
    boolean tradeResult(long transId);

    /**
     * 获取支付默认状态
     *
     * @return
     */
    PaymentStatus getDefaultPaymentStatus();
}
