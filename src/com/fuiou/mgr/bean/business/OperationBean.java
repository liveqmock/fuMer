package com.fuiou.mgr.bean.business;

import com.fuiou.mer.model.TBusiRut;
import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.VPmsBankInf;

public class OperationBean {
	private String issCd;			// 发卡机构号
	
	private TCardBin tCardBin;		// 卡bin
	
	private boolean hasError;		// 校验是否有错
	
	private boolean oneTxnHasError;	// 代收中的1笔付款或代付中的1笔收款对应的N笔交易如果业务校验都失败设置为true，否则为false
	
	private boolean feeHasError;	// 手续费是否有错，当到账金额小于零是记为业务校验错误
	
	private boolean feeSetHasError = false; // 该商户的手续费方案是否有错   有错：true；正确：false；
	
	private boolean feeSetD4HasError = false; // 该月结商户如果有生效的手续费方案报错 有错：true；正确：false；
	
	private String issInsRes;
	
	private String subBankName;
	
	private long destTxnAmt = 0;  //目标金额(分)
	
	private long txnFeeAmt = 0;   //手续费(分)
	
	private Integer txnFeeAmtId ;   //手续费标识
	
	private boolean feeFlag;    //手续费成功修改标识
	
	private TBusiRut tBusiRut;									//交易路由
	
	private String feeMd;
	
	private FileRowError fileRowErrro = new FileRowError();
	// 关联退票
	private String returnTicketRlatKbpsSrcSettleDt;
	private String returnTicketRlatModuleCd;
	private String returnTicketRlatKbpsTraceNo;
	private short returnTicketRlatTxnSeq;
	// 第一笔原交易KEY
	private String firstKbpsSrcSettleDt;
	private String firstModuleCd;
	private String firstKbpsTraceNo;
	private short firstTxnSeq;
	
	public FileRowError getFileRowErrro() {
		return fileRowErrro;
	}

	public void setFileRowErrro(FileRowError fileRowErrro) {
		this.fileRowErrro = fileRowErrro;
	}

	public Integer getTxnFeeAmtId() {
		return txnFeeAmtId;
	}

	public void setTxnFeeAmtId(Integer txnFeeAmtId) {
		this.txnFeeAmtId = txnFeeAmtId;
	}

	public boolean isOneTxnHasError() {
		return oneTxnHasError;
	}

	public void setOneTxnHasError(boolean oneTxnHasError) {
		this.oneTxnHasError = oneTxnHasError;
	}

	public TBusiRut gettBusiRut() {
		return tBusiRut;
	}

	public void settBusiRut(TBusiRut tBusiRut) {
		this.tBusiRut = tBusiRut;
	}

	public boolean isFeeFlag() {
		return feeFlag;
	}

	public void setFeeFlag(boolean feeFlag) {
		this.feeFlag = feeFlag;
	}

	public long getDestTxnAmt() {
		return destTxnAmt;
	}

	public void setDestTxnAmt(long destTxnAmt) {
		this.destTxnAmt = destTxnAmt;
	}

	public long getTxnFeeAmt() {
		return txnFeeAmt;
	}

	public void setTxnFeeAmt(long txnFeeAmt) {
		this.txnFeeAmt = txnFeeAmt;
	}

	public String getSubBankName() {
		return subBankName;
	}

	public void setSubBankName(String subBankName) {
		this.subBankName = subBankName;
	}

	private VPmsBankInf vPmsBankInf;

	public String getIssInsRes() {
		return issInsRes;
	}

	public void setIssInsRes(String issInsRes) {
		this.issInsRes = issInsRes;
	}

	public VPmsBankInf getvPmsBankInf() {
		return vPmsBankInf;
	}

	public void setvPmsBankInf(VPmsBankInf vPmsBankInf) {
		this.vPmsBankInf = vPmsBankInf;
	}

	public String getIssCd() {
		return issCd;
	}

	public void setIssCd(String issCd) {
		this.issCd = issCd;
	}

	public TCardBin gettCardBin() {
		return tCardBin;
	}

	public void settCardBin(TCardBin tCardBin) {
		this.tCardBin = tCardBin;
	}

	public boolean getHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public boolean isFeeHasError() {
		return feeHasError;
	}

	public void setFeeHasError(boolean feeHasError) {
		this.feeHasError = feeHasError;
	}

	public boolean isFeeSetHasError() {
		return feeSetHasError;
	}

	public void setFeeSetHasError(boolean feeSetHasError) {
		this.feeSetHasError = feeSetHasError;
	}

	public String getReturnTicketRlatKbpsSrcSettleDt() {
		return returnTicketRlatKbpsSrcSettleDt;
	}

	public void setReturnTicketRlatKbpsSrcSettleDt(String returnTicketRlatKbpsSrcSettleDt) {
		this.returnTicketRlatKbpsSrcSettleDt = returnTicketRlatKbpsSrcSettleDt;
	}

	public String getReturnTicketRlatModuleCd() {
		return returnTicketRlatModuleCd;
	}

	public void setReturnTicketRlatModuleCd(String returnTicketRlatModuleCd) {
		this.returnTicketRlatModuleCd = returnTicketRlatModuleCd;
	}

	public String getReturnTicketRlatKbpsTraceNo() {
		return returnTicketRlatKbpsTraceNo;
	}

	public void setReturnTicketRlatKbpsTraceNo(String returnTicketRlatKbpsTraceNo) {
		this.returnTicketRlatKbpsTraceNo = returnTicketRlatKbpsTraceNo;
	}

	public short getReturnTicketRlatTxnSeq() {
		return returnTicketRlatTxnSeq;
	}

	public void setReturnTicketRlatTxnSeq(short returnTicketRlatTxnSeq) {
		this.returnTicketRlatTxnSeq = returnTicketRlatTxnSeq;
	}

	public String getFirstKbpsSrcSettleDt() {
		return firstKbpsSrcSettleDt;
	}

	public void setFirstKbpsSrcSettleDt(String firstKbpsSrcSettleDt) {
		this.firstKbpsSrcSettleDt = firstKbpsSrcSettleDt;
	}

	public String getFirstModuleCd() {
		return firstModuleCd;
	}

	public void setFirstModuleCd(String firstModuleCd) {
		this.firstModuleCd = firstModuleCd;
	}

	public String getFirstKbpsTraceNo() {
		return firstKbpsTraceNo;
	}

	public void setFirstKbpsTraceNo(String firstKbpsTraceNo) {
		this.firstKbpsTraceNo = firstKbpsTraceNo;
	}

	public short getFirstTxnSeq() {
		return firstTxnSeq;
	}

	public void setFirstTxnSeq(short firstTxnSeq) {
		this.firstTxnSeq = firstTxnSeq;
	}

	public String getFeeMd() {
		return feeMd;
	}

	public void setFeeMd(String feeMd) {
		this.feeMd = feeMd;
	}

	public boolean isFeeSetD4HasError() {
		return feeSetD4HasError;
	}

	public void setFeeSetD4HasError(boolean feeSetD4HasError) {
		this.feeSetD4HasError = feeSetD4HasError;
	}
}
