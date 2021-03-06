package com.fuiou.mgr.bean.access;
/**
 * 代收信息接入
 * yangliehui
 *
 */
public class InComeForInfAccess extends AccessBean{
	private String bankCd;			// 开户行
	private String bankAccount;		// 银行账号
	private String accountName;		// 户名
	private String amount;			// 金额
	private String enpSeriaNo;		// 企业流水号
	private String memo;			// 备注
	private String mobile;			// 手机号
	private String merdt;			// 请求日期，HTTP直连接口时用
	private String orderno;			// 请求流水，HTTP直连接口时用
	private String certificateTp;	//证件类型2.04版新增
	private String certificateNo;	//证件号码2.04版新增
	public String getCertificateTp() {
		return certificateTp;
	}
	public void setCertificateTp(String certificateTp) {
		this.certificateTp = certificateTp;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	
	public String getBankCd() {
		return bankCd;
	}
	public void setBankCd(String bankCd) {
		this.bankCd = bankCd;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getEnpSeriaNo() {
		return enpSeriaNo;
	}
	public void setEnpSeriaNo(String enpSeriaNo) {
		this.enpSeriaNo = enpSeriaNo;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
