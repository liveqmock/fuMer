package com.fuiou.mgr.doTransaction.Access;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fuiou.mer.model.TBusiRut;
import com.fuiou.mer.model.TMchntBusiRut;
import com.fuiou.mer.service.TBusiRutService;
import com.fuiou.mer.service.TMchntBusiRutService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;

public class TRutBusinessAccess {
	
	public ForAccessTxnFeeAmtRutBean doRutBusiness(ForAccessTxnFeeAmtRutBean forAccessRutBean){
		TMchntBusiRutService tMchntBusiRutService = new TMchntBusiRutService();
	    TBusiRutService tBusiRutService = new TBusiRutService();
	    //查询
		//代收路由流程
		if(forAccessRutBean.getBusiCd().equals(TDataDictConst.BUSI_CD_INCOMEFOR)){
			TMchntBusiRut tMchntBusiRutIncomeFor = tMchntBusiRutService.selectByMchntBusiCd(forAccessRutBean.getMchntCd(), forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_INCOMEFOR);
			if(null == tMchntBusiRutIncomeFor){
	            return forAccessRutBean;
	        }
			for(int i =0;i<forAccessRutBean.getInComeForList().size();i++){
				//处理收款取得收款明细:n
				InComeForBusiDetailRowBean inComeForBusiDetailRowBean = forAccessRutBean.getInComeForList().get(i);
				String cardAttr = null;
				if(inComeForBusiDetailRowBean.getOperationBean().gettCardBin()!=null){
					cardAttr = inComeForBusiDetailRowBean.getOperationBean().gettCardBin().getCARD_ATTR();
				}
				TBusiRut tBusiRut = tBusiRutService.selectByCondition(forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_INCOMEFOR,inComeForBusiDetailRowBean.getDetailAmt(),inComeForBusiDetailRowBean.getBankCd(), null, null, cardAttr, null,tMchntBusiRutIncomeFor.getBUSI_RUT_ID());
				if(null != tBusiRut){
					inComeForBusiDetailRowBean.getOperationBean().settBusiRut(tBusiRut);
				}else{
					RowColumnError error = new RowColumnError();
					error.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
					error.setErrMemo("卡号为："+inComeForBusiDetailRowBean.getBankAccount()+"的交易未找到路由");
					List<RowColumnError> rowColumnErrors = inComeForBusiDetailRowBean.getOperationBean().getFileRowErrro().getColumnErrors();
					if(rowColumnErrors==null){
						rowColumnErrors = new ArrayList<RowColumnError>();
						inComeForBusiDetailRowBean.getOperationBean().getFileRowErrro().setColumnErrors(rowColumnErrors);
					}
					rowColumnErrors.add(error);
				}
			}
		}else if(forAccessRutBean.getBusiCd().equals(TDataDictConst.BUSI_CD_PAYFOR)){
			TMchntBusiRut tMchntBusiRutIncomeFor = tMchntBusiRutService.selectByMchntBusiCd(forAccessRutBean.getMchntCd(), forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_INCOMEFOR);
			String busiRutId="";
			if(null==tMchntBusiRutIncomeFor){
				return forAccessRutBean;
			}
			TMchntBusiRut tMchntBusiRutPayFor = tMchntBusiRutService.selectByMchntBusiCd(forAccessRutBean.getMchntCd(), forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_PAYFOR);
			if(null == tMchntBusiRutIncomeFor || null == tMchntBusiRutPayFor){
	            return forAccessRutBean;
	        }
			busiRutId=tMchntBusiRutPayFor.getBUSI_RUT_ID();
		    Set<String> payForBankCds = new HashSet<String>();
    		//代付路由流程
    		for(int i =0;i<forAccessRutBean.getPayForList().size();i++){
    			//处理付款取得付款明细:n
    			PayForBusiDetailRowBean payForBusiDetailRowBean = forAccessRutBean.getPayForList().get(i);
    			payForBankCds.add(payForBusiDetailRowBean.getBankCd());
    			String cardAttr = null;
    			if(payForBusiDetailRowBean.getOperationBean().gettCardBin()!=null){
    				cardAttr = payForBusiDetailRowBean.getOperationBean().gettCardBin().getCARD_ATTR();
    			}
    			String rltBankCd = forAccessRutBean.getInComeForList().get(0).getBankCd();
    			long destAmt = payForBusiDetailRowBean.getOperationBean().getDestTxnAmt();
    			TBusiRut tBusiRut = tBusiRutService.selectByCondition(forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_PAYFOR, destAmt, payForBusiDetailRowBean.getBankCd(), payForBusiDetailRowBean.getProvCode(), payForBusiDetailRowBean.getCityCode(), cardAttr, rltBankCd,busiRutId );
    			if(null!=tBusiRut){
    				payForBusiDetailRowBean.getOperationBean().settBusiRut(tBusiRut);
    			}else{
					RowColumnError error = new RowColumnError();
					error.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
					error.setErrMemo("卡号为："+payForBusiDetailRowBean.getBankAccount()+"的交易未找到路由");
					List<RowColumnError> rowColumnErrors = payForBusiDetailRowBean.getOperationBean().getFileRowErrro().getColumnErrors();
					if(rowColumnErrors==null){
						rowColumnErrors = new ArrayList<RowColumnError>();
						payForBusiDetailRowBean.getOperationBean().getFileRowErrro().setColumnErrors(rowColumnErrors);
					}
					rowColumnErrors.add(error);
				}
    		}
    		for(int i =0;i<forAccessRutBean.getInComeForList().size();i++){
    			//处理收款取得收款明细:1
    			InComeForBusiDetailRowBean inComeForBusiDetailRowBean = forAccessRutBean.getInComeForList().get(i);
    			String cardAttr = null;
    			if(inComeForBusiDetailRowBean.getOperationBean().gettCardBin()!=null){
    				cardAttr = inComeForBusiDetailRowBean.getOperationBean().gettCardBin().getCARD_ATTR();
    			}
    			String rltBankCd = null;
    			if(payForBankCds.size()==1){
    			    for(String payForBankCd: payForBankCds){
    			        rltBankCd = payForBankCd;
    			    }
    			}
    			TBusiRut tBusiRut =tBusiRutService.selectByCondition(forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_INCOMEFOR,inComeForBusiDetailRowBean.getDetailAmt(),inComeForBusiDetailRowBean.getBankCd(), null, null,cardAttr, rltBankCd,tMchntBusiRutIncomeFor.getBUSI_RUT_ID());
    			if(null!=tBusiRut){
    				inComeForBusiDetailRowBean.getOperationBean().settBusiRut(tBusiRut);
    			}
    		}
    	}
		else if(forAccessRutBean.getBusiCd().equals(TDataDictConst.BUSI_CD_VERIFY)){
			 TMchntBusiRut tMchntBusiRutVerify = tMchntBusiRutService.selectByMchntBusiCd(forAccessRutBean.getMchntCd(), forAccessRutBean.getBusiCd(), null);
		    if(null == tMchntBusiRutVerify){
                return forAccessRutBean;
            }
			for(int i =0;i<forAccessRutBean.getVerifyList().size();i++){
				VerifyBusiDetailRowBean verifyBusiDetailRowBean = forAccessRutBean.getVerifyList().get(i);
    			String cardAttr = null;
    			if(verifyBusiDetailRowBean.getOperationBean().gettCardBin()!=null){
    				cardAttr = verifyBusiDetailRowBean.getOperationBean().gettCardBin().getCARD_ATTR();
    			}
    			TBusiRut tBusiRut = tBusiRutService.selectByCondition(forAccessRutBean.getBusiCd(), TDataDictConst.CAPITAL_DIR_INCOMEFOR, verifyBusiDetailRowBean.getOperationBean().getDestTxnAmt(), verifyBusiDetailRowBean.getBankCd(), null, null, cardAttr, null, tMchntBusiRutVerify.getBUSI_RUT_ID());
    			if(null!=tBusiRut){
    				verifyBusiDetailRowBean.getOperationBean().settBusiRut(tBusiRut);
    			}
			}
		}
	    return forAccessRutBean;
		
	}
}
