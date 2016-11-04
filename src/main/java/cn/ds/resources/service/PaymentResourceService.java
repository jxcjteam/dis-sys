package cn.ds.resources.service;

import cn.ds.model.enums.PaymentType;

import java.util.Map;

/**
 * PaymentResourceService
 *
 * @author zj
 * @date 2016/11/4
 */
public interface PaymentResourceService {
    /**
     * 支付异步回调处理
     *
     * @param type
     * @param map
     * @return
     */
    String callBack(PaymentType type, Map map);
}
