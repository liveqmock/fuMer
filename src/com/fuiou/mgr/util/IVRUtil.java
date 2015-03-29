package com.fuiou.mgr.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.util.FuMerUtil;

public class IVRUtil {

	private static final String jdbcUrl = "jdbc:sqlserver://192.168.82.15:1433;databaseName=AltiCDR;user=sa;password=22222";
	private static final String tableNm = "Alti_225661860";

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
	 * 添加语音验证码
	 * 
	 * @param mobile_no
	 *            手机号
	 * @param code
	 *            验证码
	 * @param flag
	 *            语音标志
	 * @throws Exception
	 */
	public static void addIvr(TCustmrBusi custmrBusi) throws Exception{
		Connection con=null;
		Statement st=null;
		ResultSet rs=null;
		try {
			con=getConn();
			for(int i=0;i<10;i++){
				String sql="insert into "+tableNm+"(id,result1,attempts,taskid,altiphone1,altiextendfield1,altiextendfield2,altiextendfield3) " +
						"	values("+i+",0,3,225661860,'"+transMobileNo(custmrBusi.getMOBILE_NO())+"','"+FuMerUtil.getCurrTime("MMddHHssmmmm")
						+"','"+custmrBusi.getACNT_NO()+"','"+custmrBusi.getRESERVED1()+"')";
				st=con.createStatement();
				rs=st.executeQuery(sql);
			}
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
//		if (StringUtil.isEmpty(mobile_no) || mobile_no.trim().length() < 7) {
//			return (mobile_no == null ? "" : mobile_no.trim());
//		}
//		boolean isShh = false;
//		for (String mobileBin : Constants.SHH_MOBILE_BIN_LIST) {
//			if (StringUtil.isNotEmpty(mobileBin)
//					&& mobileBin.trim().equals(mobile_no.substring(0, 7))) {
//				isShh = true;
//				break;
//			}
//		}
//		if (isShh) {
			return mobile_no.trim();
//		} else {
//			return "0" + mobile_no.trim();
//		}
	}

	public static void main(String[] args) throws Exception {
		TCustmrBusi custmrBusi = new TCustmrBusi();
		custmrBusi.setMOBILE_NO("13261568767");
		custmrBusi.setACNT_NO("6227001215050471420");
		custmrBusi.setRESERVED1("测试商户");
		addIvr(custmrBusi);
	}

}
