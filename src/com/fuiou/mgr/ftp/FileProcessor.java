package com.fuiou.mgr.ftp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;

/**
 * Richard Xiong
 * 文件处理对象，用于校验，处理和入库文件信息和文件明细信息
 */
public abstract class FileProcessor {
    private static Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    /** 被处理的文件 */
	protected File file;
	/** 被处理文件的业务类型 */
	protected String busiCd;
	/** 文件名 */
	protected String fileName;
	/** 商户代码 */
	protected String mchntCd;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMchntCd() {
		return mchntCd;
	}

	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}

	/**
	 * 处理入口方法
	 */
    public void process(){
        logger.info("begin process upload file: " + file.getName());
        mchntCd = FileUtil.getMchntCdByFilePath(file);
        // 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_FTP;
		// 交易数据类型
		String txnDataType = "FILE";
		// 接入对象(文件)
		FileAccess accessBean = new FileAccess();
		accessBean.setBusiCd(busiCd);
		accessBean.setTxnInfSource(srcModuleCd);
		accessBean.setFile(file);
		accessBean.setFileName(file.getName());
		/////////////////////////////////////////////////////////////////////////////////
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();

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
		//判余额，并预授权交易
		tradeCalculate.checkAccountBalance(packBusiBean, fileError,srcModuleCd);
		
		// 调用交易数据、文件处理器，对交易数据入库、交易文件保存等操作。
		TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
		txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
        logger.info("end process upload file: " + file.getName());
    }
    
    public abstract void setBusiCd();
}
