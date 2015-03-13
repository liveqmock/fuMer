package com.fuiou.mgr.action.verify;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.CorrectDetInf;
import com.fuiou.mer.model.FileNameInf;
import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.service.TStlDtStService;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.business.VerifyBusiBean;
import com.fuiou.mgr.bean.business.VerifyBusiDetailRowBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.checkout.verify.VerificationFileValidator;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.fuiou.mgr.util.DetailRow;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings(value ={ "rawtypes","unchecked","unused"})
public class VerifyFileUpload extends ActionSupport {
	
    private static final long serialVersionUID = -1227785286187571424L;
    private static Logger logger = LoggerFactory.getLogger(VerifyFileUpload.class);
	/** 保存路径 */
    private String savePath;
	private FileNameInf fileNameInf;
	private File verifyFile;
	private String verifyFileContentType;
	private String verifyFileFileName;
	
    private HashMap fNMap;
	private HttpServletRequest request; 
	VerificationFileValidator validator = new VerificationFileValidator();
	   /** 文件内容 */
    private TDataDictService tDataDictService = new TDataDictService();
	private TFileInfService tFileInfService = new TFileInfService();
    private TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
    /** 错误信息 */
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	/** 错误信息集合 */
	private List<TFileErrInf> errListMap = new ArrayList<TFileErrInf>();
	private List<DetailRow> detailRowBean = new ArrayList<DetailRow>();
	private CheckOutBase checkOutBase =  new CheckOutBase();
	/** 相同文件信息 */
	private HashMap<String, String> sameFileMap = new HashMap<String, String>();
	public LinkedHashMap<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(LinkedHashMap<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	public File getVerifyFile() {
		return verifyFile;
	}

	public void setVerifyFile(File verifyFile) {
		this.verifyFile = verifyFile;
	}

	public String getVerifyFileContentType() {
		return verifyFileContentType;
	}

	public void setVerifyFileContentType(String verifyFileContentType) {
		this.verifyFileContentType = verifyFileContentType;
	}

	public String getVerifyFileFileName() {
		return verifyFileFileName;
	}

	public void setVerifyFileFileName(String verifyFileFileName) {
		this.verifyFileFileName = verifyFileFileName;
	}

	@Override
	/**
	 * 1.验证文件名，错误返回页面
	 * 2.验证文件内容，根据checkOut_result处理
	 * 		0：文件无格式错误，订单信息入库，
	 * 			保存文件，文件信息入库，返回上传成功
	 * 		1：汇总信息行有误，返回错误信息
	 * 		2：明细行有误，返回错误信息List
	 * 		3：文件内容为空，返回错误信息
	 * 		4：系统异常错误，返回错误信息
	 */
	public String execute() throws Exception {
	    request = (HttpServletRequest)ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
        if((verifyFile == null) || !verifyFile.exists() || verifyFile.length() == 0){
            logger.error("上传文件为空");
            setActionContext(null, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_CONTENT), null,
                    null, null, null);
            return ERROR;
        }
	
     // 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		String mchntCd = tOperatorInf.getMCHNT_CD();
		TFileInf f = tFileInfService.selectByMchntCdAndFileName(mchntCd, verifyFileFileName.substring(0,verifyFileFileName.indexOf(".")));
		if(f!=null){
			 ActionContext.getContext().put("errMsg", "文件"+verifyFileFileName+"重复上传");
	        return ERROR;
		}
		// 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
		// 业务类型
		String busiCd = TDataDictConst.BUSI_CD_VERIFY;
		// 交易数据类型
		String txnDataType = "FILE";
		// 接入对象(文件)
		FileAccess accessBean = new FileAccess();
		accessBean.setBusiCd(busiCd);
		accessBean.setTxnInfSource(srcModuleCd);
		accessBean.setFile(verifyFile);
		accessBean.setFileName(verifyFileFileName);
		accessBean.setMchntCd(mchntCd);
		accessBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();
		
		VerifyTxnInfBean verifyTxnInfBean = (VerifyTxnInfBean)txnInfBean;
		int rowCount = verifyTxnInfBean.getVerifyTxnInfDetailRowBeanList().size();
		int errCount = 0;
		
		List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
		if(fileRowErrorList != null){
			errCount = fileRowErrorList.size();
			for(int i = 0;i<fileRowErrorList.size();i++){
				DetailRow detailRow = new DetailRow();
				
				FileRowError fileRowError = fileRowErrorList.get(i);
				int rowNo = fileRowError.getRowNo();
				String line = fileRowError.getRow();
				detailRow.setRowNo(rowNo);
				detailRow.setLine(line);
				
				String msgInf = "";
				for(RowColumnError rowColumnError:fileRowError.getColumnErrors()){
					msgInf += rowColumnError.getErrMemo()+";";
				}
				detailRow.setErrMsg(msgInf);
				detailRowBean.add(detailRow);
			}
		}
		int corCount = rowCount - errCount;
		if(fileError.getFileSumError().getErrCode() != null||fileError.getFileSumError().getErrMemo() != null || fileError.getFileSumError().getRow() != null){
			logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo()+verifyFileFileName);
			if(fileRowErrorList != null){
				setActionContext(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_SAVE_ER),"存在不正确记录",detailRowBean,rowCount,corCount,errCount);
				request.setAttribute("fileName", verifyFileFileName);
				return SUCCESS;
			}
			setActionContext(fileError.getFileSumError().getErrMemo(),null,null,null,null,null);
			request.setAttribute("fileName",verifyFileFileName);
			return ERROR;
		}
		
		// 调用交易数据、文件处理器，对交易数据入库、交易文件保存等操作。
		VerifyBusiBean verifyBusiBean = (VerifyBusiBean)busiBean;
		List<VerifyBusiDetailRowBean> verifyBusiDetailRowBeanList = verifyBusiBean.getVerifyFileBusiDetailRowBeanList();
	
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
			
			// 组合业务对象
			PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
			BusiBean packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
			packBusiBean.setOprUsrId(tOperatorInf.getLOGIN_ID());
			
			TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
			txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
			
			List<FileRowError> fileRowOperateErrors = txnDbService.getFileRowOperateErrors();
			if(fileRowOperateErrors.size() != 0){
				for(int i = 0;i<fileRowOperateErrors.size();i++){
					DetailRow detailRow = new DetailRow();
					
					FileRowError fileRowError = fileRowOperateErrors.get(i);
					int rowNo = fileRowError.getRowNo();
					String line = fileRowError.getRow();
					detailRow.setRowNo(rowNo);
					detailRow.setLine(line);
					
					String msgInf = "";
					for(RowColumnError rowColumnError:fileRowError.getColumnErrors()){
						msgInf += rowColumnError.getErrMemo()+";";
					}
					detailRow.setErrMsg(msgInf);
					detailRowBean.add(detailRow);
				}
			}
			
			FileError fileErrorFromDbService = txnDbService.getFileError();
			if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
				setActionContext(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_SAVE_ER),null,detailRowBean,rowCount,0,errCount);
			}else{
				if(detailRowBean.size()>0){
					setActionContext(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED),null,detailRowBean,rowCount,corCount,errCount);
				}else{
					setActionContext(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED),null,null,rowCount,corCount,errCount);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setActionContext(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_SAVE_ER),null,null,rowCount,corCount,errCount);
 			request.setAttribute("fileName", verifyFileFileName);
 			return ERROR;
		}
		// 查询是否有相同文件
//		checkOutBase.selectUniformFile(sameFileMap, mchntCd, 0, verifyBusiDetailRowBeanList.size(),TDataDictConst.BUSI_CD_VERIFY);
		request.setAttribute("fileName", verifyFileFileName);
		//修改交易的状态为商户已确认
		int i=tFileInfService.updateTFileInfAndTApsTxnLog(TDataDictConst.FILE_ST_MCHNT_CONFIRM, tOperatorInf.getMCHNT_CD(), verifyFileFileName.substring(0,verifyFileFileName.lastIndexOf(".")), TDataDictConst.FILE_ST_MCHNT_INIT);
		if(i<=0){
			setActionContext("修改交易状态失败",null,null,null,null,null);
			request.setAttribute("fileName",verifyFileFileName);
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * 设置action返回信息
	 * @param msg 上传结果
	 * @param errMsg 错误信息
	 * @param errList 错误行列表
	 * @param rowCnt 文件总行数
	 * @param corCnt 验证正确行数
	 * @param errCnt 验证错误行数
	 */
    private void setActionContext(String msg,String errMsg, List errList, 
			Integer rowCnt, Integer corCnt, Integer errCnt) {
		if(msg != null) {
			request.setAttribute("message",msg);
		}
		if(errMsg != null) {
			request.setAttribute("errMsg",errMsg);
		}
		if(errList != null) {
			request.setAttribute("errList",errList);
		}
		if(rowCnt != null) {
			request.setAttribute("rowCount",rowCnt);
		}
		if(corCnt != null) {
			request.setAttribute("corCount",corCnt);
		}
		if(errCnt != null) {
			request.setAttribute("errCount",errCnt);
		}
	}
}
