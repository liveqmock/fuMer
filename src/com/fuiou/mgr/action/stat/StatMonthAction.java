package com.fuiou.mgr.action.stat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
@SuppressWarnings ({ "unchecked", "rawtypes"})
public class StatMonthAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private String startDate;
	private String busiCd;
	private String srcMchntName;//商户名
	private int sucCnt;//成功笔数
	private int failCnt;//失败笔数
	private String sucAmt;//成功总金额
	private String failAmt;//失败总金额
	private String feeAmt;//手续费
	private List resultList;
	private List<TInsMchntInf> mchntList;
	public List<TInsMchntInf> getMchntList() {
		return mchntList;
	}

	public void setMchntList(List<TInsMchntInf> mchntList) {
		this.mchntList = mchntList;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	private int cnt;
	
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public int getSucCnt() {
		return sucCnt;
	}

	public void setSucCnt(int sucCnt) {
		this.sucCnt = sucCnt;
	}

	public int getFailCnt() {
		return failCnt;
	}

	public void setFailCnt(int failCnt) {
		this.failCnt = failCnt;
	}

	public String getSucAmt() {
		return sucAmt;
	}

	public void setSucAmt(String sucAmt) {
		this.sucAmt = sucAmt;
	}

	public String getFailAmt() {
		return failAmt;
	}

	public void setFailAmt(String failAmt) {
		this.failAmt = failAmt;
	}

	public String getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getSrcMchntName() {
		return srcMchntName;
	}

	public void setSrcMchntName(String srcMchntName) {
		this.srcMchntName = srcMchntName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getBusiCd() {
		return busiCd;
	}

	public void setBusiCd(String busiCd) {
		this.busiCd = busiCd;
	}

	public String init() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Map busiCdMap = new LinkedHashMap();
		busiCdMap.put(TDataDictConst.BUSI_CD_VERIFY, "账户验证");
		busiCdMap.put(TDataDictConst.BUSI_CD_INCOMEFOR, "代收交易");
		busiCdMap.put(TDataDictConst.BUSI_CD_PAYFOR, "付款交易");
		busiCdMap.put(TDataDictConst.BUSI_CD_REFUND_TICKET, "提回退票");
		request.setAttribute("busiCdMap", busiCdMap);
		//当前登录的商户信息
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		TInsMchntInfService insMchntInfService = new TInsMchntInfService();
		if(tInsInf.getMCHNT_TP().equals("F304")&&tInsInf.getINS_TP().equals("1B")){//如果是代理商
			mchntList = insMchntInfService.getMchntListByRlatMchntCd(tInsInf.getMCHNT_CD(),"1");
		}
		return "inqu";
	}

	public String stat() throws Exception {
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		String srcMchntCd = null; 
		if(tInsInf.getMCHNT_TP().equals("F304")&&tInsInf.getINS_TP().equals("1B")){//如果是代理商
			String[] mchntInfo = srcMchntName.split(":");
			srcMchntName = mchntInfo[1];
			srcMchntCd = mchntInfo[0];
		}else{
			srcMchntName = tInsInf.getINS_NAME_CN();
			srcMchntCd = tInsInf.getMCHNT_CD();
		}
		srcMchntName = tInsInf.getINS_NAME_CN();
		TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
		if(TDataDictConst.BUSI_CD_VERIFY.equals(busiCd)){
			resultList = tApsTxnLogService.selectTApsTxnLogForMonthStatByVerify(srcMchntCd,startDate,8);
			handlerVerifyTxn(resultList);
		}else if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(busiCd) || TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
			resultList = tApsTxnLogService.selectTApsTxnLogForMonthStatByIncomefor(srcMchntCd,startDate,8,busiCd);
			handlerIncomeforTxn(resultList);
		}else if(TDataDictConst.BUSI_CD_REFUND_TICKET.equals(busiCd)){//借记(退票)
			resultList = tApsTxnLogService.selectTApsTxnLogForMonthStatByRedTkt(srcMchntCd,startDate,8);
			handlerTP01Txn(resultList);
		}
		return "result_"+busiCd;
	}

	private void handlerTP01Txn(List list) {
		long sucTxnAmt = 0;
		Map map = null;
		for(int i=0;i<list.size();i++){
			map = (Map) list.get(i);
			cnt = cnt + Integer.valueOf(map.get("CNT").toString());
			sucTxnAmt = sucTxnAmt + Long.valueOf(map.get("DEST_TXN_AMT").toString());
			map.put("DEST_TXN_AMT", FuMerUtil.formatFenToYuan(map.get("DEST_TXN_AMT").toString()));
		}
		sucAmt = FuMerUtil.formatFenToYuan(sucTxnAmt);
	}

	private void handlerVerifyTxn(List list) {
		Map map = null;
		for(int i=0;i<list.size();i++){
			map = (Map) list.get(i);
			cnt = cnt + Integer.valueOf(map.get("CNT").toString());
			sucCnt = sucCnt + Integer.valueOf(map.get("SUC_CNT").toString());
			failCnt = failCnt + Integer.valueOf(map.get("FAIL_CNT").toString());
		}
	}

	/**
	 * 代收结果处理
	 * @param sucList:成功集合
	 * @param failList:失败集合
	 * @return
	 */
	private void handlerIncomeforTxn(List list) {
		long sucTxnAmt = 0;//总成功金额
		long failTxnAmt = 0;//总失败金额
		if(list!=null && list.size()>0){
			Map map = null;
			for(int i=0;i<list.size();i++){
				map = (Map) list.get(i);
				sucTxnAmt = sucTxnAmt + Long.valueOf(map.get("SUC_DEST_TXN_AMT").toString());
				failTxnAmt = failTxnAmt + Long.valueOf(map.get("FAIL_DEST_TXN_AMT").toString());
				sucCnt = sucCnt + Integer.valueOf(map.get("SUC_CNT").toString());
				failCnt = failCnt + Integer.valueOf(map.get("FAIL_CNT").toString());
				
				map.put("SUC_DEST_TXN_AMT", FuMerUtil.formatFenToYuan(map.get("SUC_DEST_TXN_AMT").toString()));
				map.put("FAIL_DEST_TXN_AMT", FuMerUtil.formatFenToYuan(map.get("FAIL_DEST_TXN_AMT").toString()));
			}
		}
		sucAmt = FuMerUtil.formatFenToYuan(sucTxnAmt);
		failAmt = FuMerUtil.formatFenToYuan(failTxnAmt);
	}


}
