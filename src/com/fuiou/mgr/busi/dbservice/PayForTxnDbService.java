package com.fuiou.mgr.busi.dbservice;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TInsBankAcnt;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.VPmsBankInf;
import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.service.TStlDtStService;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfSumBean;
import com.fuiou.mgr.util.VirtAcntUtil;

public class PayForTxnDbService extends TxnDbService{
	private static Logger logger = LoggerFactory.getLogger(PayForTxnDbService.class);
	// 一笔付款的主键
	String payForKbpsSrcSettleDt = "";
	String payForSrcModuleCd = "";
	String payForKbpsTraceNo = "";
	short payForSubTxnSeq = 0;
	String[] refundKeys = null;
	@Override
	public void setTfileInf() {
		PayForTxnInfBean payForTxnInfBean = (PayForTxnInfBean)txnInfBean;
		PayForTxnInfSumBean payForTxnInfSumBean = payForTxnInfBean.getPayForTxnInfSumBean();
		int idxFileFixes = payForTxnInfBean.getFileName().indexOf('.');
        String fileName = payForTxnInfBean.getFileName().substring(0, idxFileFixes);
        String fileNameSuffix = payForTxnInfBean.getFileName().substring(idxFileFixes);
		tFileInf.setFILE_MCHNT_CD(payForTxnInfSumBean.getMchntCd());
        tFileInf.setFILE_BUSI_TP(payForTxnInfSumBean.getBusiCd());
        tFileInf.setFILE_DT(payForTxnInfSumBean.getTxnDate());
        tFileInf.setFILE_SEQ(payForTxnInfSumBean.getDayNo());
        long amt = 0;
        int row = 0;
        try {
        	amt = Long.valueOf(FuMerUtil.formatYuanToFen(Double.parseDouble(payForTxnInfSumBean.getSumAmt())));
        	row = Integer.parseInt(payForTxnInfSumBean.getSumDetails());
        } catch (Exception e) {
        	logger.error("Exception:",e);
        }
        tFileInf.setFILE_ROWS(row);
        tFileInf.setFILE_AMT(amt);
        tFileInf.setSRC_MODULE_CD(srcModuleCd);
        tFileInf.setFILE_NM(fileName);
        tFileInf.setFILE_NM_SFFX(fileNameSuffix);
        String savePath = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.PAYFOR_FILE_PATH);
        savePath += payForTxnInfSumBean.getMchntCd() + "/";
        tFileInf.setFILE_PATH(savePath);
        tFileInf.setFILE_SIZE(Integer.parseInt(String.valueOf(payForTxnInfBean.getFile().length())));
        tFileInf.setROW_CRT_TS(currDbTime);
        tFileInf.setREC_UPD_TS(currDbTime);
        tFileInf.setFILE_RSP_ST("0");
        if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)){
        	tFileInf.setFILE_ST(TDataDictConst.FILE_ST_REVIEW);
        }else{
        	tFileInf.setFILE_ST(TDataDictConst.FILE_ST_MCHNT_INIT);
        	tFileInf.setOPR_USR_ID(payForTxnInfBean.getOprUsrId());
        }
	}

	@Override
	public void setTApsTxnLog() {
		PayForBusiBean payForBusiBean = (PayForBusiBean)busiBean;
		List<PayForBusiDetailRowBean> list = payForBusiBean.getPayForBusiDetailRowBeanList();
		InComeForBusiDetailRowBean inComeForBusiDetailRowBean = payForBusiBean.getInComeForBusiDetailRowBean();
		PayForTxnInfBean payForTxnInfBean = (PayForTxnInfBean)txnInfBean;
		PayForTxnInfSumBean payForTxnInfSumBean = payForTxnInfBean.getPayForTxnInfSumBean();
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(payForTxnInfSumBean.getMchntCd(), "1");
		String mchntTp = tInsMchntInf.getMCHNT_TP();
		String fileName = "";
		String abchinaMchntTp = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, "abchinaMchntTp");
		if("FILE".equals(txnDataType)){
			int idxFileFixes = payForTxnInfBean.getFileName().indexOf('.');
			fileName = payForTxnInfBean.getFileName().substring(0, idxFileFixes);
		}
		if(list == null||list.size()==0){
			logger.error("文件名 [" + fileName + "] [没有需要入库的交易明细 ] ");
			return;
		}
		String kbpsSrcSettleDt = new TStlDtStService().getCurSettleDt();
		TInsMchntInf tInsInf = tInsMchntInfService.selectTInsInfByMchntCd(payForTxnInfSumBean.getMchntCd());
		List<String> seqList = new TSeqService().getKbpsTraceNo12s(currDbTime, list.size());
		int seqNo = -1;
		boolean isMchntCapital = false;
		boolean isRultFail= false;		// 是否计算出路由，没有作业务校验处理
		boolean tInsBankAcntFail = false;
		// 设置1笔收款业务
		List<String> seqListInCome = new TSeqService().getKbpsTraceNo12s(currDbTime, 1);
		String inComeFor_KBPS_TRACE_NO = seqListInCome.get(0);
		String inComeFor_DEST_SSN = seqListInCome.get(0).substring(6);
		// 业务bean
		OperationBean operationBeanInComeFor = inComeForBusiDetailRowBean.getOperationBean();
        TCardBin tCardBinInComeFor = operationBeanInComeFor.gettCardBin();
        String IssCdInComeFor = operationBeanInComeFor.getIssCd();
        boolean hasErrorInComeFor = false;
        FileRowError fileRowErrorOneTxn = operationBeanInComeFor.getFileRowErrro();	// 一笔收款业务校验失败错误原因
        if(fileRowErrorOneTxn.getColumnErrors() == null){
    		List<RowColumnError> rowColumnErrorList = new ArrayList<RowColumnError>();
    		fileRowErrorOneTxn.setColumnErrors(rowColumnErrorList);
    	}
    	hasErrorInComeFor = operationBeanInComeFor.getHasError();
    	TApsTxnLog tApsTxnLogInComeFor = new TApsTxnLog();
    	// 明细序列|扣款人开户行代码(总行代码)|扣款人银行帐号|户名|金额|企业流水号|备注|手机号
    	String lineStrInCome = inComeForBusiDetailRowBean.getDetailSeriaNo()+"|"+ inComeForBusiDetailRowBean.getBankCd()+"|"+ inComeForBusiDetailRowBean.getBankAccount()+"|"+ inComeForBusiDetailRowBean.getAccountName()+"|"+ FuMerUtil.formatFenToYuan(inComeForBusiDetailRowBean.getDetailAmt()) +"|"+ inComeForBusiDetailRowBean.getEnpSeriaNo()+"|"+ inComeForBusiDetailRowBean.getMemo()+"|"+ inComeForBusiDetailRowBean.getMobile();
    	if(fileRowErrorOneTxn.getRow() == null){
    		fileRowErrorOneTxn.setRow(lineStrInCome);
    	}
    	tApsTxnLogInComeFor.setSEC_CTRL_INF(payForBusiBean.getOprUsrId());//增加了操作员字段
    	tApsTxnLogInComeFor.setKBPS_SRC_SETTLE_DT(kbpsSrcSettleDt);
    	tApsTxnLogInComeFor.setSRC_MODULE_CD(srcModuleCd);
    	tApsTxnLogInComeFor.setSRC_MCHNT_CD(payForTxnInfSumBean.getMchntCd());
    	tApsTxnLogInComeFor.setSUB_TXN_SEQ(TDataDictConst.SUB_TXN_SEQ);
    	tApsTxnLogInComeFor.setKBPS_TRACE_NO(inComeFor_KBPS_TRACE_NO);
    	tApsTxnLogInComeFor.setDEST_SSN(inComeFor_DEST_SSN);
    	tApsTxnLogInComeFor.setBUSI_CD(busiCd);
    	if("FILE".equals(txnDataType)){
    		tApsTxnLogInComeFor.setSRC_ORDER_NO(fileName);
    	}
    	tApsTxnLogInComeFor.setACQ_INS_CD(tInsInf.getINS_CD());
    	tApsTxnLogInComeFor.setSRC_MCHNT_TXN_DT(FuMerUtil.date2String(currDbTime, "yyyyMMdd"));
    	tApsTxnLogInComeFor.setSRC_MCHNT_TXN_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
    	tApsTxnLogInComeFor.setLOC_DT(FuMerUtil.date2String(currDbTime, "MMdd"));
    	tApsTxnLogInComeFor.setLOC_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
    	tApsTxnLogInComeFor.setSRC_INS_CD(tInsInf.getINS_CD());
    	tApsTxnLogInComeFor.setDETAIL_INQR_DATA(lineStrInCome);
    	
    	// 判断路由是否匹配成功
    	if(operationBeanInComeFor.gettBusiRut() != null){
			// 广银联(KBSP)
    		if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
    			tApsTxnLogInComeFor.setRCV_INS_CD(TDataDictConst.INS_CD_AP01_D);
    		}else{
    			if(operationBeanInComeFor.gettBusiRut().getCHNL_INS_CD().equals(TDataDictConst.INS_CD_GZ_UNION_PAY)){
    				tApsTxnLogInComeFor.setRCV_INS_CD(TDataDictConst.INS_CD_KBPS);
    				// 非广银联(BPS)
    			}else{
    				tApsTxnLogInComeFor.setRCV_INS_CD(TDataDictConst.INS_CD_BPS);
    			}
    		}
			tApsTxnLogInComeFor.setDEST_INS_CD(operationBeanInComeFor.gettBusiRut().getCHNL_INS_CD());
			if(TDataDictConst.INS_CD_ABCHINA.equals(operationBeanInComeFor.gettBusiRut().getCHNL_INS_CD())){
				tApsTxnLogInComeFor.setDEST_MCHNT_TP(abchinaMchntTp);
			}
    		tApsTxnLogInComeFor.setMSG_RSN_CD(operationBeanInComeFor.gettBusiRut().getBUSI_RUT_ID());
    		tApsTxnLogInComeFor.setTXN_MD(operationBeanInComeFor.gettBusiRut().getMCHNT_CAP_CFM());
    		tApsTxnLogInComeFor.setPOS_ENTRY_MD_CD(operationBeanInComeFor.gettBusiRut().getCHNL_CAP_CFM());
    		// 查询富友入账户
    		TInsBankAcnt tInsBankAcnt = tInsBankAcntService.selectByKey(operationBeanInComeFor.gettBusiRut().getCHNL_INS_CD(), "0", "1", "1");
    		if(tInsBankAcnt == null){
    			isRultFail = true;
    			tInsBankAcntFail = true;
    		}else{
    			tApsTxnLogInComeFor.setORIG_ACQ_INS_CD(tInsBankAcnt.getINTER_BANK_NO());
    		}
    	}else{
    		isRultFail = true;
    	}
    	tApsTxnLogInComeFor.setTO_TXN_CD((operationBeanInComeFor.getTxnFeeAmtId() != null && operationBeanInComeFor.getTxnFeeAmtId() != -1)?operationBeanInComeFor.getTxnFeeAmtId().toString():"");
    	tApsTxnLogInComeFor.setTO_TS(currDbTime);
    	tApsTxnLogInComeFor.setTXN_RCV_TS(currDbTime);
    	tApsTxnLogInComeFor.setTXN_FIN_TS(currDbTime);
    	tApsTxnLogInComeFor.setSRC_SETTLE_DT(kbpsSrcSettleDt);
    	tApsTxnLogInComeFor.setDEST_SETTLE_DT(kbpsSrcSettleDt);
    	tApsTxnLogInComeFor.setKBPS_DEST_SETTLE_DT(kbpsSrcSettleDt);
    	tApsTxnLogInComeFor.setSRC_MCHNT_TP(tInsInf.getMCHNT_TP());
    	tApsTxnLogInComeFor.setSRC_CHNL("07");
    	tApsTxnLogInComeFor.setTXN_CD("S22");
    	tApsTxnLogInComeFor.setSRC_SSN(inComeForBusiDetailRowBean.getDetailSeriaNo());
    	tApsTxnLogInComeFor.setID_NO(inComeForBusiDetailRowBean.getAccountName());
    	tApsTxnLogInComeFor.setSRC_TXN_AMT(inComeForBusiDetailRowBean.getDetailAmt());
    	tApsTxnLogInComeFor.setDEST_TXN_AMT(operationBeanInComeFor.getDestTxnAmt());
    	tApsTxnLogInComeFor.setTXN_FEE_AMT((int)operationBeanInComeFor.getTxnFeeAmt());
    	tApsTxnLogInComeFor.setIC_FLDS(inComeForBusiDetailRowBean.getEnpSeriaNo() + "|" + inComeForBusiDetailRowBean.getMemo() + "|" + inComeForBusiDetailRowBean.getMobile());
    	tApsTxnLogInComeFor.setDEBIT_ACNT_NO(String.valueOf(inComeForBusiDetailRowBean.getBankAccount().length()) + inComeForBusiDetailRowBean.getBankAccount());
    	
    	tApsTxnLogInComeFor.setCAPITAL_DIR("D");
    	tApsTxnLogInComeFor.setGOODS_NUM((long)list.size());
    	tApsTxnLogInComeFor.setMSG_TP(TDataDictConst.MSG_TP_NOTALLOW_RETURN);
    	tApsTxnLogInComeFor.setDEST_TXN_ST(TDataDictConst.TXN_DEST_RECORD_SUC);
    	tApsTxnLogInComeFor.setEXPIRE_DT(TDataDictConst.RES_TP_NOTALLOW_RETURN);//	响应文件不可回盘
    	tApsTxnLogInComeFor.setKBPS_SETTLE_IND("0");//AP01的D不参与清算
    	
    	// 如果1笔业务没有查询到机构账户信息也作业务校验失败处理。
    	if(busiCd.equals(TDataDictConst.BUSI_CD_PAYFOR)){
    		// 如果N笔业务校验都失败或1笔收款没有找到机构账户信息或匹配路由失败
    		if(operationBeanInComeFor.isOneTxnHasError() || hasErrorInComeFor || isRultFail){
    			tApsTxnLogInComeFor.setSRC_TXN_ST(TDataDictConst.TXN_SRC_SEND_FAIL);
    			String acqRspCd = TDataDictConst.BUSI_VER_ER;
    			String kbpsRspCd = new TDataDictService().getKbpsRspCdByAcqRspCd(acqRspCd);
    			tApsTxnLogInComeFor.setACQ_RSP_CD(acqRspCd);
    			tApsTxnLogInComeFor.setKBPS_RSP_CD(kbpsRspCd);
    			tApsTxnLogInComeFor.setADDN_PRIV_DATA(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER));
    			tApsTxnLogInComeFor.setBUSI_END_IND((short) 1);
    			if(isRultFail && tInsBankAcntFail){
    				List<RowColumnError> rowColumnErrorList = fileRowErrorOneTxn.getColumnErrors();
    				RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.FUIOU_IN_ACNT_ER);
    				rowColumnError.setErrMemo("没有找到富友入账户");
    				rowColumnErrorList.add(rowColumnError);
    			}else if(isRultFail && !tInsBankAcntFail){
    				List<RowColumnError> rowColumnErrorList = fileRowErrorOneTxn.getColumnErrors();
    				RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
    				rowColumnError.setErrMemo("收款没有匹配到路由");
    				rowColumnErrorList.add(rowColumnError);
    			}
    			hasErrorInComeFor = true;
    		}else{
    			tApsTxnLogInComeFor.setSRC_TXN_ST(TDataDictConst.TXN_SRC_SEND_SUCCESS);
    			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
    				String mchnt_cap_cfm = operationBeanInComeFor.gettBusiRut().getMCHNT_CAP_CFM();// 商户资金到账
    				if("0".equals(mchnt_cap_cfm)){
    					isMchntCapital = true;
    					tApsTxnLogInComeFor.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_CAPITAL_AUC_SURE);
    				}else{
    					tApsTxnLogInComeFor.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_SECOND_SURE);
    				}
    			}
    		}
    	}else{
    		if(tCardBinInComeFor != null){
    			if(null == IssCdInComeFor || IssCdInComeFor.length() != 10){
    				hasErrorInComeFor = true; //不一致
    			}else{
    				tApsTxnLogInComeFor.setISS_INS_CD_D(IssCdInComeFor);// 付款ISS_INS_CD_C,代收ISS_INS_CD_D
    				tApsTxnLogInComeFor.setCARD_BIN_D(tCardBinInComeFor.getCARD_BIN());// 付款CARD_BIN_C,代收CARD_BIN_D
    				tApsTxnLogInComeFor.setCARD_ATTR_D(tCardBinInComeFor.getCARD_ATTR());
    			}
    		}else{
    			if(hasErrorInComeFor == false){
    				tApsTxnLogInComeFor.setDEBIT_ACNT_NO(String.valueOf(inComeForBusiDetailRowBean.getBankAccount().length()) + inComeForBusiDetailRowBean.getBankAccount());
    				tApsTxnLogInComeFor.setISS_INS_CD_D(IssCdInComeFor);// 付款ISS_INS_CD_C,代收ISS_INS_CD_D
    			}
    		}
    		if (operationBeanInComeFor.isOneTxnHasError() || hasErrorInComeFor || isRultFail) {
    			tApsTxnLogInComeFor.setSRC_TXN_ST(TDataDictConst.SRCTXNST_RES_FAIL);
    			String acqRspCd = TDataDictConst.BUSI_VER_ER;
    			String kbpsRspCd = new TDataDictService().getKbpsRspCdByAcqRspCd(acqRspCd);
    			tApsTxnLogInComeFor.setACQ_RSP_CD(acqRspCd);
    			tApsTxnLogInComeFor.setKBPS_RSP_CD(kbpsRspCd);
    			tApsTxnLogInComeFor.setADDN_PRIV_DATA(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER));
    			tApsTxnLogInComeFor.setBUSI_END_IND((short) 1);
    			if(isRultFail && tInsBankAcntFail){
    				List<RowColumnError> rowColumnErrorList = fileRowErrorOneTxn.getColumnErrors();
    				RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.FUIOU_IN_ACNT_ER);
    				rowColumnError.setErrMemo("没有找到富友入账户");
    				rowColumnErrorList.add(rowColumnError);
    			}else if(isRultFail && !tInsBankAcntFail){
    				List<RowColumnError> rowColumnErrorList = fileRowErrorOneTxn.getColumnErrors();
    				RowColumnError rowColumnError = new RowColumnError();
    				rowColumnError.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
    				rowColumnError.setErrMemo("收款没有匹配到路由");
    				rowColumnErrorList.add(rowColumnError);
    			}
    			hasErrorInComeFor = true;
    		} else {
    			tApsTxnLogInComeFor.setSRC_TXN_ST(TDataDictConst.SRCTXNST_RES_SUCC);
    			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
    				tApsTxnLogInComeFor.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_SECOND_SURE);
    			}
    		}
    	}
    	tApsTxnLogs.add(tApsTxnLogInComeFor);
		int fileRightRow = 0;
		long fileRightAmt = 0;
		long payAuthAmt = 0;//需要预授权的总金额
		int payAuthCount = 0;//预授权的总笔数
		// 循环设置N笔付款业务
		for(PayForBusiDetailRowBean detailBean:list){
			boolean isRultErrorPay = false;
			boolean isFeeErrorPay = false;
			seqNo++;
			// 业务bean
			OperationBean operationBean = detailBean.getOperationBean();
			TCardBin tCardBin = operationBean.gettCardBin();
            String IssCd = operationBean.getIssCd();
            boolean hasError = operationBean.getHasError();
            boolean feeHasError = operationBean.isFeeHasError();
            boolean feeSetHasError = operationBean.isFeeSetHasError();// 实时结算商户是否找到手续费方案
            boolean feeSetD4HasError = operationBean.isFeeSetD4HasError();	// 月结商户是否有生效的手续费方案，有则报错
            feeSetHasErrors = feeSetHasError;
            feeSetD4HasErrors = feeSetD4HasError;
            String issInsRes = operationBean.getIssInsRes();
            VPmsBankInf vPmsBankInf = operationBean.getvPmsBankInf();
			
			TApsTxnLog tApsTxnLog = new TApsTxnLog();
			long detailAmtLong = 0;
			String feeMd = detailBean.getOperationBean().getFeeMd();
			if("1".equals(feeMd)){
				detailAmtLong = detailBean.getDetailAmt();
			}else if("0".equals(feeMd)){
				detailAmtLong = detailBean.getDetailAmt()-detailBean.getOperationBean().getTxnFeeAmt();
			}else{
				detailAmtLong = detailBean.getDetailAmt();
			}
			String lineStr = detailBean.getDetailSeriaNo() + "|" + detailBean.getBankCd() + "|" + detailBean.getCityCode() + "|" + detailBean.getBranchBank() + "|" + detailBean.getBankAccount() + "|" + detailBean.getAccountName() + "|" + FuMerUtil.formatFenToYuan(detailAmtLong) + "|" + detailBean.getEnpSeriaNo() + "|" + detailBean.getMemo() + "|" + detailBean.getMobile();
			// 明细序列|收款人开户行代码(总行代码)|收款人开户行城市代码|收款人开户行支行名称|收款人银行帐号|户名|金额|企业流水号|备注|手机号
			tApsTxnLog.setKBPS_SRC_SETTLE_DT(kbpsSrcSettleDt);
			tApsTxnLog.setSRC_MCHNT_CD(payForTxnInfSumBean.getMchntCd());
			tApsTxnLog.setSRC_MODULE_CD(srcModuleCd);
			tApsTxnLog.setSUB_TXN_SEQ(TDataDictConst.SUB_TXN_SEQ);
			tApsTxnLog.setBUSI_CD(busiCd);
			if("FILE".equals(txnDataType)){
				tApsTxnLog.setSRC_ORDER_NO(fileName);
			}
			tApsTxnLog.setSEC_CTRL_INF(payForBusiBean.getOprUsrId());//增加了操作员字段
			tApsTxnLog.setACQ_INS_CD(tInsInf.getINS_CD());
			tApsTxnLog.setSRC_MCHNT_TXN_DT(FuMerUtil.date2String(currDbTime, "yyyyMMdd"));
			tApsTxnLog.setSRC_MCHNT_TXN_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
			tApsTxnLog.setLOC_DT(FuMerUtil.date2String(currDbTime, "MMdd"));
			tApsTxnLog.setLOC_TM(FuMerUtil.date2String(currDbTime, "HHmmss"));
			tApsTxnLog.setSRC_INS_CD(tInsInf.getINS_CD());
			tApsTxnLog.setKBPS_SETTLE_IND("1");
			tApsTxnLog.setTO_TS(currDbTime);
			tApsTxnLog.setTXN_RCV_TS(currDbTime);
			tApsTxnLog.setTXN_FIN_TS(currDbTime);
			tApsTxnLog.setSRC_SETTLE_DT(kbpsSrcSettleDt);
			tApsTxnLog.setDEST_SETTLE_DT(kbpsSrcSettleDt);
			tApsTxnLog.setKBPS_DEST_SETTLE_DT(kbpsSrcSettleDt);
			tApsTxnLog.setSRC_MCHNT_TP(tInsInf.getMCHNT_TP());
			tApsTxnLog.setTXN_CD("S32");
			tApsTxnLog.setSRC_CHNL("07");
			tApsTxnLog.setSRC_SSN(detailBean.getDetailSeriaNo());
			tApsTxnLog.setID_NO(detailBean.getAccountName());
			tApsTxnLog.setSRC_TXN_AMT(detailBean.getDetailAmt());
			tApsTxnLog.setDEST_TXN_AMT(operationBean.getDestTxnAmt());
			tApsTxnLog.setTXN_FEE_AMT((int)operationBean.getTxnFeeAmt());
			tApsTxnLog.setAUTH_RSP_CD(detailBean.getOperationBean().getFeeMd());
//			tApsTxnLog.setMAC(mac);
			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
				tApsTxnLog.setORIG_SRC_ORDER_NO(detailBean.getOrderno());// 请求流水号
            	tApsTxnLog.setORIG_ORDER_DT(detailBean.getMerdt());		// 请求日期
			}
			if(operationBean.gettBusiRut() != null){
				// 深结算、天津代收付
				if(operationBean.gettBusiRut().getCHNL_INS_CD().equals(TDataDictConst.INS_CD_SZ_STL_CNTR)
						|| operationBean.gettBusiRut().getCHNL_INS_CD().equals(TDataDictConst.INS_CD_TJ_MS)){
					tApsTxnLog.setRCV_INS_CD(operationBean.gettBusiRut().getCHNL_INS_CD());
				// BPS
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
				// 查询富友出账户
	            TInsBankAcnt tInsBankAcnt = tInsBankAcntService.selectByKey(operationBean.gettBusiRut().getCHNL_INS_CD(), "1", "1", "1");
	            if(tInsBankAcnt == null){
	            	isRultErrorPay = true;
	            }else{
	            	tApsTxnLog.setORIG_FWD_INS_CD(tInsBankAcnt.getINTER_BANK_NO());
	            }
			}else{
				isRultErrorPay = true;
			}
			if(operationBean.getTxnFeeAmtId() == null && mchntTp.startsWith("D6")){
				isFeeErrorPay = true;
			}else{
				isFeeErrorPay = false;
			}
			tApsTxnLog.setTO_TXN_CD((operationBean.getTxnFeeAmtId() != null && operationBean.getTxnFeeAmtId() != -1)?operationBean.getTxnFeeAmtId().toString():"");
			tApsTxnLog.setDETAIL_INQR_DATA(lineStr);
            tApsTxnLog.setIC_FLDS(detailBean.getEnpSeriaNo() + "|" + detailBean.getMemo() + "|" + detailBean.getMobile());
			tApsTxnLog.setCREDIT_ACNT_NO(String.valueOf(detailBean.getBankAccount().length()) + detailBean.getBankAccount());
			tApsTxnLog.setDEBIT_ACNT_NO(String.valueOf(detailBean.getBankAccount().length()) + detailBean.getBankAccount());
			// 加入鉴别码20140718,紧急上线
			try {
				String md5Source = tApsTxnLog.getCREDIT_ACNT_NO()+tApsTxnLog.getID_NO()+tApsTxnLog.getDEST_TXN_AMT();
				md5Source = md5Source.replace(" ", "");//替换掉空格
				md5Source = DigestUtils.md5Hex(md5Source.getBytes(TDataDictConst.DB_CHARSET))+tApsTxnLog.getKBPS_SRC_SETTLE_DT();
				logger.info("CREDIT_ACNT_NO="+tApsTxnLog.getCREDIT_ACNT_NO()+";ID_NO="+tApsTxnLog.getID_NO()+";DEST_TXN_AMT="+tApsTxnLog.getDEST_TXN_AMT()+";KBPS_SRC_SETTLE_DT="+tApsTxnLog.getKBPS_SRC_SETTLE_DT());
				tApsTxnLog.setCARD_ACPTR_NM_LOC(DigestUtils.md5Hex(md5Source.getBytes(TDataDictConst.DB_CHARSET)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// 状态
			tApsTxnLog.setDEST_TXN_ST(TDataDictConst.TXN_DEST_RECORD_SUC);
			tApsTxnLog.setCAPITAL_DIR(TDataDictConst.CAPITALDIR_C);
            if(!"".equals(IssCd)){
            	tApsTxnLog.setISS_INS_CD_C(IssCd);
            }
            if (tCardBin != null) {
            	tApsTxnLog.setCARD_BIN_C(tCardBin.getCARD_BIN());// 付款CARD_BIN_C,代收CARD_BIN_D
            	tApsTxnLog.setCARD_ATTR_C(tCardBin.getCARD_ATTR());
            }
            if (null != issInsRes) {
            	tApsTxnLog.setISS_INS_RES(issInsRes);
            	if (vPmsBankInf != null && vPmsBankInf.getDOM_IND() == 0) { // 同城
            		tApsTxnLog.setPOS_COND_CD("0");
            		tApsTxnLog.setPOS_PIN_CAP_CD("0");
            		tApsTxnLog.setORIG_MSG_TP(vPmsBankInf.getINS_TP());
            	} else { // 异地
            		tApsTxnLog.setPOS_COND_CD("1");
            		tApsTxnLog.setPOS_PIN_CAP_CD("1");
            		tApsTxnLog.setORIG_MSG_TP("00");
            	}
            }
            if (hasError || hasErrorInComeFor || isRultErrorPay || isFeeErrorPay || feeHasError || feeSetHasError || feeSetD4HasError) {
            	tApsTxnLog.setMSG_TP(TDataDictConst.MSG_TP_ALLOW_RETURN);
            	tApsTxnLog.setSRC_TXN_ST(TDataDictConst.TXN_SRC_SEND_FAIL);
            	tApsTxnLog.setEXPIRE_DT(TDataDictConst.RES_TP_ALLOW_RETURN);	// 响应文件可回盘
            	tApsTxnLog.setPAY_MD(TDataDictConst.PAY_MD_NONEED); // 不需要生成银行文件
            	String acqRspCd = TDataDictConst.BUSI_VER_ER;
                String kbpsRspCd = new TDataDictService().getKbpsRspCdByAcqRspCd(acqRspCd);
                tApsTxnLog.setACQ_RSP_CD(acqRspCd);
                tApsTxnLog.setKBPS_RSP_CD(kbpsRspCd);
                tApsTxnLog.setADDN_PRIV_DATA(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER));
				tApsTxnLog.setBUSI_END_IND((short) 1);
				if(isRultErrorPay){
					// 记录错误信息
					FileRowError fileRowError = detailBean.getOperationBean().getFileRowErrro();
					List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
					RowColumnError rowColumnError = new RowColumnError();
					rowColumnError.setErrCode(TDataDictConst.RUT_DATA_NULL_ER);
					rowColumnError.setErrMemo("没有匹配到路由");
					rowColumnErrorList.add(rowColumnError);
				}
				if(isFeeErrorPay){
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
//            	if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
//            		TBusiRut busiRut = operationBean.gettBusiRut();
//                	if("0".equals(busiRut.getMCHNT_CAP_CFM())&&"0".equals(busiRut.getCHNL_CAP_CFM())){//无需清算介入的交易
//                		
//    				}
//            	}
            	tApsTxnLog.setMSG_TP(TDataDictConst.MSG_TP_NOTALLOW_RETURN);
            	tApsTxnLog.setSRC_TXN_ST(TDataDictConst.TXN_SRC_SEND_SUCCESS);
            	tApsTxnLog.setEXPIRE_DT(TDataDictConst.RES_TP_NOTALLOW_RETURN);	// 响应文件不可回盘
            	tApsTxnLog.setPAY_MD(TDataDictConst.PAY_MD_NEED);
            	if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){
    				if(busiCd.equals(TDataDictConst.BUSI_CD_PAYFOR)){
    					String isChnlCapital = operationBean.gettBusiRut().getCHNL_CAP_CFM();	// 渠道资金到账
    					if(isMchntCapital){
    						tApsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_CAPITAL_AUC_SURE);
    						if("0".equals(isChnlCapital)){
    							payAuthCount ++;
    	                		payAuthAmt = payAuthAmt +detailBean.getDetailAmt();
    							tApsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_CHNL_CAPITAL_SECOND_SURE);
    						}
    					}else{
    						tApsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_SECOND_SURE);
    					}
    				}else{
    					tApsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_SECOND_SURE);
    				}
    			}
            }
            fileRightRow ++ ;
        	if("1".equals(feeMd)){
				fileRightAmt += detailBean.getDetailAmt();
			}else if("0".equals(feeMd)){
				fileRightAmt += detailBean.getDetailAmt()-detailBean.getOperationBean().getTxnFeeAmt();
			}else{
				fileRightAmt += detailBean.getDetailAmt();
			}
            tApsTxnLog.setKBPS_TRACE_NO(seqList.get(seqNo));
			tApsTxnLog.setDEST_SSN(seqList.get(seqNo).substring(6));
				tApsTxnLog.setRLAT_KBPS_TRACE_NO(inComeFor_KBPS_TRACE_NO);
				tApsTxnLog.setRLAT_KBPS_SRC_SETTLE_DT(kbpsSrcSettleDt);
				tApsTxnLog.setRLAT_SRC_MODULE_CD(srcModuleCd);
				tApsTxnLog.setRLAT_SUB_TXN_SEQ(TDataDictConst.SUB_TXN_SEQ);
            tApsTxnLogs.add(tApsTxnLog);
		}
		if(TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP) || srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_HTTP)){		
				if(payAuthCount > 0){
					String acnt = "";
					TCustStlInfService custStlInfService = new TCustStlInfService();
					List<String> acntNos = custStlInfService.getAcntNosByIndCd(tInsMchntInf.getINS_CD());
					if(acntNos.size() <= 0){
						logger.error("商户:"+tInsMchntInf.getMCHNT_CD()+",没有开通虚拟账户");
					}else{
						boolean payAuthIsError = false;//预授权是否失败
						String[] result = null;
						acnt = acntNos.get(0);
						logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
						.format(new Date())+"调用预授权交易开始");
						try {
							result = FasService.payAuth(tInsMchntInf.getINS_CD(), acnt, payAuthAmt+"", VirtAcntUtil.getSsn(), "", tInsMchntInf.getMCHNT_CD(), tInsMchntInf.getMCHNT_TP(),"AP01","S32");
							if(result != null){
								if("0000".equals(result[2])){
									logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
									.format(new Date())+"预授权交易成功");
									for(TApsTxnLog tApsTxnLog : tApsTxnLogs){
										String isChnlCapital = tApsTxnLog.getPOS_ENTRY_MD_CD();	// 渠道资金到账
										if(isMchntCapital){
											if("0".equals(isChnlCapital)){
												tApsTxnLog.setRETRI_REF_NO(result[6]!=null?result[6]:"");//预授权标示码
											}
										}
									}
								}else{
									payAuthIsError = true;
									logger.error(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
									.format(new Date())+"预授权交易失败");
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							payAuthIsError = true;
							logger.error("预授权超时");
							logger.error("Exception:",e);
						}
						if(payAuthIsError){
							for(TApsTxnLog tApsTxnLog : tApsTxnLogs){
								String isChnlCapital = tApsTxnLog.getPOS_ENTRY_MD_CD();	// 渠道资金到账
								if(isMchntCapital){
									if("0".equals(isChnlCapital)){
										tApsTxnLog.setCUSTMR_TP(TDataDictConst.TXN_CUSTMR_REFUND);//预授权失败CUSTMR_TP改为:T1
										tApsTxnLog.setDEST_RSP_CD(TDataDictConst.PAYAUTH_HAS_ERROR);
										tApsTxnLog.setADDN_PRIV_DATA("预授权失败");
									}
								}
							}
						}
					}
				}
			}
		}
		tFileInf.setFILE_RIGHT_AMT(fileRightAmt);
		tFileInf.setFILE_RIGHT_ROWS(fileRightRow);
	}

	@Override
	public void setTFileErrorInf() {
		PayForTxnInfBean payForTxnInfBean = (PayForTxnInfBean)txnInfBean;
		int idxFileFixes = payForTxnInfBean.getFileName().indexOf('.');
        String fileName = payForTxnInfBean.getFileName().substring(0, idxFileFixes);
		List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
		if(fileRowErrorList == null || fileRowErrorList.size() == 0){
			return;
		}
		for(int i = 0;i<fileRowErrorList.size();i++){
			TFileErrInf tFileErrorInf = new TFileErrInf();
			FileRowError fileRowError = fileRowErrorList.get(i);
			tFileErrorInf.setFILE_NM(fileName);
			tFileErrorInf.setFILE_MCHNT_CD(payForTxnInfBean.getMchntCd());
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
		String savePath = new TDataDictService().selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.PAYFOR_FILE_PATH);
		if(null == savePath){
			logger.error("ftpPayFor savepath null");
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
		// 入库N笔的交易数量
		int txnCount = tApsTxnLogs.size()-1;		// 非退款、重发业务，需要去掉一笔D方向的交易
		PayForBusiBean payForBusiBean = (PayForBusiBean)busiBean;
		InComeForBusiDetailRowBean inComeForBusiDetailRowBean = payForBusiBean.getInComeForBusiDetailRowBean();
		List<PayForBusiDetailRowBean> payforBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
		FileRowError inComeForFileRowError = inComeForBusiDetailRowBean.getOperationBean().getFileRowErrro();
		List<RowColumnError> rowColumnErrorList = inComeForFileRowError.getColumnErrors();
		int errorCount = 0;
		// 判断，如果业务校验都失败则不入库，提示错误。
		for(TApsTxnLog tapsTxnLog:tApsTxnLogs){
			String SRC_TXN_ST = tapsTxnLog.getSRC_TXN_ST();
			if(TDataDictConst.TXN_SRC_SEND_FAIL.equals(SRC_TXN_ST) && tapsTxnLog.getCAPITAL_DIR().equals("C")){
				errorCount ++;
			}
		}
		if(txnCount == errorCount && feeSetHasErrors){
			FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FEE_SET_ER);
            fileSumError.setErrMemo("实时结算商户没有找到手续费方案");
            fileSumError.setRow("");
		}else if(txnCount == errorCount && feeSetD4HasErrors){
			FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FEE_SET_ER);
            fileSumError.setErrMemo("月结商户存在生效的手续费方案");
            fileSumError.setRow("");
		}else if(txnCount == errorCount && rowColumnErrorList.size() != 0){
			FileSumError fileSumError = getFileSumError();
			for(RowColumnError rowColumnError : rowColumnErrorList){
				String errCode = rowColumnError.getErrCode();
				String memo = rowColumnError.getErrMemo();
	            fileSumError.setErrCode(errCode);
	            fileSumError.setErrMemo("收款  "+memo);
	            fileSumError.setRow("");
			}
		}else if(txnCount == errorCount){
			FileSumError fileSumError = getFileSumError();
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.BUSI_VER_ER);
            fileSumError.setErrCode(TDataDictConst.BUSI_VER_ER);
            fileSumError.setErrMemo(memo);
            fileSumError.setRow("");
			for(PayForBusiDetailRowBean payForBusiDetailRowBean : payforBusiDetailRowBeanList){
				FileRowError fileRowError = getFileRowError();
				FileRowError fileRowErrorPayFor = payForBusiDetailRowBean.getOperationBean().getFileRowErrro();
				fileRowError.setRowNo(fileRowErrorPayFor.getRowNo());
				fileRowError.setRow(fileRowErrorPayFor.getRow());
				fileRowError.setColumnErrors(fileRowErrorPayFor.getColumnErrors());
			}
		}
	}

	@Override
	public void setOperationErrorInf() {
		// 1、获取业务对象
		PayForBusiBean payForBusiBean = (PayForBusiBean)busiBean;
		// 2、获取明细信息，明细中每条交易记录了业务校验时的错误信息
		List<PayForBusiDetailRowBean> list = payForBusiBean.getPayForBusiDetailRowBeanList();
		InComeForBusiDetailRowBean inComeForBusiDetailRowBean = payForBusiBean.getInComeForBusiDetailRowBean();
		// 3、循环交易明细
		for(PayForBusiDetailRowBean payForBusiDetailRowBean : list){
			// 4、获取每条交易的业务信息对象
			OperationBean operationBean = payForBusiDetailRowBean.getOperationBean();
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
		FileRowError inComeForFileRowError = inComeForBusiDetailRowBean.getOperationBean().getFileRowErrro();
		oneTxnErrors.add(inComeForFileRowError);
	}

}
