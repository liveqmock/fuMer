package com.fuiou.mgr.action.payfor;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TPmsBankInfService;
import com.fuiou.mer.service.TStlDtStService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.BaseAction;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.PayForInfAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.fuiou.mgr.util.Formator;
import com.fuiou.mgr.util.StringUtil;

public class PayforReqAction extends BaseAction {
    private static final long serialVersionUID = -1607974661646060473L;
    private static Logger logger = LoggerFactory.getLogger(PayforReqAction.class);
	private String selectProv;
	private String selectProv_nm;
	private String selectProv_cd;
	private String selectCity;
	private String selectCity_nm;
	private String selectCity_cd;
	private String selectBank;// 开户银行
	private String selectBank_nm;
	private String selectBank_cd;
	private String bankNam_nm;// 开户行名称+联行号
	private String bankNam;
	private String bankNam_cd;//开户行行号
	private String feeAmt;//手续费
	private String srcAmt;// 原金额
	private String destAmt;//目标金额
	private String feeMsg;
	private String isDisplayFeeMsg;
	
	/** 错误信息集合 */
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	TStlDtStService tStlDtStService = new TStlDtStService();
	TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	TFileInfService tFileInfService = new TFileInfService();
	TApsTxnLog ApsTxnLog = new TApsTxnLog();
	
	// 文件来源
	private String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
	// 交易数据类型
	private String txnDataType = "DATA";
	
	private BusiBean busiBean;
	private FileError fileError;
	private TxnInfBean txnInfBean;
	private BusiBean busiBeanProcess;
	private BusiBean packBusiBean;
	private PayForInfAccess accessBean;
	
	public PayForInfAccess getAccessBean() {
		return accessBean;
	}
	public void setAccessBean(PayForInfAccess accessBean) {
		this.accessBean = accessBean;
	}
	public String getSelectProv() {
		return selectProv;
	}

	public void setSelectProv(String selectProv) {
		this.selectProv = selectProv;
	}

	public String getSelectProv_nm() {
		return selectProv_nm;
	}

	public void setSelectProv_nm(String selectProv_nm) {
		this.selectProv_nm = selectProv_nm;
	}

	public String getSelectProv_cd() {
		return selectProv_cd;
	}

	public void setSelectProv_cd(String selectProv_cd) {
		this.selectProv_cd = selectProv_cd;
	}

	public String getSelectCity() {
		return selectCity;
	}

	public void setSelectCity(String selectCity) {
		this.selectCity = selectCity;
	}

	public String getSelectCity_nm() {
		return selectCity_nm;
	}

	public void setSelectCity_nm(String selectCity_nm) {
		this.selectCity_nm = selectCity_nm;
	}

	public String getSelectCity_cd() {
		return selectCity_cd;
	}

	public void setSelectCity_cd(String selectCity_cd) {
		this.selectCity_cd = selectCity_cd;
	}

	public String getSelectBank() {
		return selectBank;
	}

	public void setSelectBank(String selectBank) {
		this.selectBank = selectBank;
	}

	public String getSelectBank_nm() {
		return selectBank_nm;
	}

	public void setSelectBank_nm(String selectBank_nm) {
		this.selectBank_nm = selectBank_nm;
	}

	public String getSelectBank_cd() {
		return selectBank_cd;
	}

	public void setSelectBank_cd(String selectBank_cd) {
		this.selectBank_cd = selectBank_cd;
	}


	public String getBankNam() {
		return bankNam;
	}

	public void setBankNam(String bankNam) {
		this.bankNam = bankNam;
	}


	public String getBankNam_cd() {
		return bankNam_cd;
	}

	public void setBankNam_cd(String bankNam_cd) {
		this.bankNam_cd = bankNam_cd;
	}

	public String getBankNam_nm() {
		return bankNam_nm;
	}

	public void setBankNam_nm(String bankNam_nm) {
		this.bankNam_nm = bankNam_nm;
	}

	public LinkedHashMap<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(LinkedHashMap<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	public String executepayForOnceReq() throws Exception {
		if(errorMap.size()>0){
			return "payfor_error";
		}
		return "success";
	}
	
	public String getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}

	public String getSrcAmt() {
		return srcAmt;
	}

	public void setSrcAmt(String srcAmt) {
		this.srcAmt = srcAmt;
	}

	public String getDestAmt() {
		return destAmt;
	}

	public void setDestAmt(String destAmt) {
		this.destAmt = destAmt;
	}

	public String getFeeMsg() {
		return feeMsg;
	}

	public void setFeeMsg(String feeMsg) {
		this.feeMsg = feeMsg;
	}

	public String getIsDisplayFeeMsg() {
		return isDisplayFeeMsg;
	}

	public void setIsDisplayFeeMsg(String isDisplayFeeMsg) {
		this.isDisplayFeeMsg = isDisplayFeeMsg;
	}

	public void validate(){
	    String mchntCd = tOperatorInf.getMCHNT_CD();					// 商户号
		try {
			String[] provs = selectProv_nm.split("\\|", 2);
			String[] citys = selectCity_nm.split("\\|", 2);
			String[] banks = selectBank_nm.split("\\|", 2);// 开户银行
			String[] bankNames = bankNam_nm.split(" ");// 支行名+号
			this.setSelectProv_cd(provs[0]);
			this.setSelectProv(provs[1]);
			this.setSelectCity_cd(citys[0]);
			this.setSelectCity(citys[1]);
			this.setSelectBank_cd(banks[0]);
			this.setSelectBank(banks[1]);
			this.setBankNam_cd(bankNames[1]);
			this.setBankNam(bankNames[0]);
			
			accessBean.setBankCd(banks[0]);
			accessBean.setBankNam(bankNames[0]);
			accessBean.setCityCode(citys[0]);
			accessBean.setMchntCd(mchntCd);
			accessBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
			/////////////////////////////////////////////////////////////////////////////////
			// 通过接入适配器 传入接入对象 返回中间对象
			AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(accessBean.getBusiCd());
			txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
			// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
			BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(accessBean.getBusiCd());
			busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
			busiBean = busiAdapter.getBusiBean();
			fileError = busiAdapter.getFileError();
		} catch (Exception e) {
			e.printStackTrace();
			errorMap.put("SYS", "数据格式不正确!");
		}
		TRootBankInf tRootBankInf = SystemParams.bankMap.get(selectBank_cd);
	      if(tRootBankInf==null){
	          errorMap.put("SYS_BANK_CD", "收款人银行不正确");
	      }
        // 检查金额
        long money = Formator.yuan2Fen(accessBean.getAmount());
        	if (money < 1 || money > new CheckOutBase().getMaxAmtPayFor()) {
        		errorMap.put("SYS_AMOUNT", "金额超限!");
        		if(new TPmsBankInfService().hasSameInterBankNo(bankNam_cd)){
        		}else{
        			errorMap.put("SYS", "电子联行号不正确!");
        		}
        		if (!StringUtil.isEmpty(accessBean.getEnpSeriaNo()) && CheckOutBase.exceedDbLenth(accessBean.getEnpSeriaNo(), 80)) {
        			errorMap.put("SYS","企业流水号超长");
        		}
        		if (!StringUtil.isEmpty(accessBean.getMemo()) && CheckOutBase.exceedDbLenth(accessBean.getMemo(), 60) ) {
        			errorMap.put("SYS","备注超长" );
        		}
        		if (!StringUtil.isEmpty(accessBean.getMobile()) && !RegexCheckUtil.checkIsDigital2(accessBean.getMobile(), 11)) {
        			errorMap.put("SYS","手机号格式不正确" );
        		}
        	}
        // 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
		// 拆分交易
		SplitTrade splitTrade = new SplitTrade();
		splitTrade.splitTrade(busiBean);
		ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean = splitTrade.getForTxnFeeAmtBean();
		BusiBean operationVerifyErrorBusibean = splitTrade.getOperationVerifyErrorBusibean();
		// 计算手续费、路由
		TradeCalculate tradeCalculate = new TradeCalculate();
		ForAccessTxnFeeAmtRutBean calculateForTxnFeeAmtBean = tradeCalculate.doCalculate(forTxnFeeAmtBean);
		List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = calculateForTxnFeeAmtBean.getPayForList();
		List<RowColumnError> rowErrors = payForBusiDetailRowBeanList.get(0).getOperationBean().getFileRowErrro().getColumnErrors();
		if(rowErrors.size()>0){
			for(RowColumnError rowError:rowErrors){
				errorMap.put(rowError.getErrCode(), rowError.getErrMemo());
			}
		}
		feeAmt = "0";
		srcAmt = "0";
		destAmt = "0";
		int feeAmtInt = 0;
		int srcAmtInt = 0;
		int destAmtInt = 0;
		for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanList){
			feeAmtInt += payForBusiDetailRowBean.getOperationBean().getTxnFeeAmt();
			srcAmtInt += payForBusiDetailRowBean.getDetailAmt();
			destAmtInt += (payForBusiDetailRowBean.getOperationBean().getDestTxnAmt());
		}
		feeMsg = "";
		if(destAmtInt<0){
			feeMsg = "目标金额小于手续费,交易失败";
		}
		feeAmt = FuMerUtil.formatFenToYuan(feeAmtInt);
		srcAmt = FuMerUtil.formatFenToYuan(srcAmtInt);
		destAmt = FuMerUtil.formatFenToYuan(destAmtInt);
		// 查询出商户类型
		TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(forTxnFeeAmtBean.getMchntCd(), "1");
		isDisplayFeeMsg  = "1";
		if(null != tInsMchntInf){
			String mchntTp = tInsMchntInf.getMCHNT_TP();
			if(mchntTp.length()>=2){
				if("D4".equals(mchntTp.substring(0, 2))){
					isDisplayFeeMsg = "0";
				}
			}
		}
		// 组合业务对象
		PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
		packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
		packBusiBean.setOprUsrId(tOperatorInf.getLOGIN_ID());//操作员
		busiBeanProcess = packBusiBean;
	}

	public String payForRequest() throws Exception {
		if(errorMap.size()>0){
			return "payfor_error";
		}
		//检查账户余额
		TradeCalculate tradeCalculate = new TradeCalculate();
		tradeCalculate.checkAccountBalance(packBusiBean, fileError,srcModuleCd);
		try {
	        request.getSession().removeAttribute("refundsSessionKeys");
			TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(accessBean.getBusiCd());
			txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, accessBean.getBusiCd(), srcModuleCd, txnDataType, false);
			FileError fileErrorFromDbService = txnDbService.getFileError();
			if(fileErrorFromDbService.getFileSumError() != null){
				if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
					List<FileRowError> fileRowErrorList = fileErrorFromDbService.getFileRowErrors();
					if(fileRowErrorList != null){
						if(fileRowErrorList.size()>0){
							for(FileRowError fileRowError : fileRowErrorList){
								List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
								if(rowColumnErrorList != null){
									if(rowColumnErrorList.size() > 0){
										String memo = "";
										String code = "";
										for(RowColumnError rowColumnError : rowColumnErrorList){
											code += rowColumnError.getErrCode()+";";
											memo += rowColumnError.getErrMemo()+";<br/>";
										}
										errorMap.put(code, memo);
										request.setAttribute("errorMap", errorMap);
										return "payfor_error";
									}
								}
							}
						}
					}
					errorMap.put(fileError.getFileSumError().getErrCode(), fileError.getFileSumError().getErrMemo());
					logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
					request.setAttribute("errorMap", errorMap);
					return "payfor_error";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("insert TApsTxnLog fail!");
			errorMap.put(TDataDictConst.UNSUCCESSFUL, new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
			return "payfor_error";
		}
		
		Date currDate = busiBeanProcess.getCurrDbTime();
		TApsTxnLog tApsTxnLog = tApsTxnLogService.getTapsTxnLogByTime(accessBean.getBusiCd(), currDate,TDataDictConst.CAPITALDIR_C);
		if(tApsTxnLog == null){
			errorMap.put(TDataDictConst.UNSUCCESSFUL, new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
			return "payfor_error";
		}
		int ii = 0;
			String rlatKbpsSrcSettleDt = tApsTxnLog.getRLAT_KBPS_SRC_SETTLE_DT();
			String rlatSrcModuleCd = tApsTxnLog.getRLAT_SRC_MODULE_CD();
			String rlatKbpsTraceNo = tApsTxnLog.getRLAT_KBPS_TRACE_NO();
			short rlatSubTxnSeq = tApsTxnLog.getRLAT_SUB_TXN_SEQ();
			// 查询出关联的交易
			TApsTxnLog tApsTxnLogRlat = tApsTxnLogService.getTapsTxnLogByKey(rlatKbpsSrcSettleDt, rlatSrcModuleCd, rlatKbpsTraceNo, rlatSubTxnSeq);
			ii = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLogRlat, TDataDictConst.TXN_CUSTMR_SURE);
		int i = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLog, TDataDictConst.TXN_CUSTMR_SURE);
		if(i>0 && ii>0){
			errorMap.put(TDataDictConst.SUCCEED, new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED));
		}else{
			errorMap.put(TDataDictConst.UNSUCCESSFUL, new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
		}
		return "payfor_error";
	}
}
