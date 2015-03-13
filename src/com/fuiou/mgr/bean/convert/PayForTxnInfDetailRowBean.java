package com.fuiou.mgr.bean.convert;
/**
 * 代付文件明细行
 * yangliehui
 *
 */
public class PayForTxnInfDetailRowBean {
	private int actualRowNum = 0;		// 物理行数
	private String detailSeriaNo = "";		//明细序号
	private String bankCd="";				//收款人开户行行别
//	private String province="";				//收款人开户行省份
	private String city="";					//收款人开户地市代码
	private String bankNo="";				//收款人开户行支行信息
	private String bankAccount="";			//收款人银行账户
	private String accountName = "";		//户名
	private String detailAmt = "";			//金额
	private String enpSeriaNo = "";			//企业流水号
	private String memo = "";				//备注
	private String mobile = "";				//手机号
	private String lineStr = "";			//明细行源记录
	private String merdt;			// 请求日期，HTTP直连接口时用
	private String orderno;			// 请求流水，HTTP直连接口时用
	private boolean flag = true;//是否验证金额的限制 2.08增加
	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getActualRowNum() {
		return actualRowNum;
	}
	public void setActualRowNum(int actualRowNum) {
		this.actualRowNum = actualRowNum;
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
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
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
	public String getDetailAmt() {
		return detailAmt;
	}
	public void setDetailAmt(String detailAmt) {
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
	public String getLineStr() {
		return lineStr;
	}
	public void setLineStr(String lineStr) {
		this.lineStr = lineStr;
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
