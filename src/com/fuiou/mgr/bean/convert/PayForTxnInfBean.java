package com.fuiou.mgr.bean.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * 代付文件转换类（中间对象）
 * yangliehui
 *
 */
public class PayForTxnInfBean extends TxnInfBean{
	//代付文件汇总行对象
	private PayForTxnInfSumBean payForTxnInfSumBean=new PayForTxnInfSumBean();
	private TxnInfBean txnInfBean=new TxnInfBean();
	public TxnInfBean getTxnInfBean() {
		return txnInfBean;
	}
	public void setTxnInfBean(TxnInfBean txnInfBean) {
		this.txnInfBean = txnInfBean;
	}
	//代付文件明细行list
	private List<PayForTxnInfDetailRowBean> payForTxnInfDetailRowBeanList = new ArrayList<PayForTxnInfDetailRowBean>();
	public PayForTxnInfSumBean getPayForTxnInfSumBean() {
		return payForTxnInfSumBean;
	}
	public void setPayForTxnInfSumBean(PayForTxnInfSumBean payForTxnInfSumBean) {
		this.payForTxnInfSumBean = payForTxnInfSumBean;
	}
	public List<PayForTxnInfDetailRowBean> getPayForTxnInfDetailRowBeanList() {
		return payForTxnInfDetailRowBeanList;
	}
	public void setPayForTxnInfDetailRowBeanList(
			List<PayForTxnInfDetailRowBean> payForTxnInfDetailRowBeanList) {
		this.payForTxnInfDetailRowBeanList = payForTxnInfDetailRowBeanList;
	}

	

}
