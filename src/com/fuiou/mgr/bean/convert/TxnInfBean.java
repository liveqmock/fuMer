package com.fuiou.mgr.bean.convert;

import java.io.File;


public class TxnInfBean {
	private String txnInfType;		// 交易类型：代收(AC01)；代付(AP01)；验证(YZ01)
	private String fileName;		// 文件名
	private File file;				// 文件
	private String mchntCd;			// 商户号(用户登录号)
	private String oprUsrId;		// 操作员
	public String getOprUsrId() {
		return oprUsrId;
	}
	public void setOprUsrId(String oprUsrId) {
		this.oprUsrId = oprUsrId;
	}
	public String getTxnInfType() {
		return txnInfType;
	}
	public void setTxnInfType(String txnInfType) {
		this.txnInfType = txnInfType;
	}
	public String getMchntCd() {
		return mchntCd;
	}
	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
