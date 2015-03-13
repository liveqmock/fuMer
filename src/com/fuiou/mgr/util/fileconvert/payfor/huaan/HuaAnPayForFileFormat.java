package com.fuiou.mgr.util.fileconvert.payfor.huaan;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.fuiou.mgr.util.VirtAcntUtil;
import com.fuiou.mgr.util.fileconvert.payfor.PayForFileFormatConvert;
import com.opensymphony.xwork2.ActionContext;

public class HuaAnPayForFileFormat extends PayForFileFormatConvert {
	private static final Logger logger = Logger.getLogger(HuaAnPayForFileFormat.class);
	@Override
	public Map<String, String> handlerFile(AccessBean convertFile) {
		// 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
		// 业务类型
		String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
		// 交易数据类型
		String txnDataType = "FILE";
		// 接入对象(文件)
		FileAccess accessBean = (FileAccess)convertFile;
		accessBean.setBusiCd(busiCd);
		accessBean.setTxnInfSource(srcModuleCd);
		accessBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
		/////////////////////////////////////////////////////////////////////////////////
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter("HAAP01");
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		if(txnInfBean == null){
			errorMap.put(TDataDictConst.FILE_READ_EP, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP));
            logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP));
            return errorMap;
		}
		payForTxnInfBean = (PayForTxnInfBean) txnInfBean;
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();
		
		List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
		if(fileRowErrorList != null){
			for(int i = 0;i<fileRowErrorList.size();i++){
				TFileErrInf tFileErrorInf = new TFileErrInf();
				FileRowError fileRowError = fileRowErrorList.get(i);
				tFileErrorInf.setFILE_NM(txnInfBean.getFileName());
				tFileErrorInf.setERR_ROW_SEQ(Integer.toString(fileRowError.getRowNo()));
				tFileErrorInf.setERR_ROW_CONTENT(fileRowError.getRow());
				List<RowColumnError> rowColumnErrorList =  fileRowError.getColumnErrors();
				String rowColumnErrors = "";
				for(RowColumnError rowColumnError:rowColumnErrorList){
					rowColumnErrors += rowColumnError.getErrMemo()+";";
				}
				tFileErrorInf.setERR_ROW_DESC(rowColumnErrors);
				tFileErrorInf.setROW_CRT_TS(new Date());
				errListMap.add(tFileErrorInf);
			}
		}
		
		if(fileError.getFileSumError().getErrCode() != null||fileError.getFileSumError().getErrMemo() != null || fileError.getFileSumError().getRow() != null){
			if(fileRowErrorList != null){
				logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
				ActionContext.getContext().put("fileNam", txnInfBean.getFileName().substring(0, txnInfBean.getFileName().indexOf(".")));// 文件名称
				ActionContext.getContext().put("fileBusiTp", TDataDictConst.BUSI_CD_PAYFOR);//业务代码
				ActionContext.getContext().put("fileRows", 0);//校验明细
				ActionContext.getContext().put("fileAmt", 0.00);//校验金额
				ActionContext.getContext().put("sameFileMap", sameFileMap);// 相同文件信息
				ActionContext.getContext().put("errListMap", errListMap);// 错误信息
				ActionContext.getContext().put("message", "批量付款文件上传失败!");
				return null;
			}
			errorMap.put(fileError.getFileSumError().getErrCode(), fileError.getFileSumError().getErrMemo());
			logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
			ActionContext.getContext().put("errorMap", errorMap);
			return errorMap;
		}
		
		PayForBusiBean payForBusiBean = (PayForBusiBean)busiBean;
		List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = payForBusiBean.getPayForBusiDetailRowBeanList();
		if(payForBusiDetailRowBeanList.size()>0){
			if(SystemParams.fileNameMap.containsKey(tOperatorInf.getMCHNT_CD())){
        		errorMap.put(TDataDictConst.FILE_NAME_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_ER));
                logger.error(TDataDictConst.FILE_NAME_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_NAME_ER));
                ActionContext.getContext().put("errorMap", errorMap);
                return errorMap;
    		}
			try {
				// 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
				// 拆分交易
				SplitTrade splitTrade = new SplitTrade();
				splitTrade.splitTrade(busiBean);
				ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean = splitTrade.getForTxnFeeAmtBean();
				BusiBean operationVerifyErrorBusibean = splitTrade.getOperationVerifyErrorBusibean();
				
				// 计算手续费、路由
				TradeCalculate tradeCalculate = new TradeCalculate();
				ForAccessTxnFeeAmtRutBean calculateForTxnFeeAmtBean = tradeCalculate.doCalculate(forTxnFeeAmtBean);
				
				List<PayForBusiDetailRowBean> payForBusiDetailRowBeanListFee = calculateForTxnFeeAmtBean.getPayForList();
				String feeAmt = "0";
				String srcAmt = "0";
				String destAmt = "0";
				int feeAmtInt = 0;
				int srcAmtInt = 0;
				int destAmtInt = 0;
				
				for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanListFee){
					feeAmtInt += payForBusiDetailRowBean.getOperationBean().getTxnFeeAmt();
					srcAmtInt += payForBusiDetailRowBean.getDetailAmt();
					destAmtInt += (payForBusiDetailRowBean.getOperationBean().getDestTxnAmt());
				}
				
				feeAmt = FuMerUtil.formatFenToYuan(feeAmtInt);
				srcAmt = FuMerUtil.formatFenToYuan(srcAmtInt);
				destAmt = FuMerUtil.formatFenToYuan(destAmtInt);
				
				// 组合业务对象
				PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
				BusiBean packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
				
				TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
				txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,false);
				
				double sumAmt = 0;
				for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanList){
					sumAmt += payForBusiDetailRowBean.getDetailAmt();
				}
				
				List<FileRowError> fileRowOperateErrors = txnDbService.getFileRowOperateErrors();
				if(fileRowOperateErrors.size() != 0){
					for(int i = 0;i<fileRowOperateErrors.size();i++){
						TFileErrInf tFileErrorInf = new TFileErrInf();
						FileRowError fileRowError = fileRowOperateErrors.get(i);
						tFileErrorInf.setFILE_NM(txnInfBean.getFileName());
						tFileErrorInf.setERR_ROW_SEQ(Integer.toString(fileRowError.getRowNo()));
						tFileErrorInf.setERR_ROW_CONTENT(fileRowError.getRow());
						List<RowColumnError> rowColumnErrorList =  fileRowError.getColumnErrors();
						String rowColumnErrors = "";
						for(RowColumnError rowColumnError:rowColumnErrorList){
							rowColumnErrors += rowColumnError.getErrMemo()+";";
						}
						tFileErrorInf.setERR_ROW_DESC(rowColumnErrors);
						tFileErrorInf.setROW_CRT_TS(new Date());
						errListMap.add(tFileErrorInf);
					}
				}
				
				List<FileRowError> oneTxnFileRowErrors = txnDbService.getOneTxnErrors();
				if(oneTxnFileRowErrors.size() != 0){
					for(int i = 0;i<oneTxnFileRowErrors.size();i++){
						FileRowError fileRowError = oneTxnFileRowErrors.get(i);
						List<RowColumnError> rowColumnErrorList =  fileRowError.getColumnErrors();
						if(rowColumnErrorList.size()>0){
							TFileErrInf tFileErrorInf = new TFileErrInf();
							tFileErrorInf.setFILE_NM(txnInfBean.getFileName());
							tFileErrorInf.setERR_ROW_SEQ(Integer.toString(fileRowError.getRowNo()));
							tFileErrorInf.setERR_ROW_CONTENT(fileRowError.getRow());
							String rowColumnErrors = "";
							for(RowColumnError rowColumnError:rowColumnErrorList){
								rowColumnErrors += rowColumnError.getErrMemo()+";";
							}
							tFileErrorInf.setERR_ROW_DESC(rowColumnErrors);
							tFileErrorInf.setROW_CRT_TS(new Date());
							oneTxnErrListMap.add(tFileErrorInf);
						}
					}
				}
				
				FileError fileErrorFromDbService = txnDbService.getFileError();
				if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
					ActionContext.getContext().put("fileRows", 0);//校验明细
					ActionContext.getContext().put("fileAmt", 0.00);//校验金额
					ActionContext.getContext().put("message", "批量付款文件上传失败,业务校验错误!");
				}else{
					ActionContext.getContext().put("fileRows", payForBusiDetailRowBeanList.size());//校验明细
					ActionContext.getContext().put("fileAmt", FuMerUtil.formatFenToYuan(sumAmt));//校验金额
					ActionContext.getContext().put("feeAmt", feeAmt);
					ActionContext.getContext().put("srcAmt", srcAmt);
					ActionContext.getContext().put("destAmt", destAmt);
					if (errListMap.size() > 0) {
						if(busiCd.equals(TDataDictConst.BUSI_CD_PAYFOR)){
							ActionContext.getContext().put("message", "批量付款文件上传成功,存在部分业务校验错误!");
						}
					}
				}
				
				// 查询是否有相同文件
				new CheckOutBase().selectUniformFile(sameFileMap, mchntCd, Long.parseLong(FuMerUtil.formatYuanToFen(sumAmt)), payForBusiDetailRowBeanList.size(),busiCd);
				
				String savePath = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.PAYFOR_FILE_PATH);

				savePath += mchntCd + "/";  // 文件保存路径加上商户号

				File savefile = new File(new File(savePath), txnInfBean.getFileName());
				boolean boo = createFile(savefile);
				if(!boo){
					logger.error("文件保存失败");
				}
			} catch (Exception e) {
				errorMap.put(TDataDictConst.FILE_SAVE_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_SAVE_ER));
				logger.error(TDataDictConst.FILE_SAVE_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_SAVE_ER));
				ActionContext.getContext().put("errorMap", errorMap);
				return errorMap;
			}
		}
		
		// 返回页面提示
		ActionContext.getContext().put("fileNam", txnInfBean.getFileName().substring(0, txnInfBean.getFileName().indexOf(".")));// 文件名称
		ActionContext.getContext().put("fileBusiTp", TDataDictConst.BUSI_CD_PAYFOR);//业务代码
		ActionContext.getContext().put("sameFileMap", sameFileMap);// 相同文件信息
		ActionContext.getContext().put("errListMap", errListMap);// 错误信息
		ActionContext.getContext().put("oneTxnErrListMap", oneTxnErrListMap);// 错误信息
		return null;
	}

	@Override
	public void setBusi_CD() {
		setBusiCd(TDataDictConst.BUSI_CD_PAYFOR);
	}

	@Override
	public void setMchnt_Cd(String mchntCdStr) {
		setMchntCd(mchntCdStr);
	}

	@Override
	public void setTOperatorInfFromPage(TOperatorInf tOperatorInfFromPage) {
		settOperatorInf(tOperatorInfFromPage);
	}

	@Override
	public void setTInsInfFromPage(TInsMchntInf tInsInfFromPage) {
		settInsMchntInf(tInsInfFromPage);
	}
}
