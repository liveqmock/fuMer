package com.fuiou.mgr.adapter.treaty.excelupload.fileupload;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mgr.adapter.payfor.fileuploadtype.Utils;
import com.fuiou.mgr.adapter.treaty.excelupload.TreatyFileUploadInterface;

public class ExcelXSLUpload implements TreatyFileUploadInterface{
	private static Logger logger = LoggerFactory.getLogger(ExcelXSLUpload.class);
	public List<String> analysisTxn(AccessBean accessBean){
		FileAccess fileaccess=(FileAccess)accessBean;
		logger.debug("开始处理文件,文件名为:"+fileaccess.getFileName());
		TDataDictService tDataDictService = new TDataDictService();
		File file=fileaccess.getFile();
		if(file==null){
			logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP)+fileaccess.getFileName());
			return null;
		}
		List<String> liststr=new ArrayList<String>();
		try {	
			// 转换poi对象
			HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
			// 获取第一个sheet
			HSSFSheet firstSheet = book.getSheetAt(0);
			if(firstSheet==null){
				return null;
			}
			int sumRows = firstSheet.getLastRowNum();// 总行数
			if (sumRows == 0) {
				return null;
			}
			if(TDataDictConst.CUSTMR_TREATY.equals(fileaccess.getBusiCd())){
				for(int i=1;i<=sumRows;i++){
					String rowValue = "";
					HSSFRow row=firstSheet.getRow(i);
					String BUSI_CD=Utils.getCellValue(row.getCell(0));//业务类型
					String USER_NM=Utils.getCellValue(row.getCell(1));//姓名
					String MOBILE_NO=Utils.getCellValue(row.getCell(2));//手机号码
					String CREDT_TP=Utils.getCellValue(row.getCell(3));//证件类型
					String CREDT_NO=Utils.getCellValue(row.getCell(4));//证件号码
					String ACNT_NO=Utils.getCellValue(row.getCell(5));//账号
					String ACNT_TP=Utils.getCellValue(row.getCell(6));//账户属性
					String BANK_CD=Utils.getCellValue(row.getCell(7));//行别
					String IS_CALLBACK=Utils.getCellValue(row.getCell(8));//是否需要语音回拨
					String RESERVED1=Utils.getCellValue(row.getCell(9));//备注
					rowValue=BUSI_CD+"|"+USER_NM+"|"+MOBILE_NO+"|"+CREDT_TP+"|"+CREDT_NO+"|"+ACNT_NO+"|"+ACNT_TP+"|"+BANK_CD+"|"+IS_CALLBACK+"|"+RESERVED1;
					liststr.add(rowValue);
				}
				return liststr;
			}else{
				
			}
		} catch (Exception e) {
			logger.error(TDataDictConst.FILE_READ_EP
					+ " "
					+ tDataDictService.selectTDataDictByClassAndKey(
							TDataDictConst.RET_CODE_SYS,
							TDataDictConst.FILE_READ_EP) + fileaccess.getFileName());
			e.printStackTrace();
		}
		return liststr;
	}
}
