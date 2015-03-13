package com.fuiou.mgr.bean.business;
/**
 * 代付文件明细行业务对象
 * yangliehui
 *
 */
public class PayForBusiDetailRowBean {
	private int actualRowNum = 0;			// 物理行数
	private String detailSeriaNo = "";		// 明细序号
	private String bankCd = "";				// 开户行
	private String cityCode = "";			// 开户地市
	private String provCode = "";			// 省代码
	private String branchBank = "";			// 支行信息
	private String bankAccount = "";		// 账户
	private String accountName = "";		// 户名
	private long detailAmt = 0;				// 金额(分)
	private String enpSeriaNo = "";			// 企业流水号
	private String memo = "";				// 备注
	private String mobile = "";				// 手机号
	private String merdt;			// 请求日期，HTTP直连接口时用
	private String orderno;			// 请求流水，HTTP直连接口时用
	private boolean flag = true; //是否验证金额的限制 2.08增加
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	private OperationBean operationBean = new OperationBean();		
											//业务信息
	
	public String getDetailSeriaNo() {
		return detailSeriaNo;
	}
	public int getActualRowNum() {
		return actualRowNum;
	}
	public void setActualRowNum(int actualRowNum) {
		this.actualRowNum = actualRowNum;
	}
	public String getProvCode() {
		return provCode;
	}
	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}
	public long getDetailAmt() {
		return detailAmt;
	}

	public void setDetailAmt(long detailAmt) {
		this.detailAmt = detailAmt;
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
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getBranchBank() {
		return branchBank;
	}
	public void setBranchBank(String branchBank) {
		this.branchBank = branchBank;
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
	public OperationBean getOperationBean() {
		return operationBean;
	}
	public void setOperationBean(OperationBean operationBean) {
		this.operationBean = operationBean;
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
