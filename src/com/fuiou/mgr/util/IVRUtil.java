package com.fuiou.mgr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.fuiou.mer.model.MenuDto;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class IVRUtil {
	private static XStream xstream = null;
	private static IvrBean ivrBean=null;
	private static Connection getConn(){
		try {
//			ivrurl=jdbc:sqlserver://192.168.82.20:1433;databaseName=AltiCDR;user=sa;password=22222
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn=DriverManager.getConnection(ivrBean.getUrl());
			return conn;
		} catch (ClassNotFoundException e) {	
			e.printStackTrace();
			return null;
		} catch (SQLException e) {	
			e.printStackTrace();
			return null;
		}
	}
	private static void closeConn(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private static void closeStmt(Statement stmt){
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private static void closeRs(ResultSet rs){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 添加语音验证码
	 * @param mobile_no  手机号
	 * @param code 验证码
	 * @param flag  语音标志
	 * @throws Exception
	 */
	public static void addIvr(String mobile_no,String code,String flag) throws Exception{
		Connection con=null;
		Statement st=null;
		ResultSet rs=null;
		try {
			String new_mobile_no=transMobileNo(mobile_no);
			con=getConn();
			String sql="select count(1) from "+ivrBean.getTable()+" where result1=0 and altiphone1='"+new_mobile_no+"'";
			st=con.createStatement();
			rs=st.executeQuery(sql);
			if(rs.next()){
				if(rs.getInt(1)>0){
//					sql="update  "+ivrBean.getTable()+" set PWD='"+code+"',SequenceID="+flag+" where result1=0 and altiphone1='"+new_mobile_no+"'";
//					LogWriter.debug("语音sql=>"+sql);
//					LogWriter.info("更新成功=>"+st.executeUpdate(sql));
				}else{
					sql="insert into "+ivrBean.getTable()+"(result1,attempts,taskid,agent,lastupdated,sessionid,mktime,connecttime,startdate,enddate,altiphone1,PWD,SequenceID) "
							+ "values (0,0,0,0,0,0,0,0,0,0,'"+ new_mobile_no+"','"+code+"',"+flag+")";
					LogWriter.debug("语音sql=>"+sql);
					if(1!=st.executeUpdate(sql)){
						throw new FUException("发送语音验证码失败");
					}
				}
			}
		} catch (FUException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally{
			closeConn(con);
			closeStmt(st);
			closeRs(rs);
		}
	}
	class IvrBean{
		private String url;
		private String table;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getTable() {
			return table;
		}
		public void setTable(String table) {
			this.table = table;
		}
	}
	public static void main(String[] args) throws Exception {
//		try{
//		List<String> list=new ArrayList<String>();
//		list.add("1861689");
//		list.add("1861688");
//		list.add("1861687");
//		Constants.SHH_MOBILE_BIN_LIST=list;
//		System.out.println(transMobileNo("18616796060"));
		addIvr("18616896064", "0532", "1");
		
		///--------------------------------------------------------------------------------------------
//		refresh();
//		Connection con=null;
//		Statement st=null;
//		ResultSet rs=null;
//		try {
//			String sql="select * from "+ivrBean.getTable()+" where altiphone1='18616896064' or 1=1";
//			con=getConn();
//			st=con.createStatement();
//			rs=st.executeQuery(sql);
//			int count=0;
//			while(rs.next()){
//				count++;
//				System.out.println("=========================");
//				ResultSetMetaData rsmd=rs.getMetaData();
//				int columnCount=rsmd.getColumnCount();
//				for(int i=1;i<=columnCount;i++){
//					
//					System.out.println(rsmd.getColumnTypeName(i)+"|"+rsmd.getColumnName(i)+"="+rs.getString(rsmd.getColumnName(i)));
//				}
//				System.out.println("=========================");
//			}
//			System.err.println("总记录数="+count);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			closeConn(con);
//			closeStmt(st);
//			closeRs(rs);
//		}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}
		
}
