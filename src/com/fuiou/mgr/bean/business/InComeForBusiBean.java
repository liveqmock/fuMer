package com.fuiou.mgr.bean.business;

import java.util.ArrayList;
import java.util.List;

/**
 * 代收文件业务对象
 * yangliehui
 *
 */
public class InComeForBusiBean extends BusiBean{
	private InComeForBusiSumBean inComeForFileBusiSumBean = new InComeForBusiSumBean();
	private List<InComeForBusiDetailRowBean> inComeForFileBusiDetailRowBeanList = new ArrayList<InComeForBusiDetailRowBean>();
	
	public InComeForBusiSumBean getInComeForFileBusiSumBean() {
		return inComeForFileBusiSumBean;
	}
	public void setInComeForFileBusiSumBean(InComeForBusiSumBean inComeForFileBusiSumBean) {
		this.inComeForFileBusiSumBean = inComeForFileBusiSumBean;
	}
	public List<InComeForBusiDetailRowBean> getInComeForFileBusiDetailRowBeanList() {
		return inComeForFileBusiDetailRowBeanList;
	}
	public void setInComeForFileBusiDetailRowBeanList(List<InComeForBusiDetailRowBean> inComeForFileBusiDetailRowBeanList) {
		this.inComeForFileBusiDetailRowBeanList = inComeForFileBusiDetailRowBeanList;
	}
}
