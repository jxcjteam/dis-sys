package cn.ds.model;

import java.io.Serializable;

/**
 * 支付宝支付请求数据
 *
 * @author zj
 * @date 2016/1/27
 */
public class AlipayReq implements Serializable {

    private String payInfo;//支付宝请求数据串

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }
}