package com.fuiou.mgr.adapter.incomefor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.FileAccessAdapter;
import com.fuiou.mgr.adapter.payfor.fileuploadtype.ExcelCSVUpload;
import com.fuiou.mgr.adapter.payfor.fileuploadtype.ExcelXLSUpload;
import com.fuiou.mgr.adapter.payfor.fileuploadtype.ExcelXLSXUpload;
import com.fuiou.mgr.adapter.payfor.fileuploadtype.IFileUploadType;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.access.InComeForInfAccess;
import com.fuiou.mgr.bean.convert.InComeForTxnInfBean;
import com.fuiou.mgr.bean.convert.InComeForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.InComeForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.util.StringUtil;
/**
 * 代收接入适配器 AC01
 * yangliehui
 *
 */
public class InComeForAccessAdapter extends FileAccessAdapter {
	private static Logger logger = LoggerFactory.getLogger(InComeForAccessAdapter.class);
	private String busiCd = "AC01";

	@Override
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource,String txnDataType) {
		InComeForTxnInfBean inComeForTxnInfBean = new InComeForTxnInfBean();
		InComeForTxnInfSumBean inComeForTxnInfSumBean = inComeForTxnInfBean.getInComeForTxnInfSumBean();
		List<InComeForTxnInfDetailRowBean> inComeForTxnInfDetailRowBeanList = inComeForTxnInfBean.getInComeForTxnInfDetailRowBeanList();
		super.analysisTxn(mchntCd, accessBean, txnInfSource,txnDataType);
		// 基本信息
		inComeForTxnInfBean.setMchntCd(mchntCd);
		inComeForTxnInfBean.setTxnInfType(busiCd);
		inComeForTxnInfBean.setOprUsrId(accessBean.getOprUsrId());
		if("DATA".equals(txnDataType)){
			// 数据类型的交易，如：单笔...
			InComeForInfAccess inComeForInfAccess = (InComeForInfAccess)accessBean;
			// 封转中间对象
			inComeForTxnInfSumBean.setBusiCd(inComeForInfAccess.getBusiCd());
			inComeForTxnInfSumBean.setMchntCd(inComeForInfAccess.getMchntCd());
			// 交易信息
			InComeForTxnInfDetailRowBean inComeForTxnInfDetailRowBean = new InComeForTxnInfDetailRowBean();
			inComeForTxnInfDetailRowBean.setAccountName(inComeForInfAccess.getAccountName()!=null?inComeForInfAccess.getAccountName().trim():null);
			inComeForTxnInfDetailRowBean.setBankAccount(inComeForInfAccess.getBankAccount()!=null?inComeForInfAccess.getBankAccount().trim():null);
			inComeForTxnInfDetailRowBean.setBankCd(inComeForInfAccess.getBankCd()!=null?inComeForInfAccess.getBankCd().trim():null);
			inComeForTxnInfDetailRowBean.setDetailAmt(inComeForInfAccess.getAmount()!=null?inComeForInfAccess.getAmount().trim():null);
			inComeForTxnInfDetailRowBean.setEnpSeriaNo(inComeForInfAccess.getEnpSeriaNo()!=null?inComeForInfAccess.getEnpSeriaNo().trim():null);
			inComeForTxnInfDetailRowBean.setMemo(inComeForInfAccess.getMemo()!=null?inComeForInfAccess.getMemo().trim():null);
			inComeForTxnInfDetailRowBean.setMobile(inComeForInfAccess.getMobile()!=null?inComeForInfAccess.getMobile().trim():null);
			inComeForTxnInfDetailRowBean.setMerdt(inComeForInfAccess.getMerdt()!=null?inComeForInfAccess.getMerdt().trim():null);
			inComeForTxnInfDetailRowBean.setOrderno(inComeForInfAccess.getOrderno()!=null?inComeForInfAccess.getOrderno().trim():null);
			inComeForTxnInfDetailRowBean.setCertificateTp(inComeForInfAccess.getCertificateTp()!=null?inComeForInfAccess.getCertificateTp().trim():null);
			inComeForTxnInfDetailRowBean.setCertificateNo(inComeForInfAccess.getCertificateNo()!=null?inComeForInfAccess.getCertificateNo().trim():null);
			// 封装信息
			inComeForTxnInfDetailRowBeanList.add(inComeForTxnInfDetailRowBean);
		}else if("FILE".equals(txnDataType)){
			// 文件类型的交易，如：批量上传，FTP...
			FileAccess fileAccess = (FileAccess)accessBean;
			inComeForTxnInfBean.setFileName(fileAccess.getFileName());
			inComeForTxnInfBean.setFile(fileAccess.getFile());
			File file = fileAccess.getFile();
			//获取文件的后缀名
	        String fileSuffix=fileAccess.getFileName().substring(fileAccess.getFileName().lastIndexOf(".")+1);
			// 解析文件
			List<String> strings = null;
			if("txt".equals(fileSuffix)){
				 try{
			         strings = FileUtils.readLines(file, TDataDictConst.FILE_CODE);
			     }catch(IOException e){
			         logger.error("file io error", e);
			         return inComeForTxnInfBean;
			     }
			}
			else if("xls".equals(fileSuffix)){
	        	IFileUploadType fileUpload=new ExcelXLSUpload();
	        	strings=fileUpload.analysisTxn(fileAccess);
	        }else if("xlsx".equals(fileSuffix)){
	        	IFileUploadType fileUpload=new ExcelXLSXUpload();
	        	strings=fileUpload.analysisTxn(fileAccess);
	        }else if("csv".equals(fileSuffix)){
	        	IFileUploadType fileUpload=new ExcelCSVUpload();
	        	strings=fileUpload.analysisTxn(fileAccess);
	        }else {
	        	 logger.error(TDataDictConst.FILE_POSTFIX_ER+" 文件："+file.getName()+"的类型不正确");
	        	 return inComeForTxnInfBean;
	        }

	        // 循环List中的文件信息
	        int actualRowNum = 0;
	        String trimedString = null;
	        for (int i = 0; i < strings.size(); i++){
	        	actualRowNum ++;
	        	if(StringUtil.isEmpty(strings.get(i))){
		            	continue;
		        }
	        	trimedString = strings.get(i).trim().replaceAll("\r", "");
	        	trimedString = trimedString.replaceAll("\n", "");
	            // 将文件去掉空白行
	           
	            if (i==0) {
	            	// 首行汇总行
	            	String[] items = trimedString.split(TDataDictConst.FILE_CONTENT_APART, 6);
	            	if (items.length>0) inComeForTxnInfSumBean.setMchntCd(items[0].trim());
	            	if (items.length>1) inComeForTxnInfSumBean.setBusiCd(items[1].trim());
	            	if (items.length>2) inComeForTxnInfSumBean.setTxnDate(items[2].trim());
	            	if (items.length>3) inComeForTxnInfSumBean.setSeriaNo(items[3].trim());
	            	if (items.length>4) inComeForTxnInfSumBean.setSumDetails(items[4].trim());
	            	if (items.length>5) inComeForTxnInfSumBean.setSumAmt(items[5].trim());
	            	inComeForTxnInfSumBean.setSumRow(trimedString);
	            	continue;
	            }
	            if (txnInfSource.equalsIgnoreCase(TDataDictConst.SRC_MODULE_CD_FTP) && i==strings.size()-1) {
	            	continue;
	            }
	            // 中间行
	            InComeForTxnInfDetailRowBean inComeForTxnInfDetailRowBean = new InComeForTxnInfDetailRowBean();
	            String[] items = trimedString.split(TDataDictConst.FILE_CONTENT_APART);
	            if (items.length>0) inComeForTxnInfDetailRowBean.setDetailSeriaNo(items[0].trim());
	            if (items.length>1) inComeForTxnInfDetailRowBean.setBankCd(items[1].trim());
	            if (items.length>2) inComeForTxnInfDetailRowBean.setBankAccount(items[2].trim());
	            if (items.length>3) inComeForTxnInfDetailRowBean.setAccountName(items[3].trim());
	            if (items.length>4) inComeForTxnInfDetailRowBean.setDetailAmt(items[4].trim());
	            if (items.length>5) inComeForTxnInfDetailRowBean.setEnpSeriaNo(items[5].trim());
	            if (items.length>6) inComeForTxnInfDetailRowBean.setMemo(items[6].trim());
	            if (items.length>7) inComeForTxnInfDetailRowBean.setMobile(items[7].trim());
	            if (items.length>8) inComeForTxnInfDetailRowBean.setCertificateTp(items[8].trim());//V2.04新增证件类型
	            if (items.length>9) inComeForTxnInfDetailRowBean.setCertificateNo(items[9].trim());//V2.04新增证件号
	            inComeForTxnInfDetailRowBean.setLineStr(trimedString);
	            inComeForTxnInfDetailRowBean.setActualRowNum(actualRowNum);
	            // 封转信息
				inComeForTxnInfDetailRowBeanList.add(inComeForTxnInfDetailRowBean);
	        }
		}
		return inComeForTxnInfBean;
	}

}
