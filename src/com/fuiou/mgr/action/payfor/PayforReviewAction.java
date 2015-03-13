package com.fuiou.mgr.action.payfor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.StringUtils;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.VirtAcntUtil;
import com.fuiou.mgr.util.page.PagerUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 账户验证文件查询
 * 
 * zx
 * 
 */
public class PayforReviewAction extends ActionSupport {

	private static final long serialVersionUID = 8374126994004224457L;

	public static Logger logger=LoggerFactory.getLogger(PayforReviewAction.class);
	private HttpServletRequest request;
	private TDataDictService tDataDictService = new TDataDictService();
	private TApsTxnLogService TApsTxnLogService = new TApsTxnLogService();
	private TFileInfService fileInfService = new TFileInfService();
	String startDate;
	String endDate;
	String stateSel;
	String fileName;
	String fileBusiTp;
	String message;
	String uploadFileName;
	String KBPS_TRACE_NO;
	String KBPS_SRC_SETTLE_DT;


	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStateSel() {
		return stateSel;
	}

	public void setStateSel(String stateSel) {
		this.stateSel = stateSel;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileBusiTp() {
		return fileBusiTp;
	}

	public void setFileBusiTp(String fileBusiTp) {
		this.fileBusiTp = fileBusiTp;
	}

	public static String getTFileInfStMe(String tFileInfSt){
		String me="";
		if("0".equals(tFileInfSt) || "1".equals(tFileInfSt)){//付款确认/删除
			me = "等待处理";
		}else if("3".equals(tFileInfSt) || "4".equals(tFileInfSt)){
			me = "处理完成";
		}else{
			me = "正在处理 ";
		}
		return me;
	}

	@Override
	public String execute() throws Exception {
		// 商户信息
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		String merId = tInsInf.getMCHNT_CD();
		request = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		//判断起始日期和截止日期是否为空
		if(StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)){
			request.setAttribute("message", "日期不能为空");
			request.setAttribute("fileCount", 0);
			return SUCCESS;
		}
		//在2个日期都不为空的情况下判断起始日期是否在截止日期之前
		if ((endDate.compareTo(startDate) < 0)) {
				request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_WRON_FILEDT));
				request.setAttribute("fileCount", 0);
				return SUCCESS;
		}
		//获取业务操作代码
		fileBusiTp = (String) request.getAttribute("fileBusiTp");
		if("file".equals(stateSel)){
			return doFileQuery(request,merId);
		}else{
			return doSingleQuery(request,merId);
		}
	}


	private String doSingleQuery(HttpServletRequest request, String merId) {
		int totalCount = TApsTxnLogService.selectTApsTxnLogCountReview(startDate, endDate, null, merId,fileBusiTp);
		request.setAttribute("fileCount", totalCount);
		if(totalCount==0){
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_NO_RECORD));
		}else{
			int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCount);
			List<TApsTxnLog> txnlogs = TApsTxnLogService.selectTApsTxnLogReview(startDate, endDate, null, merId,fileBusiTp, pageInfo[0] , pageInfo[1] );
			for(TApsTxnLog txnlog:txnlogs){
				txnlog.setDEBIT_ACNT_NO(txnlog.getDEBIT_ACNT_NO().substring(2));
				txnlog.setFIRST_KBPS_SRC_SETTLE_DT(FuMerUtil.formatFenToYuan(txnlog.getSRC_TXN_AMT()));//交易金额
				txnlog.setFIRST_KBPS_TRACE_NO(FuMerUtil.formatFenToYuan(txnlog.getTXN_FEE_AMT()));//手续费
				String detailInorData=txnlog.getDETAIL_INQR_DATA();
				String[] items=detailInorData.split(TDataDictConst.FILE_CONTENT_APART, 3);
				if(items!=null){
					if(items.length>2){
						TRootBankInf tRootBankInf=SystemParams.bankMap.get(items[1]);
						if(tRootBankInf!=null){
							txnlog.setFIRST_SRC_MODULE_CD(tRootBankInf.getBANK_NM());
						}
					}
				}
			}
			request.setAttribute("fileList", txnlogs);
		}
		return "singleReview";
	}

	private String doFileQuery(HttpServletRequest request, String merId) {
		int totalCount = fileInfService.countUploadFiles(startDate, endDate, merId, fileBusiTp, fileName, TDataDictConst.FILE_ST_MCHNT_CONFIRM);
		request.setAttribute("fileCount", totalCount);
		if(totalCount==0){
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_NO_RECORD));
		}else{
			int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCount);
			List<TFileInf> files = fileInfService.getUploadFiles(startDate, endDate, merId, fileBusiTp, fileName, TDataDictConst.FILE_ST_MCHNT_CONFIRM, pageInfo[0], pageInfo[1]);
			for (TFileInf file:files) {
				file.setRESERVE1(FuMerUtil.formatFenToYuan(file.getFILE_RIGHT_AMT()));
			}
			request.setAttribute("fileList", files);
		}
		return SUCCESS;
	}
	
	public String fileReview() throws Exception {
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);// 商户信息
		String mchntCd=tInsInf.getMCHNT_CD();
		request = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		//updateFileSt
		int i = fileInfService.updateByFileNameAndFileSeq(TDataDictConst.FILE_ST_REVIEW, mchntCd, uploadFileName, TDataDictConst.FILE_ST_MCHNT_CONFIRM);
		if(1==i){
			logger.debug("===========update success,file_nm："+uploadFileName+",mchnt_cd:"+mchntCd+"=============");
			if(TDataDictConst.BUSI_CD_PAYFOR.equals(fileBusiTp)){
				return reviewPayforFile(request,tInsInf);
			}else{
				return reviewOtherFile(request,mchntCd);
			}
		}else{
			logger.debug("===========update fail,file_nm："+uploadFileName+",mchnt_cd:"+mchntCd+"=============");
			message = "文件状态修改失败，无法完成复核";
			return "payfor_error";
		}
	}
	
	/**
	 * 其他类型文件复核
	 * @param request
	 * @param mchntCd
	 * @param operatorId
	 * @return
	 */
	private String reviewOtherFile(HttpServletRequest request, String mchntCd) {
		logger.debug("=============ready update "+uploadFileName+" txnlog ================");
		//修改交易
		int i = TApsTxnLogService.updateTapsTxnLogToMchntSure(mchntCd, uploadFileName, TDataDictConst.CUSTMR_TP_REVIEWED, TDataDictConst.TXN_CUSTMR_SURE,  TDataDictConst.CAPITAL_DIR_INCOMEFOR,KBPS_SRC_SETTLE_DT);
		if(i>0){
			logger.debug("==========txnlog updated "+i+" rows,"+uploadFileName+"===========",this);
			message="文件复核成功！"+"文件名:" + uploadFileName + "商户号:" + mchntCd;
		}else{
			logger.error("==========txnlog updated 0 rows,occur db exception,"+uploadFileName+"===========",this);
			message="文件复核失败！"+"文件名:" + uploadFileName + "商户号:" + mchntCd;
		}
		return "payfor_error";
	}

	/**
	 * AP01复核
	 * @param request
	 * @param mchntCd
	 * @param operatorId
	 * @return
	 */
	private String reviewPayforFile(HttpServletRequest request, TInsMchntInf mchntInf) throws Exception{
		logger.debug("=============ready update "+uploadFileName+" txnlog ================");
		String mchntCd = mchntInf.getMCHNT_CD();
		String insCd = mchntInf.getINS_CD();
		try{
			//修改交易C0 -- > C1
			int i = TApsTxnLogService.updateTapsTxnLogToMchntSure(mchntCd, uploadFileName, TDataDictConst.CUSTMR_TP_REVIEWED, TDataDictConst.TXN_CUSTMR_SURE,  null,KBPS_SRC_SETTLE_DT);
			if(i>0){
				logger.debug("==========txnlog updated "+i+" rows,"+uploadFileName+"===========",this);
				//1、判断付款D方向是否需要清算复核商户资金到账
				TApsTxnLog txnLogD = TApsTxnLogService.getTApsTxnLogFromMchntSure(mchntCd, uploadFileName, TDataDictConst.CUSTMR_TP_REVIEWED, TDataDictConst.CAPITAL_DIR_INCOMEFOR).get(0);//获取D方向交易
				
				//如果不需要商户资金到账则需要判断商户余额是否足够,如果不需要商户资金到账则需要做预授权相关操作
				logger.debug("=========kbps_trace_no="+txnLogD.getKBPS_TRACE_NO()+",txn_md="+txnLogD.getTXN_MD()+"============");
				if("0".equals(txnLogD.getTXN_MD())){
					payAuth(txnLogD, insCd, mchntCd, mchntInf.getMCHNT_TP());
				}else{
					message="文件复核成功！"+"文件名:" + uploadFileName + "商户号:" + mchntCd;
				}
			}else{
				logger.error("==========txnlog updated 0 rows,occur db exception,"+uploadFileName+"===========",this);
				message="文件复核失败！"+"文件名:" + uploadFileName + "商户号:" + mchntCd;
			}
		}catch (Exception e) {
			message = "系统处理异常";
			e.printStackTrace();
		}
		return "payfor_error";
	}
	
	
	private void payAuth(TApsTxnLog txnLogD,String insCd,String mchntCd,String mchntTp) throws Exception{
		//如果不需要商户资金到账则需要判断商户余额是否足够,如果不需要商户资金到账则需要做预授权相关操作
		TCustStlInfService custStlInfService = new TCustStlInfService();
		List<String> acntNos = custStlInfService.getAcntNosByIndCd(insCd);
		boolean flag = checkBalance(insCd,txnLogD.getSRC_TXN_AMT(),acntNos.get(0));
		if(!flag){
			message = "商户余额不足，请及时充值并联系技术人员处理";
			logger.error("===========mchntCd :"+mchntCd+"=====  balance is not enough==========");
		}else{
			logger.debug("=========balance enough===========");
			String ssn = VirtAcntUtil.getSsn();
			String[] results = FasService.payAuth(insCd, acntNos.get(0), txnLogD.getSRC_TXN_AMT()+"",ssn , null, mchntCd, mchntTp, txnLogD.getBUSI_CD(), "S32");
			if("0000".equals(results[2])){
				txnLogD.setDEST_TXN_ST(TDataDictConst.TXN_DEST_RES_SUC);
				int i = TApsTxnLogService.updateTxnLogByPK(txnLogD);//修改D方向交易
				logger.debug("===========update "+i+" rows ============");
				//2、判断付款C方向是否需要清算复核渠道资金到账
				i=0;
				List<TApsTxnLog> txnLogsC = TApsTxnLogService.getTApsTxnLogFromMchntSure(mchntCd, uploadFileName, TDataDictConst.CUSTMR_TP_REVIEWED, TDataDictConst.CAPITAL_DIR_PAYFOR);//获取C方向交易
				for(TApsTxnLog txnLogC:txnLogsC){
					txnLogC.setRETRI_REF_NO(results[6]);
					if("0".equals(txnLogC.getPOS_ENTRY_MD_CD()))
						txnLogC.setCUSTMR_TP(TDataDictConst.CUSTMR_TP_NO_CONFIRM);
					else
						txnLogC.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_CAPITAL_AUC_SURE);
					i = i+TApsTxnLogService.updateTxnLogByPK(txnLogC);
				}
				logger.debug("===========update "+i+" rows ============");
				message = "复核成功";
			}else{
				message = "预授权失败，请联系技术人员处理";
				logger.error("=============payAuth fail,ssn:"+ssn+"=================");
			}
		}
	}
	
	
	/**
	 * 判断商户余额是否足够
	 * @param insCd
	 * @return
	 */
	private boolean checkBalance(String insCd,long amt,String acnt) {
		if(StringUtils.isNotEmpty(acnt)){
			try {
				String[] balances = FasService.queryBalance(insCd, acnt.trim(), VirtAcntUtil.getSsn());
				return Double.valueOf(balances[4]) >= amt;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	//单笔交易复核通过
	public String singleReview() throws Exception{
		TApsTxnLog log=TApsTxnLogService.selectByPrimaryKey(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, (short)0);//获取交易
		int i = TApsTxnLogService.updateTapsTxnLogSure(log, TDataDictConst.TXN_CUSTMR_SECOND_SURE);//修改交易
		if(i==1){
			message="交易复核成功！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT;
		}else{
			message="交易复核失败！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT;
		}
		if(TDataDictConst.BUSI_CD_PAYFOR.equals(fileBusiTp)){
			TApsTxnLog txnLogD = TApsTxnLogService.selectByPrimaryKey(log.getRLAT_KBPS_SRC_SETTLE_DT(), log.getRLAT_SRC_MODULE_CD(), log.getRLAT_KBPS_TRACE_NO(), log.getRLAT_SUB_TXN_SEQ());//获取D方向的交易
			TApsTxnLogService.updateTapsTxnLogSure(txnLogD, TDataDictConst.TXN_CUSTMR_SECOND_SURE);//修改交易
			if("0".equals(txnLogD.getTXN_MD())){
				TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);// 商户信息
				String mchntCd=tInsInf.getMCHNT_CD();
				payAuth(txnLogD, tInsInf.getINS_CD(), mchntCd, tInsInf.getMCHNT_TP());
			}
		}
		return "payfor_error";
	}

	public String singleReviewNoPass() throws Exception{
		if(TDataDictConst.BUSI_CD_PAYFOR.equals(fileBusiTp)){
			TApsTxnLog log=TApsTxnLogService.selectByPrimaryKey(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, (short)0);
			int i = TApsTxnLogService.deleteTapsTxnLogByRlat(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, (short)0);
			i=i+TApsTxnLogService.deleteTapsTxnLogByRlat(log.getRLAT_KBPS_SRC_SETTLE_DT(), log.getRLAT_SRC_MODULE_CD(), log.getRLAT_KBPS_TRACE_NO(), log.getRLAT_SUB_TXN_SEQ());
			if(i==2){
				logger.debug("============delete success,kbps_trace_no:"+KBPS_TRACE_NO+"==============");
				message = "操作成功";
			}else{
				message = "操作失败";
				logger.debug("============delete fail,kbps_trace_no:"+KBPS_TRACE_NO+"==============");
			}
		}else{
			int i = TApsTxnLogService.deleteTapsTxnLogByRlat(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, (short)0);
			if(i==1){
				logger.debug("============delete success,kbps_trace_no:"+KBPS_TRACE_NO+"==============");
				message = "操作成功";
			}else{
				message = "操作失败";
				logger.debug("============delete fail,kbps_trace_no:"+KBPS_TRACE_NO+"==============");
			}
		}
		return "payfor_error";
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getKBPS_TRACE_NO() {
		return KBPS_TRACE_NO;
	}

	public void setKBPS_TRACE_NO(String kBPS_TRACE_NO) {
		KBPS_TRACE_NO = kBPS_TRACE_NO;
	}

	public String getKBPS_SRC_SETTLE_DT() {
		return KBPS_SRC_SETTLE_DT;
	}

	public void setKBPS_SRC_SETTLE_DT(String kBPS_SRC_SETTLE_DT) {
		KBPS_SRC_SETTLE_DT = kBPS_SRC_SETTLE_DT;
	}

}

