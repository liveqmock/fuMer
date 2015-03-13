package com.fuiou.mgr.bean.access;

public class VerifyInfAccess extends AccessBean {
	private String bankno;		// 总行代码
	private String accntno;		// 账号
	private String accntnm;		// 账号名称
	private String certtp;		// 证件类型
	private String certno;		// 证件名称
	private String merdt;			// 请求日期，HTTP直连接口时用
	private String orderno;			// 请求流水，HTTP直连接口时用
	public String getBankno() {
		return bankno;
	}
	public void setBankno(String bankno) {
		this.bankno = bankno;
	}
	public String getAccntno() {
		return accntno;
	}
	public void setAccntno(String accntno) {
		this.accntno = accntno;
	}
	public String getAccntnm() {
		return accntnm;
	}
	public void setAccntnm(String accntnm) {
		this.accntnm = accntnm;
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
	public String getMerdt() {
		return merdt;
	}
	public void setMerdt(String merdt) {
		this.merdt = merdt;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
}
