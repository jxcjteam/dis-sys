package cn.ds.model;

import java.io.Serializable;

/**
 * RequestData
 * 支付请求数据对象
 * @author zj
 * @date 2016/1/27
 */
public class RequestData implements Serializable{

    private Long orderId;//订单ID
    private Long fromUserId;//下单支付的用户ID，与交易ID进行绑定
    private Long toUserId;//支付给用户ID,如商户公众号等
    private String subject;//订单标题
    private String body;//订单描述
    private Long totalFee;//待支付金额（金额：毫）
    private Long expireTime;//支付超时时间（绝对时间）
    private String ip;//支付时，用户使用设备的网络ip
    private String attach;//支付附带数据信息
    private Integer accountType;//账户类型，实惠现金 1人民币 2实惠现金
    private String remark;//备注
    private String batchNo;//批次号

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

}
