package com.fuiou.mgr.action.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.incomefor.SingleIcfReqAction;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.VerifyInfAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class SimpleVerifyAction extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(SingleIcfReqAction.class);
	private String bank;//银行代码
	private String dedNum;//银行账号
	private String userName;//用户名
	private String certtp;//证件类型
	private String certno;//证件号
	private String message;//错误信息
	private String confirm;//商户确定的信息
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getDedNum() {
		return dedNum;
	}
	public void setDedNum(String dedNum) {
		this.dedNum = dedNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCerttp() {
		return certtp;
	}
	public void setCerttp(String certtp) {
		this.certtp = certtp;
	}
	public String getCertno() {
		return certno;
	}
	public void setCertno(String certno) {
		this.certno = certno;
	}
	public String init(){
		//SystemParams.bankMap
		String[] bankCds = SystemParams.getProperty("BANK_CDS_VERIFY_SZ_STL_CNTR").split("_");
		List<TRootBankInf> tRootBankInfs = new ArrayList<TRootBankInf>();
		for(String bankCd: bankCds){
			TRootBankInf tRootBankInf = SystemParams.bankMap.get(bankCd);
			if(tRootBankInf!=null){
				 tRootBankInfs.add(tRootBankInf);
			}
		}
		ActionContext.getContext().put("tRootBankInfs", tRootBankInfs);
		return "simpleVerify";
	}
	public String simpleCommit(){
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		if(tOperatorInf==null){
			message="编码："+TDataDictConst.UNSUCCESSFUL+  "描述：操作失败";
			return "result";
		}
		try {
			String mchntCd = tOperatorInf.getMCHNT_CD();
			// 文件来源
			String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
			// 业务类型
			String busiCd = TDataDictConst.BUSI_CD_VERIFY;
			// 交易数据类型
			String txnDataType = "DATA";
			// 接入对象(文件)
			VerifyInfAccess accessBean = new VerifyInfAccess();
			accessBean.setBusiCd(busiCd);
			accessBean.setTxnInfSource(srcModuleCd);
			accessBean.setMchntCd(mchntCd);
			accessBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
			accessBean.setBankno(bank.trim());
			accessBean.setAccntno(dedNum.trim());
			accessBean.setAccntnm(userName.trim());
			accessBean.setCerttp(certtp.trim());
			accessBean.setCertno(certno.trim());
			
			TxnInfBean txnInfBean = null;
			BusiBean busiBean = null;
			FileError fileError = null;
			// 通过接入适配器 传入接入对象 返回中间对象
			AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
			txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
			// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
			BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
			busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
			busiBean = busiAdapter.getBusiBean();
			fileError = busiAdapter.getFileError();
			
			List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
			if(fileRowErrorList != null){
				for(int i = 0;i<fileRowErrorList.size();i++){
					FileRowError fileRowError = fileRowErrorList.get(i);
					if(fileRowError.getColumnErrors().size() > 0){
						RowColumnError rowColumnError = fileRowError.getColumnErrors().get(0);
						message="编码： "+rowColumnError.getErrCode()+"  描述："+rowColumnError.getErrMemo();
						return "result";
					}
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
			
			//商户确定
			if("confirm".equals(confirm)){
				// 组合业务对象
				PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
				BusiBean packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
				packBusiBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
				TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
				txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
				
				List<FileRowError> fileRowOperateErrors = txnDbService.getFileRowOperateErrors();
				if(fileRowOperateErrors.size() != 0){
					for(int i = 0;i<fileRowOperateErrors.size();i++){
						FileRowError fileRowError = fileRowOperateErrors.get(i);
						if(fileRowError.getColumnErrors().size() > 0){
							RowColumnError rowColumnError = fileRowError.getColumnErrors().get(0);
							message=rowColumnError.getErrCode()+"  "+rowColumnError.getErrMemo();
							return "result";
						}
					}
				}
				FileError fileErrorFromDbService = txnDbService.getFileError();
				if(fileErrorFromDbService.getFileSumError() != null){
					if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
						fileRowErrorList = fileErrorFromDbService.getFileRowErrors();
						if(fileRowErrorList != null){
							if(fileRowErrorList.size()>0){
								for(FileRowError fileRowError : fileRowErrorList){
									List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
									if(rowColumnErrorList != null){
										if(rowColumnErrorList.size() > 0){
											RowColumnError rowColumnError = rowColumnErrorList.get(0);
											message="编码： "+rowColumnError.getErrCode()+"  描述："+rowColumnError.getErrMemo();
											logger.error(rowColumnError.getErrCode()+" "+rowColumnError.getErrMemo());
											return "result";
										}
									}
								}
							}
						}
						logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
						message=fileError.getFileSumError().getErrCode()+"  "+fileError.getFileSumError().getErrMemo();
						return "result";
					}
				}
				// 修改交易状态
				Date currDate = busiBean.getCurrDbTime();
				TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
				
				TApsTxnLog tApsTxnLog = tApsTxnLogService.getTapsTxnLogByTime(busiCd, currDate,TDataDictConst.CAPITALDIR_D);
				if(tApsTxnLog == null){
					message="编码： "+TDataDictConst.UNSUCCESSFUL+"  描述：确认发送交易失败";
		            return "result";
				}
				int i = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLog, TDataDictConst.TXN_CUSTMR_SURE);
				if(i>0){			
					message="编码： "+TDataDictConst.SUCCEED+"  描述： 操作成功";
					TRootBankInf rootBankInf = SystemParams.bankMap.get(bank);
					String bankName=rootBankInf.getBANK_NM();
					ActionContext.getContext().put("accessBean", accessBean);
					ActionContext.getContext().put("bankName", bankName);
					ActionContext.getContext().put("success", "交易状态：商户已确认，等待确认发送");
					return "success";
				}else{
					message="编码： "+TDataDictConst.UNSUCCESSFUL+"  描述：确认发送交易失败";
					return "result";
				}
			}else{
				TRootBankInf rootBankInf = SystemParams.bankMap.get(bank);
				String bankName=rootBankInf.getBANK_NM();
				ActionContext.getContext().put("accessBean", accessBean);
				ActionContext.getContext().put("bankName", bankName);
				return "confirm";
			}
		} catch (Exception e) {
			logger.error("exception:",e);
			message="编码： "+TDataDictConst.UNSUCCESSFUL+"  描述：操作失败";
			return "result";
		}
	}
}
