package com.fuiou.mgr.bean.business;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证文件转换业务对象
 * yangliehui
 *
 */
public class VerifyBusiBean extends BusiBean{
	private VerifyBusiSumBean verifyFileBusiSumBean = new VerifyBusiSumBean();
	private List<VerifyBusiDetailRowBean> verifyFileBusiDetailRowBeanList = new ArrayList<VerifyBusiDetailRowBean>();
	public VerifyBusiSumBean getVerifyFileBusiSumBean() {
		return verifyFileBusiSumBean;
	}
	public void setVerifyFileBusiSumBean(VerifyBusiSumBean verifyFileBusiSumBean) {
		this.verifyFileBusiSumBean = verifyFileBusiSumBean;
	}
	public List<VerifyBusiDetailRowBean> getVerifyFileBusiDetailRowBeanList() {
		return verifyFileBusiDetailRowBeanList;
	}
	public void setVerifyFileBusiDetailRowBeanList(
			List<VerifyBusiDetailRowBean> verifyFileBusiDetailRowBeanList) {
		this.verifyFileBusiDetailRowBeanList = verifyFileBusiDetailRowBeanList;
	}
	
}
