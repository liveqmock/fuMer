package com.fuiou.mgr.busi.dbservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.dao.DaoConfig;
import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileErrInfService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TInsBankAcntService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TMchntKeyService;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.InComeForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.ibatis.dao.client.DaoManager;

/**
 * 业务处理器
 * yangliehui
 *
 */
public abstract class TxnDbService {
	private static Logger logger = LoggerFactory.getLogger(TxnDbService.class);
	// 业务校验失败的错误记录
	protected List<FileRowError> fileRowOperateErrors = new ArrayList<FileRowError>();
	// 1笔交易（付款、代付、重发、退款的1笔收）的错误记录
	protected List<FileRowError> oneTxnErrors = new ArrayList<FileRowError>();
	protected BusiBean busiBean;
	protected FileError fileError;
	protected TxnInfBean txnInfBean;
	protected String busiCd;
	protected String srcModuleCd;
	protected String txnDataType;
	protected TFileInf tFileInf = new TFileInf();
	protected List<TApsTxnLog> tApsTxnLogs = new ArrayList<TApsTxnLog>();
	protected List<TFileErrInf> tFileErrInfs = new ArrayList<TFileErrInf>();
	protected TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
	protected TDataDictService dataDictService = new TDataDictService();
	protected TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	protected TInsBankAcntService tInsBankAcntService = new TInsBankAcntService();
	protected TDataDictService tDataDictService = new TDataDictService();
	protected Date currDbTime ;
	protected boolean feeSetHasErrors = false;
	protected boolean feeSetD4HasErrors = false;
	
	public FileError getFileError() {
		return this.fileError;
	}

	public List<FileRowError> getFileRowOperateErrors() {
		return fileRowOperateErrors;
	}

	public void setFileRowOperateErrors(List<FileRowError> fileRowOperateErrors) {
		this.fileRowOperateErrors = fileRowOperateErrors;
	}

	public List<FileRowError> getOneTxnErrors() {
		return oneTxnErrors;
	}

	public void setOneTxnErrors(List<FileRowError> oneTxnErrors) {
		this.oneTxnErrors = oneTxnErrors;
	}

	/**
	 * 获取文件汇总错误对象，如果没有被引用对象就创建
	 * @return
	 */
	public FileSumError getFileSumError(){
	    getFileError();
	    if(fileError.getFileSumError() == null){
	        FileSumError fileSumError = new FileSumError();
	        fileError.setFileSumError(fileSumError);
	    }
	    return fileError.getFileSumError();
	}
	public FileRowError getFileRowError(){
	    if(fileError.getFileRowErrors() == null){
	        List<FileRowError> fileRowErrors = new ArrayList<FileRowError>();
	        getFileError().setFileRowErrors(fileRowErrors);
	    }
        FileRowError fileRowError = new FileRowError();
        fileError.getFileRowErrors().add(fileRowError);
        return fileRowError;
	}
	
	
	/**数据操作入口
	 * 
	 * @param busiBean   业务对象
	 * @param fileError  错误对象
	 * @param txnInfBean 中间对象
	 * @param busiCd	 业务类型	：AC01；AP01；YZ01...
	 * @param srcModuleCd 数据来源：FTP；WEB；HTTP...
	 * @param txnDataType 交易数据类型：FILE；DATA...
	 * @param isCreateFile 是否创建文件
	 */
	public void dataProcess(BusiBean busiBean,FileError fileError,TxnInfBean txnInfBean,String busiCd,String srcModuleCd,String txnDataType,boolean isCreateFile){
		this.busiBean = busiBean;
		this.currDbTime = busiBean.getCurrDbTime();
		this.fileError = fileError;
		this.txnInfBean = txnInfBean;
		this.busiCd = busiCd;
		this.srcModuleCd = srcModuleCd;
		this.txnDataType = txnDataType;
		//基本格式校验
		boolean isReject = false;
		if(fileError.getFileRowErrors() != null && fileError.getFileRowErrors().size()>0){
			isReject = true;
		}
		if(fileError.getFileSumError()!= null && StringUtils.isNotEmpty(fileError.getFileSumError().getErrCode())){  
			isReject = true;
        } 
		if(isReject){
			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)){//FTP
				createRejectFile();
				moveToHistoryDir();
			}
			return ;  
		}
		//判断业务校验结果
		setOperationErrorInf();
		if(this.fileRowOperateErrors!=null&&this.fileRowOperateErrors.size()>0){//有业务校验失败的
			if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)){//FTP
				createRejectFile();
				moveToHistoryDir();
			}
			return ;
		}
		if("FILE".equals(txnDataType)){//如果是文件方式
			setTfileInf();//设置文件信息
			setTFileErrorInf();
		}
		setTApsTxnLog();
		insertToDb();//入库
		if("FILE".equals(txnDataType)&&isCreateFile){//如果是文件类型
			copyToWeb();
		}
		if(srcModuleCd.equals(TDataDictConst.SRC_MODULE_CD_FTP)){//FTP
			moveToHistoryDir();
		}
	}
	
	/**
	 * 封装至文件信息model
	 */
	public abstract void setTfileInf();
	
	/**
	 * 封装至文件日志model
	 */
	public abstract void setTApsTxnLog();
	
	/**
	 * 封装至文件错误信息model
	 */
	public abstract void setTFileErrorInf();
	
	/**
	 * 保存文件
	 */
	public abstract void copyToWeb();
	
	/**
	 * 确认业务校验是否全部失败
	 */
	public abstract void sureOperation();
	
	/**
	 * 业务校验错误的信息提示用户
	 */
	public abstract void setOperationErrorInf();
	
	
	/**
	 * 生成拒绝文件
	 */
	public void createRejectFile(){
		if (fileError == null) {
            logger.error("FileError为空");
            return;
        }
		if(null == txnInfBean.getFileName() || "".equals(txnInfBean.getFileName())){
			logger.error("FileName为空");
			return;
		}
		int idxFileFixes = txnInfBean.getFileName().indexOf('.');
        String fileName = txnInfBean.getFileName().substring(0, idxFileFixes);
        // 1.准备文件名，路径
        File merchantPath = txnInfBean.getFile().getParentFile().getParentFile();// 商户路径
        File path = new File(merchantPath, TDataDictConst.FTP_DIR_REJECT);
        // 2解析错误信息
        // 汇总行信息
        String sumErrCode = fileError.getFileSumError().getErrCode();// 错误返回码
        String sumErrMemo = fileError.getFileSumError().getErrMemo();// 错误说明
        String sumRow = fileError.getFileSumError().getRow();// 原汇总行

        // 3生成文件
        File f = new File(path, fileName + ".reject.txt");

        // 获得商户号，商户ID，通过商户ID获得商户密码，调用签名方法
        String pwd = MemcacheUtil.getMchntKey(txnInfBean.getMchntCd());
        if (pwd == null) {
            logger.error("商户密码为空");
            return;
        }
        logger.debug("开始生成拒绝文件：" + f.getName());

        // 5向拒绝文件写入数据
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), TDataDictConst.FILE_CODE));
            out.print((StringUtils.isEmpty(sumErrCode)?TDataDictConst.BUSI_ERROR:sumErrCode) + "|");
            out.print((StringUtils.isEmpty(sumErrCode)?"业务校验失败":sumErrMemo) + "|");
            if (StringUtils.isEmpty(sumRow)) {
            	if(TDataDictConst.BUSI_CD_PAYFOR.equals(txnInfBean.getTxnInfType())){
            		PayForTxnInfBean  payForTxnInfBean = (PayForTxnInfBean) txnInfBean;
            		sumRow = payForTxnInfBean.getPayForTxnInfSumBean().getSumRow();
            	}else if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(txnInfBean.getTxnInfType())){
            		InComeForTxnInfBean inComeForTxnInfBean = (InComeForTxnInfBean)txnInfBean;
            		sumRow = inComeForTxnInfBean.getInComeForTxnInfSumBean().getSumRow();
            	}
            } 
            out.println(sumRow);
            List<FileRowError> errors = new ArrayList<FileRowError>();
            List<FileRowError> fileRowErrors = fileError.getFileRowErrors();//业务校验错误
            if(fileRowErrors!=null&&fileRowErrors.size()>0){
            	errors.addAll(fileRowErrors);
            }
            if(fileRowOperateErrors!=null&&fileRowOperateErrors.size()>0){
            	 errors.addAll(fileRowOperateErrors);
            }
            if(oneTxnErrors!=null&&oneTxnErrors.size()>0){
           	 errors.addAll(oneTxnErrors);
           }
            if(errors != null && errors.size()>0){
                for (FileRowError fileRowError : errors) {
                    String rowNo = String.valueOf(fileRowError.getRowNo());// 错误明细行号
                    String row = fileRowError.getRow();// 原明细行
                    out.print(rowNo + "|");
                    List<RowColumnError> rowColumnErrors = fileRowError.getColumnErrors();
                    if(rowColumnErrors!=null&&rowColumnErrors.size()>0){
                    	for (RowColumnError rowColumnError : rowColumnErrors) {
                            String errCod = rowColumnError.getErrCode();
                            String errMem = rowColumnError.getErrMemo();
                            out.print(errCod + ":");
                            out.print(errMem + ";");
                        }
                    }
                    out.print("|");
                    if ("".equals(row) || row == null) {
                        out.println();
                    } else {
                        out.println(row);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("生成拒绝文件异常" + f.getName(), e);
            return;
        } finally {
            if(out != null){
                out.close();
            }
        }
        // 签名
        FileSigner.sign(f, txnInfBean.getMchntCd(), pwd);
	}
	
	/**
	 * 数据入库
	 */
	public void insertToDb(){
		if("FILE".equals(txnDataType)){
			if (tFileInf == null || tApsTxnLogs == null || tApsTxnLogs.size() == 0) {
				logger.error("汇总行信息，或正确信息集为空");
				return;
			}
			if (tFileErrInfs == null || tFileErrInfs.size() == 0) {
				logger.debug("汇总行信息，正确信息集入库开始");
				boolean r = this.insertInf(tFileInf, tApsTxnLogs);
				if (r) {
					logger.debug("汇总行信息，正确信息集入库完成");
				} else {
					logger.error("汇总行信息，正确信息集入库失败");
				}
				return ;
			} 
			logger.debug("汇总行信息，正确信息集，错误信息集入库开始");
			boolean r1 = this.insertInf(tFileInf, tApsTxnLogs, tFileErrInfs);
			if (r1) {
				logger.debug("汇总行信息，正确信息集，错误信息集入库完成");
			} else {
				logger.error("汇总行信息，正确信息集，错误信息集入库失败");
			}
		}else if("DATA".equals(txnDataType)){
			logger.debug("信息入库开始");
			boolean r = this.insertInf(tApsTxnLogs);
			if (r) {
				logger.debug("正确信息入库完成");
			} else {
				logger.error("正确信息入库失败");
			}
			return ;
		}
	}
	
	private boolean insertInf(List<TApsTxnLog> tApsTxnLogs) {
		boolean result = false;
		DaoManager daoManager = DaoConfig.getDaoManagerBatDb();
		try {
			// 开始事务
			daoManager.startTransaction();
			// 正确新下集入库
			for (int j = 0; j < tApsTxnLogs.size(); j++) {
				TApsTxnLog apsTxnLog = (TApsTxnLog) tApsTxnLogs.get(j);
				TApsTxnLogService tApsTxnLogService = new TApsTxnLogService(daoManager);
				tApsTxnLogService.insertSelective(apsTxnLog);
			}
			// 提交事务
			daoManager.commitTransaction();
			result = true;
		} catch (Exception e) {
			logger.error("commit error", e);
		} finally {
			daoManager.endTransaction();
		}
		return result;
	}
	
	/**
	 * 汇总行信息，正确信息集入库
	 * **/
	private boolean insertInf(TFileInf fileInf, List<TApsTxnLog> tApsTxnLogs) {
		boolean result = false;
		DaoManager daoManager = DaoConfig.getDaoManagerBatDb();
		try {
			// 开始事务
			daoManager.startTransaction();
			// 正确新下集入库
			for (int j = 0; j < tApsTxnLogs.size(); j++) {
				TApsTxnLog apsTxnLog = (TApsTxnLog) tApsTxnLogs.get(j);
				TApsTxnLogService tApsTxnLogService = new TApsTxnLogService(daoManager);
				tApsTxnLogService.insertSelective(apsTxnLog);
			}
			// 汇总行信息入库
			TFileInfService tFileInfService = new TFileInfService(daoManager);
			tFileInfService.insertSelective(fileInf);

			// 提交事务
			daoManager.commitTransaction();
			result = true;
		} catch (Exception e) {
			logger.error("commit error", e);
		} finally {
			daoManager.endTransaction();
		}
		return result;
	}

	/**
	 * 文件和明细信息入库
	 * @param fileInf
	 * @param tApsTxnLogs
	 * @param fileErrInfs
	 * @return
	 */
	private boolean insertInf(TFileInf fileInf, List<TApsTxnLog> tApsTxnLogs, List<TFileErrInf> fileErrInfs) {
		boolean result = false;
		DaoManager daoManager = DaoConfig.getDaoManagerBatDb();
		try {
			// 开始事务
			daoManager.startTransaction();
			// 错误信息集入库-FILE_ERR_INF |List<TFileErrInf> fileErrInfs
			// |TFileErrInfDAO fileErrInfDAO
			for (int i = 0; i < fileErrInfs.size(); i++) {
				TFileErrInf fileErrInf = (TFileErrInf) fileErrInfs.get(i);
				TFileErrInfService tFileErrInfService = new TFileErrInfService(daoManager);
				tFileErrInfService.insertSelective(fileErrInf);
			}
			// 正确信息集入库_APS_TXN_LOG | List<TApsTxnLog>
			// tApsTxnLogs|TApsTxnLogDAO tApsTxnLogDAO
			for (int j = 0; j < tApsTxnLogs.size(); j++) {
				TApsTxnLog apsTxnLog = (TApsTxnLog) tApsTxnLogs.get(j);
				TApsTxnLogService tApsTxnLogService = new TApsTxnLogService(daoManager);
				tApsTxnLogService.insertSelective(apsTxnLog);
			}
			// 3汇总行信息入库 T_FILE_INF |TFileInf fileInf |TFileInfDAO
			// tFileInfDAO
			TFileInfService tFileInfService = new TFileInfService(daoManager);
			tFileInfService.insertSelective(fileInf);
			// 提交事务
			daoManager.commitTransaction();
			result = true;
		} catch (Exception e) {
			logger.error("commit error", e);
		} finally {
			daoManager.endTransaction();
		}
		return result;
	}
	
	/**
	 * 移动文件至历史目录（FTP）
	 */
	public void moveToHistoryDir(){
		if (txnInfBean.getFile() == null) {
			logger.error("src file null");
			return;
		}
		File destDir = new File(txnInfBean.getFile().getParentFile().getParentFile(), TDataDictConst.FTP_DIR_UPLOAD_HIS);
		File destFile = new File(destDir, txnInfBean.getFile().getName());
		int i = 1;
		while (destFile.exists()) {
			i++;
			destFile = new File(destDir, txnInfBean.getFile().getName() + "." + String.valueOf(i));
		}
		try {
			FileUtils.moveFile(txnInfBean.getFile(), destFile);
		} catch (IOException e) {
			logger.error("move file failed", e);
		}
	}
}
