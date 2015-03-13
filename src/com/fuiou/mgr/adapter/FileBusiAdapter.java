package com.fuiou.mgr.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TMchntKeyService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.ftp.FileSigner;

/**
 * 业务适配器
 * yangliehui
 *
 */
public abstract class FileBusiAdapter implements BusiAdapter{
	private static final Logger logger = LoggerFactory.getLogger(FileBusiAdapter.class);
	
	protected String mchntCd;
	protected String txnInfSource;
	protected String txnDataType;
	protected TxnInfBean txnInfBean;//基本交易信息
	protected String[] fileNames;
	protected String fileName;
	protected String fileNameSuffix;
	
	protected FileError fileError;//错误文件信息，包含汇总错误、明细错误
    /** 文件汇总行 */
	protected String sumRow;
	/** ORM服务对象 */
	protected TDataDictService dataDictService = new TDataDictService();
	protected TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	protected Date currDbTime = tApsTxnLogService.getCurrDbTime();
	protected TFileInfService fileInfService = new TFileInfService();
	protected TDataDictService tDataDictService = new TDataDictService();
	
	@Override
	public FileError getFileError(){
	    if(fileError == null){
	        fileError = new FileError();
	    }
		return fileError;
	}
	
	/**
	 * 获取文件汇总错误对象，如果没有被引用对象就创建
	 * @return
	 */
	public FileSumError getFileSumError(){
	    getFileError();//获取fileError对象
	    if(fileError.getFileSumError() == null){
	        FileSumError fileSumError = new FileSumError();
	        fileError.setFileSumError(fileSumError);
	    }
	    return fileError.getFileSumError();
	}
	
	/**
     * 获取文件汇总错误对象，如果没有被引用对象就创建
     * @return
     */
	public FileRowError getFileRowError(){
	    FileSumError fileSumError = getFileSumError();
	    fileSumError.setErrCode(TDataDictConst.FIlE_ROW_FORMAT_ER);
	    fileSumError.setErrMemo(dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_ROW_FORMAT_ER));
	    fileSumError.setRow(sumRow);
	    if(fileError.getFileRowErrors() == null){
	        List<FileRowError> fileRowErrors = new ArrayList<FileRowError>();
	        getFileError().setFileRowErrors(fileRowErrors);
	    }
        FileRowError fileRowError = new FileRowError();
        fileError.getFileRowErrors().add(fileRowError);
        return fileRowError;
	}
	
	/**
     * 获取文件汇总错误对象，如果没有被引用对象就创建
     * @return
     */
	public RowColumnError getColumnError(int rowNo, String row){
	    FileRowError fileRowError = getFileRowError();//每一行的错误对象
	    fileRowError.setRow(row);
	    fileRowError.setRowNo(rowNo);
	    if(fileRowError.getColumnErrors() == null){
	        List<RowColumnError> columnErrors = new ArrayList<RowColumnError>();
	        fileRowError.setColumnErrors(columnErrors);
	    }
	    List<RowColumnError> columnErrors = fileRowError.getColumnErrors();
	    RowColumnError rowColumnError = new RowColumnError();
	    columnErrors.add(rowColumnError);
	    return rowColumnError;
	}

	@Override
	public void analysisTxnInfBean(String mchntCd, TxnInfBean txnInfBean, String txnInfSource, String txnDataType) {
		this.mchntCd = mchntCd;
		this.txnInfBean = txnInfBean;
		this.txnInfSource = txnInfSource;
		this.txnDataType = txnDataType;
		beanInit();//主要初始化交易信息
		// 调用校验器
		verify();
		getFileError();
	}
	
	/*
	 * 校验
	 */
	public void verify(){
		if("FILE".equals(txnDataType)){
			int idxFileFixes = txnInfBean.getFileName().indexOf('.');
			//校验文件格式、签名等基本信息
			if(idxFileFixes == -1){
				String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_FORMAT_ER);
				String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_FORMAT_ER);
				FileSumError fileSumError = getFileSumError();//获取总的错误对象
				fileSumError.setErrCode(TDataDictConst.FILE_FORMAT_ER);
				fileSumError.setErrMemo(memo);
	            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_FORMAT_ER + "]");
	            return;
			}
	        fileName = txnInfBean.getFileName().substring(0, idxFileFixes);
	        fileNameSuffix = txnInfBean.getFileName().substring(idxFileFixes);
	        fileNames = fileName.split(TDataDictConst.FILE_TITLE_APART, 3);
	        //校验文件命名格式
	        if(fileNames.length != 3){
				String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
				String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
				FileSumError fileSumError = getFileSumError();
				fileSumError.setErrCode(TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
				fileSumError.setErrMemo(memo);
	            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_DATE_FORMAT_ER + "]");
	            return;
			}
	        //校验文件大小
	        if(txnInfBean.getFile().length() > TDataDictConst.FILE_SIZE){ // 超过文件大小限制
	            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_SIZE_ER);
	            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_SIZE_ER);
	            FileSumError fileSumError = getFileSumError();
	            fileSumError.setErrCode(TDataDictConst.FIlE_SIZE_ER);
	            fileSumError.setErrMemo(memo);
	            logger.error("文件名[" + fileName + "]" + memoSys + "[" + TDataDictConst.FIlE_SIZE_ER + "]");
	            return;
	        }
	        // 文件名验证
			fileNameValidate();
			if(fileError != null){
	            return;
	        }
			if(TDataDictConst.SRC_MODULE_CD_FTP.equals(txnInfSource)){
				// 签名验证
				fileSign();
				if(fileError != null){
		            return;
		        }
			}
			// 解释时验证汇总行
			sumRowValidate();
			if(getFileSumError().getErrCode() != null || getFileSumError().getErrMemo() != null || getFileSumError().getRow() != null ){
	            return;
	        }
		}
		// 记录格式校验
		fileRowValidate();
		if("FILE".equals(txnDataType)){
			// 解析后验证汇总行
			fileSumValidate();
			if(getFileSumError().getErrCode() != null || getFileSumError().getErrMemo() != null || getFileSumError().getRow() != null ){
	            return;
	        }
		}
		// 4、业务校验，电子连行号或卡bin未找到做特殊处理
		operationValidate();
	}
	
	private void fileSign() {
		String key = MemcacheUtil.getMchntKey(mchntCd);
        if(null == key || !FileSigner.verifySign(txnInfBean.getFile(), mchntCd, key)){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_SIGNER_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_SIGNER_ER);
            FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FIlE_SIGNER_ER);
            fileSumError.setErrMemo(memo);
            logger.error("文件名[" + fileName + "]" + memoSys + "[" + TDataDictConst.FIlE_SIGNER_ER + "]");
        }
	}

	private void fileNameValidate() {
		if (null == txnInfBean.getFileName() || "".equals(txnInfBean.getFileName().trim())) {
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_NULL_ER);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_NULL_ER);
			FileSumError fileSumError = getFileSumError();
			fileSumError.setErrCode(TDataDictConst.FILE_NULL_ER);
			fileSumError.setErrMemo(memo);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NULL_ER + "]");
            return;
		}
		// 交易日期
        if(!FuMerUtil.date2String(currDbTime, "yyyyMMdd").equals(fileNames[1])){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
            FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
            fileSumError.setErrMemo(memo);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_DATE_FORMAT_ER + "]");
            return;
        }
        // 文件名重复验证
        if(null != fileInfService.selectByMchntCdAndFileName(mchntCd, fileName)){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_ER);
            FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FILE_NAME_ER);
            fileSumError.setErrMemo(memo);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_ER + "]");
            return;
        }
        //业务代码验证 
        if(!txnInfBean.getTxnInfType().equals(fileNames[0])){
            String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_BUSI_TP_ER);
            String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_BUSI_TP_ER);
            FileSumError fileSumError = getFileSumError();
            fileSumError.setErrCode(TDataDictConst.FILE_BUSI_TP_ER);
            fileSumError.setErrMemo(memo);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_BUSI_TP_ER + "]");
            return;
        }
        //当日序号为数字和字母,并且长度不大于30位
		if(!fileNames[2].matches("^[a-zA-z0-9]+$")||txnInfBean.getFileName().length()>30){//\\d{4}
			String memo = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_NAME_SEQ_ER);
			String memoSys = dataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_NAME_SEQ_ER);
			FileSumError fileSumError = getFileSumError();
			fileSumError.setErrCode(TDataDictConst.FILE_NAME_SEQ_ER);
			fileSumError.setErrMemo(memo);
            logger.error("文件名[" + fileName + "] " + memoSys + "[" + TDataDictConst.FILE_NAME_SEQ_ER + "]");
            return;
		}
	}
	
	/**
	 * 将基类类型转换为对应的业务类型
	 */
	protected abstract void beanInit();

	/**
	 * 解析时验证汇总行
	 */
	protected abstract void sumRowValidate();
	
	/**
	 * 记录格式校验
	 */
	protected abstract int fileRowValidate();
	
	/**
	 * 解析后验证汇总行
	 */
	protected abstract void fileSumValidate();
	
	/**
	 * 业务校验，电子连行号或卡bin未找到做特殊处理
	 */
	protected abstract void operationValidate();
}