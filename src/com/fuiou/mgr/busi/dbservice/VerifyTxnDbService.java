package com.fuiou.mgr.busi.dbservice;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.service.TStlDtStService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.business.VerifyBusiBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfSumBean;

public class VerifyTxnDbService extends TxnDbService {
	private static Logger logger = LoggerFactory.getLogger(VerifyTxnDbService.class);
	private TDataDictService tDataDictService = new TDataDictService();
	@Override
	public void setTfileInf() {
		VerifyTxnInfBean verifyTxnInfBean = (VerifyTxnInfBean)txnInfBean;
		VerifyTxnInfSumBean verifyTxnInfSumBean = verifyTxnInfBean.getVerifyTxnInfSumBean();
		int idxFileFixes = verifyTxnInfBean.getFileName().indexOf('.');
        String fileName = verifyTxnInfBean.getFileName().substring(0, idxFileFixes);
        String fileNameSuffix = verifyTxnInfBean.getFileName().substring(idxFileFixes);
		tFileInf.setFILE_MCHNT_CD(verifyTxnInfSumBean.getMchntCd());
        tFileInf.setFILE_BUSI_TP(verifyTxnInfSumBean.getBusiCd());
        tFileInf.setFILE_DT(verifyTxnInfSumBean.getTxnDate());
        tFileInf.setFILE_SEQ(verifyTxnInfSumBean.getSeriaNo());
        int row = 0;
        try {
			row = Integer.parseInt(verifyTxnInfSumBean.getSumDetails());
		} catch (Exception e) {
			logger.error("Exception:",e);
		}
        tFileInf.setFILE_ROWS(row);
        tFileInf.setSRC_MODULE_CD(TDataDictConst.SRC_MODULE_CD_FTP);
        tFileInf.setFILE_NM(fileName);
        tFileInf.setFILE_NM_SFFX(fileNameSuffix);
        String savePath = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.VALIDATION_FILE_PATH);
        savePath += verifyTxnInfSumBean.getMchntCd() + "/";
        tFileInf.setFILE_PATH(savePath);
        tFileInf.setFILE_SIZE(Integer.parseInt(String.valueOf(verifyTxnInfBean.getFile().length())));
        tFileInf.setFILE_RIGHT_ROWS(row);
        tFileInf.setROW_CRT_TS(currDbTime);
        tFileInf.setREC_UPD_TS(currDbTime);
        tFileInf.setFILE_RSP_ST("0");
        if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)){
        	tFileInf.setFILE_ST(TDataDictConst.FILE_ST_REVIEW);
        }else{
        	tFileInf.setFILE_ST(TDataDictConst.FILE_ST_MCHNT_INIT);
        	tFileInf.setOPR_USR_ID(verifyTxnInfBean.getOprUsrId());
        }

	}

	@Override
	public void setTApsTxnLog() {
		VerifyBusiBean verifyBusiBean = (VerifyBusiBean)busiBean;
		List<VerifyBusiDetailRowBean> list = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
		
		VerifyTxnInfBean verifyTxnInfBean = (VerifyTxnInfBean)txnInfBean;
		VerifyTxnInfSumBean verifyTxnInfSumBean = verifyTxnInfBean.getVerifyTxnInfSumBean();
		String fileName = "";
//		String fileNameSuffix = "";
		if("FILE".equals(txnDataType)){
			int idxFileFixes = verifyTxnInfBean.getFileName().indexOf('.');
			fileName = verifyTxnInfBean.getFileName().substring(0, idxFileFixes);
//			fileNameSuffix = verifyTxnInfBean.getFileName().substring(idxFileFixes);
		}
		
		if(list == null||list.size()==0){
			return;
		}
		String kbpsSrcSettleDt = new TStlDtStService().getCurSettleDt();
		TInsMchntInf tInsInf = tInsMchntInfService.selectTInsInfByMchntCd(verifyTxnInfSumBean.getMchntCd());
		List<String> seqList = new TSeqService().getKbpsTraceNo12s(currDbTime, list.size());
		int seqNo = -1;
		for(VerifyBusiDetailRowBean detailBean:list){
			seqNo ++;
			boolean isRultErrorVerify = false;
			TApsTxnLog apsTxnLog = new TApsTxnLog();
			String lineStr = detailBean.getDetailSeriaNo()+"|"+
							 detailBean.getBankCd()+"|"+
							 detailBean.getBankAccount()+"|"+
							 detailBean.getAccountName()+"|"+
							 detailBean.getCertificateTp()+"|"+
							 detailBean.getCertificateNo();
			apsTxnLog.setKBPS_SRC_SETTLE_DT(kbpsSrcSettleDt);
			apsTxnLog.setSEC_CTRL_INF(busiBean.getOprUsrId());//增加了操作员字段
            apsTxnLog.setSRC_MCHNT_CD(verifyTxnInfSumBean.getMchntCd());
            apsTxnLog.setSRC_MODULE_CD(srcModuleCd);
            apsTxnLog.setSUB_TXN_SEQ(TDataDictConst.SUB_TXN_SEQ);
            apsTxnLog.setBUSI_CD(TDataDictConst.BUSI_CD_VERIFY);
            apsTxnLog.setSRC_ORDER_NO(fileName);
            apsTxnLog.setACQ_INS_CD(tInsInf.getINS_CD());
            apsTxnLog.setSRC_MCHNT_TXN_DT(FuMerUtil.date2String(currDbTime, "yyyyMMdd"));
            apsTxnLog.setSRC_MCHNT_TXN_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
            apsTxnLog.setLOC_DT(FuMerUtil.date2String(currDbTime, "MMdd"));
            apsTxnLog.setLOC_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
            apsTxnLog.setSRC_INS_CD(tInsInf.getINS_CD());
            //String mac = new TStlDtStService().getCurLogNo();
            apsTxnLog.setMAC("");
            // 走BPS还是文件方式，默认为1-BPS；0-文件
            String sjsVerifyType = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, "sjsVerifyType");
            if(detailBean.getOperationBean().gettBusiRut() != null){
            	if("1".equals(sjsVerifyType)){
            		apsTxnLog.setRCV_INS_CD(TDataDictConst.INS_CD_BPS);
            	}else{
            		apsTxnLog.setRCV_INS_CD(detailBean.getOperationBean().gettBusiRut().getCHNL_INS_CD());
            	}
                apsTxnLog.setDEST_INS_CD(detailBean.getOperationBean().gettBusiRut().getCHNL_INS_CD());
                apsTxnLog.setMSG_RSN_CD(detailBean.getOperationBean().gettBusiRut().getBUSI_RUT_ID());
            }else{
            	isRultErrorVerify = true;
            }
//          apsTxnLog.setRCV_INS_CD(TDataDictConst.INS_CD_SZ_STL_CNTR);
//          apsTxnLog.setDEST_INS_CD(TDataDictConst.INS_CD_SZ_STL_CNTR);
            if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
            	apsTxnLog.setORIG_SRC_ORDER_NO(detailBean.getOrderno());// 请求流水号
            	apsTxnLog.setORIG_ORDER_DT(detailBean.getMerdt());		// 请求日期
            }
            apsTxnLog.setKBPS_SETTLE_IND("1");
            apsTxnLog.setTO_TS(currDbTime);
            apsTxnLog.setTXN_RCV_TS(currDbTime);
            apsTxnLog.setTXN_FIN_TS(currDbTime);
            apsTxnLog.setSRC_SETTLE_DT(kbpsSrcSettleDt);
            apsTxnLog.setDEST_SETTLE_DT(kbpsSrcSettleDt);
            apsTxnLog.setKBPS_DEST_SETTLE_DT(kbpsSrcSettleDt);
            apsTxnLog.setCAPITAL_DIR("D");
            apsTxnLog.setSRC_MCHNT_TP(tInsInf.getMCHNT_TP());
            apsTxnLog.setTXN_CD("S00");
            apsTxnLog.setSRC_CHNL("07");
            apsTxnLog.setSRC_SSN(detailBean.getDetailSeriaNo());
            apsTxnLog.setID_NO(detailBean.getAccountName());
            apsTxnLog.setCUSTMR_NO_TP(detailBean.getCertificateTp());
            apsTxnLog.setCUSTMR_NO(detailBean.getCertificateNo());
            apsTxnLog.setDETAIL_INQR_DATA(lineStr);
            apsTxnLog.setDEBIT_ACNT_NO(String.valueOf(detailBean.getBankAccount().length()) + detailBean.getBankAccount());
            apsTxnLog.setKBPS_TRACE_NO(seqList.get(seqNo));
            apsTxnLog.setDEST_SSN(seqList.get(seqNo).substring(6));
            OperationBean operationBean = detailBean.getOperationBean();
            TCardBin tCardBin = operationBean.gettCardBin();
            String IssCd = operationBean.getIssCd();
            apsTxnLog.setISS_INS_CD_D(IssCd);
            boolean hasError = operationBean.getHasError();
            if(tCardBin!=null){
                apsTxnLog.setCARD_ATTR_D(tCardBin.getCARD_ATTR());
            }
            if (IssCd != null) {
                apsTxnLog.setISS_INS_CD_D(IssCd);
            }
            
            apsTxnLog.setDEST_TXN_ST(TDataDictConst.DESTTXNST_INIT);
            if(hasError || isRultErrorVerify){
            	apsTxnLog.setPAY_MD(TDataDictConst.PAY_MD_NONEED); //不需要生成银行文件
                apsTxnLog.setSRC_TXN_ST(TDataDictConst.SRCTXNST_RES_FAIL);
                apsTxnLog.setMSG_TP(TDataDictConst.MSG_TP_ALLOW_RETURN);
                apsTxnLog.setEXPIRE_DT(TDataDictConst.RES_TP_ALLOW_RETURN);
                String acqRspCd = TDataDictConst.BUSI_VER_ER;
                String kbpsRspCd = new TDataDictService().getKbpsRspCdByAcqRspCd(acqRspCd);
                apsTxnLog.setACQ_RSP_CD(acqRspCd);
                apsTxnLog.setKBPS_RSP_CD(kbpsRspCd);
                apsTxnLog.setADDN_PRIV_DATA(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER));
                apsTxnLog.setTXN_FIN_TS(currDbTime);
                apsTxnLog.setBUSI_END_IND((short) 1);
                
                if(isRultErrorVerify){
                	// 记录错误信息
					FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
					rowColumnError.setErrMemo("没有匹配到路由");
					rowColumnErrorList.add(rowColumnError);
                }
            } else{
            	apsTxnLog.setSRC_TXN_ST(TDataDictConst.SRCTXNST_RES_SUCC);
                apsTxnLog.setPAY_MD(TDataDictConst.PAY_MD_NEED); //需要生成银行文件
                apsTxnLog.setMSG_TP(TDataDictConst.MSG_TP_NOTALLOW_RETURN);
                apsTxnLog.setEXPIRE_DT(TDataDictConst.RES_TP_NOTALLOW_RETURN);
                if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)||srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
                	apsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_SECOND_SURE);
                }
                apsTxnLog.setDEST_TERM_ID(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.MAP_BANK_CD_SZ_STL_CNTR, detailBean.getBankCd()));
                apsTxnLog.setGOODS_CD(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.MAP_ID_TP_SZ_STL_CNTR, detailBean.getCertificateTp()));
            }
            if(tCardBin!=null){
                apsTxnLog.setCARD_ATTR_D(tCardBin.getCARD_ATTR());
            }
            tApsTxnLogs.add(apsTxnLog);
            }
	}

	@Override
	public void setTFileErrorInf() {
		VerifyTxnInfBean verifyTxnInfBean = (VerifyTxnInfBean)txnInfBean;
		int idxFileFixes = verifyTxnInfBean.getFileName().indexOf('.');
        String fileName = verifyTxnInfBean.getFileName().substring(0, idxFileFixes);
		List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
		if(fileRowErrorList == null || fileRowErrorList.size() == 0){
			return;
		}
		for(int i = 0;i<fileRowErrorList.size();i++){
			TFileErrInf tFileErrorInf = new TFileErrInf();
			FileRowError fileRowError = fileRowErrorList.get(i);
			tFileErrorInf.setFILE_NM(fileName);
			tFileErrorInf.setFILE_MCHNT_CD(verifyTxnInfBean.getMchntCd());
			tFileErrorInf.setERR_ROW_SEQ(Integer.toString(fileRowError.getRowNo()));
			tFileErrorInf.setERR_ROW_CONTENT(fileRowError.getRow());
			List<RowColumnError> rowColumnErrorList =  fileRowError.getColumnErrors();
			String rowColumnErrors = "";
			for(RowColumnError rowColumnError:rowColumnErrorList){
				rowColumnErrors += rowColumnError.getErrMemo();
			}
			tFileErrorInf.setERR_ROW_DESC(rowColumnErrors);
			tFileErrorInf.setROW_CRT_TS(new Date());
			tFileErrInfs.add(tFileErrorInf);
		}
	}


	@Override
	public void copyToWeb() {
		if (txnInfBean.getFile() == null || !txnInfBean.getFile().exists()) {
            logger.error("src file null");
            return ;
        }
        if(null == txnInfBean.getMchntCd()){
            logger.error("src mchntCd null");
            return;
        }
        String savePath = new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.VALIDATION_FILE_PATH);
        if(null == savePath){
            logger.error("verify savepath null");
            return;
        }
        File destDir = new File(new File(savePath,txnInfBean.getMchntCd()), txnInfBean.getFileName());
        // 上传文件夹不存在就创建
        if (!destDir.getParentFile().exists()) {
        	destDir.getParentFile().mkdirs();
		}
        try {
        	FileUtils.copyFile(txnInfBean.getFile(), destDir);
        } catch (IOException e) {
            logger.error("move file failed", e);
        }

	}

	@Override
	public void sureOperation() {
		int errorCount = 0;
		VerifyBusiBean verifyBusiBean = (VerifyBusiBean)busiBean;
		List<VerifyBusiDetailRowBean> list = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
		// 判断，如果业务校验都失败则不入库，提示错误。
		for(TApsTxnLog tapsTxnLog:tApsTxnLogs){
			String SRC_TXN_ST = tapsTxnLog.getSRC_TXN_ST();
			if(TDataDictConst.TXN_SRC_SEND_FAIL.equals(SRC_TXN_ST)){
				errorCount ++;
			}
		}
		if(tApsTxnLogs.size() == errorCount){
			FileSumError fileSumError = getFileSumError();
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER);
            fileSumError.setErrCode(TDataDictConst.BUSI_VER_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow("");
			for(VerifyBusiDetailRowBean verifyBusiDetailRowBean : list){
				FileRowError fileRowError = getFileRowError();
				FileRowError fileRowErrorPayFor = verifyBusiDetailRowBean.getOperationBean().getFileRowErrro();
				fileRowError.setRowNo(fileRowErrorPayFor.getRowNo());
				fileRowError.setRow(fileRowErrorPayFor.getRow());
				fileRowError.setColumnErrors(fileRowErrorPayFor.getColumnErrors());
			}
		}
	}

	@Override
	public void setOperationErrorInf() {
		// 1、获取业务对象
		VerifyBusiBean verifyBusiBean = (VerifyBusiBean)busiBean;
		// 2、获取明细信息，明细中每条交易记录了业务校验时的错误信息
		List<VerifyBusiDetailRowBean> list = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
		// 3、循环交易明细
		for(VerifyBusiDetailRowBean verifyBusiDetailRowBean : list){
			// 4、获取每条交易的业务信息对象
			OperationBean operationBean = verifyBusiDetailRowBean.getOperationBean();
			// 5、获取业务对象中的业务校验错误的信息
			FileRowError fileRowError = operationBean.getFileRowErrro();
			// 6、判断该笔交易是否有业务校验失败的信息
			List<RowColumnError> columnErrorList = fileRowError.getColumnErrors();
			if(columnErrorList.size() == 0){
				continue;
			}
			// 7、封装至业务校验失败的List
			fileRowOperateErrors.add(fileRowError);
		}
	}

}
