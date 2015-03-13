package com.fuiou.mgr.adapter.verify;

import java.io.File;
import java.io.IOException;
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
import com.fuiou.mgr.bean.access.VerifyInfAccess;
import com.fuiou.mgr.bean.convert.VerifyTxnInfBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.VerifyTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.util.StringUtil;
/**
 * 验证接入适配器 AC01
 * yangliehui
 *
 */
public class VerifyAccessAdapter extends FileAccessAdapter {
	private static Logger logger = LoggerFactory.getLogger(VerifyAccessAdapter.class);
	private String busiCd = "YZ01";

	@Override
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource,String txnDataType) {
		VerifyTxnInfBean verifyTxnInfBean = new VerifyTxnInfBean();
		VerifyTxnInfSumBean VerifyTxnInfSumBean = verifyTxnInfBean.getVerifyTxnInfSumBean();
		List<VerifyTxnInfDetailRowBean> verifyTxnInfDetailRowBeanList = verifyTxnInfBean.getVerifyTxnInfDetailRowBeanList();
		super.analysisTxn(mchntCd, accessBean, txnInfSource,txnDataType);
		// 基本信息
		verifyTxnInfBean.setMchntCd(mchntCd);
		verifyTxnInfBean.setTxnInfType(busiCd);
		verifyTxnInfBean.setOprUsrId(accessBean.getOprUsrId());
		if("FILE".equals(txnDataType)){
			// 文件类型的交易，如：批量上传，FTP...
			FileAccess fileAccess = (FileAccess)accessBean;
			verifyTxnInfBean.setFileName(fileAccess.getFileName());
			verifyTxnInfBean.setFile(fileAccess.getFile());
			File file = fileAccess.getFile();
			// 解析文件
			List<String> strings = null;
			//获取文件的后缀名
	        String fileSuffix=fileAccess.getFileName().substring(fileAccess.getFileName().lastIndexOf(".")+1);
	        if("txt".equals(fileSuffix)){
				 try{
					 strings = FileUtils.readLines(file, TDataDictConst.FILE_CODE);
			     }catch(IOException e){
			         logger.error("file io error", e);
			         return verifyTxnInfBean;
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
	        	 return verifyTxnInfBean;
	        }
	        // 循环List中的文件信息
	        int actualRowNum = 0;
	        String trimedString = null;
	        for (int i = 0; i < strings.size(); i++){
	        	actualRowNum ++;
	        	 // 将文件去掉空白行
	            if(StringUtil.isEmpty(strings.get(i))){
	            	continue;
	            }
	            trimedString = strings.get(i).trim().replaceAll("\r", "");
	            trimedString = trimedString.replaceAll("\n", "");
	            if (i==0) {
	            	// 首行汇总行
	            	String[] items = trimedString.split(TDataDictConst.FILE_CONTENT_APART, 5);
	            	if (items.length>0) VerifyTxnInfSumBean.setMchntCd(items[0].trim());
	            	if (items.length>1) VerifyTxnInfSumBean.setBusiCd(items[1].trim());
	            	if (items.length>2) VerifyTxnInfSumBean.setTxnDate(items[2].trim());
	            	if (items.length>3) VerifyTxnInfSumBean.setSeriaNo(items[3].trim());
	            	if (items.length>4) VerifyTxnInfSumBean.setSumDetails(items[4].trim());
	            	VerifyTxnInfSumBean.setSumRow(trimedString);
	            	continue;
	            }
	            if (txnInfSource.equalsIgnoreCase(TDataDictConst.SRC_MODULE_CD_FTP) && i==strings.size()-1) {
	            	continue;
	            }
	            // 中间行
	            VerifyTxnInfDetailRowBean verifyTxnInfDetailRowBean = new VerifyTxnInfDetailRowBean();
	            String[] items = trimedString.split(TDataDictConst.FILE_CONTENT_APART, 6);
	            if (items.length>0) verifyTxnInfDetailRowBean.setDetailSeriaNo(items[0].trim());
	            if (items.length>1) verifyTxnInfDetailRowBean.setBankCd(items[1].trim());
	            if (items.length>2) verifyTxnInfDetailRowBean.setBankAccount(items[2].trim());
	            if (items.length>3) verifyTxnInfDetailRowBean.setAccountName(items[3].trim());
	            if (items.length>4) verifyTxnInfDetailRowBean.setCertificateTp(items[4].trim());
	            if (items.length>5) verifyTxnInfDetailRowBean.setCertificateNo(items[5].trim());
	            verifyTxnInfDetailRowBean.setLineStr(trimedString);
	            verifyTxnInfDetailRowBean.setActualRowNum(actualRowNum);
	            // 封转信息
				verifyTxnInfDetailRowBeanList.add(verifyTxnInfDetailRowBean);
	        }
	    }else if("DATA".equals(txnDataType)){
			// 数据类型的交易，如：单笔...
			VerifyInfAccess verifyInfAccess=(VerifyInfAccess)accessBean;
			// 封转中间对象 汇总
			VerifyTxnInfSumBean.setBusiCd(verifyInfAccess.getBusiCd()!=null?verifyInfAccess.getBusiCd().trim():null);
			VerifyTxnInfSumBean.setMchntCd(verifyInfAccess.getMchntCd()!=null?verifyInfAccess.getMchntCd().trim():null);
			// 交易信息 明细 
			VerifyTxnInfDetailRowBean verifyTxnInfDetailRowBean = new VerifyTxnInfDetailRowBean();
			verifyTxnInfDetailRowBean.setAccountName(verifyInfAccess.getAccntnm()!=null?verifyInfAccess.getAccntnm().trim():null);
			verifyTxnInfDetailRowBean.setBankAccount(verifyInfAccess.getAccntno()!=null?verifyInfAccess.getAccntno().trim():null);
			verifyTxnInfDetailRowBean.setBankCd(verifyInfAccess.getBankno()!=null?verifyInfAccess.getBankno().trim():null);
			verifyTxnInfDetailRowBean.setCertificateNo(verifyInfAccess.getCertno()!=null?verifyInfAccess.getCertno().trim():null);
			verifyTxnInfDetailRowBean.setCertificateTp(verifyInfAccess.getCerttp()!=null?verifyInfAccess.getCerttp().trim():null);
			verifyTxnInfDetailRowBean.setMerdt(verifyInfAccess.getMerdt()!=null?verifyInfAccess.getMerdt().trim():null);
			verifyTxnInfDetailRowBean.setOrderno(verifyInfAccess.getOrderno()!=null?verifyInfAccess.getOrderno().trim():null);
			// 封装信息
			verifyTxnInfDetailRowBeanList.add(verifyTxnInfDetailRowBean);
		}
		return verifyTxnInfBean;
	}

}
