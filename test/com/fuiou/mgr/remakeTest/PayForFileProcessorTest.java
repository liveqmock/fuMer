package com.fuiou.mgr.remakeTest;

import java.io.File;


import org.junit.Test;

import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.access.PayForInfAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;

public class PayForFileProcessorTest {
	/**
	 * WEB方式批量上传
	 */
	@Test
	public void payForFileProcessor_WEB(){
		SystemParams.getBankMap();
		SystemParams.getCardBinMap();
		SystemParams.getTPmsBankInfDftMap();
		// 设置测试数据
		// 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
		// 业务类型
		String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
		// 文件
		File file = new File("D:/Excel/代收/AP01_20111214_0003.csv");
		// 文件名
		String fileName = "AP01_20111214_0003.csv";
		// 商户号
		String mchntCd = "0002900F0011398";
		// 交易数据类型
		String txnDataType = "FILE";
		// 接入对象(文件)
		FileAccess accessBean = new FileAccess();
		accessBean.setBusiCd(busiCd);
		accessBean.setTxnInfSource(srcModuleCd);
		accessBean.setFile(file);
		accessBean.setFileName(fileName);
		/////////////////////////////////////////////////////////////////////////////////
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();
		System.out.println(busiBean);
		System.out.println(fileError);
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
		
		// 调用交易数据、文件处理器，对交易数据入库、交易文件保存等操作。
		TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
		txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
	}
	/**
	 * FTP方式上传
	 */
	@Test
	public void payForFileProcessor_FTP(){
		SystemParams.getBankMap();
		SystemParams.getCardBinMap();
		SystemParams.getTPmsBankInfDftMap();
		// 设置测试数据
		// 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_FTP;
		// 业务类型
		String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
		// 文件
		File file = new File("D:\\fuiou\\ftpRootDir\\00011200M000000\\upload\\AP01_20110720_0005.txt");
		// 文件名
		String fileName = "AP01_20110720_0005.txt";
		// 商户号
		String mchntCd = "0002900F0011398";
		// 交易数据类型
		String txnDataType = "FILE";
		// 接入对象(文件)
		FileAccess accessBean = new FileAccess();
		accessBean.setBusiCd(busiCd);
		accessBean.setTxnInfSource(srcModuleCd);
		accessBean.setFile(file);
		accessBean.setFileName(fileName);
		/////////////////////////////////////////////////////////////////////////////////
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();
		System.out.println(busiBean);
		System.out.println(fileError);
		// 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
		
		// 调用交易数据、文件处理器，对交易数据入库、交易文件保存等操作。
		TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
		txnDbService.dataProcess(busiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
	}
	/**
	 * 单笔
	 */
	@Test
	public void payForDataProcessor(){
		SystemParams.getBankMap();
		SystemParams.getCardBinMap();
		SystemParams.getTPmsBankInfDftMap();
		// 设置测试数据
		// 文件来源
		String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
		// 业务类型
		String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
		// 商户号
		String mchntCd = "00011200M000000";
		// 交易数据类型
		String txnDataType = "DATA";
		// 接入对象(文件)
		PayForInfAccess accessBean = new PayForInfAccess();
		accessBean.setBankCd("0102");
		accessBean.setBankAccount("4270191111111111");
		accessBean.setAccountName("李四");
		accessBean.setAmount("2.00");
		accessBean.setEnpSeriaNo("2");
		accessBean.setMemo("测试");
		accessBean.setMobile("13922222222");
		accessBean.setBankNam("");
		accessBean.setCityCode("1410");
		accessBean.setMchntCd(mchntCd);
		accessBean.setOprUsrId("cxw1");
		accessBean.setBusiCd(busiCd);
		/////////////////////////////////////////////////////////////////////////////////
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
		TxnInfBean txnInfBean = accessAdapter.analysisTxn(mchntCd, accessBean, srcModuleCd, txnDataType);
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd, txnDataType);
		BusiBean busiBean = busiAdapter.getBusiBean();
		FileError fileError = busiAdapter.getFileError();
		System.out.println(busiBean);
		System.out.println(fileError);
		// 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
		
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
		
		
		// 调用交易数据、文件处理器，对交易数据入库、交易文件保存等操作。
		TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
		txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,false);
	}
}
