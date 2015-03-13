package com.fuiou.mgr.action.incomefor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.InComeForBusiBean;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.opensymphony.xwork2.ActionContext;

/**
 * 代收文件上传
 * */
public class IncomeforFileUpload {
	private static final Logger logger = LoggerFactory.getLogger(IncomeforFileUpload.class);
	private File upload;
	/** 文件名称 */
	private String uploadFileName;
	/** 提示信息 */
	private String message = "";
	/** 错误信息 */
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	private List<String> errMsgs = new ArrayList<String>();

	/** 相同文件信息 */
	private HashMap<String, String> sameFileMap = new HashMap<String, String>();
	/** 错误信息集合 */
    private List<TFileErrInf> errListMap = new ArrayList<TFileErrInf>();
	/** 操作类型 */
	private String btnType;
	/** 文件内容 */
	private TDataDictService tDataDictService = new TDataDictService();
	private TFileInfService tFileInfService = new TFileInfService();
	private String isDisplayFeeMsg;

	public String getBtnType() {
		return btnType;
	}

	public void setBtnType(String btnType) {
		this.btnType = btnType;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getIsDisplayFeeMsg() {
		return isDisplayFeeMsg;
	}

	public void setIsDisplayFeeMsg(String isDisplayFeeMsg) {
		this.isDisplayFeeMsg = isDisplayFeeMsg;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 *            String 文件名，包含文件路径
	 * @return int 成功 >0；失败 -1
	 */
	private int deleteFile(String fileName) {
		int results = 0;
		File file = new File(fileName);
		if (file.exists()) {
			if (file.delete()) {
				results = results + 1;
			}
		}
		return results;
	}

	public String executeBtnType() throws Exception {
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		TFileInf fileInf = tFileInfService.selectByMchntCdAndFileName(tOperatorInf.getMCHNT_CD(), uploadFileName);
		if (null == fileInf) {
			errorMap.put(TDataDictConst.SELECT_DATA_NULL_ER, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SELECT_DATA_NULL_ER));
			logger.error(TDataDictConst.SELECT_DATA_NULL_ER + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.SELECT_DATA_NULL_ER) + uploadFileName);
			ActionContext.getContext().put("message", message);
			ActionContext.getContext().put("errorMap", errorMap);
			return "incomefor_error";
		}
		// ********文件状态是用户未确认
		if ("OK".equals(btnType)) {// 确认提交，并忽略错误记录，
			if (tFileInfService.updateTFileInfAndTApsTxnLog(TDataDictConst.FILE_ST_MCHNT_CONFIRM, tOperatorInf.getMCHNT_CD(), fileInf.getFILE_NM(), TDataDictConst.FILE_ST_MCHNT_INIT) > 0) {
				errorMap.put(TDataDictConst.SUCCEED, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + " 批量代收文件确认提交");
			} else {
				errorMap.put(TDataDictConst.SUCCEED, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + " 批量代收文件确认提交");
			}
			ActionContext.getContext().put("errorMap", errorMap);
			return "incomefor_error";
		}
		if ("NO".equals(btnType)) {// 取消，删除文件信息与记录
			// 删除文件
			if (tFileInfService.deleteByMchntCdAndFileNmAndFileStOrTApsTxnLog(tOperatorInf.getMCHNT_CD(),  fileInf.getFILE_NM()) > 0) {
				deleteFile(fileInf.getFILE_PATH() + fileInf.getFILE_NM()+fileInf.getFILE_NM_SFFX());
				errorMap.put(TDataDictConst.SUCCEED, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + "取消批量代收文件");
			} else {
				errorMap.put(TDataDictConst.UNSUCCESSFUL, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + "取消批量代收文件失败");
			}

			ActionContext.getContext().put("errorMap", errorMap);
			return "incomefor_error";
		}
		ActionContext.getContext().put("message", "系统错误!");
		return "incomefor_error";
	}

    
	public String executeInformForUpload() throws Exception {
        if(null == upload || !upload.exists() || upload.length() == 0){// 文件是否为空
            errorMap.put(TDataDictConst.FILE_NAME_CONTENT,
                    tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_CONTENT));
            logger.error(TDataDictConst.FILE_NAME_CONTENT + " "
                    + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_NAME_CONTENT) + upload);
            ActionContext.getContext().put("errorMap", errorMap);
            return "incomefor_error";
        }
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		String mchntCd = tOperatorInf.getMCHNT_CD();
		TFileInf f = tFileInfService.selectByMchntCdAndFileName(mchntCd, uploadFileName.substring(0,uploadFileName.indexOf(".")));
		if(f!=null){
			errorMap.put(TDataDictConst.FILE_NAME_ER, "文件"+uploadFileName+"重复上传");
			 ActionContext.getContext().put("errorMap", errorMap);
	            return "incomefor_error";
		}
		// 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
		// 业务类型
		String busiCd = TDataDictConst.BUSI_CD_INCOMEFOR;
		// 交易数据类型
		String txnDataType = "FILE";
		// 接入对象(文件)
		FileAccess accessBean = new FileAccess();
		accessBean.setBusiCd(busiCd);
		accessBean.setTxnInfSource(srcModuleCd);
		accessBean.setFile(upload);
		accessBean.setFileName(uploadFileName);
		accessBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
		accessBean.setMchntCd(mchntCd);
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();
		// 查看错误信息
		List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
		if(null != fileRowErrorList){
			for(int i = 0;i<fileRowErrorList.size();i++){
				FileRowError fileRowError = fileRowErrorList.get(i);
				List<RowColumnError> rowColumnErrorList =  fileRowError.getColumnErrors();
				for(RowColumnError rowColumnError:rowColumnErrorList){
					errMsgs.add("第"+fileRowError.getRowNo()+"行"+rowColumnError.getErrCode()+":"+rowColumnError.getErrMemo());
				}
			}
			ActionContext.getContext().put("errorList", errMsgs);
			return "incomefor_error";
		}
		
		if(fileError.getFileSumError().getErrCode() != null||fileError.getFileSumError().getErrMemo() != null || fileError.getFileSumError().getRow() != null){
			errorMap.put(fileError.getFileSumError().getErrCode(), fileError.getFileSumError().getErrMemo());
			logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo()+uploadFileName);
			ActionContext.getContext().put("errorMap", errorMap);
			return "incomefor_error";
		}
		
		// 调用交易数据、文件处理器，对交易数据入库、交易文件保存等操作。
		InComeForBusiBean inComeForBusiBean = (InComeForBusiBean)busiBean;
		List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = inComeForBusiBean.getInComeForFileBusiDetailRowBeanList();
		if(inComeForBusiDetailRowBeanList.size()>0){
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
				List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanListFee = calculateForTxnFeeAmtBean.getInComeForList();
				//判断计算路由的结果,如果有没有找到路由的则直接拒绝掉交易文件
				boolean flag = false;
				for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanListFee){
					FileRowError fileRowError = inComeForBusiDetailRowBean.getOperationBean().getFileRowErrro();
					if(fileRowError.getColumnErrors().size()>0){
						flag = true;
						for(RowColumnError error:fileRowError.getColumnErrors()){
							errMsgs.add("第"+fileRowError.getRowNo()+"行"+error.getErrCode()+":"+error.getErrMemo());
						}
					}
				}
				if(flag){
					ActionContext.getContext().put("errorList", errMsgs);
					return "incomefor_error";
				}
				String feeAmt = "0";
				String srcAmt = "0";
				String destAmt = "0";
				int feeAmtInt = 0;
				int srcAmtInt = 0;
				int destAmtInt = 0;
				
				// 查询出商户类型
				TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
				TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(forTxnFeeAmtBean.getMchntCd(), "1");
				String mchntTp = tInsMchntInf.getMCHNT_TP();
				isDisplayFeeMsg  = "1";
				if(null != tInsMchntInf){
					if(mchntTp.length()>=2){
						if("D4".equals(mchntTp.substring(0, 2))){
							isDisplayFeeMsg = "0";
						}
					}
				}
				OperationBean operationBean = null;
				for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanListFee){
					operationBean = inComeForBusiDetailRowBean.getOperationBean();
					//只有当目标金额大于手续费的时候才算
					if(operationBean.getDestTxnAmt() > operationBean.getTxnFeeAmt()){
						if(mchntTp.startsWith("D6") && operationBean.getTxnFeeAmtId() == null){	// 实时结算
							continue;
						}
						feeAmtInt += operationBean.getTxnFeeAmt();
						srcAmtInt += operationBean.getDestTxnAmt();
						destAmtInt += (operationBean.getDestTxnAmt()-operationBean.getTxnFeeAmt());
					}
				}
				
				feeAmt = FuMerUtil.formatFenToYuan(feeAmtInt);
				srcAmt = FuMerUtil.formatFenToYuan(srcAmtInt);
				destAmt = FuMerUtil.formatFenToYuan(destAmtInt);
				
				// 组合业务对象
				PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
				BusiBean packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
				packBusiBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
				TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
				txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
				
				double sumAmt = 0;
				for(InComeForBusiDetailRowBean inComeForBusiDetailRowBean:inComeForBusiDetailRowBeanList){
					sumAmt += inComeForBusiDetailRowBean.getDetailAmt();
				}
				List<FileRowError> fileRowOperateErrors = txnDbService.getFileRowOperateErrors();
				if(fileRowOperateErrors.size() != 0){
					for(int i = 0;i<fileRowOperateErrors.size();i++){
						FileRowError fileRowError = fileRowOperateErrors.get(i);
						List<RowColumnError> rowColumnErrorList =  fileRowError.getColumnErrors();
						for(RowColumnError rowColumnError:rowColumnErrorList){
							errMsgs.add("第"+fileRowError.getRowNo()+"行"+rowColumnError.getErrCode()+":"+rowColumnError.getErrMemo());
						}
					}
					ActionContext.getContext().put("errorList", errMsgs);
					return "incomefor_error";
				}
				
				FileError fileErrorFromDbService = txnDbService.getFileError();
				if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
					ActionContext.getContext().put("fileRows", 0);//校验明细
					ActionContext.getContext().put("fileAmt", 0.00);//校验金额
					ActionContext.getContext().put("message", "批量代收文件上传失败,业务校验错误!");
					errorMap.put(fileErrorFromDbService.getFileSumError().getErrCode(), fileErrorFromDbService.getFileSumError().getErrMemo());
					ActionContext.getContext().put("errorMap", errorMap);
					return "incomefor_error";
				}else{
					ActionContext.getContext().put("fileRows", inComeForBusiDetailRowBeanList.size());// 校验明细
					ActionContext.getContext().put("fileAmt", FuMerUtil.formatFenToYuan(sumAmt));// 校验金额
					ActionContext.getContext().put("feeAmt", feeAmt);
					ActionContext.getContext().put("srcAmt", srcAmt);
					ActionContext.getContext().put("destAmt", destAmt);
				}
			} catch (Exception e) {
				e.printStackTrace();
				errorMap.put(TDataDictConst.FILE_SAVE_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_SAVE_ER));
				logger.error(TDataDictConst.FILE_SAVE_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_SAVE_ER)+uploadFileName);
				ActionContext.getContext().put("errorMap", errorMap);
				return "incomefor_error";
			}
		}
		
		// 返回页面提示
		String forWord = "incomefor_succeed_error";
		ActionContext.getContext().put("fileNam", uploadFileName.substring(0, uploadFileName.indexOf(".")));// 文件名称
		ActionContext.getContext().put("fileBusiTp", TDataDictConst.BUSI_CD_INCOMEFOR);// 业务代码

		ActionContext.getContext().put("sameFileMap", sameFileMap);// 相同文件信息
		ActionContext.getContext().put("errListMap", errListMap);// 错误信息
		return forWord;
	}
}
