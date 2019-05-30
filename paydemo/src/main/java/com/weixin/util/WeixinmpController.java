package com.weixin.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.weixin.util.WeixinmpController.PostEntities.Entity;


/**
 * 公众平台控制器实现类
 * @author tangQingWang
 * @version V1.0  
 * @date 2017-3-13上午9:50:57
 */
@Service
public class WeixinmpController{
	/** XML文件储存路径 */
	private File dataDir;

	/** 编码类型 */
	private String encoding;

	/** appID */
	private String appid;
	

	/** appsecret */
	private String appsecret;

	/** AccessToken有效时间，单位：秒 */
	private Integer accessTokenExpiresTime;
	
	/** JsapiTicket有效时间，单位：秒 */
	private Integer jsapiTicketExpiresTime;
	
	/** 代理服务器 */
	private Proxy proxy;

	/** SSL */
	private SSLSocketFactory sslSocketFactory;
	

	/** 上次获取accessToken的时间 */
	private long lastAccessTokenTime = 0;
	
	/** 上次获取jsapiTicket的时间 */
	private long lastJsapiTicketTime = 0;

	/** 上次获取到的accessToken */
	private final AccessToken lastAccessToken = new AccessToken();
	/** GSON缓存 */
	private static Map<Long, Gson> gsonCache = new HashMap<Long, Gson>();
	
	public WeixinmpController(){
		encoding = SitePropertiesUtils.getProperty("encoding", "utf-8", false);
		appid = SitePropertiesUtils.getProperty("appid", null, true);
		appsecret = SitePropertiesUtils.getProperty("appsecret", null, true);
		// Integer.valueOf(SitePropertiesUtils.getProperty("timestampLimit",
		// null,
		// false));
		accessTokenExpiresTime = Integer.valueOf(SitePropertiesUtils.getProperty(
				"accessTokenExpiresTime", null, false));
		jsapiTicketExpiresTime =Integer.valueOf(SitePropertiesUtils.getProperty(
				"jsapiTicketExpiresTime", null, false));
		dataDir = new File(SitePropertiesUtils.getProperty("dataDir", null, false));
		initSSLSocketFactory();
	}
	/**
	 * 初始化SSL
	 */
	private void initSSLSocketFactory() {
		try {
			System.setProperty("https.protocols", "TLSv1");
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, null, null);
			sslSocketFactory = sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从缓存中获取AccessToken <br>
	 * 在超时有效期时自动重新获取
	 * 
	 * @param renew
	 *            是否更新token
	 * @return
	 */
	public AccessToken getAccessToken(boolean renew) throws WeixinException {
		synchronized (lastAccessToken) {
			// 如果上次获取到的token仍然在有效期则直接返回
			long now = new Date().getTime();
			if (renew
					|| (now - lastAccessTokenTime)/1000 >= accessTokenExpiresTime) {
				AccessToken token = getAccessToken(appid, appsecret);//重新获取
				lastAccessTokenTime = now;
				lastAccessToken.access_token = token.access_token;
				lastAccessToken.expires_in = token.expires_in;
			}
			return lastAccessToken;
		}
	}
	/**
	 *获取token方法
	 * @param appid
	 * @param secert
	 * @return
	 * @throws WeixinException
	 */
	private AccessToken getAccessToken(String appid, String secert)
			throws WeixinException {
		System.out.println("请求AccessToken：appid=" + appid + "，secert=" + secert);
		if (appid == null || secert == null) {
			throw new WeixinException(CommonUtils.getNextId()
					+ "_NoAppIDOrAppSecert", "调用高级服务需要提供appid和secert", null);
		}
		String url = SitePropertiesUtils
				.getProperty("accessToken_url", null, false);
		//url = url.replaceFirst("APPID", appid).replaceFirst("APPSECRET", secert);
		url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secert;
		try {
			AccessToken token = post(url, null, AccessToken.class,
					"getAccessToken");//发送请求
			return token;
		} catch (WeixinException e) {
			System.out.println("Failed to getAccessToken"+ e);
			if (e.isNeedLog()) {
				saveToFile(e.getLogFilename(), e.getLogContent());
			}
		}
		return null;
	}
	/**
	 * 执行一个post请求（以实体的方式发送）
	 * 
	 * @param url
	 *            请求地址
	 * @param entities
	 *            请求参数对象，会被转换为json字符串
	 * @param actionName
	 *            用于储存日志的动作名称
	 * @param returnType
	 *            要求返回的类型，从响应json字符串映射为这个类型的对象
	 * @return 返回returnType指定的对象
	 * @throws WeixinException
	 *             如果序列化、反序列化json出错
	 */
	protected final <T> T post(String url, PostEntities entities,
			Class<T> returnType, String actionName) throws WeixinException {
		String id = CommonUtils.getNextId();
		OutputStream out = null;
		InputStream is = null;
		DataInputStream dis = null;
		try {
			Gson gson = getGson();
			// 打开连接
			url = replaceAccessToken(url);
			HttpURLConnection conn = openConnection(url);
			//超时设置
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			// 设置请求头
			String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// 发送实体附件
			if (entities != null) {
				// 准备输出流
				out = new DataOutputStream(conn.getOutputStream());
				byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n")
						.getBytes(encoding);// 定义最后数据分隔线
				Iterator<Entity> iter = entities.entities.iterator();
				StringBuilder sb = new StringBuilder();
				int bytes = 0;
				byte[] bufferOut = new byte[8192];
				// 循环输出实体
				while (iter.hasNext()) {
					// 输出分割线
					sb.setLength(0);
					sb.append("--").append(BOUNDARY).append("\r\n");
					// 输出实体头和内容
					Entity entity = iter.next();
					switch (entity.type) {
					case binary:
						File file = (File) entity.obj;
						sb.append("Content-Disposition: form-data;name=\""
								+ entity.name + "\";filename=\""
								+ file.getAbsolutePath() + "\"\r\n");
						sb.append("Content-Type:application/octet-stream\r\n\r\n");
						out.write(sb.toString().getBytes(encoding));
						dis = new DataInputStream(new FileInputStream(file));
						while ((bytes = dis.read(bufferOut)) != -1) {
							out.write(bufferOut, 0, bytes);
						}
						break;
					case json:
						sb.append("Content-Disposition: form-data;name=\""
								+ entity.name + "\"\r\n");
						sb.append("Content-Type:text/plain\r\n\r\n");
						out.write(sb.toString().getBytes(encoding));
						String json = gson.toJson(entity.obj);
						out.write(json.getBytes(encoding));
						break;
					case text:
						sb.append("Content-Disposition: form-data;name=\""
								+ entity.name + "\"\r\n");
						sb.append("Content-Type:text/plain\r\n\r\n");
						out.write(sb.toString().getBytes(encoding));
						out.write(entity.obj.toString().getBytes(encoding));
						break;
					}
					out.write("\r\n".getBytes(encoding)); // 多个文件时，二个文件之间加入这个
					dis.close();
					out.flush();
				}
				// 输出结束符
				out.write(end_data);
				out.flush();
				out.close();
			}
			// 接收响应
			is = conn.getInputStream();
			Object[] data = CommonUtils.readXml(getDataFileDir(),
					encoding, //
					id + "_" + actionName + ".data", conn.getContentLength(),
					is);
			String json = data[1].toString();
			System.out.println("请求:" + url);
			System.out.println("返回: " + json);
			// 检查是否出现错误
//			GlobalError error = gson.fromJson(json, GlobalError.class);
//			if (error.errcode != null) {
//				if (error.errcode == 42001) {
//					// 如果token超时则重新获取
//					System.out.println("AccessToken过时，重新获取。");
//					AccessToken token = getAccessToken(true);
//					// 重新请求
//					url = url.replaceAll("access_token=[^&]+", "access_token="
//							+ token.access_token);
//					return post(url, entities, returnType, actionName);
//				} else if (error.errcode != 0) {
//					// 其他错误
//					throw new WeixinException(error.errcode, error.toString(),
//							null);
//				}
//			}
			// 请求完成
			T obj = gson.fromJson(data[1].toString(), returnType);
			return obj;
		} catch (JsonSyntaxException e) {
			throw new WeixinException(id + "_JsonSyntaxException",
					e.getMessage(), e);
		} catch (IOException e) {
			throw new WeixinException(id + "_IOException", e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	/**
	 * 返回每个线程的gson解析器
	 * 
	 * @return
	 */
	private Gson getGson() {
		long id = Thread.currentThread().getId();
		Gson gson = gsonCache.get(id);
		if (gson == null) {
			gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
					.create();
			gsonCache.put(id, gson);
		}
		return gson;
	}
	/**
	 * 替换AccessToken
	 * 
	 * @author jianqing.cai@qq.com,
	 *         https://github.com/caijianqing/weixinmp4java/, 2014-2-24
	 *         下午3:12:53
	 * @param url
	 * @return
	 * @throws WeixinException
	 */
//	private String replaceAccessToken(String url) throws WeixinException {
//		if (url.indexOf("ACCESS_TOKEN") != -1) {
//			init();
//			url = url.replaceFirst("ACCESS_TOKEN",
//					getAccessToken(false).access_token);
//		}
//		return url;
//	}
	
	//////////////////////////////////////////////////
	
	/**
	 * 请求的数据实体，用于进行post提交
	 * 
	 * @author jianqing.cai@qq.com,
	 *         https://github.com/caijianqing/weixinmp4java/
	 */
	protected static class PostEntities {

		/** 实体集合 */
		List<Entity> entities = new ArrayList<Entity>();

		/**
		 * 增加一个实体
		 * 
		 * @param type
		 *            数据类型
		 * @param name
		 *            变量名
		 * @param obj
		 *            对象
		 * @return 返回当前对象，以支持链式操作
		 */
		public PostEntities addEntity(TYPE type, String name, Object obj) {
			entities.add(new Entity(type, name, obj));
			return this;
		}

		/**
		 * 数据类型
		 * 
		 * @author jianqing.cai@qq.com,
		 *         https://github.com/caijianqing/weixinmp4java/
		 */
		public static enum TYPE {
			/** 普通文本，直接调用key=value.toString */
			text,
			/** 需要转换为json格式 */
			json,
			/** 二进制数据，对象必须为文件对象 */
			binary;
		}

		/**
		 * 数据实体
		 * 
		 * @author jianqing.cai@qq.com,
		 *         https://github.com/caijianqing/weixinmp4java/
		 */
		public static class Entity {

			/** 数据类型 */
			TYPE type;

			/** 属性名 */
			String name;

			/** 值对象 */
			Object obj;

			public Entity(TYPE type, String name, Object obj) {
				this.type = type;
				this.name = name;
				this.obj = obj;
			}

		}

	}
	/**
	 * 打开一个URL连接
	 * 
	 * @author jianqing.cai@qq.com,
	 *         https://github.com/caijianqing/weixinmp4java/, 2014-2-24
	 *         下午1:52:57
	 * @param url
	 *            需要访问的url，如果URL带有“ACCESS_TOKEN”时自动替换为有效的access_token
	 * @return 返回一个有效的connection对象
	 * @throws MalformedURLException
	 *             如果URL不符合规范
	 * @throws IOException
	 *             如果发生IO错误
	 * @throws WeixinException
	 *             如果获取accessToken时发生错误
	 */
	private HttpURLConnection openConnection(String url)
			throws MalformedURLException, IOException, WeixinException {
		// 填充accessToken
		url = replaceAccessToken(url);
		// 创建connection
		HttpURLConnection conn;
		if (proxy != null) {
			conn = (HttpURLConnection) new URL(url).openConnection(proxy);
		} else {
			conn = (HttpURLConnection) new URL(url).openConnection();
		}
		// 配置SSL
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection https = (HttpsURLConnection) conn;
			https.setSSLSocketFactory(sslSocketFactory);
		}
		return conn;
	}
	/**
	 * 把文本内容储存到数据文件
	 * 
	 * @param name
	 *            文件名
	 * @param context
	 *            需要储存的内容
	 * @throws IOException
	 */
	protected final void saveToFile(String name, String context) {
		File file = new File(getDataFileDir(), name);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file, encoding);
			pw.write(context);
			pw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	/**
	 * 返回数据目录的路径（根据年月日产生不同的目录）
	 * 
	 * @return
	 */

	protected final File getDataFileDir() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		String year = String.valueOf(y);
		String month = m >= 10 ? String.valueOf(m) : "0" + m;
		String day = d >= 10 ? String.valueOf(d) : "0" + m;
		StringBuffer sb = new StringBuffer();
		sb.append(year).append('/');
		sb.append(year).append('-').append(month).append('/');
		sb.append(year).append('-').append(month).append('-').append(day)
				.append('/');
		File dir = new File(dataDir, sb.toString());
		dir.mkdirs();
		return dir;
	}
	public void init() {
		appid=SitePropertiesUtils.getProperty("appid", null,
				false);
		appsecret = SitePropertiesUtils.getProperty("appsecret", null,
				false);;
	}
	
	/**
	 * 执行一个post请求（以json的方式发送参数）
	 * 
	 * @param url
	 *            请求地址
	 * @param param
	 *            请求参数对象，会被转换为json字符串
	 * @param returnType
	 *            要求返回的类型，从响应json字符串映射为这个类型的对象
	 * @param actionName
	 *            用于储存日志的动作名称
	 * @return 返回returnType指定的对象
	 * @throws WeixinException
	 *             如果序列化、反序列化json出错
	 * @throws IOException
	 *             如果打开连接或者保存文件的时候出错
	 */
	protected final <T> T postWithJson(String url, Object param,
			Class<T> returnType, String actionName) throws WeixinException,
			IOException {
		String id = CommonUtils.getNextId();
		OutputStream os = null;
		InputStream is = null;
		try {
			Gson gson = getGson();
			// 打开连接
			url = replaceAccessToken(url);			
			HttpURLConnection conn = openConnection(url);
			//超时设置
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setRequestMethod("POST");
			if (param != null) {
				conn.setDoOutput(true);
				String reqJson = gson.toJson(param);
				System.out.println("reqJson:" + reqJson);
				os = conn.getOutputStream();
				os.write(reqJson.getBytes(encoding)); // 必须这样getByte(encoding)才不会乱码
				os.flush();
				os.close();
			}
			// 接收响应
			is = conn.getInputStream();
			Object[] data = CommonUtils.readXml(getDataFileDir(),
					encoding, //
					id + "_" + actionName + ".data", conn.getContentLength(),
					is);
			String json = data[1].toString();
			System.out.println("请求:" + url + " ，返回: " + json);
			// 检查是否出现错误
			GlobalError error = gson.fromJson(json, GlobalError.class);
			System.out.println("error code:" + error);
			if (error.errcode != null) {
				if (error.errcode == 42001) {
					// 如果token超时则重新获取
					System.out.println("AccessToken过时，重新获取。");
					AccessToken token = getAccessToken(true);
					// 重新请求
					url = url.replaceAll("access_token=[^&]+", "access_token="
							+ token.access_token);
					return postWithJson(url, param, returnType, actionName);
				} else if (error.errcode != 0) {
					// 其他错误
					throw new WeixinException(error.errcode, error.toString(),
							null);
				}
			}
			// 请求完成
			T obj = gson.fromJson(data[1].toString(), returnType);
			return obj;
		} catch (JsonSyntaxException e) {
			throw new WeixinException(id + "_JsonSyntaxException",
					e.getMessage(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	/**
	 * 替换AccessToken
	 * 
	 * @author jianqing.cai@qq.com,
	 *         https://github.com/caijianqing/weixinmp4java/, 2014-2-24
	 *         下午3:12:53
	 * @param url
	 * @return
	 * @throws WeixinException
	 */
	private String replaceAccessToken(String url) throws WeixinException {
		if (url.indexOf("ACCESS_TOKEN") != -1) {
			init();
			url = url.replaceFirst("ACCESS_TOKEN",
					getAccessToken(false).access_token);
		}
		return url;
	}
	
	
	
	//网页授权接口调用
	/**
	 * 根据code得到openId
	 * @param code
	 * @return
	 * @throws WeixinException
	 */
//	public String getOpenIdByCode(String code) throws WeixinException {
//		String url = SitePropertiesUtils.getProperty("accessTokenByCode_url", null,
//				false);
//		url = url.replaceFirst("CODE", code);
//		try {
//			AccessTokenByCode accessTokenByCode = postWithJson(url,
//					null, AccessTokenByCode.class, "getOpenIdByCode");
//			return accessTokenByCode.openid;
//		} catch (WeixinException e) {
//			System.out.println("Failed to get getOpenIdByCode"+e);
//			throw e;
//		} catch (IOException e) {
//			System.out.println("Failed to get getOpenIdByCode:"+e);
//			throw new WeixinException(CommonUtils.getNextId() + "uploadNews",
//					e.getMessage(), e);
//		}
//	}
	public WxUserInfosByOpenID getUserInfoByOpenID(String openID,String token) throws WeixinException {
		String url = SitePropertiesUtils.getProperty("user_info_url", null,
				false);
		url = url.replaceFirst("ACCESS_TOKEN", token).replaceFirst("OPENID", openID);
		try {
			WxUserInfosByOpenID accessTokenByCode = postWithJson(url,
					null, WxUserInfosByOpenID.class, "getOpenIdByCode");
			return accessTokenByCode;
		} catch (WeixinException e) {
			System.out.println("Failed to get getOpenIdByCode"+e);
			throw e;
		} catch (IOException e) {
			System.out.println("Failed to get getOpenIdByCode"+e);
			throw new WeixinException(CommonUtils.getNextId() + "uploadNews",
					e.getMessage(), e);
		}
	}
	
	
	
}
