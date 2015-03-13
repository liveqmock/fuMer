package com.fuiou.mgr.checker;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfSumBean;

public class VerifyCherker {
	private static final Logger logger = LoggerFactory.getLogger(VerifyCherker.class);
	protected static TDataDictService dataDictService = new TDataDictService();
	private static TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();

	public static void sumRowValidate(VerifyTxnInfBean verifyTxnInfBean, FileSumError fileSumError) {
		Date currDbTime = tApsTxnLogService.getCurrDbTime();
		VerifyTxnInfSumBean verifyTxnInfSumBean = verifyTxnInfBean.getVerifyTxnInfSumBean();
		String mchntCd = verifyTxnInfBean.getMchntCd();
		String fileFullName = verifyTxnInfBean.getFileName();
		int idxFileFixes = fileFullName.indexOf('.');
		String fileName = fileFullName.substring(0, idxFileFixes);
		String[] fileNames = fileName.split(TDataDictConst.FILE_TITLE_APART, 3);
		String sumRow = verifyTxnInfSumBean.getSumRow();
		String[] strs = sumRow.split(TDataDictConst.FILE_CONTENT_APART, 6);
		if (strs.length != 5) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_READ_EP);
			fileSumError.setErrCode(TDataDictConst.FILE_READ_EP);
			fileSumError.setErrMemo(memo);
			fileSumError.setErrMemo(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_READ_EP + "]" + sumRow);
			return;
		}
		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].trim();
		}
		// 比较文件中商户号与登录用户的商户号是否一致
		if (!mchntCd.equals(strs[0])) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_MCHNT_CD_ER);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_MCHNT_CD_ER);
			fileSumError.setErrCode(TDataDictConst.FILE_MCHNT_CD_ER);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_MCHNT_CD_ER + "]" + sumRow);
			return;
		}
		// 验证是否为付款业务代码
		if (!TDataDictConst.BUSI_CD_VERIFY.equals(strs[1])) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_BUSI_TP_ER);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_BUSI_TP_ER);
			fileSumError.setErrCode(TDataDictConst.FILE_BUSI_TP_ER);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_MCHNT_CD_ER + "]" + sumRow);
			return;
		}
		// 验证日期
		if (!FuMerUtil.date2String(currDbTime, "yyyyMMdd").equals(strs[2])) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
			fileSumError.setErrCode(TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_DATE_FORMAT_ER + "]" + sumRow);
			return;
		}
		// 验证文件名序号与汇总里边的序号是否一致
		if (!fileNames[2].equals(strs[3])) {
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
		try {
			rows = Integer.parseInt(strs[4]);
		} catch (NumberFormatException e) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_FORMAT_EP);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_FORMAT_EP);
			fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_FORMAT_EP);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_FORMAT_EP + "]" + sumRow);
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
		} else if (rows > TDataDictConst.FILE_CONTENT_MAX_ROWS
				|| verifyTxnInfBean.getVerifyTxnInfDetailRowBeanList().size() > TDataDictConst.FILE_CONTENT_MAX_ROWS) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_MAXROWS_EP);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_CONTENT_MAXROWS_EP);
			fileSumError.setErrCode(TDataDictConst.FILE_CONTENT_MAXROWS_EP);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_CONTENT_MAXROWS_EP + "]" + sumRow);
			return;
		} else if (rows != verifyTxnInfBean.getVerifyTxnInfDetailRowBeanList().size()) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
			fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_TOTAL_EP);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_TOTAL_EP + "]明细:"
					+ verifyTxnInfBean.getVerifyTxnInfDetailRowBeanList().size() + " 汇总:" + verifyTxnInfSumBean.getSumRow());
			return;
		}
	}

	public static void fileSumValidate(VerifyTxnInfBean verifyTxnInfBean, List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanList, FileSumError fileSumError,
			String txnInfSource) {
		VerifyTxnInfSumBean verifyTxnInfSumBean = verifyTxnInfBean.getVerifyTxnInfSumBean();
		String sumRow = verifyTxnInfSumBean.getSumRow();
		String fileFullName = verifyTxnInfBean.getFileName();
		int idxFileFixes = fileFullName.indexOf('.');
		String fileName = fileFullName.substring(0, idxFileFixes);
		// 无交易入库
		if (verifyBusiDetailRowBeanList.size() == 0) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_NO_RECORD);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FIlE_QUERY_NO_RECORD);
			fileSumError.setErrCode(TDataDictConst.FIlE_QUERY_NO_RECORD);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FIlE_QUERY_NO_RECORD + "]");
			return;
		}
		// 待入库交易笔数和文件行数不一致
		if (verifyBusiDetailRowBeanList.size() != Integer.parseInt(verifyTxnInfSumBean.getSumDetails())
				&& txnInfSource.equals(TDataDictConst.SRC_MODULE_CD_FTP)) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_TOTAL_EP);
			fileSumError.setErrCode(TDataDictConst.FILE_COLLECT_TOTAL_EP);
			fileSumError.setErrMemo(memo);
			fileSumError.setRow(sumRow);
			logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_COLLECT_TOTAL_EP + "]交易:" + verifyBusiDetailRowBeanList.size() + " 汇总:"
					+ verifyTxnInfSumBean.getSumDetails());
			return;
		}
	}

}
