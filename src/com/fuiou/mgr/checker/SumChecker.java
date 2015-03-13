package com.fuiou.mgr.checker;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.convert.InComeForTxnInfBean;
import com.fuiou.mgr.bean.convert.InComeForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.checkout.CheckOutBase;

/**
 * 总行校验器
 * yangliehui
 *
 */
public class SumChecker {
	protected static TDataDictService dataDictService = new TDataDictService();
	private static final Logger logger = LoggerFactory.getLogger(SumChecker.class);
	private static TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	/**
	 * 解析时校验汇总行
	 */
	public static void sumRowValidate(TxnInfBean txnInfBean,FileSumError fileSumError){
		Date currDbTime = tApsTxnLogService.getCurrDbTime();
		String mchntCd = txnInfBean.getMchntCd();
		String fileFullName = txnInfBean.getFileName();
		// 需要校验的参数
		String sumRow = "";
		String mchntCdVerify = "";
		String busiCdVerify = "";
		String txnDataVerify = "";
		String seriaNoVerify = "";
		String sumDetailsVerify = "";
		int detailRowSizeVerify = 0;
		String sumAmtVerify = "";
		if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(txnInfBean.getTxnInfType())){
			InComeForTxnInfBean inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
			InComeForTxnInfSumBean inComeForTxnInfSumBean = inComeForTxnInfBean.getInComeForTxnInfSumBean();
			sumRow = inComeForTxnInfSumBean.getSumRow();
			mchntCdVerify = inComeForTxnInfSumBean.getMchntCd();
			busiCdVerify = inComeForTxnInfSumBean.getBusiCd();
			txnDataVerify = inComeForTxnInfSumBean.getTxnDate();
			seriaNoVerify = inComeForTxnInfSumBean.getSeriaNo();
			sumDetailsVerify = inComeForTxnInfSumBean.getSumDetails();
			detailRowSizeVerify = inComeForTxnInfBean.getInComeForTxnInfDetailRowBeanList().size();
			sumAmtVerify = inComeForTxnInfSumBean.getSumAmt();
		}else if(TDataDictConst.BUSI_CD_PAYFOR.equals(txnInfBean.getTxnInfType())){
			PayForTxnInfBean payForTxnInfBean = (PayForTxnInfBean)txnInfBean;
			PayForTxnInfSumBean payForTxnInfSumBean = payForTxnInfBean.getPayForTxnInfSumBean();
			sumRow = payForTxnInfSumBean.getSumRow();
			mchntCdVerify = payForTxnInfSumBean.getMchntCd();
			busiCdVerify = payForTxnInfSumBean.getBusiCd();
			txnDataVerify = payForTxnInfSumBean.getTxnDate();
			seriaNoVerify = payForTxnInfSumBean.getDayNo();
			sumDetailsVerify = payForTxnInfSumBean.getSumDetails();
			detailRowSizeVerify = payForTxnInfBean.getPayForTxnInfDetailRowBeanList().size();
			sumAmtVerify = payForTxnInfSumBean.getSumAmt();
		}
		int idxFileFixes = fileFullName.indexOf('.');
        String fileName = fileFullName.substring(0, idxFileFixes);
        String[] fileNames = fileName.split(TDataDictConst.FILE_TITLE_APART, 3);
		String[] strs = sumRow.split(TDataDictConst.FILE_CONTENT_APART, 6);
		if (strs.length != 6) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_READ_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_READ_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_READ_EP + "]" + sumRow);
            return;
        }
		
        // 比较文件中商户号与登录用户的商户号是否一致
        if (!mchntCd.equals(mchntCdVerify)) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_MCHNT_CD_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_MCHNT_CD_ER);
            fileSumError.setErrCode(TDataDictConst.FILE_MCHNT_CD_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_MCHNT_CD_ER + "]" + sumRow);
            return;
        }
        // 验证是否为代扣业务代码
        if (!txnInfBean.getTxnInfType().equals(busiCdVerify)) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_BUSI_TP_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_BUSI_TP_ER);
            fileSumError.setErrCode(TDataDictConst.FILE_BUSI_TP_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_MCHNT_CD_ER + "]" + sumRow);
            return;
        }
        // 验证日期
        if (!FuMerUtil.date2String(currDbTime, "yyyyMMdd").equals(txnDataVerify)) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
            fileSumError.setErrCode(TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_DATE_FORMAT_ER + "]" + sumRow);
            return;
        }
        // 验证文件名序号与汇总里边的序号是否一致
        if (!fileNames[2].equals(seriaNoVerify)) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_SEQ_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_NAME_SEQ_ER);
            fileSumError.setErrCode(TDataDictConst.FILE_NAME_SEQ_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_SEQ_ER + "]" + sumRow);
            return;
        }
        // 行数
        int rows = 0;
        try{
            rows = Integer.parseInt(sumDetailsVerify);
        }catch(NumberFormatException e){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_FORMAT_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_FORMAT_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_FORMAT_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys +"[" + TDataDictConst.FILE_COLLECT_FORMAT_EP + "]" + sumRow);
            return;
        }
        if (rows <= 0) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_TOTAL_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_TOTAL_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_TOTAL_EP + "]" + sumRow);
            return;
        } else if (rows > TDataDictConst.FILE_CONTENT_MAX_ROWS || detailRowSizeVerify > TDataDictConst.FILE_CONTENT_MAX_ROWS) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_MAXROWS_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_CONTENT_MAXROWS_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_CONTENT_MAXROWS_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_CONTENT_MAXROWS_EP + "]" + sumRow);
            return;
        } else if (rows != detailRowSizeVerify){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_TOTAL_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_TOTAL_EP + "]明细:" + detailRowSizeVerify + " 汇总:" + sumDetailsVerify);
            return;
        }
        // 金额
        if (!CheckOutBase.validMoney(sumAmtVerify)) {
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_AMT_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_AMT_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_AMT_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_AMT_EP + "]" + sumRow);
            return;
        }
	}
	
	/**
	 * 解析后验证汇总行
	 */
	public static void beanSumValidate(TxnInfBean txnInfBean,BusiBean busiBean,FileSumError fileSumError){
		String fileFullName = txnInfBean.getFileName();
		int idxFileFixes = fileFullName.indexOf('.');
		String fileName = fileFullName.substring(0, idxFileFixes);
		String sumRow = "";
        int detailRowSizeVerify = 0;
        String sumDetailsVerify = "";
        double sumAmt = 0;
        String sumAmtVerify = "";
        int busiDetailSize = 0;
        if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(txnInfBean.getTxnInfType())){
        	InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
        	List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
        	InComeForTxnInfBean inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
			InComeForTxnInfSumBean inComeForTxnInfSumBean = inComeForTxnInfBean.getInComeForTxnInfSumBean();
			
			sumRow = inComeForTxnInfSumBean.getSumRow();
			detailRowSizeVerify = inComeForTxnInfBean.getInComeForTxnInfDetailRowBeanList().size();
			sumDetailsVerify = inComeForTxnInfSumBean.getSumDetails();
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanList){
	        	sumAmt += inComeForBusiDetailRowBean.getDetailAmt();
	        }
			sumAmtVerify = inComeForTxnInfSumBean.getSumAmt();
			busiDetailSize = inComeForBusiDetailRowBeanList.size();
        }else if(TDataDictConst.BUSI_CD_PAYFOR.equals(txnInfBean.getTxnInfType())){
        	PayForBusiBean payForBusiBean = (PayForBusiBean)busiBean;
        	List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
        	PayForTxnInfBean payForTxnInfBean = (PayForTxnInfBean)txnInfBean;
			PayForTxnInfSumBean payForTxnInfSumBean = payForTxnInfBean.getPayForTxnInfSumBean();
			
			sumRow = payForTxnInfSumBean.getSumRow();
			detailRowSizeVerify = payForTxnInfBean.getPayForTxnInfDetailRowBeanList().size();
			sumDetailsVerify = payForTxnInfSumBean.getSumDetails();
			for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanList){
				sumAmt += payForBusiDetailRowBean.getDetailAmt();
			}
			sumAmtVerify = payForTxnInfSumBean.getSumAmt();
			busiDetailSize = payForBusiDetailRowBeanList.size();
        }
		// 无交易入库
        if(busiDetailSize==0){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_NO_RECORD);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FIlE_QUERY_NO_RECORD);
            fileSumError.setErrCode(TDataDictConst.FIlE_QUERY_NO_RECORD);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FIlE_QUERY_NO_RECORD + "]");
            return;
        }
        // 待入库交易笔数和文件行数不一致
        if(detailRowSizeVerify != Integer.parseInt(sumDetailsVerify)){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_TOTAL_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_TOTAL_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_TOTAL_EP + "]交易:" + detailRowSizeVerify + " 汇总:" + sumDetailsVerify);
            return;
        }
        // 读取的总行数 == 验证总行数,检验汇总行的金额是否一致
        if(sumAmt != Double.parseDouble(FuMerUtil.formatYuanToFen(sumAmtVerify))){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_AMT_EP);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_AMT_EP);
            fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_AMT_EP);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow(sumRow);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_AMT_EP + "]明细金额:" + sumAmt + " 汇总金额:" + sumAmtVerify);
            return;
        }
	}
}
