package cn.ds.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * 支付状态
 *
 * @author zj
 * @date 2016/1/27
 */
public enum PaymentStatus implements Serializable {

    PaymentUnpaid(1, "未付款"),
    PaymentHadPaid(2, "已付款"),
    PaymentOutOfTime(3, "整笔交易超时退回");

    private int value;
    private String name;

    PaymentStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, PaymentStatus> values = new HashMap<>();

    static {
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            values.put(paymentStatus.value, paymentStatus);
        }
    }

    public static PaymentStatus parse(int value) {
        return values.get(value);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
