package com.fuiou.mgr.action.contract;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.TSysOperatLog;
import com.fuiou.mer.service.EmapMtSingleService;
import com.fuiou.mer.service.SysOperatLogService;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.service.TRootBankInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.CustmrBusiValidator;
import com.fuiou.mgr.util.page.PagerUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 客户协议库
 * 
 * Jerry
 * 
 */
public class CustmrBusiContractMgr extends ActionSupport {

	private static final long serialVersionUID = 1L;
	public static Logger logger=LoggerFactory.getLogger(CustmrBusiContractMgr.class);

	private File file;// 批量文件
	private TCustmrBusi custmrBusi ;
	private Map<String, TRootBankInf> bankMap;
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();
	private CustmrBusiContractUtil custmrBusiContractUtil = new CustmrBusiContractUtil();
	private String message;
	private List<String> messages;
	private int totalPage;// 总页数
	private int pageNum;// 当前页数
	private int totalCount;// 信息总条数
	private int pageSize;// 每页的条数
	private String rowIds;
	private boolean result;
	private String actionType;
	private String xml ;
	private String sign;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getRowIds() {
		return rowIds;
	}

	public void setRowIds(String rowIds) {
		this.rowIds = rowIds;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, TRootBankInf> getBankMap() {
		return bankMap;
	}

	public void setBankMap(Map<String, TRootBankInf> bankMap) {
		this.bankMap = bankMap;
	}

	public TCustmrBusi getCustmrBusi() {
		return custmrBusi;
	}

	public void setCustmrBusi(TCustmrBusi custmrBusi) {
		this.custmrBusi = custmrBusi;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * 查询所有的客服协议
	 * 
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public String selectCustmrBusis() throws IllegalAccessException,
			InvocationTargetException {
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		TInsMchntInf tInsInf = (TInsMchntInf) context.getSession().get(TDataDictConst.INS_INF);
		HashMap<String,Object> pageParmas = (HashMap<String, Object>) context.getSession().get("pagerParameters");
		if(pageParmas!=null){
			pageSize = null==pageParmas.get("pageSize")?15:(Integer)pageParmas.get("pageSize");
			pageNum = null==pageParmas.get("pageNo")?1:(Integer)pageParmas.get("pageNo");
		}else{
			pageSize = 15;
			pageNum = 1;
		}
		custmrBusi.setMCHNT_CD(tInsInf.getMCHNT_CD());
		// 获取信息的总条数
		totalCount = custmrBusiService.selectCount(custmrBusi);
		int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCount);//分页信息
		List<TCustmrBusi> custmrBusiList = custmrBusiService.selectCustmrBusiByMap(custmrBusi,pageInfo[0],pageInfo[1]);
		setBankNm(custmrBusiList);
		context.put("custmrBusiList", custmrBusiList);
		return "custmrBusiList";
	}

	public String pager() throws IllegalAccessException, InvocationTargetException{
		String userName=(String) ActionContext.getContext().getSession().get("USER_NM");
		custmrBusi.setUSER_NM(userName);
		return this.selectCustmrBusis();
	}
	/**
	 * 批量增加
	 * 
	 * @return
	 */
	public String batAddCustmrBusi() {
		return Action.SUCCESS;
	}

	/**
	 * 单笔增加商户协议库
	 * @return
	 */
	public String addCustmrBusi() {
		TOperatorInf operatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		if (custmrBusi != null) {
			custmrBusi.setMCHNT_CD(operatorInf.getMCHNT_CD());
			custmrBusi.setREC_UPD_USR(operatorInf.getLOGIN_ID());
			custmrBusi.setSrcChnl(CustmrBusiValidator.SRC_CHNL_WEB);
			Map<String,String> resultMap = CustmrBusiValidator.validate(custmrBusi);//校验
			if(resultMap.size()>0){
				for(Map.Entry<String, String> entry:resultMap.entrySet()){
					message = entry.getKey()+":"+entry.getValue();
				}
			}else{
				custmrBusi = custmrBusiContractUtil.addCustmrBusi(custmrBusi);
				//转换成前台的消息
				message = custmrBusiContractUtil.getResultInfo(custmrBusi);
			}
		}else{
			message = "签约失败";
		}
		return Action.SUCCESS;
	}

	/**
	 * 初始化
	 * 
	 * @return
	 */
	public String init() {
		bankMap = new TRootBankInfService().selectByRootBank();// 行别
		return "init";
	}

	// 单笔解约
	public String rescindCustmrBusi() throws IllegalAccessException,
			InvocationTargetException {
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext()
				.getSession().get(TDataDictConst.OPERATOR_INF);
		custmrBusi.setREC_UPD_USR(tOperatorInf.getLOGIN_ID());
		custmrBusi.setREC_UPD_TS(new Date());
		custmrBusi.setRESERVED2("1");// 标示已解约
		custmrBusi.setCONTRACT_ST(CustmrBusiContractUtil.CONTRACT_ST_INVALID);//不生效
		custmrBusi.setACNT_IS_VERIFY_1(CustmrBusiContractUtil.VERIFY_UNPASS);
		custmrBusi.setACNT_IS_VERIFY_2(CustmrBusiContractUtil.VERIFY_UNPASS);
		custmrBusi.setACNT_IS_VERIFY_3(CustmrBusiContractUtil.VERIFY_UNPASS);
		custmrBusi.setACNT_IS_VERIFY_4(CustmrBusiContractUtil.VERIFY_UNPASS);
		int rows = custmrBusiService.updateByRowId(custmrBusi);
		if (rows > 0) {
			saveOperatLog("UNCONTRACT",custmrBusi.getROW_ID());//记录日志
			EmapMtSingleService emapMtSingleService = new EmapMtSingleService();
			emapMtSingleService.batSendCustmrSms(Arrays.asList(new Integer[]{custmrBusi.getROW_ID()}));//发送短信
			result = true;
		} else {
			result = false;
		}
		return "jsonSuccess";
	}

	// 批量解约
	public String rescindCustmrBusis() throws IllegalAccessException,
			InvocationTargetException {
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext()
				.getSession().get(TDataDictConst.OPERATOR_INF);
		String[] strs = rowIds.split(",");
		int result=custmrBusiService.batUnbind(strs,tOperatorInf.getLOGIN_ID());
		for(String str:strs){
			saveOperatLog("UNCONTRACT",Integer.valueOf(str));//记录日志
		}
		message = "成功解约"+result+"条,失败"+(strs.length-result)+"条";
		return "batUnbind";
	}
	

	public String findCustmrBusiById() {
		custmrBusi = custmrBusiService.selectById(custmrBusi);
		custmrBusi.setRESERVED3(FuMerUtil.formatFenToYuan(custmrBusi.getSUC_TXN_AMT_BY_MONTH()));
		bankMap = new TRootBankInfService().selectByRootBank();// 行别
		setActionType(actionType);
		TRootBankInf tRootBankInf=SystemParams.bankMap.get(custmrBusi.getBANK_CD());
		String bankNm="";
		if(tRootBankInf!=null){
			bankNm=tRootBankInf.getBANK_NM();
			ActionContext.getContext().put("bankNm", bankNm);
		}
		return "findCustmrBusiById";
	}

	public String checkExist(){
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		String busiCd=custmrBusi.getBUSI_CD();
		String acntNo=custmrBusi.getACNT_NO();
		TCustmrBusi cust = custmrBusiService.selectByAcntAndBusiCd(tOperatorInf.getMCHNT_CD(), busiCd, acntNo,CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_WEB));
		if(cust!=null){
			result=false;
		}else{
			result=true;
		}
		return "jsonSuccess";
	}
	
	public void setBankNm(List<TCustmrBusi> tCustmrBusis){
		for(TCustmrBusi tCustmrBusi:tCustmrBusis){
			TRootBankInf tRootBankInf=SystemParams.bankMap.get(tCustmrBusi.getBANK_CD());
			tCustmrBusi.setBANK_CD(tRootBankInf==null?"":tRootBankInf.getBANK_NM());
		}
	}

	
	private void saveOperatLog(String type,Integer rowId){
		TCustmrBusi operatBusi = new TCustmrBusi();
		operatBusi.setROW_ID(rowId);
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		HttpSession session = request.getSession();
		TOperatorInf tOperatorInf = (TOperatorInf) session.getAttribute(TDataDictConst.OPERATOR_INF);
		operatBusi = custmrBusiService.selectById(operatBusi);
		SysOperatLogService logService = new SysOperatLogService();
		TSysOperatLog log = new TSysOperatLog();
		log.setIP(request.getRemoteAddr());
		log.setKEYWORD("{\"acntNo\":\""+operatBusi.getACNT_NO()+"\",\"userNm\":\""+operatBusi.getUSER_NM()+"\",\"mobileNo\":\""+operatBusi.getMOBILE_NO()+"\"}");
		log.setMCHNT_CD(operatBusi.getMCHNT_CD());
		log.setOPER_TYPE(type);
		log.setREC_CRT_USR(tOperatorInf.getLOGIN_ID());
		log.setROW_CRT_TS(new Date());
		log.setURL(request.getRequestURL().toString());
		logService.insertOperatLog(log);
	}
}
