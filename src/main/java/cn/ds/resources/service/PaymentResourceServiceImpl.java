package cn.ds.resources.service;

import cn.ds.model.enums.PaymentType;
import cn.ds.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * PaymentResourceServiceImpl
 *
 * @author zj
 * @date 2016/11/4
 */
@Service("paymentResourceService")
public class PaymentResourceServiceImpl implements PaymentResourceService{

    @Resource
    private PaymentService paymentService;

    /**
     * 支付异步回调处理
     *
     * @param type
     * @param map
     * @return
     */
    @Override
    public String callBack(PaymentType type, Map map) {
        return paymentService.asynNotify(type , map);
    }
}
