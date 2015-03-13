package com.fuiou.mgr.checker;

import java.util.List;

import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.InComeForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfDetailRowBean;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.util.StringUtil;

/**
 * 字段校验器
 * @author yangliehui
 */
public class FeildsCherker {
	protected static TDataDictService dataDictService = new TDataDictService();
	
	public static void verifyFeilds(Object txnInfDetailRowBean,String txnDataType,List<RowColumnError> rowColumnErrors,String busiCd,String mchntCd){
		// 代收和代付字段
		String detailSeriaNoVerify = "";
		String bankCdVerify = "";
		String bankAccountVerify = "";
		String accountNameVerify = "";
		String detailAmtVerify = "";
		String enpSeriaNoVerify = "";
		String memoVerify = "";
		String mobileVerify = "";
		// 代付字段
		String city = "";
		String bankNo = "";
		// 校验字段 
		String certificateTp = "";
		String certificateNo = "";
		if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(busiCd)){
			InComeForTxnInfDetailRowBean inComeForTxnInfDetailRowBean = (InComeForTxnInfDetailRowBean)txnInfDetailRowBean;
			detailSeriaNoVerify = inComeForTxnInfDetailRowBean.getDetailSeriaNo().trim();
			bankCdVerify = inComeForTxnInfDetailRowBean.getBankCd().trim();
			bankAccountVerify = inComeForTxnInfDetailRowBean.getBankAccount().trim();
			accountNameVerify = inComeForTxnInfDetailRowBean.getAccountName().trim();
			detailAmtVerify = inComeForTxnInfDetailRowBean.getDetailAmt().trim();
			enpSeriaNoVerify = inComeForTxnInfDetailRowBean.getEnpSeriaNo().trim();
			memoVerify = inComeForTxnInfDetailRowBean.getMemo().trim();
			mobileVerify = inComeForTxnInfDetailRowBean.getMobile().trim();
			certificateTp = inComeForTxnInfDetailRowBean.getCertificateTp();
			certificateNo = inComeForTxnInfDetailRowBean.getCertificateNo();
		}else if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
			PayForTxnInfDetailRowBean payForTxnInfDetailRowBean = (PayForTxnInfDetailRowBean)txnInfDetailRowBean;
			detailSeriaNoVerify = payForTxnInfDetailRowBean.getDetailSeriaNo().trim();
			bankCdVerify = payForTxnInfDetailRowBean.getBankCd().trim();
			bankAccountVerify = payForTxnInfDetailRowBean.getBankAccount().trim();
			accountNameVerify = payForTxnInfDetailRowBean.getAccountName().trim();
			detailAmtVerify = payForTxnInfDetailRowBean.getDetailAmt().trim();
			enpSeriaNoVerify = payForTxnInfDetailRowBean.getEnpSeriaNo()==null?"":payForTxnInfDetailRowBean.getEnpSeriaNo().trim();
			memoVerify = payForTxnInfDetailRowBean.getMemo().trim();
			mobileVerify = payForTxnInfDetailRowBean.getMobile().trim();
			city = payForTxnInfDetailRowBean.getCity().trim();
			bankNo = payForTxnInfDetailRowBean.getBankNo().trim();
		}else if(TDataDictConst.BUSI_CD_VERIFY.equals(busiCd)){
			VerifyTxnInfDetailRowBean verifyTxnInfDetailRowBean = (VerifyTxnInfDetailRowBean)txnInfDetailRowBean;
			detailSeriaNoVerify = verifyTxnInfDetailRowBean.getDetailSeriaNo().trim();
			bankCdVerify = verifyTxnInfDetailRowBean.getBankCd().trim();
			bankAccountVerify = verifyTxnInfDetailRowBean.getBankAccount().trim();
			accountNameVerify = verifyTxnInfDetailRowBean.getAccountName().trim();
			certificateTp = verifyTxnInfDetailRowBean.getCertificateTp().trim();
			certificateNo = verifyTxnInfDetailRowBean.getCertificateNo().trim();
		}
		
		// 代收、代付、验证都要校验的字段
		// 如果是File，则校验交易序号
		if("FILE".equals(txnDataType)){
			if (StringUtil.isEmpty(detailSeriaNoVerify)) {// 空
			    RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ER);
				rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
				rowColumnErrors.add(rowColumnError);
			} else if (!RegexCheckUtil.checkIsDigital2(detailSeriaNoVerify, TDataDictConst.FILE_CONTENT_SEQ_LENTTH)) {// 非数字
			    RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.FIlE_DETAIL_ROW_SEQ_ER);
				rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_SEQ_ER));
				rowColumnErrors.add(rowColumnError);
			}
		}
        // 开户行代码(总行代码)
        if (StringUtil.isEmpty(bankCdVerify)) {
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ER);
            rowColumnError.setErrMemo("开户行代码" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
            rowColumnErrors.add(rowColumnError);
        } else if (!RegexCheckUtil.checkIsDigital2(bankCdVerify, 4)) {
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
            rowColumnError.setErrMemo("开户行代码" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
            rowColumnErrors.add(rowColumnError);
        }
        // 银行帐号
        if (StringUtil.isEmpty(bankAccountVerify)) {
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ER);
            rowColumnError.setErrMemo("银行帐号" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
            rowColumnErrors.add(rowColumnError);
        } else if (!RegexCheckUtil.checkIsDigital(bankAccountVerify)) {
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_FORMAT_ER);
            rowColumnError.setErrMemo("银行帐号" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_FORMAT_ER));
            rowColumnErrors.add(rowColumnError);
        } else if (bankAccountVerify.length() < 10 || CheckOutBase.exceedDbLenth(bankAccountVerify, 28)) {// 超出长度
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
            rowColumnError.setErrMemo("银行帐号" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
            rowColumnErrors.add(rowColumnError);
        }
        // 户名
        if (StringUtil.isEmpty(accountNameVerify)) {
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ER);
            rowColumnError.setErrMemo("户名" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
            rowColumnErrors.add(rowColumnError);
        } else if (CheckOutBase.exceedDbNameLenth(accountNameVerify, 60)) {
            RowColumnError rowColumnError = new RowColumnError();
            rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
            rowColumnError.setErrMemo("户名" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
            rowColumnErrors.add(rowColumnError);
        } 
        // 代收、代付都要校验的字段
        if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(busiCd) || TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
        	// 金额
            if (StringUtil.isEmpty(detailAmtVerify)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ER);
                rowColumnError.setErrMemo("金额" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
                rowColumnErrors.add(rowColumnError);
            } else if (!CheckOutBase.validMoney(detailAmtVerify)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_AMT_ER);
                rowColumnError.setErrMemo("金额" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_AMT_ER));
                rowColumnErrors.add(rowColumnError);
            }
            // 企业流水号
            if (CheckOutBase.exceedDbLenth(enpSeriaNoVerify, 80)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
                rowColumnError.setErrMemo("企业流水号" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
                rowColumnErrors.add(rowColumnError);
            }
            // 备注
            if (CheckOutBase.exceedDbLenth(memoVerify, 60)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
                rowColumnError.setErrMemo("备注" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
                rowColumnErrors.add(rowColumnError);
            }
            // 手机号strs[7]
            if (!StringUtil.isEmpty(mobileVerify) && !RegexCheckUtil.checkIsDigital2(mobileVerify, 11)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
                rowColumnError.setErrMemo("手机号" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
                rowColumnErrors.add(rowColumnError);
            }
        }
        // 代付需要校验的字段
        if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
        	// 收款人开户行城市代码
			if (StringUtil.isEmpty(city)) {
			    RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ER);
				rowColumnError.setErrMemo("城市代码" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
				rowColumnErrors.add(rowColumnError);
			} else if (!RegexCheckUtil.checkIsDigital2(city, 4)) {
			    RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_FORMAT_ER);
                rowColumnError.setErrMemo("城市代码" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_FORMAT_ER));
                rowColumnErrors.add(rowColumnError);
			}
			// 收款人支行名称 中行\建行\广发 没有限制
			if (CheckOutBase.exceedDbLenth(bankNo, 250)) {// 超出长度
			    RowColumnError rowColumnError = new RowColumnError();
				rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
				rowColumnError.setErrMemo("开户行支行名称" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
				rowColumnErrors.add(rowColumnError);
			}
        }
        if(TDataDictConst.BUSI_CD_VERIFY.equals(busiCd)){
        	// 证件类型strs[4]
           if (TDataDictConst.ID_TYPES_SZ_STL_CNTR.indexOf(certificateTp) == -1) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE);
                rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE));
                rowColumnErrors.add(rowColumnError);
            }
            // 证件号码strs[5]
            if (CheckOutBase.exceedDbLenth(certificateNo, 20)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
                rowColumnError.setErrMemo("证件号码" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
                rowColumnErrors.add(rowColumnError);
            }
        }
        
        if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(busiCd)){
        	// 证件类型strs[4]
            if (StringUtil.isNotEmpty(certificateTp) && certificateTp.length() != 1) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FIlE_DETAIL_ROW_WRONG_ID_TYPE);
                rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_WRONG_ID_TYPE));
                rowColumnErrors.add(rowColumnError);
            }else if (StringUtil.isNotEmpty(certificateTp) && TDataDictConst.ID_TYPES_SZ_STL_CNTR.indexOf(certificateTp) == -1) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE);
                rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE));
                rowColumnErrors.add(rowColumnError);
            }
            // 证件号码strs[5]
            if (StringUtil.isNotEmpty(certificateNo) && CheckOutBase.exceedDbLenth(certificateNo, 20)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER);
                rowColumnError.setErrMemo("证件号码" + dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
                rowColumnErrors.add(rowColumnError);
            }
            // 手机号码
            if (MemcacheUtil.isForceVerifyMobile(mchntCd) && !RegexCheckUtil.checkMobile(mobileVerify)) {
                RowColumnError rowColumnError = new RowColumnError();
                rowColumnError.setErrCode(TDataDictConst.CONTACT_MOBILE);
                rowColumnError.setErrMemo("手机号码格式错误");
                rowColumnErrors.add(rowColumnError);
            }
        }
	}
}
