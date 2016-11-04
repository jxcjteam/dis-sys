package cn.ds.model;

import java.io.Serializable;

/**
 * 微信支付请求数据
 * @author zj
 * @date 2016/1/27
 */
public class WxpayReq implements Serializable {

    private String appid;//开放平台ID，公众号身份的唯一标识，从配置中读取
    private String partnerid;//商户ID MCH_ID，身份标识 10位数字串，最初申请的是8位的，从配置中读取
    private String prepayId;//预支付ID
    private String packageValue;//固定 "Sign=WXPay"
    private String nonceStr;//随机字符串
    private String timeStamp;//时间戳
    private String sign;//签名

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
