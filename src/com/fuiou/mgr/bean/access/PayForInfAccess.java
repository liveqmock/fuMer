package com.fuiou.mgr.bean.access;

public class PayForInfAccess extends AccessBean {

	private String cityCode;	// 开户市代码
	private String bankCd;		// 开户银行 代码
	private String bankNam;		// 开户行名称
	private String bankAccount; // 收款人银行账号
	private String accountName;	// 户名
	private String amount;		// 金额
	private String enpSeriaNo;	// 企业流水账号
	private String memo;		// 备注
	private String mobile;		// 手机号
	private String merdt;			// 请求日期，HTTP直连接口时用
	private String orderno;			// 请求流水，HTTP直连接口时用
	private boolean flag = true;//是否验证金额的限制 2.08增加
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getBankCd() {
		return bankCd;
	}
	public void setBankCd(String bankCd) {
		this.bankCd = bankCd;
	}
	public String getBankNam() {
		return bankNam;
	}
	public void setBankNam(String bankNam) {
		this.bankNam = bankNam;
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
