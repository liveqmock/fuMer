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
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.InComeForTxnInfBean;
import com.fuiou.mgr.bean.convert.InComeForTxnInfSumBean;

/**
 * 代收交易入库
 * yangliehui
 *
 */
public class InComeForTxnDbService extends TxnDbService{
	private static Logger logger = LoggerFactory.getLogger(InComeForTxnDbService.class);
	
	@Override
	public void copyToWeb(){
		if (txnInfBean.getFile() == null || !txnInfBean.getFile().exists()) {
            logger.error("src file null");
            return ;
        }
        if(null == txnInfBean.getMchntCd()){
            logger.error("src mchntCd null");
            return;
        }
        String savePath = new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.INCOMEFOR_FILE_PATH);
        if(null == savePath){
            logger.error("ftpIncomeFor savepath null");
            return;
        }
//        File destDir = new File(savePath, txnInfBean.getMchntCd());
        File destDir = new File(new File(savePath,txnInfBean.getMchntCd()), txnInfBean.getFileName());
        // 上传文件不存在就创建
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
	public void setTfileInf() {
		InComeForTxnInfBean inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
		InComeForTxnInfSumBean inComeForTxnInfSumBean = inComeForTxnInfBean.getInComeForTxnInfSumBean();
		
		InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
		List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
		long rightSumAmt = 0;
		for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanList){
			rightSumAmt += inComeForBusiDetailRowBean.getDetailAmt();
		}
		int idxFileFixes = inComeForTxnInfBean.getFileName().indexOf('.');
        String fileName = inComeForTxnInfBean.getFileName().substring(0, idxFileFixes);
        String fileNameSuffix = inComeForTxnInfBean.getFileName().substring(idxFileFixes);
		tFileInf.setFILE_MCHNT_CD(inComeForTxnInfSumBean.getMchntCd());
        tFileInf.setFILE_BUSI_TP(inComeForTxnInfSumBean.getBusiCd());
        tFileInf.setFILE_DT(inComeForTxnInfSumBean.getTxnDate());
        tFileInf.setFILE_SEQ(inComeForTxnInfSumBean.getSeriaNo());
        long amt = 0;
        int row = 0;
        try {
        	amt = Long.valueOf(FuMerUtil.formatYuanToFen(Double.parseDouble(inComeForTxnInfSumBean.getSumAmt())));
        	row = Integer.parseInt(inComeForTxnInfSumBean.getSumDetails());
        } catch (Exception e) {
        	logger.error("Exception:",e);
        }
        tFileInf.setFILE_ROWS(row);
        tFileInf.setFILE_AMT(amt);
        tFileInf.setSRC_MODULE_CD(srcModuleCd);
        tFileInf.setFILE_NM(fileName);
        tFileInf.setFILE_NM_SFFX(fileNameSuffix);
        String savePath = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.INCOMEFOR_FILE_PATH);
        savePath += inComeForTxnInfSumBean.getMchntCd() + "/";
        tFileInf.setFILE_PATH(savePath);
        tFileInf.setFILE_SIZE(Integer.parseInt(String.valueOf(inComeForTxnInfBean.getFile().length())));
        tFileInf.setFILE_RIGHT_AMT(rightSumAmt);
        tFileInf.setFILE_RIGHT_ROWS(inComeForBusiDetailRowBeanList.size());
        tFileInf.setROW_CRT_TS(currDbTime);
        tFileInf.setREC_UPD_TS(currDbTime);
        tFileInf.setFILE_RSP_ST("0");
        if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)){
        	tFileInf.setFILE_ST(TDataDictConst.FILE_ST_REVIEW);
        }else{
        	tFileInf.setFILE_ST(TDataDictConst.FILE_ST_MCHNT_INIT);
        	tFileInf.setOPR_USR_ID(inComeForTxnInfBean.getOprUsrId());
        }
	}

	@Override
	public void setTApsTxnLog() {
		InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
		List<InComeForBusiDetailRowBean> list = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
//		PayForBusiDetailRowBean payForBusiDetailRowBean = inComeForBusiBean.getPayForBusiDetailRowBean();
		InComeForTxnInfBean inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
		InComeForTxnInfSumBean inComeForTxnInfSumBean = inComeForTxnInfBean.getInComeForTxnInfSumBean();
		String fileName = "";
//		String fileNameSuffix = "";
		String abchinaMchntTp = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, "abchinaMchntTp");
		if("FILE".equals(txnDataType)){
			int idxFileFixes = inComeForTxnInfBean.getFileName().indexOf('.');
			fileName = inComeForTxnInfBean.getFileName().substring(0, idxFileFixes);
//			fileNameSuffix = inComeForTxnInfBean.getFileName().substring(idxFileFixes);
		}
		
		if(list == null||list.size()==0){
			return;
		}
		
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(inComeForTxnInfSumBean.getMchntCd(), "1");
		String mchntTp = tInsMchntInf.getMCHNT_TP();
		
		String kbpsSrcSettleDt = new TStlDtStService().getCurSettleDt();
		TInsMchntInf tInsInf = tInsMchntInfService.selectTInsInfByMchntCd(inComeForTxnInfSumBean.getMchntCd());
	
		List<String> seqList = new TSeqService().getKbpsTraceNo12s(currDbTime, list.size());
		int seqNo = -1;
		// N笔收款
		for(InComeForBusiDetailRowBean detailBean:list){
			OperationBean operationBean = detailBean.getOperationBean();
            TCardBin tCardBin = operationBean.gettCardBin();
            String IssCd = operationBean.getIssCd();
            boolean hasError = operationBean.getHasError();
            boolean feeHasError = operationBean.isFeeHasError();
            boolean isRultErrorInCome = false;
            boolean isCaFeeErrIncome = false;
			boolean feeSetHasError = operationBean.isFeeSetHasError();
			boolean feeSetD4HasError = operationBean.isFeeSetD4HasError();
			feeSetHasErrors = feeSetHasError;
			feeSetD4HasErrors = feeSetD4HasError;
			seqNo ++;
			TApsTxnLog tApsTxnLog = new TApsTxnLog();
			// 明细序列|扣款人开户行代码(总行代码)|扣款人银行帐号|户名|金额|企业流水号|备注|手机号
			StringBuilder lineStr = new StringBuilder(detailBean.getDetailSeriaNo()+"|"+ detailBean.getBankCd()+"|"+ detailBean.getBankAccount()+"|"+ detailBean.getAccountName()+"|"+ FuMerUtil.formatFenToYuan(detailBean.getDetailAmt()) +"|"+ detailBean.getEnpSeriaNo()+"|"+ detailBean.getMemo()+"|"+ detailBean.getMobile());
			if(null != detailBean.getCertificateTp())
				lineStr.append("|"+detailBean.getCertificateTp());
			if(null != detailBean.getCertificateNo())
				lineStr.append("|"+detailBean.getCertificateNo());
			tApsTxnLog.setSEC_CTRL_INF(inComeForBusiBean.getOprUsrId());//增加了操作员字段
			tApsTxnLog.setKBPS_SRC_SETTLE_DT(kbpsSrcSettleDt);
            tApsTxnLog.setSRC_MCHNT_CD(inComeForTxnInfSumBean.getMchntCd());
            tApsTxnLog.setSRC_MODULE_CD(srcModuleCd);
            tApsTxnLog.setSUB_TXN_SEQ(TDataDictConst.SUB_TXN_SEQ);
            tApsTxnLog.setBUSI_CD(TDataDictConst.BUSI_CD_INCOMEFOR);
            if("FILE".equals(txnDataType)){
            	tApsTxnLog.setSRC_ORDER_NO(fileName);
            }
            tApsTxnLog.setACQ_INS_CD(tInsInf.getINS_CD());
            tApsTxnLog.setSRC_MCHNT_TXN_DT(FuMerUtil.date2String(currDbTime, "yyyyMMdd"));
            tApsTxnLog.setSRC_MCHNT_TXN_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
            tApsTxnLog.setLOC_DT(FuMerUtil.date2String(currDbTime, "MMdd"));
            tApsTxnLog.setLOC_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
            tApsTxnLog.setSRC_INS_CD(tInsInf.getINS_CD());
            //String mac = new TStlDtStService().getCurLogNo();
            tApsTxnLog.setMAC("");
            if(operationBean.gettBusiRut() != null){
            	// 广银联(KBSP)
            	if(operationBean.gettBusiRut().getCHNL_INS_CD().trim().equals(TDataDictConst.INS_CD_GZ_UNION_PAY)){
            		tApsTxnLog.setRCV_INS_CD(TDataDictConst.INS_CD_KBPS);
            	// 非广银联(BPS)
            	}else{
            		tApsTxnLog.setRCV_INS_CD(TDataDictConst.INS_CD_BPS);
            	}
        		tApsTxnLog.setDEST_INS_CD(operationBean.gettBusiRut().getCHNL_INS_CD());
        		if(TDataDictConst.INS_CD_ABCHINA.equals(operationBean.gettBusiRut().getCHNL_INS_CD())){
        			tApsTxnLog.setDEST_MCHNT_TP(abchinaMchntTp);
				}
                tApsTxnLog.setMSG_RSN_CD(operationBean.gettBusiRut().getBUSI_RUT_ID());
                tApsTxnLog.setTXN_MD(operationBean.gettBusiRut().getMCHNT_CAP_CFM());
    			tApsTxnLog.setPOS_ENTRY_MD_CD(operationBean.gettBusiRut().getCHNL_CAP_CFM());
            }else{
            	isRultErrorInCome = true;
            }
    		
            if(operationBean.getTxnFeeAmtId() == null && mchntTp.startsWith("D6")){
            	isCaFeeErrIncome = true;
            }else{
            	isCaFeeErrIncome = false;
            }
            
            tApsTxnLog.setTO_TXN_CD((operationBean.getTxnFeeAmtId() != null && operationBean.getTxnFeeAmtId() != -1)?operationBean.getTxnFeeAmtId().toString():"");
//          tApsTxnLog.setRCV_INS_CD(TDataDictConst.INS_CD_KBPS);
//    		tApsTxnLog.setDEST_INS_CD(TDataDictConst.INS_CD_KBPS);
//    		tApsTxnLog.setCARD_SEQ_ID("1");
//    		tApsTxnLog.setTXN_MD("1");
//    		tApsTxnLog.setPOS_ENTRY_MD_CD("1");
            
            tApsTxnLog.setKBPS_SETTLE_IND("1");
            tApsTxnLog.setTO_TS(currDbTime);
            tApsTxnLog.setTXN_RCV_TS(currDbTime);
            tApsTxnLog.setTXN_FIN_TS(currDbTime);
            tApsTxnLog.setSRC_SETTLE_DT(kbpsSrcSettleDt);
            tApsTxnLog.setDEST_SETTLE_DT(kbpsSrcSettleDt);
            tApsTxnLog.setKBPS_DEST_SETTLE_DT(kbpsSrcSettleDt);
            tApsTxnLog.setCAPITAL_DIR(TDataDictConst.CAPITAL_DIR_INCOMEFOR);
            tApsTxnLog.setSRC_MCHNT_TP(tInsInf.getMCHNT_TP());
            tApsTxnLog.setSRC_CHNL("07");
            tApsTxnLog.setTXN_CD("S22");
            tApsTxnLog.setSRC_SSN(detailBean.getDetailSeriaNo());
            tApsTxnLog.setID_NO(detailBean.getAccountName());
//            tApsTxnLog.setSRC_TXN_AMT(detailBean.getDetailAmt());
            // AC01 源和目标一样
            long srcTxnAmt = 0;
            String feeMd = "";
            if(operationBean.getFeeMd() != null && !"".equals(operationBean.getFeeMd())){
            	feeMd = operationBean.getFeeMd();
            }
            if("1".equals(feeMd)){
            	srcTxnAmt = detailBean.getDetailAmt();
            }else if("0".equals(feeMd)){
            	srcTxnAmt = detailBean.getDetailAmt() + operationBean.getTxnFeeAmt();
            }else{
            	srcTxnAmt = detailBean.getDetailAmt();
            }
            tApsTxnLog.setSRC_TXN_AMT(srcTxnAmt);
            tApsTxnLog.setDEST_TXN_AMT(operationBean.getDestTxnAmt());
            tApsTxnLog.setTXN_FEE_AMT((int)operationBean.getTxnFeeAmt());
            tApsTxnLog.setDETAIL_INQR_DATA(lineStr.toString());
            tApsTxnLog.setAUTH_RSP_CD(operationBean.getFeeMd());
            
            tApsTxnLog.setDEST_TXN_ST(TDataDictConst.TXN_DEST_RECORD_SUC);
            
            tApsTxnLog.setIC_FLDS(detailBean.getEnpSeriaNo() + "|" + detailBean.getMemo() + "|" + detailBean.getMobile());
            tApsTxnLog.setDEBIT_ACNT_NO(String.valueOf(detailBean.getBankAccount().length()) + detailBean.getBankAccount());
            
            if(tCardBin != null){
            	if(null == IssCd || IssCd.length() != 10){
                    hasError = true; //不一致
                }else{
                	tApsTxnLog.setISS_INS_CD_D(IssCd);// 付款ISS_INS_CD_C,代收ISS_INS_CD_D
                	tApsTxnLog.setCARD_BIN_D(tCardBin.getCARD_BIN());// 付款CARD_BIN_C,代收CARD_BIN_D
                	tApsTxnLog.setCARD_ATTR_D(tCardBin.getCARD_ATTR());
                }
            }else{
                if(hasError == false){
                	tApsTxnLog.setDEBIT_ACNT_NO(String.valueOf(detailBean.getBankAccount().length()) + detailBean.getBankAccount());
                	tApsTxnLog.setISS_INS_CD_D(IssCd);// 付款ISS_INS_CD_C,代收ISS_INS_CD_D
                }
            }
            if (hasError || isRultErrorInCome || isCaFeeErrIncome || feeHasError || feeSetHasError || feeSetD4HasError) {
                tApsTxnLog.setPAY_MD(TDataDictConst.PAY_MD_NONEED); // 不需要生成银行文件
                tApsTxnLog.setSRC_TXN_ST(TDataDictConst.TXN_SRC_SEND_FAIL);
                tApsTxnLog.setMSG_TP(TDataDictConst.MSG_TP_ALLOW_RETURN);
                tApsTxnLog.setEXPIRE_DT(TDataDictConst.RES_TP_ALLOW_RETURN);
                String acqRspCd = TDataDictConst.BUSI_VER_ER;
                String kbpsRspCd = new TDataDictService().getKbpsRspCdByAcqRspCd(acqRspCd);
                tApsTxnLog.setACQ_RSP_CD(acqRspCd);
                tApsTxnLog.setKBPS_RSP_CD(kbpsRspCd);
                tApsTxnLog.setADDN_PRIV_DATA(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER));
                tApsTxnLog.setBUSI_END_IND((short) 1);
                
                if(isRultErrorInCome){
                	// 记录错误信息
					FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
					rowColumnError.setErrMemo("没有匹配到路由");
					rowColumnErrorList.add(rowColumnError);
                }
                
                if(isCaFeeErrIncome){
                	// 记录错误信息
					FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.CALCULATE_FEE_ER);
					rowColumnError.setErrMemo("计算手续费失败");
					rowColumnErrorList.add(rowColumnError);
                }
                
                if(feeHasError){
					FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FILE_CONTENT_AMT_ER);
					rowColumnError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_AMT_ER)+",目标金额小于手续费"); 
					rowColumnErrorList.add(rowColumnError);
				}
                if(feeSetHasError){
					FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FEE_SET_ER);
					rowColumnError.setErrMemo("实时结算商户没有找到手续费方案");
					rowColumnErrorList.add(rowColumnError);
				}
                if(feeSetD4HasError){
                	FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.FEE_SET_ER);
					rowColumnError.setErrMemo("月结商户存在生效的手续费方案");
					rowColumnErrorList.add(rowColumnError);
                }
            } else {
                tApsTxnLog.setPAY_MD(TDataDictConst.PAY_MD_NEED);
                if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
                	tApsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_SECOND_SURE);
                }
                tApsTxnLog.setSRC_TXN_ST(TDataDictConst.TXN_SRC_SEND_SUCCESS);
                tApsTxnLog.setMSG_TP(TDataDictConst.MSG_TP_NOTALLOW_RETURN);
                tApsTxnLog.setEXPIRE_DT(TDataDictConst.RES_TP_NOTALLOW_RETURN);
            }
            if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
            	tApsTxnLog.setORIG_SRC_ORDER_NO(detailBean.getOrderno());// 请求流水号
            	tApsTxnLog.setORIG_ORDER_DT(detailBean.getMerdt());		// 请求日期
            }
            tApsTxnLog.setKBPS_TRACE_NO(seqList.get(seqNo));
			tApsTxnLog.setDEST_SSN(seqList.get(seqNo).substring(6));
			tApsTxnLog.setCUSTMR_NO_TP(detailBean.getCertificateTp());
			tApsTxnLog.setCUSTMR_NO(detailBean.getCertificateNo());
            tApsTxnLogs.add(tApsTxnLog);
		}
	}

	@Override
	public void setTFileErrorInf() {
		InComeForTxnInfBean inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
//		InComeForTxnInfSumBean inComeForTxnInfSumBean = inComeForTxnInfBean.getInComeForTxnInfSumBean();
		int idxFileFixes = inComeForTxnInfBean.getFileName().indexOf('.');
        String fileName = inComeForTxnInfBean.getFileName().substring(0, idxFileFixes);
//        String fileNameSuffix = inComeForTxnInfBean.getFileName().substring(idxFileFixes);
		List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
		if(fileRowErrorList == null || fileRowErrorList.size() == 0){
			return;
		}
		for(int i = 0;i<fileRowErrorList.size();i++){
			TFileErrInf tFileErrorInf = new TFileErrInf();
			FileRowError fileRowError = fileRowErrorList.get(i);
			tFileErrorInf.setFILE_NM(fileName);
			tFileErrorInf.setFILE_MCHNT_CD(inComeForTxnInfBean.getMchntCd());
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
	public void sureOperation() {
		int errorCount = 0;
		InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
		List<InComeForBusiDetailRowBean> list = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
		// 判断，如果业务校验都失败则不入库，提示错误。
		for(TApsTxnLog tapsTxnLog:tApsTxnLogs){
			String SRC_TXN_ST = tapsTxnLog.getSRC_TXN_ST();
			if(TDataDictConst.TXN_SRC_SEND_FAIL.equals(SRC_TXN_ST)){
				errorCount ++;
			}
		}
		if(tApsTxnLogs.size() == errorCount && feeSetHasErrors){
			FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FEE_SET_ER);
            fileSumError.setErrMemo("实时结算商户没有找到手续费方案");
            fileSumError.setRow("");
		}else if(tApsTxnLogs.size() == errorCount && feeSetD4HasErrors){
			FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FEE_SET_ER);
            fileSumError.setErrMemo("月结商户存在生效的手续费方案");
            fileSumError.setRow("");
		}else if(tApsTxnLogs.size() == errorCount){
			FileSumError fileSumError = getFileSumError();
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER);
            fileSumError.setErrCode(TDataDictConst.BUSI_VER_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow("");
			for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean : list){
				FileRowError fileRowError = getFileRowError();
				FileRowError fileRowErrorPayFor = inComeForBusiDetailRowBean.getOperationBean().getFileRowErrro();
				fileRowError.setRowNo(fileRowErrorPayFor.getRowNo());
				fileRowError.setRow(fileRowErrorPayFor.getRow());
				fileRowError.setColumnErrors(fileRowErrorPayFor.getColumnErrors());
			}
		}
	}

	@Override
	public void setOperationErrorInf() {
		// 1、获取业务对象
		InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
		// 2、获取明细信息，明细中每条交易记录了业务校验时的错误信息
		List<InComeForBusiDetailRowBean> list = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
		// 3、循环交易明细
		for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean : list){
			// 4、获取每条交易的业务信息对象
			OperationBean operationBean = inComeForBusiDetailRowBean.getOperationBean();
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
