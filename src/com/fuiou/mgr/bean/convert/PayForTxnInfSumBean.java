package com.fuiou.mgr.bean.convert;
/**
 * 代付文件汇总行
 * yangliehui
 *
 */
public class PayForTxnInfSumBean {
	private String mchntCd = "";	//商户号
	private String busiCd = "";		//业务代码
	private String txnDate = "";	//交易日期
	private String dayNo="";		//当日序号
	private String sumDetails = "";	//明细数目
	private String sumAmt = "";		//汇总金额
	private String sumRow = "";		//汇总行源记录
	
	public String getMchntCd() {
		return mchntCd;
	}
	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}
	public String getBusiCd() {
		return busiCd;
	}
	public void setBusiCd(String busiCd) {
		this.busiCd = busiCd;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public String getDayNo() {
		return dayNo;
	}
	public void setDayNo(String dayNo) {
		this.dayNo = dayNo;
	}
	public String getSumDetails() {
		return sumDetails;
	}
	public void setSumDetails(String sumDetails) {
		this.sumDetails = sumDetails;
	}
	public String getSumAmt() {
		return sumAmt;
	}
	public void setSumAmt(String sumAmt) {
		this.sumAmt = sumAmt;
	}
	public String getSumRow() {
		return sumRow;
	}
	public void setSumRow(String sumRow) {
		this.sumRow = sumRow;
	}
	
	

}
