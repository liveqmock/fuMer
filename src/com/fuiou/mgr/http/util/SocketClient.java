package com.fuiou.mgr.http.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.StringUtils;
import com.fuiou.mer.util.SystemParams;


/**
 * Socket客户端
 * Jerry
 */
public class SocketClient {
	
	private static Logger logger = LoggerFactory.getLogger(SocketClient.class);
	
	private static final int CONNECT_TIMEOUT = 60 * 1000; // mini seconds
    private static final int READ_TIMEOUT = 60 * 1000; // mini seconds
    private static final String CHARSET = "utf-8";
    
    public static String sendToAps(String mchntCd,String merdt,String orderno){
    	String xml = "<APS_TXN_LOG><SRC_MCHNT_CD>"+mchntCd+"</SRC_MCHNT_CD><SRC_MCHNT_TXN_DT>"+merdt+"</SRC_MCHNT_TXN_DT><ORIG_SRC_ORDER_NO>"+orderno+"</ORIG_SRC_ORDER_NO></APS_TXN_LOG>";
    	return send(SystemParams.getProperty("apsIp"), Integer.parseInt(SystemParams.getProperty("apsPort")), xml, "UTF-8", 4);
    }
	
    /**
     * 用socket往服务端发送报文
     * @param ip：服务端IP
     * @param port：服务端端口
     * @param str：带发送报文信息
     * @param charSet：字符编码
     * @param packLen：包头长度
     * @return
     */
	public static String send(String ip,int port,String str,String charSet,int packLen){
			logger.info("准备发送报文,IP="+ip+";port="+port);
			Socket socket = new Socket();
			SocketAddress address = new InetSocketAddress(ip, port);
			String rspStr = null;
			try {
				socket.connect(address, CONNECT_TIMEOUT);
				socket.setSoTimeout(READ_TIMEOUT);
				OutputStream os = socket.getOutputStream();
				InputStream is = socket.getInputStream();
				BufferedInputStream inputStream = new BufferedInputStream(is);
				String packLenStr = StringUtils.leftPadWithZero(str.getBytes(charSet).length+"", packLen);//计算出包长
				logger.debug("outStr:"+(packLenStr+str));
				os.write((packLenStr+str).getBytes(charSet));
				os.flush();
				byte[] inPackLen = new byte[packLen];
				inputStream.read(inPackLen);
				int lengthInStr = Integer.parseInt(new String(inPackLen, charSet));
				byte[] inBytes = new byte[lengthInStr];
				inputStream.read(inBytes);
				rspStr = new String(inBytes,CHARSET);
				logger.debug("rspStr:"+rspStr);
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				rspStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><incomeforrsp><ret>999999</ret><memo>交易超时</memo></incomeforrsp>";
			}finally{
				try {
					socket.close();
				} catch (IOException e) {
					logger.error("socket 关闭发生异常"+e.getMessage());
				}
			}
			return rspStr;
	}
	
	

}
