package com.fuiou.mgr.doTransaction.Access;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TBusiRut;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.StringUtils;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;
import com.fuiou.mgr.util.VirtAcntUtil;

/**
 * 交易计算
 * 手续费计算&路由匹配
 * yangliehui
 *
 */
public class TradeCalculate {
	private static Logger logger = LoggerFactory.getLogger(TradeCalculate.class);
	/** 错误信息 */
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	public LinkedHashMap<String, String> getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(LinkedHashMap<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	/**
	 * 数据计算入口
	 */
	public ForAccessTxnFeeAmtRutBean doCalculate(ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean){
		List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = forTxnFeeAmtBean.getInComeForList();
		List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = forTxnFeeAmtBean.getPayForList();
		List<VerifyBusiDetailRowBean> verifyBusiDetailRowBean = forTxnFeeAmtBean.getVerifyList();
		if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(forTxnFeeAmtBean.getBusiCd())){
			// 没有要进行计算的交易
			if(inComeForBusiDetailRowBeanList == null || inComeForBusiDetailRowBeanList.size()==0){
				logger.error("代收业务，没有要进行计算的交易记录，业务校验都不正确");
				errorMap.put("01", "代收业务，没有要进行计算的交易记录，业务校验都不正确");
				return forTxnFeeAmtBean;
			}
			ForAccessTxnFeeAmtRutBean feeForTxnFeeAmtBean = feeCalculate(forTxnFeeAmtBean);
			ForAccessTxnFeeAmtRutBean routeForTxnFeeAmtBean = routeCalculate(feeForTxnFeeAmtBean);
			return routeForTxnFeeAmtBean;
		}else if(TDataDictConst.BUSI_CD_PAYFOR.equals(forTxnFeeAmtBean.getBusiCd())){
			if(inComeForBusiDetailRowBeanList == null || inComeForBusiDetailRowBeanList.size()==0){
				logger.error("付款业务，1笔付款交易为空");
				errorMap.put("05", "付款业务，1笔付款交易为空");
				return forTxnFeeAmtBean;
			}
			if(payForBusiDetailRowBeanList == null || payForBusiDetailRowBeanList.size() == 0){
				logger.error("付款业务，没有要进行计算的交易记录，业务校验都不正确");
				errorMap.put("06", "付款业务，没有要进行计算的交易记录，业务校验都不正确");
				return forTxnFeeAmtBean;
			}
			// 一笔收款业务校验失败，关联的所有的付款也校验失败，不用作手续费和路由计算
			if(inComeForBusiDetailRowBeanList.get(0).getOperationBean().getHasError()){
				logger.error("付款业务，1笔收款业务校验失败");
				errorMap.put("08", "代付业务，1笔收款业务校验失败");
				return forTxnFeeAmtBean;
			}
			// 退票业务不计算手续费
			if(TDataDictConst.BUSI_CD_PAYFOR.equals(forTxnFeeAmtBean.getBusiCd())){
				ForAccessTxnFeeAmtRutBean feeForTxnFeeAmtBean = feeCalculate(forTxnFeeAmtBean);
				ForAccessTxnFeeAmtRutBean routeForTxnFeeAmtBean = routeCalculate(feeForTxnFeeAmtBean);
				return routeForTxnFeeAmtBean;
			}
		}else if(TDataDictConst.BUSI_CD_VERIFY.equals(forTxnFeeAmtBean.getBusiCd())){
			if(verifyBusiDetailRowBean == null || verifyBusiDetailRowBean.size() == 0){
				logger.error("实名验证业务，没有要进行计算的交易记录，业务校验都不正确");
				return forTxnFeeAmtBean;
			}
			ForAccessTxnFeeAmtRutBean routeForTxnFeeAmtBean = routeCalculate(forTxnFeeAmtBean);
			return routeForTxnFeeAmtBean;
		}
		return forTxnFeeAmtBean;
	}
	
	/**
	 * 手续费计算
	 */
	public ForAccessTxnFeeAmtRutBean feeCalculate(ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean){
		TxnFeeAmtBusinessAccess txnFeeAmtBusiness = new TxnFeeAmtBusinessAccess();
		ForAccessTxnFeeAmtRutBean returnFeeForTxnFeeAmtBean = txnFeeAmtBusiness.doTxnFeeAmtBusiness(forTxnFeeAmtBean);
		return returnFeeForTxnFeeAmtBean;
	}
	
	/**
	 * 路由匹配
	 */
	public ForAccessTxnFeeAmtRutBean routeCalculate(ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean){
		TRutBusinessAccess tRutBusinessAccess = new TRutBusinessAccess();
		ForAccessTxnFeeAmtRutBean forAccessTxnFeeAmtRutBean = tRutBusinessAccess.doRutBusiness(forTxnFeeAmtBean);
		return forAccessTxnFeeAmtRutBean;
	}
	
	/**
	 * 检查账户余额是否足够
	 * @param calculateForTxnFeeAmtBean 手续费业务对象
	 * @param fileError 错误对象
	 */
	public synchronized void checkAccountBalance(BusiBean packBusiBean,FileError fileError,String srcModuleCd){
		String acnt = "";
		//如果有业务校验失败，则返回
		if(fileError.getFileSumError() == null){
			if(fileError.getFileRowErrors() != null){
				return;
			}
		}else{
			if(fileError.getFileRowErrors() != null||
					fileError.getFileSumError().getErrCode() != null || fileError.getFileSumError().getErrMemo() != null || fileError.getFileSumError().getRow() != null){
				return ;
			}
		}
		//只判断AP01
		if(!TDataDictConst.BUSI_CD_PAYFOR.equals(packBusiBean.getBusiCd())){
			return;
		}
		String mchntCd = packBusiBean.getMchntCd();
		TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(mchntCd, "1");
		PayForBusiBean payForBusiBean = (PayForBusiBean)packBusiBean;
		List<PayForBusiDetailRowBean> list = payForBusiBean.getPayForBusiDetailRowBeanList();
		String insCd = tInsMchntInf.getINS_CD();
		TCustStlInfService custStlInfService = new TCustStlInfService();
		List<String> acntNos = custStlInfService.getAcntNosByIndCd(insCd);
		
		FileSumError fileSumError=new FileSumError();
		FileRowError fileRowError=new FileRowError();
		List<RowColumnError> rowColumnErrors=new ArrayList<RowColumnError>();
		List<FileRowError> fileRowErrors=new ArrayList<FileRowError>();
		
		if(acntNos.size() <= 0){
			RowColumnError rowColumnError=new RowColumnError(); 
			fileSumError.setErrCode(TDataDictConst.UNOPEN_ACCOUNT);
			fileSumError.setErrMemo("没有开通虚拟账户");
			fileError.setFileSumError(fileSumError);
			rowColumnError.setErrCode(TDataDictConst.UNOPEN_ACCOUNT);
			rowColumnError.setErrMemo("没有开通虚拟账户");
			fileRowError.setRow("");
			fileRowError.setRowNo(1);
			rowColumnErrors.add(rowColumnError);
			fileRowError.setColumnErrors(rowColumnErrors);
			logger.error("没有开通虚拟账户");
		}else{
			String balances = null;//虚拟账户余额
			acnt = acntNos.get(0);
			OperationBean operationBeanInComeFor = payForBusiBean.getInComeForBusiDetailRowBean().getOperationBean();
			boolean isMchntCapital = false;
			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)
        			|| srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_WEB)){
				TBusiRut busiRut = operationBeanInComeFor.gettBusiRut();
				if(busiRut == null){
					logger.error("没有找到路由");
					return;
				}
				String mchnt_cap_cfm = busiRut.getMCHNT_CAP_CFM();// 商户资金到账
				if("0".equals(mchnt_cap_cfm)){
					isMchntCapital = true;
					balances = queryBlac(acnt, insCd);//余额查询
				}
			}
			//文件的总金额
			long sumAmt = 0;
			for(PayForBusiDetailRowBean detailBean:list){
				OperationBean operationBean = detailBean.getOperationBean();
				if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)
            			|| srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_WEB)){
					if(packBusiBean.getBusiCd().equals(TDataDictConst.BUSI_CD_PAYFOR)){
						TBusiRut busiRut = operationBean.gettBusiRut();
						if(busiRut == null ){
							continue;
						}
						String isChnlCapital = busiRut.getCHNL_CAP_CFM();	// 渠道资金到账
						if(isMchntCapital){
							if("0".equals(isChnlCapital)){
								sumAmt = sumAmt +detailBean.getDetailAmt();
								String lineStr = detailBean.getDetailSeriaNo()+"|"+detailBean.getBankCd()+"|"+detailBean.getCityCode()+"|"
								+detailBean.getBranchBank()+"|"+detailBean.getBankAccount()+"|"+detailBean.getAccountName()+"|"+FuMerUtil.formatFenToYuan(detailBean.getDetailAmt())
								+"|"+detailBean.getEnpSeriaNo()+"|"+detailBean.getMemo()+"|"+detailBean.getMobile();
								if(StringUtils.isEmpty(balances)){
									RowColumnError rowColumnError=new RowColumnError(); 
									fileSumError.setErrCode(TDataDictConst.QUERY_BALANCE_HAS_ERROR);
									fileSumError.setErrMemo("账户查余发生错误");
									fileError.setFileSumError(fileSumError);
									rowColumnError.setErrCode(TDataDictConst.QUERY_BALANCE_HAS_ERROR);
									rowColumnError.setErrMemo("账户查余发生错误");
									fileRowError.setRow(lineStr);
									fileRowError.setRowNo(detailBean.getActualRowNum());
									rowColumnErrors.add(rowColumnError);
									fileRowError.setColumnErrors(rowColumnErrors);
									logger.error("账户查余发生错误");
									break;
								}else{
									String[] amts = balances.split("\\|");
									if(Double.parseDouble(amts[1])<sumAmt){
										RowColumnError rowColumnError=new RowColumnError(); 
										fileSumError.setErrCode(TDataDictConst.BALANCE_NOT_ENOUGH);
										fileSumError.setErrMemo("累计付款金额已超出账户余额");
										fileError.setFileSumError(fileSumError);
										rowColumnError.setErrCode(TDataDictConst.BALANCE_NOT_ENOUGH);
										rowColumnError.setErrMemo("累计付款金额已超出账户余额");
										fileRowError.setRow(lineStr);
										fileRowError.setRowNo(detailBean.getActualRowNum());
										rowColumnErrors.add(rowColumnError);
										fileRowError.setColumnErrors(rowColumnErrors);
										logger.error("累计付款金额已超出账户余额");
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		if(fileRowError.getColumnErrors()!=null){
			if(fileRowError.getColumnErrors().size()>0){	
				fileRowErrors.add(fileRowError);
			}
		}
		if(fileRowErrors.size()>0){		
			fileError.setFileRowErrors(fileRowErrors);
		}
	}
	
	 /**
	 * 余额查询
	 * @return
	 */
	public String queryBlac(String acnt,String mngInsCd){
		String[] strs = null;
		String balance = "";
		try {
			try{
				strs = FasService.queryBalance(mngInsCd, acnt.trim(), VirtAcntUtil.getSsn());
			}catch (Exception e) {
				e.printStackTrace();
			}
			String balance0 = Double.valueOf(strs[3]) + "";//账面余额
			String balance1 = Double.valueOf(strs[4]) + "";//可用余额
			String balance2 = Double.valueOf(strs[5]) + "";//未结余额
			String balance3 = Double.valueOf(strs[6]) + "";//冻结余额
			balance = balance0+"|"+balance1+"|"+balance2+"|"+balance3;
		} catch (Exception e) {
			e.printStackTrace();
			balance = null;
		}//查询余额
		return balance;
	}
}
