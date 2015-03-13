package com.fuiou.mgr.bean.convert;

import java.util.ArrayList;
import java.util.List;


/**
 * 验证文件转换类（中间对象）
 * 
 * yangliehui
 * 
 */
public class VerifyTxnInfBean extends TxnInfBean {
	private VerifyTxnInfSumBean  verifyTxnInfSumBean = new VerifyTxnInfSumBean();
	// 验证文件汇总行对象
	private List<VerifyTxnInfDetailRowBean> verifyTxnInfDetailRowBeanList = new ArrayList<VerifyTxnInfDetailRowBean>();
	public VerifyTxnInfSumBean getVerifyTxnInfSumBean() {
		return verifyTxnInfSumBean;
	}
	public void setVerifyTxnInfSumBean(VerifyTxnInfSumBean verifyTxnInfSumBean) {
		this.verifyTxnInfSumBean = verifyTxnInfSumBean;
	}
	public List<VerifyTxnInfDetailRowBean> getVerifyTxnInfDetailRowBeanList() {
		return verifyTxnInfDetailRowBeanList;
	}
	public void setVerifyTxnInfDetailRowBeanList(
			List<VerifyTxnInfDetailRowBean> verifyTxnInfDetailRowBeanList) {
		this.verifyTxnInfDetailRowBeanList = verifyTxnInfDetailRowBeanList;
	}

	
}
