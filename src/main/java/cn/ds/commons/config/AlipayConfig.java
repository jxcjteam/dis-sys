package cn.ds.commons.config;


import cn.zb.commons.framework.tools.EnvUtil;

/**
 * 类名：AlipayConfig
 * 功能：基础配置类
 * 详细：设置帐户有关信息及返回路径
 *
 * @author zj
 */
public class AlipayConfig {

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串
    public static String partner = "";

    // 交易安全检验码，由数字和字母组成的32位字符串
    // 如果签名方式设置为“MD5”时，请设置该参数
    public static String key = "";

    // 商户的私钥
    public static String privatekey = "";

    // 支付宝的公钥，无需修改该值
    public static String aliPublicKey  = "";

    // 卖家支付宝账号
    public static String sellerEmail = "";

    //支付宝提供给商户的服务接入网关URL(新)
    public static final String alipayGatewayNew = "https://mapi.alipay.com/gateway.do?";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String inputCharset = "utf-8";

    // 签名方式 不需修改
    public static String signType = "RSA";

    // 支付异步通知URL
    public static final String notifyUrl;

    static {
        EnvUtil.Env env = EnvUtil.getEnv();
        switch (env) {
            case prod:
                notifyUrl = "http://order.hiwemeet.com/v3/payment/notify/alipay";
                break;
            default:
                notifyUrl = "http://test2.order.hiwemeet.com/v3/payment/notify/alipay";
                break;
        }
    }


}
