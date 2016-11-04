package cn.ds.resources;

import cn.ds.commons.utils.XmlUtil;
import cn.ds.model.enums.PaymentType;
import cn.ds.resources.service.PaymentResourceService;
import cn.zb.commons.framework.auth.Access;
import cn.zb.commons.framework.context.RequestContext;
import cn.zb.commons.util.ApiLogger;
import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * PaymentController
 *
 * @author zj
 * @date 2016/1/29 -
 */
@Controller
@RequestMapping("/v1/payment")
public class PaymentResource {

    @Resource
    private PaymentResourceService paymentResourceService;

    /**
     * 异步通知处理
     * 演示url http://127.0.0.1/payment/notify/alipay
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(path = "/notify/alipay", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @Access(type = Access.AccessType.PUBLIC)
    public @ResponseBody String alipay(HttpServletRequest request, HttpServletResponse response) {
        ApiLogger.info("进入支付宝异步回调api");
        RequestContext rc = RequestContext.getRequestContext();
        rc.getResult().setWrap(false);
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        ApiLogger.info("支付宝异步通知数据！map:" + JSON.toJSONString(params));
        return paymentResourceService.callBack(PaymentType.Alipay, params);
    }

    /**
     * 异步通知处理
     * 演示url http://127.0.0.1/payment/notify/wxpay
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(path = "/notify/wxpay", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @Access(type = Access.AccessType.PUBLIC)
    public @ResponseBody String wxpay(HttpServletRequest request, HttpServletResponse response) {
        ApiLogger.info("进入微信异步回调api");
        RequestContext context = RequestContext.getRequestContext();
        context.getResult().setWrap(false);
        Map map;
        try {
            ServletInputStream inputStream = request.getInputStream();
            map = XmlUtil.doXMLParse(inputStream);
            ApiLogger.info("微信异步通知数据！map:" + JSON.toJSONString(map));
        } catch (Exception e) {
            ApiLogger.error("微信异步回调获取数据失败：" + e.getMessage());
            return "fail";
        }
        return paymentResourceService.callBack(PaymentType.Alipay, map);
    }
}
