package com.fuiou.mgr.action.sysmng;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TInsBankAcnt;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.TUsers;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TInsBankAcntService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TUsersService;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.Pager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserInfoManagerAction extends ActionSupport {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoManagerAction.class);
	private static final long serialVersionUID = 1L;
	private TUsers user=new TUsers();
	
	private String strView;
	
	private String result;
	
	private String userName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	private boolean vilidateResult;

	public boolean isVilidateResult() {
		return vilidateResult;
	}
	public void setVilidateResult(boolean vilidateResult) {
		this.vilidateResult = vilidateResult;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getStrView() {
		return strView;
	}
	public void setStrView(String strView) {
		this.strView = strView;
	}
	private static TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
	private TUsersService tUsersService=new TUsersService();
	
	private int totalPage;//总页数
	private int pageNum;//当前页数
	private int totalCount;//信息总条数
	private int pageSize;//每页的条数
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	/** 错误信息 */
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	
	public TUsers getUser() {
		return user;
	}
	public void setUser(TUsers user) {
		this.user = user;
	}
	public String init(){
		return "init";
	}
	public String findUsers(){
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		user.setMCHNT_CD(tOperatorInf.getMCHNT_CD());
		if(pageSize==0){
			pageSize=15;
		}
		if(pageNum==0){
			pageNum=1;
		}
		//获取信息的总条数
		totalCount=tUsersService.selectCount(user);
		//计数总页数
		totalPage=(totalCount+pageSize-1)/pageSize;
		if(pageNum>totalPage){
			pageNum=totalPage;
		}
		//用来装查询条件的包含分页的信息
		Map map=new HashMap();
		//用来装连接的参数不含分页的信息
		Map<String, String> timpMap=new HashMap<String, String>();
		map.put("MCHNT_CD", tOperatorInf.getMCHNT_CD());
		timpMap.put("user.MCHNT_CD", tOperatorInf.getMCHNT_CD());
		String USER_NAME=user.getUSER_NAME()==null?"":user.getUSER_NAME().trim();
		String ACCT_NO=user.getACCT_NO()==null?"":user.getACCT_NO().trim();
		String MOBILE_NO=user.getMOBILE_NO()==null?"":user.getMOBILE_NO().trim();
		if(!"".equals(USER_NAME)){
			map.put("USER_NAME", USER_NAME);
			timpMap.put("user.USER_NAME", USER_NAME);
		}
		if(!"".equals(ACCT_NO)){
			map.put("ACCT_NO", ACCT_NO);
			timpMap.put("user.ACCT_NO", ACCT_NO);
		}
		if(!"".equals(MOBILE_NO)){
			map.put("MOBILE_NO", MOBILE_NO);
			timpMap.put("user.MOBILE_NO", MOBILE_NO);
		}
		map.put("START_INDEX", (pageNum-1)*pageSize+1);
		map.put("END_INDEX", pageNum*pageSize);
		List<TUsers> userList=tUsersService.seleectByPager(map);
		ActionContext.getContext().put("userList", userList);
		//获取分页的字符串
		String pagerStr=Pager.getPagerStr("userInfoMgr_findUsers.action", pageNum, totalCount, pageSize,timpMap);
		ActionContext.getContext().put("pagerStr", pagerStr);
		return "userList";
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String addInit(){
		return "adduser";
	}
	public String addUser(){
		ActionContext context=ActionContext.getContext();
		try {
			// 操作员
			TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
			TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(tOperatorInf.getMCHNT_CD(), "1");
			if(tInsMchntInf==null){
				logger.error(TDataDictConst.FILE_MCHNT_CD_ER + "没有找到对应的商户");
				errorMap.put(TDataDictConst.FILE_MCHNT_CD_ER, "没有找到对应的商户");
				context.put("errorMap", errorMap);
				return "userAdd_error";
			}
			//获取机构代码
			String insCd = tInsMchntInf.getINS_CD();
			TInsBankAcntService tInsBankAcntService = new TInsBankAcntService();
			//获取机构账户信息
			TInsBankAcnt tInsBankAcnt = tInsBankAcntService.selectByKey(insCd, "3", "1", "1");
			//获取电联行子号
			String interBankNo = "";
			if(tInsBankAcnt!=null){
				interBankNo=tInsBankAcnt.getINTER_BANK_NO();
			}
			user.setINTER_BANK_NO(interBankNo);
			
			if(user.getPROV_CODE()!=null&&!"".equals(user.getPROV_CODE())){	
				String[] PROV_CODE=user.getPROV_CODE().split(TDataDictConst.FILE_CONTENT_APART, 2);
				String[] CITY_CODE=user.getCITY_CODE().split(TDataDictConst.FILE_CONTENT_APART, 2);
				String[] BANK_CD=user.getBANK_CD().split(TDataDictConst.FILE_CONTENT_APART, 2);
				user.setPROV_CODE(PROV_CODE[0]);
				user.setPROV_NAME(PROV_CODE[1]);
				user.setCITY_CODE(CITY_CODE[0]);
				user.setCITY_NAME(CITY_CODE[1]);
				user.setBANK_CD(BANK_CD[0]);
				user.setBANK_NAME(BANK_CD[1]);
			}else{
				TRootBankInf tRootBankInf=SystemParams.bankMap.get(user.getBANK_CD());
				if(tRootBankInf!=null){
					user.setBANK_NAME(tRootBankInf.getBANK_NM());
				}else{
					user.setBANK_NAME("");
				}
			}
			user.setMCHNT_CD(tOperatorInf.getMCHNT_CD());
			user.setMCHNT_NAME(tInsMchntInf.getINS_NAME_CN());
			user.setREC_UPD_USR(tOperatorInf.getLOGIN_ID());
			//获取当前时间
			Timestamp time=new Timestamp(new Date().getTime());
			user.setREC_CMT_TS(time);
			user.setROW_CRT_TS(time);
			if(tUsersService.insertTUsers(user)>0){
				logger.error(TDataDictConst.SUCCEED + "操作成功");
				errorMap.put(TDataDictConst.SUCCEED, "操作成功");
				context.put("errorMap", errorMap);
				return "userAdd_error";
			}else{
				logger.error(TDataDictConst.UNSUCCESSFUL + "入库失败");
				errorMap.put(TDataDictConst.UNSUCCESSFUL, "入库失败");
				context.put("errorMap", errorMap);
				return "userAdd_error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(TDataDictConst.UNSUCCESSFUL + "系统异常");
			errorMap.put(TDataDictConst.UNSUCCESSFUL, "系统异常");
			context.put("errorMap", errorMap);
			return "userAdd_error";
		}
	}
	public String deleteUser() throws IOException{
		HttpServletResponse response=(HttpServletResponse)ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		HttpServletRequest request=(HttpServletRequest)ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		user.setMCHNT_CD(tOperatorInf.getMCHNT_CD());
		int i = tUsersService.deleteUser(user);
		user=new TUsers();
		if("deleteUser".equals(request.getParameter("ajaxDelete"))){
			if(i>0){
				response.getWriter().write("0");
			}else{
				response.getWriter().write("1");
			}
			return null;
		}
		return this.findUsers();
	}
	public String eidtUser(){
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		user.setMCHNT_CD(tOperatorInf.getMCHNT_CD());
		TUsers u=tUsersService.selectById(user);
		ActionContext.getContext().put("u", u);
		user=new TUsers();
		return "editUser";
	}
	public String confirmEdit(){
		ActionContext context=ActionContext.getContext();
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		user.setMCHNT_CD(tOperatorInf.getMCHNT_CD());
		if(user.getPROV_CODE()!=null&&!"".equals(user.getPROV_CODE())){	
			String[] PROV_CODE=user.getPROV_CODE().split(TDataDictConst.FILE_CONTENT_APART, 2);
			String[] CITY_CODE=user.getCITY_CODE().split(TDataDictConst.FILE_CONTENT_APART, 2);
			String[] BANK_CD=user.getBANK_CD().split(TDataDictConst.FILE_CONTENT_APART, 2);
			user.setPROV_CODE(PROV_CODE[0]);
			user.setPROV_NAME(PROV_CODE[1]);
			user.setCITY_CODE(CITY_CODE[0]);
			user.setCITY_NAME(CITY_CODE[1]);
			user.setBANK_CD(BANK_CD[0]);
			user.setBANK_NAME(BANK_CD[1]);
		}
		//获取当前时间
		Timestamp time=new Timestamp(new Date().getTime());
		user.setREC_UPD_TS(time);
		int i=tUsersService.editUser(user);
		if(i>0){
			logger.error(TDataDictConst.SUCCEED + "操作成功");
			errorMap.put(TDataDictConst.SUCCEED, "操作成功");
			context.put("errorMap", errorMap);
			user=new TUsers();
			return "userAdd_error";
		}else{
			logger.error(TDataDictConst.UNSUCCESSFUL + "修改失败");
			errorMap.put(TDataDictConst.UNSUCCESSFUL, "修改失败");
			context.put("errorMap", errorMap);
			return "userAdd_error";
		}
	}
	//用ajax查询
	public String getUsers() throws UnsupportedEncodingException{
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		String mchntCd=tOperatorInf.getMCHNT_CD();
		if(user.getUSER_NAME()!=null){
			String userName2=URLDecoder.decode(user.getUSER_NAME(),"utf-8");
			user.setUSER_NAME(userName2);
		}
		if(user.getACCT_NO()!=null){
			String acctNo=URLDecoder.decode(user.getACCT_NO(),"utf-8");
			user.setACCT_NO(acctNo);
		}
		if(user.getMOBILE_NO()!=null){
			String mobileNo=URLDecoder.decode(user.getMOBILE_NO(),"utf-8");
			user.setMOBILE_NO(mobileNo);
		}
		user.setMCHNT_CD(mchntCd);
		strView=tUsersService.getUserTableVIew(user, mchntCd, pageNum);
		return  "success";
	} 
	//用ajax添加
	public String createUser() throws UnsupportedEncodingException{
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		String userName="";
		if(user.getUSER_NAME()!=null){
			userName=URLDecoder.decode(user.getUSER_NAME(),"utf-8");
			user.setUSER_NAME(userName);
		}
		if(user.getPMS_BANK_NAME()!=null){
			String bankName=URLDecoder.decode(user.getPMS_BANK_NAME(),"utf-8");
			user.setPMS_BANK_NAME(bankName);
		}
		if(user.getBANK_CD()!=null){
			String bankCd=URLDecoder.decode(user.getBANK_CD(),"utf-8");
			user.setBANK_CD(bankCd);
		}
		if(user.getPROV_CODE()!=null){
			String provCode=URLDecoder.decode(user.getPROV_CODE(),"utf-8");
			user.setPROV_CODE(provCode);
		}
		if(user.getCITY_CODE()!=null){
			String cityCode=URLDecoder.decode(user.getCITY_CODE(),"utf-8");
			user.setCITY_CODE(cityCode);
		}
		TUsers u = tUsersService.findTUserByName(userName,tOperatorInf.getMCHNT_CD(),user.getACCT_NO());
		if(u!=null){
			result="2";
			return  "success";
		}
		this.addUser();
		if("操作成功".equals(errorMap.get(TDataDictConst.SUCCEED))){
			result="0";
		}else{
			result="1";
		}
		return  "success";
	}
	public String vilidateUser() throws Exception{
		HttpServletResponse response=(HttpServletResponse)ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		response.setHeader("cache-control", "no-cache");
		response.setHeader("expires", "0");
		userName = URLDecoder.decode(userName, "utf-8");
		logger.info("--------------------------------联系人姓名:>"+userName+"<---------------------------------");
		TUsers u = tUsersService.findTUserByName(userName,tOperatorInf.getMCHNT_CD(),user.getACCT_NO());
		if(u!=null){
			if(user.getROW_ID()!=null){
				if(!u.getROW_ID().equals(user.getROW_ID())){	
					vilidateResult=false;
					return "success";
				}else{
					vilidateResult=true;
					return "success";
				}
			}
		}
		if(u!=null){
			vilidateResult=false;
		}else{
			vilidateResult=true;
		}
		return "success";
	}
}
