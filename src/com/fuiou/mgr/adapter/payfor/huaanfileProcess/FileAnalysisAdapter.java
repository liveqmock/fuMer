package com.fuiou.mgr.adapter.payfor.huaanfileProcess;

import java.util.ArrayList;
import java.util.List;

import com.fuiou.mgr.adapter.payfor.huaanfileProcess.excel.ExcelAnalysis;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;

public abstract class FileAnalysisAdapter implements IFileAnalysis{
	/**
	 * 解析交易
	 */
	public static IFileAnalysis analysisTxn(String fileName){
		String postfixName=fileName.substring(fileName.lastIndexOf(".")+1);
		if("xls".equals(postfixName)||"xlsx".equals(postfixName)||"csv".equals(postfixName)){
			return new ExcelAnalysis();
		}else if("doc".equals(postfixName)){
			return null;
		}else{
			return null;
		}
	}
}
