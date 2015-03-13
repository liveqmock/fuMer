package com.fuiou.mgr.checker;

import java.util.ArrayList;
import java.util.List;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TPmsBankInfDft;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.VPmsBankInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TPmsBankInfService;
import com.fuiou.mer.service.VPmsBankInfService;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.business.VerifyBusiBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.util.StringUtil;

/**
 * 业务校验器
 * yangliehui
 *
 */
public class BusiChecker {
	private static TDataDictService tDataDictService = new TDataDictService();
	/**
	 * 代收业务校验器
	 * @param inComeForBusiBean
	 * @return
	 */
	public static InComeForBusiBean InComeForOperationValidate(InComeForBusiBean inComeForBusiBean) {
		List<InComeForBusiDetailRowBean> inComeforFileBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
		for (InComeForBusiDetailRowBean inComeForFileBusiDetailRowBean: inComeforFileBusiDetailRowBeanList) {
			int rowNo = inComeForFileBusiDetailRowBean.getActualRowNum();
		    boolean hasError = false; //false：不存在业务错误，true：存在业务错误
		    
		    String lineStrInCome = inComeForFileBusiDetailRowBean.getDetailSeriaNo()+"|"+ inComeForFileBusiDetailRowBean.getBankCd()+"|"+ inComeForFileBusiDetailRowBean.getBankAccount()+"|"+ inComeForFileBusiDetailRowBean.getAccountName()+"|"+ FuMerUtil.formatFenToYuan(inComeForFileBusiDetailRowBean.getDetailAmt()) +"|"+ inComeForFileBusiDetailRowBean.getEnpSeriaNo()+"|"+ inComeForFileBusiDetailRowBean.getMemo()+"|"+ inComeForFileBusiDetailRowBean.getMobile();
		    FileRowError fileRowError = inComeForFileBusiDetailRowBean.getOperationBean().getFileRowErrro();
		    fileRowError.setRowNo(rowNo);
			fileRowError.setRow(lineStrInCome);
			List<RowColumnError> rowColumnErrorList = new ArrayList<RowColumnError>();
			fileRowError.setColumnErrors(rowColumnErrorList);
		    
            if(SystemParams.bankMap.get(inComeForFileBusiDetailRowBean.getBankCd()) == null){
            		hasError = true;
                    RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.INTER_BANK_CD_ER);
    				rowColumnError.setErrMemo("开户行代码" + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.INTER_BANK_CD_ER));
    				rowColumnErrorList.add(rowColumnError);
            }
            // 获取卡bin
            TCardBin tCardBin = CardUtil.getTCardBinByCardNo(inComeForFileBusiDetailRowBean.getBankAccount(), SystemParams.cardBinMap);
            // 获取发卡机构号issCd，校验开户行代码substring(2,6)与总行代码是否一致,
            String issCd = CardUtil.getIssCdByCardNo(inComeForFileBusiDetailRowBean.getBankAccount(), SystemParams.cardBinMap);
            if(tCardBin != null){ // 卡BIN不为空，需要校验用户总行代号和卡BIN表里的是否一致
                if(null == issCd || issCd.length() != 10){
                    hasError = true; //不一致
                    RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.FILE_BANK_ACCOUNTS_ER);
    				rowColumnError.setErrMemo("扣款人银行账号" + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_BANK_ACCOUNTS_ER));
    				rowColumnErrorList.add(rowColumnError);
                }
            }else{ // 卡BIN为空，找到总行代号对应的开户行代号 4位转10位
                TRootBankInf tRootBankInf = SystemParams.bankMap.get(inComeForFileBusiDetailRowBean.getBankCd());
                if(tRootBankInf == null){
                    hasError = true;
                    RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.INTER_BANK_CD_ER);
    				rowColumnError.setErrMemo("开户行代码" + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.INTER_BANK_CD_ER));
    				rowColumnErrorList.add(rowColumnError);
                }else{
                	issCd = tRootBankInf.getFY_BANK_CD();
                }
            }
            long money = inComeForFileBusiDetailRowBean.getDetailAmt();
        	if(!TDataDictConst.BUSI_CD_WITHDRAW.equals(inComeForBusiBean.getBusiCd())){//PW35提现的交易没有金额的限制
        		if (money < 1 || money > new CheckOutBase().getMaxAmtIncomeFor()) {
        			hasError = true;
        			RowColumnError rowColumnError = new RowColumnError();
        			rowColumnError.setErrCode(TDataDictConst.FILE_MONEY_QUOTA);
        			rowColumnError.setErrMemo("金额"+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_MONEY_QUOTA));
        			rowColumnErrorList.add(rowColumnError);
        		}
        	}
            OperationBean operationBean = inComeForFileBusiDetailRowBean.getOperationBean();
            operationBean.setHasError(hasError);
            operationBean.setIssCd(issCd);
            operationBean.settCardBin(tCardBin);
		}
		return inComeForBusiBean;
	}
	
	/**
	 * 代付业务校验器
	 * @param payForBusiBean
	 * @return
	 */
	public static PayForBusiBean payForOperationValidate(PayForBusiBean payForBusiBean){
		List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
		for (PayForBusiDetailRowBean payForBusiDetailRowBean : payForBusiDetailRowBeanList) {
			// 业务校验错误记录
			int rowNo = payForBusiDetailRowBean.getActualRowNum();
			String lineStr = payForBusiDetailRowBean.getDetailSeriaNo() + "|" + payForBusiDetailRowBean.getBankCd() + "|" + payForBusiDetailRowBean.getCityCode() + "|" + payForBusiDetailRowBean.getBranchBank() + "|" + payForBusiDetailRowBean.getBankAccount() + "|" + payForBusiDetailRowBean.getAccountName() + "|" + FuMerUtil.formatFenToYuan(payForBusiDetailRowBean.getDetailAmt()) + "|" + payForBusiDetailRowBean.getEnpSeriaNo() + "|" + payForBusiDetailRowBean.getMemo() + "|" + payForBusiDetailRowBean.getMobile();
			FileRowError fileRowError = payForBusiDetailRowBean.getOperationBean().getFileRowErrro();
			fileRowError.setRowNo(rowNo);
			fileRowError.setRow(lineStr);
			List<RowColumnError> rowColumnErrorList = new ArrayList<RowColumnError>();
			fileRowError.setColumnErrors(rowColumnErrorList);
		    boolean hasError = false; //false：不存在业务错误，true：存在业务错误
			TRootBankInf tRootBankInf = SystemParams.bankMap.get(payForBusiDetailRowBean.getBankCd());
			String issCd = "";
			if (tRootBankInf == null) {
				hasError = true;
				RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.INTER_BANK_CD_ER);
				rowColumnError.setErrMemo("开户行代码" + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.INTER_BANK_CD_ER));
				rowColumnErrorList.add(rowColumnError);
			} else {
				issCd = tRootBankInf.getFY_BANK_CD();
			}
			// 获取卡bin carbin中的行别，和商户填写的行别的校验已经不需要了
			TCardBin tCardBin = CardUtil.getTCardBinByCardNo(payForBusiDetailRowBean.getBankAccount(), SystemParams.cardBinMap);
			//获取电子联行号
			String issInsRes = null;
			String subBankName = "";
			TPmsBankInfService tPmsBankInfService = new TPmsBankInfService();
	        if(TDataDictConst.BANK_CD_CSH.equals(payForBusiDetailRowBean.getBankCd())){ //城商行，先查全名，再查"***银行%分行"，再查"***银行"
	            if (StringUtil.isNotEmpty(payForBusiDetailRowBean.getBranchBank())) {
	                issInsRes = tPmsBankInfService.selectByBankCdAndCityCdAndBankName(payForBusiDetailRowBean.getBankCd(),payForBusiDetailRowBean.getCityCode(),payForBusiDetailRowBean.getBranchBank());
	                if(null == issInsRes){
	                    subBankName = new CheckOutBase().subBankName3(payForBusiDetailRowBean.getBranchBank());
	                    if(StringUtil.isNotEmpty(subBankName)){
	                    	issInsRes = tPmsBankInfService.selectTPmsBankInfByNotLike(payForBusiDetailRowBean.getBankCd(), payForBusiDetailRowBean.getCityCode(), "%"+subBankName+"%分行"+"%");
	                        if(issInsRes==null){
	                            issInsRes = tPmsBankInfService.selectByBankCdAndCityCdAndBankName(payForBusiDetailRowBean.getBankCd(), payForBusiDetailRowBean.getCityCode(), subBankName);
	                        }
	                    }
	                }
	            }
	        } else if(TDataDictConst.BANK_CD_NSH.equals(payForBusiDetailRowBean.getBankCd()) || TDataDictConst.BANK_CD_NSH2.equals(payForBusiDetailRowBean.getBankCd()) || TDataDictConst.BANK_CD_NSH3.equals(payForBusiDetailRowBean.getBankCd())) { //农商行，查全名
	            if (!StringUtil.isEmpty(payForBusiDetailRowBean.getBranchBank())) {//如果支行没有填就按行别和城市代码在农村关键字来查,填了全匹配
	                issInsRes = tPmsBankInfService.selectByBankCdAndCityCdAndBankName(payForBusiDetailRowBean.getBankCd(),payForBusiDetailRowBean.getCityCode(),payForBusiDetailRowBean.getBranchBank());
	            }
	            if(issInsRes==null){
                	issInsRes = tPmsBankInfService.selectTPmsBankInfByNotLike(payForBusiDetailRowBean.getBankCd(), payForBusiDetailRowBean.getCityCode(),"%营业%");
                }
                if(issInsRes == null){
                	issInsRes = tPmsBankInfService.selectTPmsBankInfByNotLike(payForBusiDetailRowBean.getBankCd(), payForBusiDetailRowBean.getCityCode(),"%分行%");
                }
                if(issInsRes == null){
                	issInsRes = tPmsBankInfService.selectTPmsBankInfByNotLike(payForBusiDetailRowBean.getBankCd(), payForBusiDetailRowBean.getCityCode(),"%农村%");
                }
	        } else {//其他的如果没有填支行名称就根据开户行代码+城市代码查询默认电子联行号，否则去查支行表，查不到再找默认的
	            if(StringUtil.isEmpty(payForBusiDetailRowBean.getBranchBank())){
		            TPmsBankInfDft tPmsBankInfDft = SystemParams.tPmsBankInfDftMap.get(payForBusiDetailRowBean.getBankCd()+payForBusiDetailRowBean.getCityCode());
		            if(null != tPmsBankInfDft){
		                issInsRes = tPmsBankInfDft.getINTER_BANK_NO();
		            }
	            }else{
		            if(RegexCheckUtil.checkIsDigital(payForBusiDetailRowBean.getBranchBank())){
		            	if(payForBusiDetailRowBean.getBranchBank().length()==12){
		            		issInsRes = tPmsBankInfService.selectByBankCdAndCityCdAndInterBankNo(payForBusiDetailRowBean.getBankCd(),payForBusiDetailRowBean.getCityCode(),payForBusiDetailRowBean.getBranchBank());
		            	}
		            }else{
		            	issInsRes = tPmsBankInfService.selectTPmsBankInfByNotLike(payForBusiDetailRowBean.getBankCd(),payForBusiDetailRowBean.getCityCode(),"%"+payForBusiDetailRowBean.getBranchBank()+"%");
		            }
		            if(issInsRes == null){	
		            	TPmsBankInfDft tPmsBankInfDft = SystemParams.tPmsBankInfDftMap.get(payForBusiDetailRowBean.getBankCd()+payForBusiDetailRowBean.getCityCode());
		            	if(null != tPmsBankInfDft){
		            		issInsRes = tPmsBankInfDft.getINTER_BANK_NO();
		            	}
		            }
	            }
	        }
	        VPmsBankInf vPmsBankInf = null;
			if (null == issInsRes) {
				hasError = true;
				RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.FILE_ISS_INS_CD_ER);
				rowColumnError.setErrMemo(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_ISS_INS_CD_ER));
				rowColumnErrorList.add(rowColumnError);
			} else {
				VPmsBankInfService vPmsBankInfService = new VPmsBankInfService();
				vPmsBankInf = vPmsBankInfService.selectByInterBankNo(issInsRes);
			}
			long money = payForBusiDetailRowBean.getDetailAmt();
			if(payForBusiDetailRowBean.isFlag()){//提现的不需验证金额的限额
				if (money < 1 || money > new CheckOutBase().getMaxAmtPayFor()) {
					hasError = true;
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FILE_MONEY_QUOTA);
					rowColumnError.setErrMemo("金额"+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_MONEY_QUOTA));
					rowColumnErrorList.add(rowColumnError);
				}
			}
			OperationBean operationBean = payForBusiDetailRowBean.getOperationBean();
			operationBean.setHasError(hasError);
			operationBean.setIssCd(issCd);
			operationBean.setIssInsRes(issInsRes);
			operationBean.settCardBin(tCardBin);
			operationBean.setvPmsBankInf(vPmsBankInf);
			operationBean.setSubBankName(subBankName);
		}
		return payForBusiBean;
	}
	
	/**
	 * 验证业务校验器
	 * @param payForBusiBean
	 * @return
	 */
	public static VerifyBusiBean verifyOperationValidate(VerifyBusiBean verifyBusiBean) {
		List<VerifyBusiDetailRowBean> verifyFileBusiDetailRowBeanList = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
		for (VerifyBusiDetailRowBean verifyFileBusiDetailRowBean: verifyFileBusiDetailRowBeanList) {
			// 业务校验错误记录
			int rowNo = verifyFileBusiDetailRowBean.getActualRowNum();
			String lineStr = verifyFileBusiDetailRowBean.getDetailSeriaNo()+"|"+ verifyFileBusiDetailRowBean.getBankCd()+"|"+ verifyFileBusiDetailRowBean.getBankAccount()+"|"+ verifyFileBusiDetailRowBean.getAccountName()+"|"+ verifyFileBusiDetailRowBean.getCertificateTp()+"|"+ verifyFileBusiDetailRowBean.getCertificateNo();
			FileRowError fileRowError = verifyFileBusiDetailRowBean.getOperationBean().getFileRowErrro();
			fileRowError.setRowNo(rowNo);
			fileRowError.setRow(lineStr);
			List<RowColumnError> rowColumnErrorList = new ArrayList<RowColumnError>();
			fileRowError.setColumnErrors(rowColumnErrorList);
			
			boolean hasError = false; //false：不存在业务错误，true：存在业务错误
            if(SystemParams.getProperty("BANK_CDS_VERIFY_SZ_STL_CNTR").indexOf(verifyFileBusiDetailRowBean.getBankCd()) == -1){
                hasError = true;
                RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.INTER_BANK_CD_ER);
				rowColumnError.setErrMemo("行别："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.INTER_BANK_CD_ER));
				rowColumnErrorList.add(rowColumnError);
            }
            if(TDataDictConst.ID_TYPES_SZ_STL_CNTR.indexOf(verifyFileBusiDetailRowBean.getCertificateTp()) == -1){
                hasError = true;
                RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE);
				rowColumnError.setErrMemo(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE));
				rowColumnErrorList.add(rowColumnError);
            }
            TRootBankInf tRootBankInf = SystemParams.bankMap.get(verifyFileBusiDetailRowBean.getBankCd());
            OperationBean operationBean = verifyFileBusiDetailRowBean.getOperationBean();
            if (tRootBankInf != null) {
                String issCd = tRootBankInf.getFY_BANK_CD();
                operationBean.setIssCd(issCd);
            }
	        TCardBin tCardBin = CardUtil.getTCardBinByCardNo(verifyFileBusiDetailRowBean.getBankAccount(), SystemParams.cardBinMap);
            operationBean.setHasError(hasError);
            operationBean.settCardBin(tCardBin);
		}
		return verifyBusiBean;
	}
	
	
}
