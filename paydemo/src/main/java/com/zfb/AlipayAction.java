package com.zfb;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.opensymphony.xwork2.ActionSupport;

public class AlipayAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{
	private HttpServletRequest request;
	private HttpServletResponse response;

	public String getAalipayQm(){
		try {
			String payables = "0.01"; 
			String subject = "风云报名费用";
			String body= "风云报名费用-描述"; 
	        String result = getAalipayIumpSum(payables,subject,body);
	        System.out.println(result);
	        AlipayConfig.logResult(result);// 记录支付日志
	        response.setContentType("text/html; charset=gbk");
	        PrintWriter out = response.getWriter();
	        out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * 电脑网站支付
	 * @param payables	支付金额
	 * @param subject	订单标题
	 * @param body		订单描述
	 * @return
	 * @throws Exception
	 */
	public String getAalipayIumpSum(String payables,String subject,String body) throws Exception {
        // 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id,
                AlipayConfig.merchant_private_key, "json", AlipayConfig.charset,
                AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = sdf.format(new Date());
        // 付款金额，必填
        String total_amount = payables.replace(",", "");
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\"" + total_amount
                + "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        // 请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return result;
	}
	
	/////////////////////////////////////手机支付开始//////////////////////////////////////////////
	public String getAalipayWappay(){
		// 商户订单号，商户网站订单系统中唯一订单号，必填
	    String out_trade_no = String.valueOf(new Date().getTime());
		// 订单名称，必填
	    String subject = "风云报名费用";
		System.out.println(subject);
	    // 付款金额，必填
	    String total_amount= "0.01";
	    // 商品描述，可空
	    String body = "风云报名费用-描述";
	    // 超时时间 可空
	   String timeout_express="2m";
	    // 销售产品码 必填
	    String product_code="QUICK_WAP_WAY";
	    /**********************/
	    // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
	    //调用RSA签名方式
	    AlipayClient client = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, 
	    		AlipayConfig.merchant_private_key, AlipayConfig.format, AlipayConfig.charset, 
	    		AlipayConfig.alipay_public_key,AlipayConfig.sign_type);
	    AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();
	    
	    // 封装请求支付信息
	    AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
	    model.setOutTradeNo(out_trade_no);
	    model.setSubject(subject);
	    model.setTotalAmount(total_amount);
	    model.setBody(body);
	    model.setTimeoutExpress(timeout_express);
	    model.setProductCode(product_code);
	    alipay_request.setBizModel(model);
	    // 设置异步通知地址
	    alipay_request.setNotifyUrl(AlipayConfig.notify_url);
	    // 设置同步地址
	    alipay_request.setReturnUrl(AlipayConfig.return_url);   
	    
	    // form表单生产
	    String form = "";
		try {
			// 调用SDK生成表单
			form = client.pageExecute(alipay_request).getBody();
			System.out.println(form);
			response.setContentType("text/html;charset=" + AlipayConfig.charset); 
	        PrintWriter out = response.getWriter();
	        out.print(form);
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return SUCCESS;
	}
	/////////////////////////////////////手机支付结束//////////////////////////////////////////////
	
	/**
	 * 
	 * @return
	 */
	public String getAalipayNotify(){
		System.out.println("=================进入商户getAalipayNotify====================");
		//详情返回参数，https://docs.open.alipay.com/270/105902/
		String trade_status = request.getParameter("trade_status");//创建订单未付款WAIT_BUYER_PAY  支付成功 TRADE_SUCCESS
		String trade_no = request.getParameter("trade_no");//支付宝交易凭证号，相当于支付宝订单号
		String out_trade_no = request.getParameter("out_trade_no");//原支付请求的商户订单号
		String out_biz_no = request.getParameter("out_biz_no");//商户业务ID，主要是退款通知中返回退款申请的流水号
		if (trade_status != null && trade_status.equals(trade_status)) {
			System.out.println("支付成功");
		}else{
			System.out.println("支付失败");
		}
		return "notify_url";
	}
	
	public String getAalipayReturn(){
		System.out.println("=================进入客户返回getAalipayReturn====================");
		return "return_url";
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
}
