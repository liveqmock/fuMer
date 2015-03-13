package com.fuiou.mgr.http.httpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

public class HttpClientHelper{
    private static Logger logger = Logger.getLogger(HttpClientHelper.class);
    public static final String GET = "GET";
    public static final String POST = "POST";
    
    public static String getNvPairs(List<String[]> list, String charSet){
        if(list==null || list.size()==0){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0; i<list.size(); i++){
            String[] nvPairStr = list.get(i);
            try{
                if(i>0){
                    stringBuffer.append("&");
                }
                stringBuffer.append(URLEncoder.encode(nvPairStr[0], charSet)).append("=").append(URLEncoder.encode(nvPairStr[1], charSet));
            }catch(UnsupportedEncodingException e){
                logger.error("exception", e);
                return null;
            }
        }
        return stringBuffer.toString();
    }
    
    public static String doHttp(String urlStr, String method, String charSet, String postStr, String timeOut){
    	logger.debug("url="+urlStr);
    	logger.debug("postStr="+postStr);
    	 HttpURLConnection httpURLConnection = null; 
    	try{
    		 URL url = new URL(urlStr);
    		 if("https".equalsIgnoreCase(urlStr.substring(0, 5))){
    	            SSLContext sslContext = SSLContext.getInstance("TLS");
    	            X509TrustManager xtmArray[] = { new HttpX509TrustManager()};
    	            sslContext.init(null, xtmArray, new SecureRandom());
    	            if(sslContext != null)
    	                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    	            HttpsURLConnection.setDefaultHostnameVerifier(new HttpHostnameVerifier());
    	      }
    		 httpURLConnection = (HttpURLConnection)url.openConnection ();
    		 System.setProperty("sun.net.client.defaultConnectTimeout", timeOut);
    	     System.setProperty("sun.net.client.defaultReadTimeout", timeOut);
    	     //设置代理服务器
//    	     System.setProperty("http.proxyHost", "24.96.4.198");  
//    	     System.setProperty("http.proxyPort", "8080");
    	     httpURLConnection.setRequestMethod(method.toUpperCase());
    	     if(POST.equalsIgnoreCase(method)){
    	            httpURLConnection.setDoOutput(true);  
    	            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), charSet));;
    	            printWriter.write(postStr);
    	            printWriter.flush();
    	            printWriter.close();
    	      }
    	     InputStream inputStream = httpURLConnection.getInputStream();
    	     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	     int data = 0;
    	     int statusCode = httpURLConnection.getResponseCode();
    	     if(statusCode<HttpURLConnection.HTTP_OK || statusCode>=HttpURLConnection.HTTP_MULT_CHOICE){
                 logger.error("失败返回码[" + statusCode + "]");
                 return null;
             }
             while((data=inputStream.read())!=-1){
                 byteArrayOutputStream.write(data);
             }
             byte[] returnBytes = byteArrayOutputStream.toByteArray();
             String returnStr = new String(returnBytes, charSet);
             return returnStr;
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			if(null!=httpURLConnection)
				httpURLConnection.disconnect();
		}
    	return null;
    }

}
