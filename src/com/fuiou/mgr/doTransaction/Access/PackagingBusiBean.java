package com.fuiou.mgr.doTransaction.Access;

import java.util.Date;
import java.util.List;

import com.fuiou.mer.model.TBusiRut;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.VerifyBusiBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;

/**
 * 计算后重新组装业务对象
 * yangliehui
 *
 */
public class PackagingBusiBean {
	TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
	private String refundsKeys[] = null;
	public String[] getRefundsKeys() {
		return refundsKeys;
	}
	public void setRefundsKeys(String[] refundsKeys) {
		this.refundsKeys = refundsKeys;
	}
	/**
	 * 组装业务对象
	 * @param busiBeanError		业务校验错误的交易，因为业务校验错误的也要入库
	 * 							所以需要重新组装业务对象
	 * @param forTxnFeeAmtBean	手续费、路由计算后返回的对象
	 * @return					入库所需要的业务对象
	 */
	public BusiBean toPackaging(BusiBean busiBeanError,ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean){
		String busiCd = forTxnFeeAmtBean.getBusiCd();
		String mchnt = forTxnFeeAmtBean.getMchntCd();
		Date currDbTime = forTxnFeeAmtBean.getCurrDbTime();
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(mchnt, "1");
		String mchntTp = tInsMchntInf.getMCHNT_TP();
		if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(busiCd)){
			InComeForBusiBean inComeForBusiBean = new InComeForBusiBean();
			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
			InComeForBusiBean inComeForBusiBeanError = (InComeForBusiBean)busiBeanError;
			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanListError = inComeForBusiBeanError.getInComeForFileBusiDetailRowBeanList();
			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanListFeeAmtBean = forTxnFeeAmtBean.getInComeForList();
			inComeForBusiBean.setBusiCd(busiCd);
			inComeForBusiBean.setMchntCd(mchnt);
			inComeForBusiBean.setCurrDbTime(currDbTime);
			// 循环业务校验失败的交易
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanListError){
				inComeForBusiDetailRowBeanList.add(inComeForBusiDetailRowBean);
			}
			// 循环计算后的N笔收款
			boolean feeSetHasError = forTxnFeeAmtBean.isFeeSetHasError();	// 手续费方案是否有错
			boolean feeSetD4HasError = forTxnFeeAmtBean.isFeeSetD4HasError();	// 月结商户手续费方案是否有错
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanListFeeAmtBean){
				inComeForBusiDetailRowBeanList.add(inComeForBusiDetailRowBean);
				TBusiRut tBusiRut = inComeForBusiDetailRowBean.getOperationBean().gettBusiRut();
				if(tBusiRut == null){
				}
				// 判断到账金额是否为负数，为负数作业务校验失败处理
				long destAmtInt = (inComeForBusiDetailRowBean.getOperationBean().getDestTxnAmt()-inComeForBusiDetailRowBean.getOperationBean().getTxnFeeAmt());
				if(destAmtInt<=0){
					inComeForBusiDetailRowBean.getOperationBean().setFeeHasError(true);
				}
				// 实时结算商户没有找到手续费方案记录为错误
				if(feeSetHasError){
					inComeForBusiDetailRowBean.getOperationBean().setFeeSetHasError(true);
				}
				// 月结商户有生效的手续费方案记录为错误
				if(feeSetD4HasError){
					inComeForBusiDetailRowBean.getOperationBean().setFeeSetD4HasError(true);
				}
			}
			return inComeForBusiBean;
		}else if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
			PayForBusiBean payForBusiBean = new PayForBusiBean();
			List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
			PayForBusiBean payForBusiBeanError = (PayForBusiBean)busiBeanError;
			List<PayForBusiDetailRowBean> payForBusiDetailRowBeanListError =  payForBusiBeanError.getPayForBusiDetailRowBeanList();
			List<PayForBusiDetailRowBean> payForBusiDetailRowBeanListFeeAmtBean = forTxnFeeAmtBean.getPayForList();
			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanListFeeAmtBean = forTxnFeeAmtBean.getInComeForList();
			payForBusiBean.setBusiCd(busiCd);
			payForBusiBean.setMchntCd(mchnt);
			payForBusiBean.setCurrDbTime(currDbTime);
			// 循环业务校验失败的交易
			for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanListError){
				payForBusiDetailRowBeanList.add(payForBusiDetailRowBean);
			}
			// 循环计算后的N笔付款
			int rultErrorCount = 0;	// N笔中没有匹配的路由的数量，如果都没有匹配到路由按业务校验失败处理
			int txnFeeErrorCount = 0; // N笔中没有找到手续费的数量，如果没有找到手续费按业务校验失败处理
			int feeErrorCount = 0;
			boolean feeSetHasError = forTxnFeeAmtBean.isFeeSetHasError();	// 手续费方案是否有错
			boolean feeSetD4HasError = forTxnFeeAmtBean.isFeeSetD4HasError(); // 月结商户手续费方案是否有错
			for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanListFeeAmtBean){
				payForBusiDetailRowBeanList.add(payForBusiDetailRowBean);
				TBusiRut tBusiRut = payForBusiDetailRowBean.getOperationBean().gettBusiRut();
				if(tBusiRut == null){
					rultErrorCount ++ ;
				}
				Integer txnFeeAmtId = payForBusiDetailRowBean.getOperationBean().getTxnFeeAmtId();
				if(txnFeeAmtId == null && mchntTp.startsWith("D6")){
					txnFeeErrorCount ++;
				}
				// 判断到账金额是否为负数，为负数作业务校验失败处理
				long destAmtInt = (payForBusiDetailRowBean.getOperationBean().getDestTxnAmt());
				if(destAmtInt<=0){
					payForBusiDetailRowBean.getOperationBean().setFeeHasError(true);
					feeErrorCount ++;
				}
				if(feeSetHasError){
					payForBusiDetailRowBean.getOperationBean().setFeeSetHasError(true);
				}
				if(feeSetD4HasError){
					payForBusiDetailRowBean.getOperationBean().setFeeSetD4HasError(true);
				}
			}
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanListFeeAmtBean){
				if(payForBusiDetailRowBeanListFeeAmtBean.size() == 0 || rultErrorCount == payForBusiDetailRowBeanListFeeAmtBean.size()||txnFeeErrorCount == payForBusiDetailRowBeanListFeeAmtBean.size()||feeErrorCount == payForBusiDetailRowBeanListFeeAmtBean.size() || feeSetHasError){
//				if(payForBusiDetailRowBeanListFeeAmtBean.size() == 0 || rultErrorCount == payForBusiDetailRowBeanListFeeAmtBean.size()||feeErrorCount == payForBusiDetailRowBeanListFeeAmtBean.size()){
					inComeForBusiDetailRowBean.getOperationBean().setOneTxnHasError(true);
					if(feeSetHasError){
						inComeForBusiDetailRowBean.getOperationBean().setFeeSetHasError(true);
					}
					if(feeSetD4HasError){
						inComeForBusiDetailRowBean.getOperationBean().setFeeSetD4HasError(true);
					}
				}else{
					inComeForBusiDetailRowBean.getOperationBean().setOneTxnHasError(false);
				}
				payForBusiBean.setInComeForBusiDetailRowBean(inComeForBusiDetailRowBean);
			}
			
			return payForBusiBean;
		}else if(TDataDictConst.BUSI_CD_VERIFY.equals(busiCd)){
			VerifyBusiBean verifyBusiBean = new VerifyBusiBean();
			List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanList = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
			VerifyBusiBean verifyBusiBeanError = (VerifyBusiBean)busiBeanError;
			List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanListError = verifyBusiBeanError.getVerifyFileBusiDetailRowBeanList();
			List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanListFeeAmtBean = forTxnFeeAmtBean.getVerifyList();
			verifyBusiBean.setBusiCd(busiCd);
			verifyBusiBean.setMchntCd(mchnt);
			verifyBusiBean.setCurrDbTime(currDbTime);
			// 循环业务校验失败的交易
			for(VerifyBusiDetailRowBean verifyBusiDetailRowBean:verifyBusiDetailRowBeanListError){
				verifyBusiDetailRowBeanList.add(verifyBusiDetailRowBean);
			}
			for(VerifyBusiDetailRowBean verifyBusiDetailRowBean:verifyBusiDetailRowBeanListFeeAmtBean){
				verifyBusiDetailRowBeanList.add(verifyBusiDetailRowBean);
			}
			return verifyBusiBean;
		}
		return  null;
	}
}
