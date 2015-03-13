package com.fuiou.mgr.doTransaction.Access;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;


public class ForAccessTxnFeeAmtRutBean {
    	private String mchntCd;										// 商户id
    	private String busiCd;										// 业务类型
    	private Date currDbTime;									// 报盘时间
    	private String feeMd;		// 手续费模式：0 – 收本金+手续费，付本金 1 – 收本金，付本金-手续费
    	private boolean feeSetHasError = false; // 该商户的手续费方案是否有错   有错：true；正确：false；
    	private boolean feeSetD4HasError = false; // 该月结商户手续方案如果生效报错 有错：true；正确：false；
    
    	private  List<InComeForBusiDetailRowBean> inComeForList = new ArrayList<InComeForBusiDetailRowBean>();	
    																// 收款集合
    	private List<PayForBusiDetailRowBean> payForList = new ArrayList<PayForBusiDetailRowBean>();			
    																// 付款集合
    	private List<VerifyBusiDetailRowBean> verifyList = new ArrayList<VerifyBusiDetailRowBean>();			
    																// 实名验证集合
		
		public List<VerifyBusiDetailRowBean> getVerifyList() {
			return verifyList;
		}
		public Date getCurrDbTime() {
			return currDbTime;
		}
		public void setCurrDbTime(Date currDbTime) {
			this.currDbTime = currDbTime;
		}
		public void setVerifyList(List<VerifyBusiDetailRowBean> verifyList) {
			this.verifyList = verifyList;
		}

		public String getMchntCd() {
			return mchntCd;
		}
		public void setMchntCd(String mchntCd) {
			this.mchntCd = mchntCd;
		}

		public String getBusiCd() {
			return busiCd;
		}
		public void setBusiCd(String busiCd) {
			this.busiCd = busiCd;
		}

		public List<InComeForBusiDetailRowBean> getInComeForList() {
			return inComeForList;
		}
		public void setInComeForList(List<InComeForBusiDetailRowBean> inComeForList) {
			this.inComeForList = inComeForList;
		}

		public List<PayForBusiDetailRowBean> getPayForList() {
			return payForList;
		}
		public void setPayForList(List<PayForBusiDetailRowBean> payForList) {
			this.payForList = payForList;
		}
		public String getFeeMd() {
			return feeMd;
		}
		public void setFeeMd(String feeMd) {
			this.feeMd = feeMd;
		}
		public boolean isFeeSetHasError() {
			return feeSetHasError;
		}
		public void setFeeSetHasError(boolean feeSetHasError) {
			this.feeSetHasError = feeSetHasError;
		}
		public boolean isFeeSetD4HasError() {
			return feeSetD4HasError;
		}
		public void setFeeSetD4HasError(boolean feeSetD4HasError) {
			this.feeSetD4HasError = feeSetD4HasError;
		}
}
