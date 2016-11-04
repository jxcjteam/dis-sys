package cn.ds.commons.config;

import cn.zb.commons.framework.tools.EnvUtil;

/**
 * 类名：WxConfig
 * 功能：基础配置类
 * 详细：设置帐户有关信息及返回路径
 *
 * @author zj
 */
public class WxConfig {

    //开放平台ID，公众号身份的唯一标识
    public static String appId;

    // 商户ID，身份标识 10位数字串，最初申请的是8位的
    public static String mchId;

    //商户支付密钥key. [微信商户后台]->[账户设置]->[密码安全]->[API安全]->[API密钥]
    public static String apiKey;

    //统一支付接口 获取prepayId
    public static final String prepayIdUrl = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");

    //交易订单查询接口
    public static final String tradeResutlUrl = "https://api.mch.weixin.qq.com/pay/orderquery";

    // 异步通知URL
    public static final String notifyUrl;


    static {
        EnvUtil.Env env = EnvUtil.getEnv();
        switch (env) {
            case prod:
                appId = "";
                mchId = "";
                apiKey = "";
                notifyUrl = "";
                break;
            default:
                appId = "";
                mchId = "";
                apiKey = "";
                notifyUrl = "";
                break;
        }
    }
}
