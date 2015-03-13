package com.fuiou.mgr.bean.business;

/**
 * 代收文件明细行业务对象
 * yangliehui
 *
 */
public class InComeForBusiDetailRowBean {
	private int actualRowNum = 0;			// 物理行数
	private String detailSeriaNo = "";			//明细序列号
	private String bankCd = "";					//开户行
	private String bankAccount = "";			//银行账号
	private String accountName = "";			//户名
	private long detailAmt = 0;					//明细金额(分)
	private String enpSeriaNo = "";				//企业流水号
	private String memo = "";					//备注
	private String mobile = "";					//手机号
	private String merdt;			// 请求日期，HTTP直连接口时用
	private String orderno;			// 请求流水，HTTP直连接口时用
	private OperationBean operationBean = new OperationBean();		//业务信息
	private String certificateTp;	//证件类型
	private String certificateNo;	//证件号码
	
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
	public int getActualRowNum() {
		return actualRowNum;
	}
	public void setActualRowNum(int actualRowNum) {
		this.actualRowNum = actualRowNum;
	}
	public OperationBean getOperationBean() {
		return operationBean;
	}
	public void setOperationBean(OperationBean operationBean) {
		this.operationBean = operationBean;
	}
	public String getDetailSeriaNo() {
		return detailSeriaNo;
	}
	public void setDetailSeriaNo(String detailSeriaNo) {
		this.detailSeriaNo = detailSeriaNo;
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
	public long getDetailAmt() {
		return detailAmt;
	}
	public void setDetailAmt(long detailAmt) {
		this.detailAmt = detailAmt;
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
