package com.fuiou.mgr.adapter.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.FileBusiAdapter;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.business.VerifyBusiBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfDetailRowBean;
import com.fuiou.mgr.checker.BusiChecker;
import com.fuiou.mgr.checker.FeildsCherker;
import com.fuiou.mgr.checker.VerifyCherker;
import com.fuiou.mgr.checkout.CheckOutBase;

public class VerifyBusiAdapter extends FileBusiAdapter{
	private static Logger logger = LoggerFactory.getLogger(VerifyBusiAdapter.class);
	private VerifyBusiBean verifyBusiBean = new VerifyBusiBean();
	private VerifyTxnInfBean verifyTxnInfBean = new VerifyTxnInfBean();
	private String busiCd = "YZ01";
	
	/** 文件汇总行 */
	public VerifyTxnInfBean getVerifyFileConvertBean() {
		return (VerifyTxnInfBean)txnInfBean;
	}
	@Override
	public BusiBean getBusiBean() {
		return this.verifyBusiBean;
	}

	@Override
	protected void beanInit() {
		verifyTxnInfBean = (VerifyTxnInfBean)txnInfBean;
		verifyBusiBean.setBusiCd(busiCd);
		verifyBusiBean.setMchntCd(mchntCd);
		verifyBusiBean.setCurrDbTime(currDbTime);
	}

	@Override
	protected void sumRowValidate() {
		VerifyCherker.sumRowValidate(verifyTxnInfBean, getFileSumError());
		
	}

	@Override
	protected int fileRowValidate() {
		List<VerifyTxnInfDetailRowBean> verifyTxnInfDetailRowBeanList = verifyTxnInfBean.getVerifyTxnInfDetailRowBeanList();
		List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanList = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
		int rowNo = 0;
		Set<String> indexSet = new HashSet<String>(); //明细序号判重
		for(VerifyTxnInfDetailRowBean verifyTxnInfDetailRowBean:verifyTxnInfDetailRowBeanList){
			List<RowColumnError> rowColumnErrors = new ArrayList<RowColumnError>();
//			rowNo ++;
			rowNo = verifyTxnInfDetailRowBean.getActualRowNum();
			VerifyBusiDetailRowBean verifyBusiDetailRowBean = new VerifyBusiDetailRowBean();
			if("FILE".equals(txnDataType)){
				if(CheckOutBase.exceedDbLenth(verifyTxnInfDetailRowBean.getLineStr(), 603)){
					String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP);
					RowColumnError rowColumnError = getColumnError(rowNo, verifyTxnInfDetailRowBean.getLineStr());
					rowColumnError.setErrCode(TDataDictConst.FILE_READ_EP);
					rowColumnError.setErrMemo(memo);
					logger.error("明细行信息数据库比对"+memo+"["+TDataDictConst.FILE_READ_EP+"]");
					continue;
				}
				String[] strs = verifyTxnInfDetailRowBean.getLineStr().split(TDataDictConst.FILE_CONTENT_APART, 6);
				if (strs.length != 6) {
					String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER);
				    RowColumnError rowColumnError = getColumnError(rowNo, verifyTxnInfDetailRowBean.getLineStr());
	                rowColumnError.setErrCode(TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER);
	                rowColumnError.setErrMemo(memo);
	                logger.error("[明细行字段数量]"+memo+"["+TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER+"]");
	                continue;
				}
				if(indexSet.contains(verifyTxnInfDetailRowBean.getDetailSeriaNo())){ // 序号重复
					String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ITERANT);
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ITERANT);
					rowColumnError.setErrMemo(memo);
					logger.debug("[明细行序号重复]"+memo+"["+TDataDictConst.FILE_CONTENT_ROWS_ITERANT+"]");
					rowColumnErrors.add(rowColumnError);
				}
				indexSet.add(verifyTxnInfDetailRowBean.getDetailSeriaNo());
			}
			//字段校验
			FeildsCherker.verifyFeilds(verifyTxnInfDetailRowBean, txnDataType, rowColumnErrors,txnInfBean.getTxnInfType(),verifyBusiBean.getMchntCd());
			if (rowColumnErrors.size() > 0) {
	            FileRowError fileRowError = getFileRowError();
	            fileRowError.setRowNo(rowNo);
	            fileRowError.setRow(verifyTxnInfDetailRowBean.getLineStr());
	            fileRowError.setColumnErrors(rowColumnErrors);
	            continue;
	        }
			verifyBusiDetailRowBean.setDetailSeriaNo(verifyTxnInfDetailRowBean.getDetailSeriaNo());
			verifyBusiDetailRowBean.setBankCd(verifyTxnInfDetailRowBean.getBankCd());
			verifyBusiDetailRowBean.setBankAccount(verifyTxnInfDetailRowBean.getBankAccount());
			verifyBusiDetailRowBean.setAccountName(verifyTxnInfDetailRowBean.getAccountName());
			verifyBusiDetailRowBean.setCertificateNo(verifyTxnInfDetailRowBean.getCertificateNo());
			verifyBusiDetailRowBean.setCertificateTp(verifyTxnInfDetailRowBean.getCertificateTp());
			verifyBusiDetailRowBean.setActualRowNum(rowNo);
			verifyBusiDetailRowBean.setMerdt(verifyTxnInfDetailRowBean.getMerdt());
			verifyBusiDetailRowBean.setOrderno(verifyTxnInfDetailRowBean.getOrderno());
			verifyBusiDetailRowBeanList.add(verifyBusiDetailRowBean);
		}
		return verifyBusiDetailRowBeanList.size();
	}

	@Override
	protected void fileSumValidate() {
		List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanList = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
		VerifyCherker.fileSumValidate(verifyTxnInfBean, verifyBusiDetailRowBeanList, getFileSumError(),txnInfSource);
		
	}

	@Override
	protected void operationValidate() {
		BusiChecker.verifyOperationValidate(verifyBusiBean);
		
	}


}
