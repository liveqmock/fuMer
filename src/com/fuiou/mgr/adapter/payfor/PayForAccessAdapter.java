package com.fuiou.mgr.adapter.payfor;

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
import com.fuiou.mgr.bean.access.PayForInfAccess;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.util.StringUtil;
/**
 * 代付接入适配器
 * yangliehui
 *
 */
public class PayForAccessAdapter extends FileAccessAdapter {

	private static Logger logger = LoggerFactory.getLogger(PayForAccessAdapter.class);
	private String busiCd;
	
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource,String txnDataType) {
		busiCd = accessBean.getBusiCd();
		PayForTxnInfBean payForTxnInfBean=new PayForTxnInfBean();//付款文件对象
		PayForTxnInfSumBean payForTxnInfSumBean=payForTxnInfBean.getPayForTxnInfSumBean();//付款文件汇总行对象
		List<PayForTxnInfDetailRowBean> payForTxnInfDetailRowBeanList=payForTxnInfBean.getPayForTxnInfDetailRowBeanList();
		super.analysisTxn(mchntCd, accessBean, txnInfSource,txnDataType);
		// 基本信息
		payForTxnInfBean.setMchntCd(mchntCd);
		payForTxnInfBean.setTxnInfType(busiCd);		
		payForTxnInfBean.setOprUsrId(accessBean.getOprUsrId());
		if("DATA".equals(txnDataType)){
			// 数据类型的交易，如：单笔...
			PayForInfAccess payForInfAccess=(PayForInfAccess)accessBean;
			// 封转中间对象 汇总
			payForTxnInfSumBean.setBusiCd(payForInfAccess.getBusiCd());
			payForTxnInfSumBean.setMchntCd(payForInfAccess.getMchntCd());
			// 交易信息 明细 
			PayForTxnInfDetailRowBean payForTxnInfDetailRowBean=new PayForTxnInfDetailRowBean();
			payForTxnInfDetailRowBean.setCity(payForInfAccess.getCityCode()!=null?payForInfAccess.getCityCode().trim():null); //收款人开户地市代码
			payForTxnInfDetailRowBean.setBankCd(payForInfAccess.getBankCd()!=null?payForInfAccess.getBankCd().trim():null); //收款人开户行行别
			payForTxnInfDetailRowBean.setBankNo(payForInfAccess.getBankNam()!=null?payForInfAccess.getBankNam().trim():null); //收款人开户行支行信息
			payForTxnInfDetailRowBean.setBankAccount(payForInfAccess.getBankAccount()!=null?payForInfAccess.getBankAccount().trim():null); //收款人银行账户
			payForTxnInfDetailRowBean.setAccountName(payForInfAccess.getAccountName()!=null?payForInfAccess.getAccountName().trim():null); //户名
			payForTxnInfDetailRowBean.setDetailAmt(payForInfAccess.getAmount()!=null?payForInfAccess.getAmount().trim():null); //金额
			payForTxnInfDetailRowBean.setEnpSeriaNo(payForInfAccess.getEnpSeriaNo()!=null?payForInfAccess.getEnpSeriaNo().trim():null); //企业流水号
			payForTxnInfDetailRowBean.setMemo(payForInfAccess.getMemo()!=null?payForInfAccess.getMemo().trim():null); //备注
			payForTxnInfDetailRowBean.setMobile(payForInfAccess.getMobile()!=null?payForInfAccess.getMobile().trim():null); //手机号
			payForTxnInfDetailRowBean.setMerdt(payForInfAccess.getMerdt()!=null?payForInfAccess.getMerdt().trim():null);
			payForTxnInfDetailRowBean.setOrderno(payForInfAccess.getOrderno()!=null?payForInfAccess.getOrderno().trim():null);
			payForTxnInfDetailRowBean.setFlag(payForInfAccess.isFlag());
			// 封装信息
			payForTxnInfDetailRowBeanList.add(payForTxnInfDetailRowBean);
		}else if("FILE".equals(txnDataType)){
			// 文件类型的交易，如：批量上传，FTP...
			FileAccess fileAccess = (FileAccess)accessBean;
			fileAccess.setMchntCd(mchntCd);
			payForTxnInfBean.setFileName(fileAccess.getFileName());
			payForTxnInfBean.setFile(fileAccess.getFile());
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
	 	            return payForTxnInfBean;
	 	        }
	        }else if("xls".equals(fileSuffix)){
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
	        	 return payForTxnInfBean;
	        }
	        if(strings==null||strings.size()==0){
	        	 return payForTxnInfBean;
	        }
	        // 循环List中的文件信息
	        boolean hasSetSumRowBean = false;
	        int actualRowNum = 0;
	        String trimedString = null ;
	        for (int i = 0; i < strings.size(); i++){
	        	actualRowNum ++;
	        	// 将文件去掉空白行
	            if(StringUtil.isEmpty(strings.get(i))){
	            	continue;
	            }
	            trimedString = strings.get(i).trim().replaceAll("\r", "");
	            trimedString = trimedString.replaceAll("\n", "");
	            if (!hasSetSumRowBean) {
	                hasSetSumRowBean = true;
	            	// 首行汇总行
	            	String[] items = trimedString.split(TDataDictConst.FILE_CONTENT_APART, 6);
	            	if (items.length>0) payForTxnInfSumBean.setMchntCd(items[0].trim()); //商户号
	            	if (items.length>1) payForTxnInfSumBean.setBusiCd(items[1].trim()); //业务代码
	            	if (items.length>2) payForTxnInfSumBean.setTxnDate(items[2].trim()); //交易日期
	            	if (items.length>3) payForTxnInfSumBean.setDayNo(items[3].trim()); //当日序号
	            	if (items.length>4) payForTxnInfSumBean.setSumDetails(items[4].trim()); //明细数目
	            	if (items.length>5) payForTxnInfSumBean.setSumAmt(items[5].trim()); //汇总金额
	            	payForTxnInfSumBean.setSumRow(trimedString); //汇总行源记录
	            	continue;
	            }
	            if (txnInfSource.equalsIgnoreCase(TDataDictConst.SRC_MODULE_CD_FTP) && i==strings.size()-1) {
	            	continue;
	            }
	            // 中间行
	            PayForTxnInfDetailRowBean payForTxnInfDetailRowBean = new PayForTxnInfDetailRowBean();
	            String[] items = trimedString.split(TDataDictConst.FILE_CONTENT_APART, 10);
	            if (items.length>0) payForTxnInfDetailRowBean.setDetailSeriaNo(items[0].trim());	//明细序号
	            if (items.length>1) payForTxnInfDetailRowBean.setBankCd(items[1].trim());	////收款人开户行行别
	            /////收款人开户行省份
	            if (items.length>2) payForTxnInfDetailRowBean.setCity(items[2].trim());	//收款人开户地市代码
	            if (items.length>3) payForTxnInfDetailRowBean.setBankNo(items[3].trim());	//收款人开户行支行信息
	            if (items.length>4) payForTxnInfDetailRowBean.setBankAccount(items[4].trim());// 账号
	            if (items.length>5) payForTxnInfDetailRowBean.setAccountName(items[5].trim());//户名
	            if (items.length>6) payForTxnInfDetailRowBean.setDetailAmt(items[6].trim()); //金额
	            if (items.length>7) payForTxnInfDetailRowBean.setEnpSeriaNo(items[7].trim());//企业流水号
	            if (items.length>8) payForTxnInfDetailRowBean.setMemo(items[8].trim());//备注
	            if (items.length>9) payForTxnInfDetailRowBean.setMobile(items[9].trim()); //手机号
	            payForTxnInfDetailRowBean.setLineStr(trimedString); //明细行源记录
	            payForTxnInfDetailRowBean.setActualRowNum(actualRowNum);
	            // 封装信息
	            payForTxnInfDetailRowBeanList.add(payForTxnInfDetailRowBean);
	        }    
		}
		return payForTxnInfBean;
		}
}
