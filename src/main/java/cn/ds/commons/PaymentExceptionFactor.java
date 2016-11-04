package cn.ds.commons;

import cn.zb.commons.framework.exception.CommonException;
import cn.zb.commons.framework.exception.ExceptionFactor;

import javax.servlet.http.HttpServletResponse;

public class PaymentExceptionFactor extends ExceptionFactor {

	private static final int LiveSystemCode = 1;

    public static final CommonException INVALID_PARAMETER_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE,LiveSystemCode, 0,
           HttpServletResponse.SC_OK, "invalid parameter!", "非法参数!");

    public static final CommonException INVALID_PAYMENT_MONEY = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 1,
           HttpServletResponse.SC_OK, "invalid payment money!", "非法支付金额!");
    
    public static final CommonException INVALID_PAYMENT_TITLE = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 2,
           HttpServletResponse.SC_OK, "invalid payment title!!", "非法支付标题!");
    
    public static final CommonException INVALID_PAYMENT_DESC = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 3,
           HttpServletResponse.SC_OK, "invalid payment desc!", "非法支付描述!");
    
    public static final CommonException INVALID_PAYMENT_EXPIRETIME = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 4,
           HttpServletResponse.SC_OK, "invalid payment expiretime!", "非法支付超时时间!");

    public static final CommonException PAYMENT_SIGN_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 5,
            HttpServletResponse.SC_OK, "sign error!", "签名错误!");

    public static final CommonException PAYMENT_DATA_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 6,
            HttpServletResponse.SC_OK, "payment data error!", "支付数据错误!");

    public static final CommonException PAYMENT_PREPAYID_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, LiveSystemCode, 7,
            HttpServletResponse.SC_OK, "generate prepayId error!", "预支付id生成失败!");
}