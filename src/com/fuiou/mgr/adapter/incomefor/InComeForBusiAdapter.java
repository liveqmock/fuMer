package com.fuiou.mgr.adapter.incomefor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.FileBusiAdapter;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.InComeForTxnInfBean;
import com.fuiou.mgr.bean.convert.InComeForTxnInfDetailRowBean;
import com.fuiou.mgr.checker.BusiChecker;
import com.fuiou.mgr.checker.FeildsCherker;
import com.fuiou.mgr.checker.SumChecker;
import com.fuiou.mgr.checkout.CheckOutBase;
/**
 * 代收业务适配器
 * yangliehui
 *
 */
public class InComeForBusiAdapter extends FileBusiAdapter{
	private static Logger logger = LoggerFactory.getLogger(InComeForAccessAdapter.class);
	private InComeForBusiBean inComeForBusiBean = new InComeForBusiBean();
	private InComeForTxnInfBean inComeForTxnInfBean = new InComeForTxnInfBean();
	private String busiCd = "AC01";
	
	/** 文件汇总行 */
	
	public InComeForTxnInfBean getInComeForFileConvertBean() {
		return (InComeForTxnInfBean)txnInfBean;
	}
	
	@Override
	protected void beanInit() {
		inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
		inComeForBusiBean.setBusiCd(busiCd);
		inComeForBusiBean.setMchntCd(mchntCd);
		inComeForBusiBean.setCurrDbTime(currDbTime);
	}
	
	@Override
	public BusiBean getBusiBean() {
		return this.inComeForBusiBean;
	}
	@Override
	protected void sumRowValidate() {
		SumChecker.sumRowValidate(txnInfBean, getFileSumError());
	}
	@Override
	protected int fileRowValidate() {
		List<InComeForTxnInfDetailRowBean> inComeForTxnInfDetailRowBeanList = inComeForTxnInfBean.getInComeForTxnInfDetailRowBeanList();
		List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
		int rowNo = 0;
		Set<String> indexSet = new HashSet<String>(); //明细序号判重
		for(InComeForTxnInfDetailRowBean inComeForTxnInfDetailRowBean:inComeForTxnInfDetailRowBeanList){
			List<RowColumnError> rowColumnErrors = new ArrayList<RowColumnError>();
			rowNo = inComeForTxnInfDetailRowBean.getActualRowNum();
			InComeForBusiDetailRowBean inComeForBusiDetailRowBean = new InComeForBusiDetailRowBean();
			if("FILE".equals(txnDataType)){
				if(CheckOutBase.exceedDbLenth(inComeForTxnInfDetailRowBean.getLineStr(), 603)){
					RowColumnError rowColumnError = getColumnError(rowNo, inComeForTxnInfDetailRowBean.getLineStr());
					rowColumnError.setErrCode(TDataDictConst.FILE_READ_EP);
					rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP));
					continue;
				}
				String[] strs = inComeForTxnInfDetailRowBean.getLineStr().split(TDataDictConst.FILE_CONTENT_APART,10);
				if (!(strs.length == 10 || strs.length == 8)) {
				    RowColumnError rowColumnError = getColumnError(rowNo, inComeForTxnInfDetailRowBean.getLineStr());
	                rowColumnError.setErrCode(TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER);
	                rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER));
	                continue;
				}
				if(indexSet.contains(inComeForTxnInfDetailRowBean.getDetailSeriaNo())){ // 序号重复
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ITERANT);
					rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ITERANT));
					rowColumnErrors.add(rowColumnError);
				}
				indexSet.add(inComeForTxnInfDetailRowBean.getDetailSeriaNo());
			}
			FeildsCherker.verifyFeilds(inComeForTxnInfDetailRowBean, txnDataType, rowColumnErrors,txnInfBean.getTxnInfType(),inComeForBusiBean.getMchntCd());
			if (rowColumnErrors.size() > 0) {
	            FileRowError fileRowError = getFileRowError();
	            fileRowError.setRowNo(rowNo);
	            fileRowError.setRow(inComeForTxnInfDetailRowBean.getLineStr());
	            fileRowError.setColumnErrors(rowColumnErrors);
	            continue;
	        }
			inComeForBusiDetailRowBean.setDetailSeriaNo(inComeForTxnInfDetailRowBean.getDetailSeriaNo());
			inComeForBusiDetailRowBean.setBankCd(inComeForTxnInfDetailRowBean.getBankCd());
			inComeForBusiDetailRowBean.setBankAccount(inComeForTxnInfDetailRowBean.getBankAccount());
			inComeForBusiDetailRowBean.setAccountName(inComeForTxnInfDetailRowBean.getAccountName());
			long amt=0;
			try {
				amt=Long.parseLong(FuMerUtil.formatYuanToFen(inComeForTxnInfDetailRowBean.getDetailAmt()));
			} catch (Exception e) {
				logger.error("Exception:",e);
			}
			inComeForBusiDetailRowBean.setDetailAmt(amt);
			inComeForBusiDetailRowBean.setEnpSeriaNo(inComeForTxnInfDetailRowBean.getEnpSeriaNo());
			inComeForBusiDetailRowBean.setMemo(inComeForTxnInfDetailRowBean.getMemo());
			inComeForBusiDetailRowBean.setMobile(inComeForTxnInfDetailRowBean.getMobile());
			inComeForBusiDetailRowBean.setActualRowNum(rowNo);
			inComeForBusiDetailRowBean.setMerdt(inComeForTxnInfDetailRowBean.getMerdt());
			inComeForBusiDetailRowBean.setOrderno(inComeForTxnInfDetailRowBean.getOrderno());
			if(StringUtils.isNotEmpty(inComeForTxnInfDetailRowBean.getCertificateTp())){
				inComeForBusiDetailRowBean.setCertificateTp(inComeForTxnInfDetailRowBean.getCertificateTp().trim());
			}
			if(StringUtils.isNotEmpty(inComeForTxnInfDetailRowBean.getCertificateNo())){
				inComeForBusiDetailRowBean.setCertificateNo(inComeForTxnInfDetailRowBean.getCertificateNo().trim());
			}
			inComeForBusiDetailRowBeanList.add(inComeForBusiDetailRowBean);
		}
		return inComeForBusiDetailRowBeanList.size();
	}
	@Override
	protected void fileSumValidate() {
		SumChecker.beanSumValidate(txnInfBean, inComeForBusiBean, getFileSumError());
	}
	@Override
	protected void operationValidate() {
		BusiChecker.InComeForOperationValidate(inComeForBusiBean);
	}
}
