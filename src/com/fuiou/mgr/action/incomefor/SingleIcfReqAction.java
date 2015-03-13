package com.fuiou.mgr.action.incomefor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.BaseAction;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.InComeForInfAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
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

public class SingleIcfReqAction extends BaseAction {

    private static final long serialVersionUID = 573961637625441677L;

    private static Logger logger = LoggerFactory.getLogger(SingleIcfReqAction.class);
	
	private String bankOfDepositName;//银行名
	private String feeAmt;//手续费
	private String srcAmt;// 原金额
	private String destAmt;//目标金额
	private String feeMsg;
	private String isDisplayFeeMsg;
	private String serialNumber;//流水号
	private String processState;//处理状态
	private String msg;
	private FileError fileError;
	private TxnInfBean txnInfBean;
	private BusiBean busiBeanProcess;
	private BusiBean packBusiBean;
	private InComeForInfAccess accessBean;
	private TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	
	public InComeForInfAccess getAccessBean() {
		return accessBean;
	}
	public void setAccessBean(InComeForInfAccess accessBean) {
		this.accessBean = accessBean;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getBankOfDepositName() {
		return bankOfDepositName;
	}
	public void setBankOfDepositName(String bankOfDepositName) {
		this.bankOfDepositName = bankOfDepositName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getProcessState() {
		return processState;
	}
	public void setProcessState(String processState) {
		this.processState = processState;
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
	    // 检查银行代码
		String gylBankCd=SystemParams.getProperty("BANK_CDS_INCOME_FOR_GZ_UNION_PAY");
        if (SystemParams.bankMap.get(accessBean.getBankCd())==null || gylBankCd.indexOf(accessBean.getBankCd())==-1) {
            this.addFieldError("bankOfDeposit", "银行不正确");
        }
        // 检查金额
        long money = Formator.yuan2Fen(accessBean.getAmount());
        if (money < 1 || money > new CheckOutBase().getMaxAmtIncomeFor()) {
        	System.out.println("金额:"+new CheckOutBase().getMaxAmtIncomeFor());
            this.addFieldError("amount", "金额超限");
        }
        if (!StringUtil.isEmpty(accessBean.getEnpSeriaNo()) && CheckOutBase.exceedDbLenth(accessBean.getEnpSeriaNo(), 80)) {
			this.addFieldError("enSerialNum","企业流水号超长" );
		}
		// 备注
		if (!StringUtil.isEmpty(accessBean.getMemo()) && CheckOutBase.exceedDbLenth(accessBean.getMemo(), 60) ) {
			this.addFieldError("remark","备注超长" );
		}
		// 手机号
		if (!StringUtil.isEmpty(accessBean.getMobile()) && !RegexCheckUtil.checkIsDigital2(accessBean.getMobile(), 11)) {
			this.addFieldError("phoneNum","手机号格式不正确" );
		}
	    if(this.getFieldErrors().size()>0){
	        return;
	    }
	    String mchntCd = tOperatorInf.getMCHNT_CD();					// 商户号
		// 接入对象
		accessBean.setMchntCd(mchntCd);
		accessBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(TDataDictConst.BUSI_CD_INCOMEFOR);
		txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, TDataDictConst.SRC_MODULE_CD_WEB, "DATA");
		txnInfBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(TDataDictConst.BUSI_CD_INCOMEFOR);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, TDataDictConst.SRC_MODULE_CD_WEB, "DATA");
		BusiBean busiBean = busiAdapter.getBusiBean();
		fileError = busiAdapter.getFileError();
		try {
			// 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
			// 拆分交易
			SplitTrade splitTrade = new SplitTrade();
			splitTrade.splitTrade(busiBean);
			ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean = splitTrade.getForTxnFeeAmtBean();
			BusiBean operationVerifyErrorBusibean = splitTrade.getOperationVerifyErrorBusibean();
			// 计算手续费、路由
			TradeCalculate tradeCalculate = new TradeCalculate();
			ForAccessTxnFeeAmtRutBean calculateForTxnFeeAmtBean = tradeCalculate.doCalculate(forTxnFeeAmtBean);
			List<RowColumnError> rowErrors = forTxnFeeAmtBean.getInComeForList().get(0).getOperationBean().getFileRowErrro().getColumnErrors();
			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = calculateForTxnFeeAmtBean.getInComeForList();
			if(rowErrors!=null&&rowErrors.size()>0){
				this.addFieldError("msg", rowErrors.get(0).getErrCode()+":"+rowErrors.get(0).getErrMemo());
				return ;
			}
			feeAmt = "0";
			srcAmt = "0";
			destAmt = "0";
			int feeAmtInt = 0;
			int srcAmtInt = 0;
			int destAmtInt = 0;
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanList){
				feeAmtInt += inComeForBusiDetailRowBean.getOperationBean().getTxnFeeAmt();
				srcAmtInt += inComeForBusiDetailRowBean.getOperationBean().getDestTxnAmt();
				destAmtInt += (inComeForBusiDetailRowBean.getOperationBean().getDestTxnAmt()-inComeForBusiDetailRowBean.getOperationBean().getTxnFeeAmt());
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
			packBusiBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
			busiBeanProcess = packBusiBean;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("业务对象拆分处理计算操作有误");
		}
	}
	
	@SkipValidation
	public String init() throws Exception{
		String[] bankCds = SystemParams.getProperty("BANK_CDS_INCOME_FOR_GZ_UNION_PAY").split("_");
		List<TRootBankInf> tRootBankInfs = new ArrayList<TRootBankInf>();
		TRootBankInf t=new TRootBankInf();
		t.setBANK_CD("");
		t.setBANK_NM("--请选择--");
		tRootBankInfs.add(t);
		for(String bankCd: bankCds){
			TRootBankInf tRootBankInf = SystemParams.bankMap.get(bankCd);
			if(tRootBankInf!=null){
				 tRootBankInfs.add(tRootBankInf);
			}
		}
		session.setAttribute("bankList", tRootBankInfs);
		boolean flag = MemcacheUtil.isForceVerifyMobile(tInsInf.getMCHNT_CD());
		session.setAttribute("isForceVerifyMobile", flag);
		return "bankList";
	}

	public String execute() throws Exception  {
	    bankOfDepositName = SystemParams.bankMap.get(accessBean.getBankCd()).getBANK_NM();
		return "success";

	}

	public String request() throws Exception  {
		try {
			
			TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(TDataDictConst.BUSI_CD_INCOMEFOR);
			
			txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, TDataDictConst.BUSI_CD_INCOMEFOR, TDataDictConst.SRC_MODULE_CD_WEB, "DATA", false);
			
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
										msg = memo;
										return "error";
									}
								}
							}
						}
					}
					logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
					msg = fileErrorFromDbService.getFileSumError().getErrMemo();
					return "error";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("insert TApsTxnLog fail!");
            msg = "数据插入失败！";
            return "error";
		}
		// 修改交易状态
		Date currDate = busiBeanProcess.getCurrDbTime();
		TApsTxnLog tApsTxnLog = tApsTxnLogService.getTapsTxnLogByTime(TDataDictConst.BUSI_CD_INCOMEFOR, currDate,TDataDictConst.CAPITALDIR_D);
		if(tApsTxnLog == null){
			msg = "确认发送交易失败";
            return "error";
		}
		int i=0;
		//如果是web方式提交的AC01和YZ01交易，则custmr_tp 为C0(商户已确认),否则custmr_tp 为C1(商户已确认)
		if(TDataDictConst.SRC_MODULE_CD_WEB.equals(tApsTxnLog.getSRC_MODULE_CD())&&
				(TDataDictConst.BUSI_CD_INCOMEFOR.equals(tApsTxnLog.getBUSI_CD())||TDataDictConst.BUSI_CD_VERIFY.equals(tApsTxnLog.getBUSI_CD()))){
			i = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLog, TDataDictConst.TXN_CUSTMR_SURE);
		}else{
			i = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLog, TDataDictConst.TXN_CUSTMR_SECOND_SURE);
		}
		if(i>0){
			msg = "商户已确认，等待确认发送";
			processState = "商户已确认，等待确认发送";
			serialNumber = tApsTxnLog.getKBPS_TRACE_NO();
			return "success";
		}else{
			msg = "确认发送交易失败";
            return "error";
		}
	}
}
