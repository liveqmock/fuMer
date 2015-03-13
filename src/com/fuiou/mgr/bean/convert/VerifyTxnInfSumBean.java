package com.fuiou.mgr.bean.convert;
/**
 * 验证文件汇总行
 * yangliehui
 *
 */
public class VerifyTxnInfSumBean  {
	private String mchntCd = "";			//商户号
	private String busiCd = "";				//业务代码
	private String txnDate = "";			//交易日期
	private String seriaNo = "";			//流水号
	private String sumDetails = "";			//总笔数
	private String sumRow = "";				//汇总行源记录
	
	public String getSumRow() {
		return sumRow;
	}
	public void setSumRow(String sumRow) {
		this.sumRow = sumRow;
	}
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
	public String getSumDetails() {
		return sumDetails;
	}
	public void setSumDetails(String sumDetails) {
		this.sumDetails = sumDetails;
	}
	
}
