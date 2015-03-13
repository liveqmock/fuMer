package com.fuiou.mgr.doTransaction.Access;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fuiou.mer.model.VCityInf;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.VerifyBusiBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;

/**
 * 业务拆分处理类
 * 代收：N笔收款+1笔付款（线下）
 * 代付：N笔付款+1笔收款
 * 验证：不需要拆分
 * yangliehui
 *
 */
public class SplitTrade {
	
	private ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean;		// 手续费计算Bean
	private BusiBean operationVerifyErrorBusibean;	// 业务校验错误的交易记录
	private long refundAmountDest;	// 退款目标
	public ForAccessTxnFeeAmtRutBean getForTxnFeeAmtBean() {
		return forTxnFeeAmtBean;
	}
	public void setForTxnFeeAmtBean(ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean) {
		this.forTxnFeeAmtBean = forTxnFeeAmtBean;
	}
	public BusiBean getOperationVerifyErrorBusibean() {
		return operationVerifyErrorBusibean;
	}
	public void setOperationVerifyErrorBusibean(BusiBean operationVerifyErrorBusibean) {
		this.operationVerifyErrorBusibean = operationVerifyErrorBusibean;
	}
	public long getRefundAmountDest() {
		return refundAmountDest;
	}
	public void setRefundAmountDest(long refundAmountDest) {
		this.refundAmountDest = refundAmountDest;
	}
	/**
	 * 业务拆分方法
	 * 1、线上交易的需要做业务校验
	 * 2、业务校验不通过的不计算手续费和路由。
	 * @param busiBean
	 */
	public void splitTrade(BusiBean busiBean){
		ForAccessTxnFeeAmtRutBean forTxnFeeAmt = new ForAccessTxnFeeAmtRutBean();
		List<InComeForBusiDetailRowBean> inComeForFeeAmt = forTxnFeeAmt.getInComeForList();
		List<PayForBusiDetailRowBean> payForFeeAmt = forTxnFeeAmt.getPayForList();
		List<VerifyBusiDetailRowBean> verifyFeeAmt = forTxnFeeAmt.getVerifyList();
		forTxnFeeAmt.setBusiCd(busiBean.getBusiCd());
		forTxnFeeAmt.setMchntCd(busiBean.getMchntCd());
		forTxnFeeAmt.setCurrDbTime(busiBean.getCurrDbTime());
		// 1、判断业务类型
		// 代收
		if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(busiBean.getBusiCd())){
			InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
			InComeForBusiBean inComeForBusiBeanError = new InComeForBusiBean();	// 代收业务校验错误集合
			// 循环业务对象中的代收交易
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanList){
				// 每笔交易业务校验后的结果
			 	OperationBean operationBean = inComeForBusiDetailRowBean.getOperationBean();
			 	// 业务校验是否有错
			 	if(operationBean.getHasError()){
			 		inComeForBusiBeanError.getInComeForFileBusiDetailRowBeanList().add(inComeForBusiDetailRowBean);
			 	}else{
			 		// 代收业务：N笔收款+1笔付款
			 		// N笔收款
			 		operationBean.setDestTxnAmt(inComeForBusiDetailRowBean.getDetailAmt());
			 		inComeForFeeAmt.add(inComeForBusiDetailRowBean);
			 	}
			}
			// 业务校验错误的交易记录
			this.operationVerifyErrorBusibean = inComeForBusiBeanError;
			// 手续费计算Bean
			this.forTxnFeeAmtBean = forTxnFeeAmt;
		}else if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiBean.getBusiCd())){
			PayForBusiBean payForBusiBean = (PayForBusiBean)busiBean;
			List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
			PayForBusiBean payForBusiBeanError = new PayForBusiBean();	// 付款业务校验错误集合
			// 循环业务对象中的付款交易
			long amt = 0;
			for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanList){
				// 每笔交易业务校验后的结果
				OperationBean operationBean = payForBusiDetailRowBean.getOperationBean();
				// 业务校验是否有错
				if(operationBean.getHasError()){
					payForBusiBeanError.getPayForBusiDetailRowBeanList().add(payForBusiDetailRowBean);
				}else{
					// 付款业务：N笔付款+1笔收款
					// N笔付款
					operationBean.setDestTxnAmt(payForBusiDetailRowBean.getDetailAmt());
					// 省代码
					String cityCode = payForBusiDetailRowBean.getCityCode();
					String provCode = null;
					if(StringUtils.isNotBlank(cityCode)){
						VCityInf vCityInf = SystemParams.cityInfMap.get(cityCode);
						if(vCityInf != null){
						    provCode = vCityInf.getCITY_PROV_CD();
						}
					}
					payForBusiDetailRowBean.setProvCode(provCode);
					payForFeeAmt.add(payForBusiDetailRowBean);
					amt += payForBusiDetailRowBean.getDetailAmt();
				}
			}
			// 1笔收款(线下,不需要做业务校验)
			InComeForBusiDetailRowBean inComeForBusiDetailRowBean = new InComeForBusiDetailRowBean();
			inComeForBusiDetailRowBean.setDetailAmt(amt);
			inComeForBusiDetailRowBean.getOperationBean().setDestTxnAmt(amt);
			inComeForFeeAmt.add(inComeForBusiDetailRowBean);
			this.operationVerifyErrorBusibean = payForBusiBeanError;
			this.forTxnFeeAmtBean = forTxnFeeAmt;
		}else if(TDataDictConst.BUSI_CD_VERIFY.equals(busiBean.getBusiCd())){
			VerifyBusiBean verifyBusiBean = (VerifyBusiBean)busiBean;
			List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanList = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
			VerifyBusiBean verifyBusiBeanError = new VerifyBusiBean();
			for(VerifyBusiDetailRowBean verifyBusiDetailRowBean:verifyBusiDetailRowBeanList){
				OperationBean operationBean = verifyBusiDetailRowBean.getOperationBean();
				if(operationBean.getHasError()){
					verifyBusiBeanError.getVerifyFileBusiDetailRowBeanList().add(verifyBusiDetailRowBean);
				}else{
					verifyFeeAmt.add(verifyBusiDetailRowBean);
				}
			}
			this.operationVerifyErrorBusibean = verifyBusiBeanError;
			this.forTxnFeeAmtBean = forTxnFeeAmt;
		}
	}
}
