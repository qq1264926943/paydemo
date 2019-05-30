<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>微信支付测试</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    weixinPAyList
    <button onclick="pay();">点击支付</button>
    <script type="text/javascript">
    function pay() {
    	if (typeof WeixinJSBridge == "undefined") {
			if (document.addEventListener) {
				document.addEventListener('WeixinJSBridgeReady',
						onBridgeReady, false);
			} else if (document.attachEvent) {
				document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
				document
						.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
			}
			//alert("初始化失败");
		} else {
			onBridgeReady();
		}
    }
    function onBridgeReady() {
		alert('appId=${payReq.appId},timeStamp=${payReq.timeStamp},nonceStr=${payReq.nonceStr},package=${payReq.packageValue},paySign=${payReq.sign}');
		WeixinJSBridge.invoke('getBrandWCPayRequest', {
			"appId" : '${payReq.appId}', //公众号名称，由商户传入     
			"timeStamp" : '${payReq.timeStamp}', //时间戳，自1970年以来的秒数     
			"nonceStr" : '${payReq.nonceStr}', //随机串     
			"package" : '${payReq.packageValue}',
			"signType" : 'MD5', //微信签名方式：     
			"paySign" : '${payReq.sign}' //微信签名 
		}, function(res) {
			if (res.err_msg == "get_brand_wcpay_request:ok") {
				alert('支付成功！');
				//getWeixUrl('sitePaySuccess.do?cdlx.id=${czdd.cdlxid}');
			} else if (res.err_msg == 'get_brand_wcpay_request:cancel') {
				alert('取消支付'); 
			} else {
				alert(res.err_msg);
			} // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
		});
		
	}
    function getWeixUrl(url){
		location.href="https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appid}&"+
		 "redirect_uri=${urlBack}${serverPath}${formSpaceName}/"+url+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
	}
    </script>
  </body>
</html>
