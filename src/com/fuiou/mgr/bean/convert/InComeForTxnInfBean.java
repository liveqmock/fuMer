package com.fuiou.mgr.bean.convert;

import java.util.ArrayList;
import java.util.List;

/**
 * 代收文件转换类（中间对象）
 * yangliehui
 *
 */
public class InComeForTxnInfBean extends TxnInfBean {
	//代收文件汇总行对象
	private InComeForTxnInfSumBean inComeForTxnInfSumBean = new InComeForTxnInfSumBean();
	//代收文件明细行list
	private List<InComeForTxnInfDetailRowBean> inComeForTxnInfDetailRowBeanList = new ArrayList<InComeForTxnInfDetailRowBean>();
	
	public InComeForTxnInfSumBean getInComeForTxnInfSumBean() {
		return inComeForTxnInfSumBean;
	}
	public void setInComeForTxnInfSumBean(InComeForTxnInfSumBean inComeForTxnInfSumBean) {
		this.inComeForTxnInfSumBean = inComeForTxnInfSumBean;
	}
	public List<InComeForTxnInfDetailRowBean> getInComeForTxnInfDetailRowBeanList() {
		return inComeForTxnInfDetailRowBeanList;
	}
	public void setInComeForTxnInfDetailRowBeanList(List<InComeForTxnInfDetailRowBean> inComeForTxnInfDetailRowBeanList) {
		this.inComeForTxnInfDetailRowBeanList = inComeForTxnInfDetailRowBeanList;
	}
}
