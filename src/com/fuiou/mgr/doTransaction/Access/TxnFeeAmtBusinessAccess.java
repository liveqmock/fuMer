package com.fuiou.mgr.doTransaction.Access;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TFeeSet;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TMchntFeeSet;
import com.fuiou.mer.service.TFeeSetService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TMchntFeeSetService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;


public class TxnFeeAmtBusinessAccess {
	private static Logger logger = LoggerFactory.getLogger(TxnFeeAmtBusinessAccess.class);
		TMchntFeeSetService tMchntFeeSetService = new TMchntFeeSetService();
		TFeeSetService tFeeSetService = new TFeeSetService();
		TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
		public ForAccessTxnFeeAmtRutBean doTxnFeeAmtBusiness(ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean){
			TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(forTxnFeeAmtBean.getMchntCd(), "1");
			// 判断是否查询到机构/商户信息
			if(null == tInsMchntInf){
				logger.error("手续费计算没有找到机构/商户信息");
			}
			String mchntTp = tInsMchntInf.getMCHNT_TP();
			if(!mchntTp.startsWith("D6")){ //月结商户
				logger.info("月结商户");
				if(mchntTp.startsWith("D4")){
					// 月结商户如果配置了手续费方案，且方案生效作严重错误处理
					TMchntFeeSet tMchntFeeSet =	tMchntFeeSetService.selectByMchntCdBusiCd(forTxnFeeAmtBean.getMchntCd(), forTxnFeeAmtBean.getBusiCd(),forTxnFeeAmtBean.getCurrDbTime());
					if(null != tMchntFeeSet){
						logger.info("月结商户存在生效的手续费方案，作严重错误处理");
						forTxnFeeAmtBean.setFeeSetD4HasError(true);
						return forTxnFeeAmtBean;
					}
				}else{
					logger.info("非D6、D4商户");
				}
			}else{ //实时结算商户
				TMchntFeeSet tMchntFeeSet =	tMchntFeeSetService.selectByMchntCdBusiCd(forTxnFeeAmtBean.getMchntCd(), forTxnFeeAmtBean.getBusiCd(),forTxnFeeAmtBean.getCurrDbTime());
				if(null==tMchntFeeSet){
					logger.info("实时结算商户没有查询到商户手续费关联关系，作严重错误处理");
					forTxnFeeAmtBean.setFeeSetHasError(true);
					return forTxnFeeAmtBean;
				}
				long incomeAmtSum=0;//代付收款汇总金额
				if(null==forTxnFeeAmtBean.getBusiCd()){
					return forTxnFeeAmtBean;
				}
				
				// 代收处理、收款处理
				
				if(forTxnFeeAmtBean.getBusiCd().equals(TDataDictConst.BUSI_CD_INCOMEFOR)){
					if(null==forTxnFeeAmtBean.getInComeForList()){
						return forTxnFeeAmtBean;
					}
					for(int i =0;i<forTxnFeeAmtBean.getInComeForList().size();i++){
						//处理收款取得收款明细:n
						InComeForBusiDetailRowBean inComeForBusiDetailRowBean = forTxnFeeAmtBean.getInComeForList().get(i);
						TFeeSet tFeeSetForIn = tFeeSetService.getTFeeSetByTMchntFeeSet(tMchntFeeSet.getFEE_SET_ID(),  (long)inComeForBusiDetailRowBean.getDetailAmt());
						//该商户没有相对应的手续费，默认为不需手续费
						if(null==tFeeSetForIn){
							continue;
						}
						//手续费收付方式为空，默认为不需要手续费
						if(null==tFeeSetForIn.getFEE_MD()){
							continue;
						}
						forTxnFeeAmtBean.setFeeMd(tFeeSetForIn.getFEE_MD());
						inComeForBusiDetailRowBean.getOperationBean().setFeeMd(tFeeSetForIn.getFEE_MD());
						//手续费
						inComeForBusiDetailRowBean.getOperationBean().setTxnFeeAmtId(tFeeSetForIn.getROW_ID());
						long feeAmt=getFeeAmt(tFeeSetForIn,inComeForBusiDetailRowBean.getDetailAmt());
						inComeForBusiDetailRowBean.getOperationBean().setTxnFeeAmt(feeAmt);
	                    if(tFeeSetForIn.getFEE_MD().equals("0")){//外扣
	                        // 收付方式：0 – 收本金+手续费，付本金
	                        // 目标金额=原金额+手续费
	                        long destTxnAmt = feeAmt + inComeForBusiDetailRowBean.getDetailAmt();
	                        inComeForBusiDetailRowBean.getOperationBean().setDestTxnAmt(destTxnAmt);
	                    }else{//内扣
	                        // 收付方式：1 – 收本金，付本金-手续费
	                        // 目标金额=原金额
	                        inComeForBusiDetailRowBean.getOperationBean().setDestTxnAmt(inComeForBusiDetailRowBean.getDetailAmt());
	                    }
					}
				}else if(forTxnFeeAmtBean.getBusiCd().equals(TDataDictConst.BUSI_CD_PAYFOR)){
					//  代付处理、收款处理
						if(null==forTxnFeeAmtBean.getPayForList()){
							return forTxnFeeAmtBean;
						}
						for(int i =0;i<forTxnFeeAmtBean.getPayForList().size();i++){
							//处理付款取得付款明细:n
							PayForBusiDetailRowBean payForBusiDetailRowBean = forTxnFeeAmtBean.getPayForList().get(i);
							TFeeSet tFeeSetForIn = tFeeSetService.getTFeeSetByTMchntFeeSet(tMchntFeeSet.getFEE_SET_ID(), (long)payForBusiDetailRowBean.getDetailAmt());
							//该商户没有相对应的手续费
							if(null==tFeeSetForIn){
								continue;
							}
							//手续费收付方式为空，默认为不需要手续费
							if(null==tFeeSetForIn.getFEE_MD()){
								continue;
							}
							forTxnFeeAmtBean.setFeeMd(tFeeSetForIn.getFEE_MD());
							payForBusiDetailRowBean.getOperationBean().setFeeMd(tFeeSetForIn.getFEE_MD());
							//手续费
							payForBusiDetailRowBean.getOperationBean().setTxnFeeAmtId(tFeeSetForIn.getROW_ID());
							long feeAmt=getFeeAmt(tFeeSetForIn,payForBusiDetailRowBean.getDetailAmt());
							payForBusiDetailRowBean.getOperationBean().setTxnFeeAmt(feeAmt);
							if(tFeeSetForIn.getFEE_MD().equals("0")){//外扣
								//收付方式：0 – 收本金+手续费，付本金   
								//目标金额=原金额-手续费
							 payForBusiDetailRowBean.setDetailAmt(payForBusiDetailRowBean.getDetailAmt()+feeAmt);
							 long destTxnAmt =payForBusiDetailRowBean.getDetailAmt()-feeAmt;
							 payForBusiDetailRowBean.getOperationBean().setDestTxnAmt(destTxnAmt);
							 if(destTxnAmt <= 0){
								 continue;
							 }
							 //付款汇总，付源金额
							 incomeAmtSum=(long) (incomeAmtSum+payForBusiDetailRowBean.getDetailAmt());
							}else{//内扣
								//收付方式：1 – 收本金，付本金-手续费
								//目标金额=原金额-手续费
								long destTxnAmt =payForBusiDetailRowBean.getDetailAmt()-feeAmt;
								payForBusiDetailRowBean.getOperationBean().setDestTxnAmt(destTxnAmt);
								 //付款汇总，付源金额-手续费
								if(destTxnAmt <= 0){
									 continue;
								}
								incomeAmtSum=incomeAmtSum+payForBusiDetailRowBean.getDetailAmt();
							}
						}
						
						for(int i =0;i<forTxnFeeAmtBean.getInComeForList().size();i++){
							//处理收款取得收款明细:1
							InComeForBusiDetailRowBean inComeForDetailRowBean = forTxnFeeAmtBean.getInComeForList().get(i);
							inComeForDetailRowBean.getOperationBean().setDestTxnAmt(incomeAmtSum);
							inComeForDetailRowBean.setDetailAmt(incomeAmtSum);
						}
				}
			}
			return forTxnFeeAmtBean;
		}
		
		
		//获取手续费
		public long getFeeAmt(TFeeSet tFeeSet , double detailAmt){
			//扣费模式为空默认手续费为0
			if(null==tFeeSet.getCALC_MD()){
				return 0;
			}else if(tFeeSet.getCALC_MD().equals("0")){
				//0扣率模式
				double feeAmt=(tFeeSet.getFEE_RATE()*detailAmt)/10000;
				if(tFeeSet.getMAX_FEE()<feeAmt){
					//手续费金额大于金额上限，以上限为准
				    return tFeeSet.getMAX_FEE();
				}
				else if(tFeeSet.getMIN_FEE()>feeAmt){
					//手续费金额小于金额下限，以下限为准
					return tFeeSet.getMIN_FEE();
				}else{
					//四舍五入
					return  new BigDecimal(feeAmt).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
				}
			}else if(tFeeSet.getCALC_MD().equals("1")){
				//1 – 按笔模式
				return tFeeSet.getFEE_AMT();
			}
			return 0;
		}
		
		

}
