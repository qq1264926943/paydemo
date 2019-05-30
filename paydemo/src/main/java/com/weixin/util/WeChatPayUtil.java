package com.weixin.util;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信支付
 * 2017-3-20
 * @author Dege
 *
 */
public class WeChatPayUtil {

    private static String WeChat_APP_ID;
    private static final String WeChat_MCH_ID="#####################";
    private static String WeChat_ID="1449143602";
    private static String WeChat_API_KEY="D04C42D5CBD8E92E377B10AC3832F39C";//西湖体育馆商户API秘钥
    //private static final String WeChat_UNIFIEDORDER="https://api.mch.weixin.qq.com/pay/unifiedorder";
    
    public static void resetRedirectAndAppid(){
    	WeChat_APP_ID=SitePropertiesUtils.getProperty("appid", null,
				false);
	WeChat_ID=SitePropertiesUtils.getProperty("mch_id", null,
			false);
	WeChat_API_KEY=SitePropertiesUtils.getProperty("weChat_key", null,
			false);
    }
    
    //微信支付第一步，先从微信的服务器获取prepayId
    public static String getPrepayId(String url,Order order) throws UnsupportedEncodingException{
    	resetRedirectAndAppid();
        Map<String,String> xml=new HashMap<String,String>();
        String prePayId = "";

        if(order != null){
            String entity = genProductArgs(order);
            byte[] buf = Util.httpPost(url, entity);
            
            if(buf != null){

                String content = new String(buf);
                String zz = new String(content.getBytes("GBK"), "utf-8");
                //Log.e("orion", content);
                try {
                    Document doc = (Document)DocumentHelper.parseText(content);
                    Element root = doc.getRootElement();
                    List<Element> list = root.elements();
                    for (Element e : list) {
                        xml.put(e.getName(), e.getText());
                    }
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                if(xml != null){
                    prePayId = xml.get("prepay_id");
                    String xx = new String(xml.get("return_msg").getBytes("GBK"), "utf-8");
                    //String xx = new String(xml.get("return_msg").getBytes("GBK"), "utf-8");
                    System.out.println(xml);
                    return prePayId;
                }
            }
        }

        return null;
    }



    //第二步，根据prepayId生成签名参数
    public static PayReq genPayReq(PayReq req,String prePayId) {
        req.setPrepayId(prePayId);
        req.setPackageValue("prepay_id="+prePayId);//Sign=WXPay
        req.setNonceStr(genNonceStr());
        req.setTimeStamp(String.valueOf(genTimeStamp()));

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appId", req.getAppId());
        parameters.put("timeStamp", req.getTimeStamp());
        parameters.put("nonceStr", req.getNonceStr());
        parameters.put("package", req.getPackageValue());
        parameters.put("signType", "MD5");

        String characterEncoding = "UTF-8";
        String mySign = createSign(characterEncoding, parameters);
        req.setSign(mySign);
        return req;
    }
    
    /**
     * 微信支付签名算法sign
     * 
     * @param characterEncoding
     * @param parameters
     * @return
     */
    public static String createSign(String characterEncoding,
            SortedMap<Object, Object> parameters) {

        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WeChatPayUtil.WeChat_API_KEY); //KEY是商户秘钥
        String sign = MD5.MD5Encode(sb.toString(),"UTF-8").toUpperCase();
        
        return sign; // D3A5D13E7838E1D453F4F2EA526C4766
                        // D3A5D13E7838E1D453F4F2EA526C4766
    }

    //第三步，调用支付接口支付
   /* public static void Pay(IWXAPI msgApi,PayReq req){
            msgApi.sendReq(req);
    }*/


    /**
     * 拼接参数
     * @param order
     * @return
     */
    private static String genProductArgs(Order order) {
        StringBuffer xml = new StringBuffer();
        try {
            String	nonceStr = genNonceStr();
            
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            
            double amount = order.getFinalAmount().doubleValue();
            packageParams.add(new BasicNameValuePair("appid", WeChat_APP_ID));
            System.out.println("appid="+WeChat_APP_ID);
            packageParams.add(new BasicNameValuePair("body", "风云体育报名费用"));//商品描述，商品或支付单简要描述，必填
            packageParams.add(new BasicNameValuePair("mch_id", WeChat_ID));//商户号
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));//随机字符串，不长于32位。必填
            packageParams.add(new BasicNameValuePair("notify_url", order.getUrl()+"/wxpay/weixinUrlBack.do"));//接收微信支付异步通知回调地址.必填
            packageParams.add(new BasicNameValuePair("openid", order.getOpenid()));//取值如下：JSAPI，NATIVE，APP，WAP,必填
            packageParams.add(new BasicNameValuePair("out_trade_no",order.getId()));//商户系统内部的订单号,32个字符内、可包含字母,必填
            packageParams.add(new BasicNameValuePair("spbill_create_ip",order.getIp()));//APP和网页支付提交用户端ip.必填
            packageParams.add(new BasicNameValuePair("total_fee", (int) (amount*100)+""));//订单总金额，只能为整数.必填
            packageParams.add(new BasicNameValuePair("trade_type", "JSAPI"));//取值如下：JSAPI，NATIVE，APP，WAP,必填

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));//签名

            String xmlstring =toXml(packageParams);
            //xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");

            return xmlstring;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拼接异步通知成功通知微信参数
     * @return
     */
    public static String returnCodeSUCCESS() {
        StringBuffer xml = new StringBuffer();
        try {
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("return_code", "SUCCESS"));//SUCCESS表示商户接收通知成功并校验成功必填
            String xmlstring =toXml(packageParams);
            xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");
            return xmlstring;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 拼接异步通知失败通知微信参数
     * @return
     */
    public static String returnCodeFAIL() {
        StringBuffer xml = new StringBuffer();
        try {
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("return_code", "FAIL"));//FAIL失败
            String xmlstring =toXml(packageParams);
            xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");
            return xmlstring;
        } catch (Exception e) {
            return null;
        }
    }




    /**
     生成签名
     */

    private static String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WeChatPayUtil.WeChat_API_KEY);

        String packageSign = MD5.MD5Encode(sb.toString(),"UTF-8").toUpperCase();
        //Log.e("orion",packageSign);
        return packageSign;
    }

    /**
     * 转换成String格式的xml
     * @param params
     * @return
     */
    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<"+params.get(i).getName()+">");


            sb.append(params.get(i).getValue());
            sb.append("</"+params.get(i).getName()+">");
        }
        sb.append("</xml>");

        //Log.e("orion",sb.toString());
        return sb.toString();
    }

    /**
     * 生成有key的app签名
     * @param params
     * @return
     */
    private static String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WeChatPayUtil.WeChat_API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        //Log.e("orion",appSign);
        return appSign;
    }

    /**
     * 时间戳
     * @return
     */
    private static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 随机字符串
     * @return
     */
    private static  String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    } 
    
    /**
     * @description 将xml字符串转换成map
     * @param xml
     * @return Map
     */
    public static Map<String, String> readStringXmlOut(String xml) {
        Map<String, String> map = new HashMap<String, String>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            @SuppressWarnings("unchecked")
            List<Element> list = rootElt.elements();// 获取根节点下所有节点
            for (Element element : list) { // 遍历节点
                map.put(element.getName(), element.getText()); // 节点的name为map的key，text为map的value
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    /**
     * 微信退款
     * @param UserID    退款人ID
     * @param orderNum  商户订单号
     * @param refund    退款金额
     * @param total     订单总金额
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getPrepayRefund(Long UserID, Long orderNum, BigDecimal refund, BigDecimal total) throws Exception{
    	String url = SitePropertiesUtils.getProperty("refund", null, false);//微信退款接口地址
    	String entity = genProductRefund(UserID, orderNum, refund, total);
        if(entity != ""){
        	Map<String, Object> map = new HashMap<String, Object>();
        	String v_strXML = Util.payHttps(url,entity);
            
            Document doc = null;    
            boolean flag = true;
            try {    
                doc = DocumentHelper.parseText(v_strXML);    
            } catch (DocumentException e2) {    
                // TODO 自动生成 catch 块    
                e2.printStackTrace();    
            } 
            
            Element root = doc.getRootElement();// 指向根节点    
            // normal解析    
            Element result_code = root.element("result_code");//成功 SUCCESS  不成功 FAIL
            if (!result_code.getText().equals("SUCCESS")) {
            	Element err_code = root.element("err_code_des");//错误提示
            	map.put("msg", err_code.getText());//退款失败提示
            	flag = false;
			}
            Element refund_fee = root.element("refund_fee");//退款金额
            map.put("refund_fee", refund_fee.getText());
            map.put("flag", flag);
            return map;
        }
        return null;
    }
    
    /**
     * 退款-拼接参数
     * @param order
     * @return
     */
    public static String genProductRefund(Long UserID, Long orderNum, BigDecimal refund, BigDecimal total) {
        StringBuffer xml = new StringBuffer();
        try {
            String	nonceStr = genNonceStr();
            
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            
            double amRefund = refund.doubleValue();
            double amTotal = refund.doubleValue();
            String dateStr = String.valueOf(new Date().getTime());
            packageParams.add(new BasicNameValuePair("appid", WeChat_APP_ID));
            packageParams.add(new BasicNameValuePair("mch_id", WeChat_ID));//商户号
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));//随机字符串，不长于32位。必填
            packageParams.add(new BasicNameValuePair("op_user_id", String.valueOf(UserID)));//操作员帐号, 默认为商户号
            packageParams.add(new BasicNameValuePair("out_refund_no", dateStr));//商户退款单号(64)
            packageParams.add(new BasicNameValuePair("out_trade_no",String.valueOf(orderNum)));//商户系统内部的订单号,32个字符内、可包含字母,必填
            packageParams.add(new BasicNameValuePair("refund_fee",(int) (amRefund*100)+""));//退款金额
            packageParams.add(new BasicNameValuePair("total_fee",(int) (amTotal*100)+""));//订单金额
            //packageParams.add(new BasicNameValuePair("transaction_id",""));//微信生成的订单号，在支付通知中有返回
            //packageParams.add(new BasicNameValuePair("transaction_id", "4200000062201804255845618464"));//商户退款单号(64)

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));//签名

            String xmlstring =toXml(packageParams);
            //xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");

            return xmlstring;

        } catch (Exception e) {
            return null;
        }
    }
    
    /*** 
     * 核心方法，里面有递归调用 
     *  
     * @param map 
     * @param ele 
     */  
    static void ele2map(Map map, Element ele) {  
    	System.out.println(ele);  
    	// 获得当前节点的子节点  
    	List<Element> elements = ele.elements();  
    	if (elements.size() == 0) {  
    		// 没有子节点说明当前节点是叶子节点，直接取值即可  
    		map.put(ele.getName(), ele.getText());  
    	} else if (elements.size() == 1) {  
    		// 只有一个子节点说明不用考虑list的情况，直接继续递归即可  
    		Map<String, Object> tempMap = new HashMap<String, Object>();  
    		ele2map(tempMap, elements.get(0));  
    		map.put(ele.getName(), tempMap);  
    	} else {  
    		// 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的  
    		// 构造一个map用来去重  
    		Map<String, Object> tempMap = new HashMap<String, Object>();  
    		for (Element element : elements) {  
    			tempMap.put(element.getName(), null);  
    		}  
    		Set<String> keySet = tempMap.keySet();  
    		for (String string : keySet) {  
    			Namespace namespace = elements.get(0).getNamespace();  
    			List<Element> elements2 = ele.elements(new QName(string,namespace));  
    			// 如果同名的数目大于1则表示要构建list  
    			if (elements2.size() > 1) {  
    				List<Map> list = new ArrayList<Map>();  
    				for (Element element : elements2) {  
    					Map<String, Object> tempMap1 = new HashMap<String, Object>();  
    					//ele2map(tempMap1, element);  
    					list.add(tempMap1);  
    				}  
    				map.put(string, list);  
    			} else {  
    				// 同名的数量不大于1则直接递归去  
    				Map<String, Object> tempMap1 = new HashMap<String, Object>();  
    				ele2map(tempMap1, elements2.get(0));  
    				map.put(string, tempMap1);  
    			}  
    		}  
    	}  
    }  
}
