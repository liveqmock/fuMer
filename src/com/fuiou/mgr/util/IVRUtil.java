package com.fuiou.mgr.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.util.MemcacheUtil;

public class IVRUtil {
	
	public static Logger logger=LoggerFactory.getLogger(IVRUtil.class);
	
	/*****远程数据库主机ip，用户密码******/
	//192.168.82.15
	//administrator
	//china#22222
	

	private static final String jdbcUrl = "jdbc:sqlserver://192.168.72.106:1433;databaseName=AltiCDR;user=sa;password=22222";
	private static final String TABLE_NAME = "Alti_230232365";
	private static final int TASK_ID = 230232365;
	private static final int ATTEMPTS = 3;//尝试外呼次数
	private static final int INIT_STATUS = 0;

	private static Connection getConn() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = DriverManager.getConnection(jdbcUrl);
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void closeConn(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeStmt(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeRs(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param mobile:手机号
	 * @throws Exception
	 */
	public static void addIvrTask(String mobile) throws Exception{
		Connection con=null;
		Statement st=null;
		ResultSet rs=null;
		try {
			con=getConn();
			st=con.createStatement();
//				String querySql = "select count(1) from "+TABLE_NAME+" where result1 in(0,1,21) and taskid="+TASK_ID+" and altiphone1='"+transMobileNo(mobile)+"'";
//				rs=st.executeQuery(querySql);
//				if(rs.next()){
//					if(rs.getInt(1)==0){
						String insertSql="insert into "+TABLE_NAME+"(id,result1,attempts,taskid,altiphone1) " +
								"	values("+Long.parseLong(new TSeqService().getId("ivrId", 8, ""))+","+INIT_STATUS+","+ATTEMPTS+","+TASK_ID+",'"+mobile+"')";
						int i = st.executeUpdate(insertSql);
						if(i==1){
							logger.debug("add ivr task success,mobile="+mobile);
						}else{
							logger.debug("add ivr task fail,mobile="+mobile);
						}
//					}
//				}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally{
			closeConn(con);
			closeStmt(st);
			closeRs(rs);
		}
	}

	/**
	 * 转换手机号，如果不是上海本地的加0
	 * 
	 * @param mobile_no
	 * @return
	 */
	private static String transMobileNo(String mobile_no) {
		if (StringUtil.isEmpty(mobile_no) || mobile_no.trim().length() < 7) {
			return (mobile_no == null ? "" : mobile_no.trim());
		}
		boolean isShh = false;
		for (String mobileBin : MemcacheUtil.getShangHaiMobileBin()) {
			if (StringUtil.isNotEmpty(mobileBin)&& mobileBin.trim().equals(mobile_no.substring(0, 7))) {
				isShh = true;
				break;
			}
		}
		if (isShh) {
			return mobile_no.trim();
		} else {
			return "0" + mobile_no.trim();
		}
	}

	public static void main(String[] args) throws Exception {
		addIvrTask("18201868440");
		addIvrTask("18201868441");
	}

}
