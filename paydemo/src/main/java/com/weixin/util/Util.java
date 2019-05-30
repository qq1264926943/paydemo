package com.weixin.util;

/**
 * @author <a href="mailto:haoran.zhang@basung.com">zhanghaoran</a>
 * @version 1.0
 * @date 15/10/29 上午11:22
 * 支付请求辅助类,这个类本来应该是Jar自带的功能，结果我没找到Jar，所以自己新建了这个辅助类，只是为了存数据而已<pre name="code" class="java">package com.jade.laiding.client.util;
*/

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import com.sun.mail.util.PropUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Util {
	
	private static final String TAG = "SDK_Sample.Util";


	public static byte[] httpGet(final String url) {
		if (url == null || url.length() == 0) {
			//Log.e(TAG, "httpGet, url is null");
			return null;
		}

		HttpClient httpClient = getNewHttpClient();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse resp = httpClient.execute(httpGet);
			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				//Log.e(TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
				return null;
			}

			return EntityUtils.toByteArray(resp.getEntity());

		} catch (Exception e) {
			//Log.e(TAG, "httpGet exception, e = " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] httpPost(String url, String entity) {
		if (url == null || url.length() == 0) {
			//Log.e(TAG, "httpPost, url is null");
			return null;
		}
		//DefaultHttpClient httpClient = new DefaultHttpClient();//http客户端
		HttpClient httpClient = getNewHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);//连接时间
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,30000);//数据传输时间
		
		HttpPost httpPost = new HttpPost(url);
		System.out.println("==========================================");
		System.out.println("url:"+url);
		System.out.println("entity:"+entity);
		System.out.println("==========================================");
		try {
			httpPost.setEntity(new StringEntity(entity,"utf-8"));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				resp = httpClient.execute(httpPost);
				//return null;
			}

			return EntityUtils.toByteArray(resp.getEntity());
		} catch (Exception e) {
			//Log.e(TAG, "httpPost exception, e = " + e.getMessage());
			System.out.println("第一次签名连接超时");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * https双向签名认证，用于支付申请退款
	 * 
	 * */
	public static String payHttps(String url,String data) throws Exception {
		//商户id
		String MCH_ID = SitePropertiesUtils.getProperty("mch_id", null,false);
		//指定读取证书格式为PKCS12
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		//证书路径
		String path = SitePropertiesUtils.getProperty("SSL_path", null,false);
		//读取本机存放的PKCS12证书文件
		FileInputStream instream = new FileInputStream(new File(path));
		try {
			//指定PKCS12的密码(商户ID)
			keyStore.load(instream, MCH_ID.toCharArray());
			System.out.println("==================指定PKCS12的密码(商户ID)===================");
		} finally {
			instream.close();
		}
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, MCH_ID.toCharArray()).build();
		//指定TLS版本
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,new String[] {"TLSv1"},null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		//设置httpclient的SSLSocketFactory
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		try {
			HttpPost httpost = new HttpPost(url); // 设置响应头信息
			httpost.addHeader("Connection", "keep-alive");
			httpost.addHeader("Accept", "*/*");
			httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpost.addHeader("Host", "api.mch.weixin.qq.com");
			httpost.addHeader("X-Requested-With", "XMLHttpRequest");
			httpost.addHeader("Cache-Control", "max-age=0");
			httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			httpost.setEntity(new StringEntity(data, "UTF-8"));
			
			
			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				//HttpEntity entity = response.getEntity();

				String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
				//System.out.println(jsonStr);
				//EntityUtils.consume(entity);
				//return EntityUtils.toByteArray(response.getEntity());
				return jsonStr;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

	public static String ClientCustomSSL(String xml) throws Exception {
		 org.apache.http.client.HttpClient hc = new org.apache.http.impl.client.DefaultHttpClient();  
         List <NameValuePair> nvps = new ArrayList <NameValuePair>();  
         nvps.add(new BasicNameValuePair("api", xml));
         UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps,"GBK");
        
        String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        //加载证书  
        java.security.KeyStore trustStore = java.security.KeyStore.getInstance(java.security.KeyStore.getDefaultType());  
        //"123456"为制作证书时的密码  //E:\\myEclipseFile\\apiclient_cert.p12
        trustStore.load(new FileInputStream(new File("D:\\webapp\\npsoft\\gyms\\WEB-INF\\apiclient_cert.p12")), "1449143602".toCharArray());  
        org.apache.http.conn.ssl.SSLSocketFactory socketFactory = new org.apache.http.conn.ssl.SSLSocketFactory(trustStore);  
        //不校验域名  
        socketFactory.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
        //这个8446是和被访问端约定的端口，一般为443  
        org.apache.http.conn.scheme.Scheme sch = new org.apache.http.conn.scheme.Scheme("https", socketFactory, 8446);  
        hc.getConnectionManager().getSchemeRegistry().register(sch);  
        org.apache.http.client.methods.HttpPost hr = new org.apache.http.client.methods.HttpPost(url);  
        
        hr.setEntity(urlEncodedFormEntity);  
        hr.setHeader("Content-Type", "application/x-www-form-urlencoded");  
        org.apache.http.HttpResponse hres = hc.execute(hr);  
        org.apache.http.HttpEntity entity = hres.getEntity();  
        int re_code = hres.getStatusLine().getStatusCode();  
        if (re_code == 200) {  
        //your successCode here  
        String repMsg = org.apache.http.util.EntityUtils.toString(entity,"GBK");  
        }else{  
        //your failCode here  
        }  
        
        
		return null;
	}
	
	private static class SSLSocketFactoryEx extends SSLSocketFactory {      
	      
	    SSLContext sslContext = SSLContext.getInstance("TLS");      
	      
	    public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {      
	        super(truststore);      
	      
	        TrustManager tm = new X509TrustManager() {      
	      
	            public X509Certificate[] getAcceptedIssuers() {      
	                return null;      
	            }      
	      

				public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				}


				public void checkServerTrusted(X509Certificate[] chain,	String authType) throws java.security.cert.CertificateException {
				}  
	        };      
	      
	        sslContext.init(null, new TrustManager[] { tm }, null);      
	    }      
	      
		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,	port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		} 
	}  

	private static HttpClient getNewHttpClient() { 
	   try { 
	       KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
	       trustStore.load(null, null); 

	       SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore); 
	       sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 

	       HttpParams params = new BasicHttpParams(); 
	       HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
	       HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 

	       SchemeRegistry registry = new SchemeRegistry(); 
	       registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	       registry.register(new Scheme("https", sf, 443));

	       ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	       return new DefaultHttpClient(ccm, params); 
	   } catch (Exception e) { 
	       return new DefaultHttpClient(); 
	   } 
	}
	
	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			//Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		//Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

		if(offset <0){
			//Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if(len <=0 ){
			//Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if(offset + len > (int) file.length()){
			//Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // ´´½¨ºÏÊÊÎÄ¼þ´óÐ¡µÄÊý×é
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			//Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}

	
	public static String sha1(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes());
			
			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<String> stringsToList(final String[] src) {
		if (src == null || src.length == 0) {
			return null;
		}
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < src.length; i++) {
			result.add(src[i]);
		}
		return result;
	}
}
