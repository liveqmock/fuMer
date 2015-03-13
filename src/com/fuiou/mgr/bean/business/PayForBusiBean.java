package com.fuiou.mgr.bean.business;

import java.util.ArrayList;
import java.util.List;

/**
 * 代付文件业务对象
 * yangliehui
 *
 */
public class PayForBusiBean extends BusiBean{
	private PayForBusiSumBean payForBusiBean = new PayForBusiSumBean();
	private List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = new ArrayList<PayForBusiDetailRowBean>();
	private InComeForBusiDetailRowBean inComeForBusiDetailRowBean = new InComeForBusiDetailRowBean();
	private String refundsKeys[] = null;
	public String[] getRefundsKeys() {
		return refundsKeys;
	}
	public void setRefundsKeys(String[] refundsKeys) {
		this.refundsKeys = refundsKeys;
	}
	public InComeForBusiDetailRowBean getInComeForBusiDetailRowBean() {
		return inComeForBusiDetailRowBean;
	}
	public void setInComeForBusiDetailRowBean(InComeForBusiDetailRowBean inComeForBusiDetailRowBean) {
		this.inComeForBusiDetailRowBean = inComeForBusiDetailRowBean;
	}
	public PayForBusiSumBean getPayForBusiBean() {
		return payForBusiBean;
	}
	public void setPayForBusiBean(PayForBusiSumBean payForBusiBean) {
		this.payForBusiBean = payForBusiBean;
	}
	public List<PayForBusiDetailRowBean> getPayForBusiDetailRowBeanList() {
		return payForBusiDetailRowBeanList;
	}
	public void setPayForBusiDetailRowBeanList(List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList) {
		this.payForBusiDetailRowBeanList = payForBusiDetailRowBeanList;
	}
}
