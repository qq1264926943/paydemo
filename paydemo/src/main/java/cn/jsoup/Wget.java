package cn.jsoup;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import sun.net.www.protocol.http.HttpURLConnection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Wget {
	
	/**
	 * 正则匹配字符串
	 * @param text    文本
	 * @param regular 正则表达式
	 * @return
	 */
	public static String getStregular (String text, String regular) {
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(text);
		String str = null;
		while (matcher.find()) {
			str = matcher.group(1);
		}
		return str;
	}
	public static void main(String[] args) {
		try {
			String url = "http://api.52mss.com/?url=http://v.youku.com/v_show/id_XNDAwNjM3NjQ0MA==.html?spm=a2h0j.11185381.listitem_page1.5!28~A&&s=66efbfbd65efbfbd3919";//列表
			Document document = Jsoup.connect(url).get();
			System.out.println(document.toString());
			String data = getStregular(document.toString(), ", (.*)}");
			data = data+"}";
			data = data.replaceAll("jskk", "1");
			Map<String, Object> map = JSON.parseObject(data);
			System.out.println(map.toString());
			
			
			String urlStr = "http://api.52mss.com/api.php";  
	        Map<String, String> textMap = new HashMap<String, String>();  
	        Map<String, String> fileMap = new HashMap<String, String>();  
	        textMap.put("hh", map.get("hh").toString());  
	        textMap.put("time", map.get("time").toString());  
	        textMap.put("key", map.get("key").toString());  
	        textMap.put("url", map.get("url").toString());  
	        textMap.put("jsk", "101000011");  
	        System.out.println(textMap.toString());
	        String ret = formUpload(urlStr, textMap, fileMap);
	        System.out.println(ret);
		
	        
	        // 屏蔽HtmlUnit等系统 log
	       /* LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
	        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
	 
	        //String url = "https://bluetata.com/";
	        System.out.println("Loading page now-----------------------------------------------: "+url);
	        
	        // HtmlUnit 模拟浏览器
	        WebClient webClient = new WebClient(BrowserVersion.CHROME);
	        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
	        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
	        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
	        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	        webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间
	        HtmlPage page = webClient.getPage(url);
	        webClient.waitForBackgroundJavaScript(30 * 1000);               // 等待js后台执行30秒
	 
	        String pageAsXml = page.asXml();
	        
	        // Jsoup解析处理
	        Document doc = Jsoup.parse(pageAsXml, url);  
	        //Elements pngs = doc.select("img[src$=.png]");                   // 获取所有图片元素集
	        // 此处省略其他操作
	        System.out.println(doc.toString());*/
	        

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**  
     *  附件上传
     * @param urlStr  上传地址
     * @param textMap 上传文件名字
     * @param fileMap 需要上传的本地文件(服务器文件)路径
     * @return 返回结果集
     */  
    public static String formUpload(String urlStr, Map<String, String> textMap,  
            Map<String, String> fileMap) {  
        String res = "";  
        HttpURLConnection conn = null;  
        String cookie ="";
        String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符  
        try {  
        	CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            URL url = new URL(urlStr);  
            conn = (HttpURLConnection) url.openConnection();  
            conn.setConnectTimeout(20000);  //设置超时20秒
            conn.setInstanceFollowRedirects(false); //防止服务器默认的跳转
            conn.setReadTimeout(30000);  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("Connection", "Keep-Alive");  
            //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)");  
            conn.setRequestProperty("Content-Type",  
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());  
            // text  
            if (textMap != null) {  
                StringBuffer strBuf = new StringBuffer();  
                Iterator iter = textMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry entry = (Map.Entry) iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\""  
                            + inputName + "\"\r\n\r\n");  
                    strBuf.append(inputValue);  
                }  
                out.write(strBuf.toString().getBytes("utf-8"));  
            }  
  
            // file  
            if (fileMap != null) {  
                Iterator iter = fileMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry entry = (Map.Entry) iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    File file = new File(inputValue);  
                    String filename = file.getName();  
                    String contentType = new MimetypesFileTypeMap()  
                            .getContentType(file);  
                    if (filename.endsWith(".png")) {  
                        contentType = "image/png";  
                    }  
                    if (contentType == null || contentType.equals("")) {  
                        contentType = "application/octet-stream";  
                    }  
  
                    StringBuffer strBuf = new StringBuffer();  
                    strBuf.append("\r\n").append("--").append(BOUNDARY)  
                            .append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\""  
                            + inputName + "\"; filename=\"" + filename  
                            + "\"\r\n");  
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
  
                    out.write(strBuf.toString().getBytes());  
  
                    DataInputStream in = new DataInputStream(  
                            new FileInputStream(file));  
                    int bytes = 0;  
                    byte[] bufferOut = new byte[1024];  
                    while ((bytes = in.read(bufferOut)) != -1) {  
                        out.write(bufferOut, 0, bytes);  
                    }  
                    in.close();  
                }  
            }  
  
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
            out.write(endData);  
            out.flush();  
            out.close();  

            // 数据返回  
            StringBuffer strBuf = new StringBuffer();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(  
                    conn.getInputStream(),"UTF-8"));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                strBuf.append(line);//.append("\n");  
            }  
            res = strBuf.toString();  
            reader.close();  
            reader = null;  
            //System.out.println("数据返回："+res);
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (conn != null) {  
                conn.disconnect();  
                conn = null;  
            }  
        }  
        return res;  
    }  
}
