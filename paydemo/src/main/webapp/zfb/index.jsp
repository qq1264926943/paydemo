<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
    
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
    支付宝跳转首页. <br>
    <form name="punchout_form" method="post" action="https://openapi.alipaydev.com/gateway.do?sign=WNynZ0QZDE8wtY7faxU6Jj9MUNPwcW35AJ5GMARekCOsKnUiv1270wURkCFyv5rRrkzvnecZKDjEqXjY7FunOqGdnf%2FlGePkLX1QowJ1ARjL8q4i5Ig%2Fpn6DXFDNLNcMZBjg5JbmMfy3qbzrYyUNBEiJhPpfP3ckNqs10lP32%2F0qMo08j2J6EO5zCq6dXlvixky5YS7ZXdwlH6F0d5G8oWtdepDZhruNnEmhyW3D7DBmBJDTs18E%2F3P%2BUGxFlL4iKr3kTJz1ipmlaEosTGdhnzbJA5VXGTptCXi3D%2FMnGnEWyfzlqnMKWbBDSe1jp7HP5apc5d4UilAfKPc4kWnFug%3D%3D&timestamp=2018-10-19+13%3A38%3A17&sign_type=RSA2&notify_url=http%3A%2F%2F2177x4017o.imwork.net%2Fpaydemo%2Fzfbpay%2FzfbNotify.do&charset=gbk&app_id=2016080100143949&method=alipay.trade.page.pay&return_url=http%3A%2F%2F2177x4017o.imwork.net%2Fpaydemo%2Fzfbpay%2FzfbReturn.do&version=1.0&alipay_sdk=alipay-sdk-java-dynamicVersionNo&format=json">
<input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;20181019133817528&quot;,&quot;total_amount&quot;:&quot;0.01&quot;,&quot;subject&quot;:&quot;风云报名费用&quot;,&quot;body&quot;:&quot;风云报名费用-描述&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
<input type="submit" value="立即支付" style="display:none" >
</form>
  </body>
</html>
