package com.fuiou.mgr.adapter.payfor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.FileBusiAdapter;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.checker.BusiChecker;
import com.fuiou.mgr.checker.FeildsCherker;
import com.fuiou.mgr.checker.SumChecker;
import com.fuiou.mgr.checkout.CheckOutBase;
/**
 * 代付业务适配器
 * yangliehui
 *
 */
public class PayForBusiAdapter extends FileBusiAdapter{
	private static Logger logger = LoggerFactory.getLogger(PayForBusiAdapter.class);
	private PayForTxnInfBean payForTxnInfBean = new PayForTxnInfBean();//付款交易信息
	private PayForBusiBean payForBusiBean = new PayForBusiBean();//付款业务信息

	public PayForTxnInfBean getPayForTxnInfBean(){
		return (PayForTxnInfBean)txnInfBean;
	}
	
	@Override
	public BusiBean getBusiBean() {
		return payForBusiBean;
	}

	@Override
	protected void beanInit() {
		payForTxnInfBean = (PayForTxnInfBean)txnInfBean;
		if(payForTxnInfBean!=null){
			payForBusiBean.setBusiCd(payForTxnInfBean.getTxnInfType());
			payForBusiBean.setMchntCd(mchntCd);
			payForBusiBean.setCurrDbTime(currDbTime);
		}
	}

	@Override
	protected void sumRowValidate() {
		SumChecker.sumRowValidate(txnInfBean, getFileSumError());
	}

	@Override
	protected int fileRowValidate() {
		List<PayForTxnInfDetailRowBean> payForTxnInfDetailRowBeanList = payForTxnInfBean.getPayForTxnInfDetailRowBeanList();
		List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
		int rowNo = 0;
		Set<String> indexSet = new HashSet<String>(); //明细序号判重
		for(PayForTxnInfDetailRowBean payForTxnInfDetailRowBean:payForTxnInfDetailRowBeanList){
			List<RowColumnError> rowColumnErrors = new ArrayList<RowColumnError>();
//			rowNo ++;
			rowNo = payForTxnInfDetailRowBean.getActualRowNum();
			PayForBusiDetailRowBean payForBusiDetailRowBean = new PayForBusiDetailRowBean();
			if("FILE".equals(txnDataType)){
				if(CheckOutBase.exceedDbLenth(payForTxnInfDetailRowBean.getLineStr(), 603)){
					String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP);
					RowColumnError rowColumnError = getColumnError(rowNo, payForTxnInfDetailRowBean.getLineStr());
					rowColumnError.setErrCode(TDataDictConst.FILE_READ_EP);
					rowColumnError.setErrMemo(memo);
					logger.error("明细行信息数据库比对"+memo+"["+TDataDictConst.FILE_READ_EP+"]");
					continue;
				}
				String[] strs = payForTxnInfDetailRowBean.getLineStr().split(TDataDictConst.FILE_CONTENT_APART, 10);
				if (strs.length != 10) {
					String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER);
				    RowColumnError rowColumnError = getColumnError(rowNo, payForTxnInfDetailRowBean.getLineStr());
	                rowColumnError.setErrCode(TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER);
	                rowColumnError.setErrMemo(memo);
	                logger.error("[明细行字段数量]"+memo+"["+TDataDictConst.FIlE_ROW_COLUMN_COUNT_ER+"]");
	                continue;
				}
				if(indexSet.contains(payForTxnInfDetailRowBean.getDetailSeriaNo())){ // 序号重复
					String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ITERANT);
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_ROWS_ITERANT);
					rowColumnError.setErrMemo(memo);
					logger.debug("[明细行序号重复]"+memo+"["+TDataDictConst.FILE_CONTENT_ROWS_ITERANT+"]");
					rowColumnErrors.add(rowColumnError);
				}
				indexSet.add(payForTxnInfDetailRowBean.getDetailSeriaNo());
			}
			//字段校验
			FeildsCherker.verifyFeilds(payForTxnInfDetailRowBean, txnDataType, rowColumnErrors,txnInfBean.getTxnInfType(),payForBusiBean.getMchntCd());
			if (rowColumnErrors.size() > 0) {
	            FileRowError fileRowError = this.getFileRowError();
	            fileRowError.setRowNo(rowNo);
	            fileRowError.setRow(payForTxnInfDetailRowBean.getLineStr());
	            fileRowError.setColumnErrors(rowColumnErrors);
	            continue;
	        }
			payForBusiDetailRowBean.setDetailSeriaNo(payForTxnInfDetailRowBean.getDetailSeriaNo());
			payForBusiDetailRowBean.setBankCd(payForTxnInfDetailRowBean.getBankCd());
			payForBusiDetailRowBean.setBankAccount(payForTxnInfDetailRowBean.getBankAccount());
			payForBusiDetailRowBean.setAccountName(payForTxnInfDetailRowBean.getAccountName());
			long amt=0;
			try {
				amt=Long.parseLong(FuMerUtil.formatYuanToFen(payForTxnInfDetailRowBean.getDetailAmt()));
			} catch (Exception e) {
				logger.error("Exception:",e);
			}
			payForBusiDetailRowBean.setDetailAmt(amt);
			payForBusiDetailRowBean.setEnpSeriaNo(payForTxnInfDetailRowBean.getEnpSeriaNo());
			payForBusiDetailRowBean.setMemo(payForTxnInfDetailRowBean.getMemo());
			payForBusiDetailRowBean.setMobile(payForTxnInfDetailRowBean.getMobile());
			payForBusiDetailRowBean.setCityCode(payForTxnInfDetailRowBean.getCity());
			payForBusiDetailRowBean.setBranchBank(payForTxnInfDetailRowBean.getBankNo());
			payForBusiDetailRowBean.setActualRowNum(rowNo);
			payForBusiDetailRowBean.setMerdt(payForTxnInfDetailRowBean.getMerdt());
			payForBusiDetailRowBean.setOrderno(payForTxnInfDetailRowBean.getOrderno());
			payForBusiDetailRowBean.setFlag(payForTxnInfDetailRowBean.isFlag());
			payForBusiDetailRowBeanList.add(payForBusiDetailRowBean);
		}
		return payForBusiDetailRowBeanList.size();
	}

	@Override
	protected void fileSumValidate() {
		SumChecker.beanSumValidate(txnInfBean, payForBusiBean, getFileSumError());
	}

	@Override
	protected void operationValidate() {
		PayForBusiBean payForBusiBeanReturn = BusiChecker.payForOperationValidate(payForBusiBean);
	}
}
