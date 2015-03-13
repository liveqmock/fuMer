package com.fuiou.mgr.bean.access;
/**
 * 接入对象基类
 * yangliehui
 *
 */
public class AccessBean {
	private String txnInfSource;		// 交易信息来源：FTP；WEB；HTTP...
	private String busiCd;				// 业务类型：AC01；AP01；YZ01...
	private String mchntCd;				// 商户号(登录)
	private String oprUsrId;		// 操作员
	public String getOprUsrId() {
		return oprUsrId;
	}
	public void setOprUsrId(String oprUsrId) {
		this.oprUsrId = oprUsrId;
	}
	public String getTxnInfSource() {
		return txnInfSource;
	}
	public void setTxnInfSource(String txnInfSource) {
		this.txnInfSource = txnInfSource;
	}
	public String getBusiCd() {
		return busiCd;
	}
	public void setBusiCd(String busiCd) {
		this.busiCd = busiCd;
	}
	public String getMchntCd() {
		return mchntCd;
	}
	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}
}
