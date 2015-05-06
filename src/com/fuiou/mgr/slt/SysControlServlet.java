package com.fuiou.mgr.slt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.DatabasePropUtils;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.http.util.SocketServer;
import com.fuiou.mgr.util.MonitorThread;
import com.fuiou.mgr.util.VPCDecodeUtil;

/**
 * Servlet implementation class Ipl
 */
public class SysControlServlet extends HttpServlet {
	private static final long serialVersionUID = -1441059079919593173L;
	private static Logger logger = LoggerFactory.getLogger(SysControlServlet.class);
	public void init() throws ServletException {
		logger.info("系统初始化");
		try {
			initJdbcConfig();
			initKey();
			SystemParams.paramInit();
			new SocketServer(10811);
			logger.info("系统初始化完成");
		} catch (Exception e) {
			logger.error("系统初始化异常:", e);
		}
		new MonitorThread("Monitor thread").start();
	}
	
	public void destroy() {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = request.getParameter("charset");//请求的编码
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8" );

		String tableName = request.getParameter("tableName");//更新的目标
		String operator = request.getParameter("operator");//请求操作员
		
		
		String result = null;
		if (null == charset || "".equals(charset)) {
			charset = "UTF-8";
		}
		try {
			if (null != tableName || !"".equals(tableName)) {
				result = flush(operator,tableName);
			} else {
				result = "3";
			}
		} catch (Exception e) {
			result = "1";
			System.out.println("内存更新错误");
			logger.error("内存更新错误:" + e.getMessage());
		}
		
		PrintWriter printWriter = response.getWriter();
		// 返回结果 0成功,1失败,3参数错误
		printWriter.write(result);
		printWriter.close();
	}

	/**
	 * 更新缓存
	 * @param operator 操作员
	 * @param tableName 更新目标
	 * @return  0成功,1失败,3参数错误
	 * @throws ServletException
	 * @throws IOException
	 */
	private String flush(String operator,String tableName) throws ServletException, IOException {
		logger.info("更新内存访问请求，请求操作员: ["+operator + "] 请求更新目标: ["+tableName+"]");
		 String result ="1";
		try {
			 //检查tableName参数，根据该参数决定执行不同的初始化动作
			 if("allTable".equals(tableName)){//更新全部
				 SystemParams.paramInit();
			} else if ("cityInfRoute".equals(tableName)) {// 省份
				SystemParams.getVCityInfMap();
			}else if("bankRoute".equals(tableName)){//银行
				SystemParams.getBankMap();
			 }else if("cardBinRoute".equals(tableName)){//卡BIN
				 SystemParams.getCardBinMap();
			 }
		} catch (Exception e) {
			logger.error("更新"+tableName+"缓存失败:"+e.getMessage());
			result = "0";
		}
		return result;
	}
	
	private void initKey() {
		String pubKeyPath = getClass().getResource("/").getPath()+File.separator+"pub.key";
		String priKeyPath = getClass().getResource("/").getPath()+File.separator+"pri.key";
		VPCDecodeUtil.init(pubKeyPath,priKeyPath);
	}

	private void initJdbcConfig(){
		String jdbcConfigFile = getClass().getResource("/").getPath()+File.separator+"jdbc.properties";
		DatabasePropUtils.initDsConfig(jdbcConfigFile, "batdb_url", "batdb_username", "batdb_password","cps","batdb");
		DatabasePropUtils.initDsConfig(jdbcConfigFile, "cfgdb_url", "cfgdb_username", "cfgdb_password","cps","cfgdb");
		DatabasePropUtils.initDsConfig(jdbcConfigFile, "olndb_url", "olndb_username", "olndb_password","cps","olndb");
		DatabasePropUtils.initDsConfig(jdbcConfigFile, "webdb_url", "webdb_username", "webdb_password","cps","webdb");
	}
}
