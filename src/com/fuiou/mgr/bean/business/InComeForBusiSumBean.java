package com.fuiou.mgr.bean.business;
/**
 * 代收文件汇总行业务对象
 * yangliehui
 *
 */
public class InComeForBusiSumBean {
	private String mchntCd = "";			//商户号
	private String busiCd = "";				//业务代码
	private String txnDate = "";			//交易日期
	private String seriaNo = "";			//流水号
	private int sumDetails = 0;				//总笔数
	private double sumAmt = 0;				//总金额
	
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
	public String getSeriaNo() {
		return seriaNo;
	}
	public void setSeriaNo(String seriaNo) {
		this.seriaNo = seriaNo;
	}
	public int getSumDetails() {
		return sumDetails;
	}
	public void setSumDetails(int sumDetails) {
		this.sumDetails = sumDetails;
	}
	public double getSumAmt() {
		return sumAmt;
	}
	public void setSumAmt(double sumAmt) {
		this.sumAmt = sumAmt;
	}
}
