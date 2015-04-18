package com.fuiou.mgr.adapter.treaty;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.adapter.treaty.excelupload.TreatyFileUploadInterface;
import com.fuiou.mgr.adapter.treaty.excelupload.fileupload.ExcelXSLUpload;
import com.fuiou.mgr.adapter.treaty.excelupload.fileupload.ExcelXSLXUpload;
import com.fuiou.mgr.adapter.treaty.excelupload.fileupload.TxtUpload;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mer.model.TCustmrBusi;

public class CustmrTreatyAdapter implements TreatyInterface {
	private Logger logger = LoggerFactory.getLogger(CustmrTreatyAdapter.class);
	private TDataDictService tDataDictService = new TDataDictService();
	//存放结果集的集合包含错误信息和商户信息
	private Map<String,Object> resultMap=new HashMap<String, Object>();
	//存放協議庫信息的集合
	private List<TCustmrBusi> tCustmrBusis=new ArrayList<TCustmrBusi>();
	
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();
	List<String> errorResult=null;
	@Override
	public Map<String, Object> analysisTxn(AccessBean bean) {
		FileAccess fileAccess=(FileAccess)bean;
		String fileName=fileAccess.getFileName();
		File file=fileAccess.getFile();
		errorResult = new ArrayList<String>();
		if(file==null){
			logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP)+fileName);
			errorResult.add(TDataDictConst.FILE_NAME_CONTENT+"|"+"文件内容为空");
			return getResultMap(errorResult, tCustmrBusis);
		}
		if(file.length()>TDataDictConst.FILE_SIZE){
			logger.error(TDataDictConst.FIlE_SIZE_ER+" 文件"+fileName+":超出文件大小限制");
			errorResult.add(TDataDictConst.FIlE_SIZE_ER+"|"+" 文件"+fileName+":超出文件大小限制");
			return getResultMap(errorResult, tCustmrBusis);
		}
		if(fileName==null||"".equals(fileName)){
			logger.error(TDataDictConst.FILE_NULL_ER+"未获取文件名");
			errorResult.add(TDataDictConst.FILE_NULL_ER+"|"+"未获取文件名");
			return getResultMap(errorResult, tCustmrBusis);
		}
		//获取文件的后缀名
        String fileSuffix=fileAccess.getFileName().substring(fileAccess.getFileName().lastIndexOf(".")+1);
		List<String> strings=null;//文件内容集合
		if("xls".equals(fileSuffix)){
			TreatyFileUploadInterface fileUpload=new ExcelXSLUpload();
        	strings=fileUpload.analysisTxn(fileAccess);
        }else if("xlsx".equals(fileSuffix)){
        	TreatyFileUploadInterface fileUpload=new ExcelXSLXUpload();
        	strings=fileUpload.analysisTxn(fileAccess);
        }else if ("txt".equals(fileSuffix)) {
			TreatyFileUploadInterface fileUpload = new TxtUpload();
			strings = fileUpload.analysisTxn(fileAccess);
		}else {
        	 logger.error(TDataDictConst.FILE_POSTFIX_ER+" 文件："+fileAccess.getFileName()+"的类型不正确");
        	 errorResult.add(TDataDictConst.FILE_POSTFIX_ER+"|"+"文件："+fileAccess.getFileName()+"的类型不正确");
        	 return getResultMap(errorResult, tCustmrBusis);
        }
		resultMap=new Excelvilidate().vilidateCustmr(strings, fileAccess,fileSuffix);
		errorResult=(List<String>) resultMap.get("errorStr");
		if(errorResult!=null&&errorResult.size()>0){
			return resultMap;
		}
		tCustmrBusis=(List<TCustmrBusi>) resultMap.get("tCustmrBusis");
		//入库
		this.insertToDatabase();
		return getResultMap(errorResult, tCustmrBusis);
	}
	public void insertToDatabase() {
		// 入库
		try {
			logger.error("开始入库");
			int i =0 ;
			for(TCustmrBusi custmrBusi:tCustmrBusis){
				i += custmrBusiService.saveCustmrBusiInfos(custmrBusi);
				CustmrBusiContractUtil.saveOperatLog(custmrBusi);
			}
			if (i > 0) {
				logger.error(TDataDictConst.SUCCEED + "|" + "入库成功");
				errorResult.add(TDataDictConst.UNSUCCESSFUL + "|" + "操作成功");
			} else {
				logger.error(TDataDictConst.UNSUCCESSFUL + "|" + "入库失败");
				errorResult.add(TDataDictConst.UNSUCCESSFUL + "|" + "入库失败");
			}
		} catch (Exception e) {
//			errorResult.add(TDataDictConst.UNSUCCESSFUL + "|" + "入库失败");
//			logger.error(TDataDictConst.UNSUCCESSFUL + "|" + "入库失败");
			e.printStackTrace();
		}
		resultMap = getResultMap(errorResult, tCustmrBusis);
	}
	public Map<String, Object> getResultMap(List<String> errorStr,List<TCustmrBusi> tCustmrBusis){
		resultMap.put("errorStr", errorStr);
		resultMap.put("tCustmrBusis", tCustmrBusis);
		return resultMap;
	}
}
