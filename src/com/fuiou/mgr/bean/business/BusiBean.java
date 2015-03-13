package com.fuiou.mgr.bean.business;

import java.util.Date;


public class BusiBean {
	private String busiCd;			// 业务类型：代收(AC01)；代付(PY51)；验证(YZ01)
	private String fileName;		// 文件名
	private String mchntCd;			// 商户号
	private Date currDbTime;		// 报盘日期
	private String fileSignStr;     // 文件签名数据
	private String srcModuleCd;		// 文件来源
	private String oprUsrId = "";        //操作员名称2.07新增
	public String getOprUsrId() {
		return oprUsrId;
	}
	public void setOprUsrId(String oprUsrId) {
		this.oprUsrId = oprUsrId;
	}
	public String getBusiCd() {
		return busiCd;
	}
	public Date getCurrDbTime() {
		return currDbTime;
	}
	public void setCurrDbTime(Date currDbTime) {
		this.currDbTime = currDbTime;
	}
	public void setBusiCd(String busiCd) {
		this.busiCd = busiCd;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMchntCd() {
		return mchntCd;
	}
	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}
	public String getFileSignStr() {
		return fileSignStr;
	}
	public void setFileSignStr(String fileSignStr) {
		this.fileSignStr = fileSignStr;
	}
	public String getSrcModuleCd() {
		return srcModuleCd;
	}
	public void setSrcModuleCd(String srcModuleCd) {
		this.srcModuleCd = srcModuleCd;
	}
}
