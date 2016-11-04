package cn.ds.model.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付类型
 *
 * @author zj
 * @date 2016/1/27
 */
public enum PaymentType implements Serializable {
    //支付类型 1支付宝 2微信支付 -1 无现金支付
    Alipay(1, "支付宝支付"),
    Wxpay(2, "微信支付"),
    NoAccount(-1, "无现金支付");//

    private int value;
    private String name;

    PaymentType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, PaymentType> values = new HashMap<>();

    static {
        for (PaymentType paymentType : PaymentType.values()) {
            values.put(paymentType.value, paymentType);
        }
    }

    public static PaymentType parse(int value) {
        return values.get(value);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
