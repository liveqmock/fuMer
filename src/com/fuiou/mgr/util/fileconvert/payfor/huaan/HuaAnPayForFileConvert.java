package com.fuiou.mgr.util.fileconvert.payfor.huaan;

import java.io.File;
import java.util.Map;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.util.fileconvert.payfor.PayForFileFormatConvert;

public class HuaAnPayForFileConvert {
	public Map<String, String> doConvert(AccessBean convertFile,String mchntCd,TOperatorInf tOperatorInfFromPage,TInsMchntInf tInsInfFromPage){
		FileAccess fileAccess=(FileAccess) convertFile;
		PayForFileFormatConvert fileFormatConvert = new HuaAnPayForFileFormat();
		fileFormatConvert.setMchnt_Cd(mchntCd);									//设置商户号
		fileFormatConvert.setTOperatorInfFromPage(tOperatorInfFromPage);
		fileFormatConvert.setTInsInfFromPage(tInsInfFromPage);
		Map<String, String> map = fileFormatConvert.handlerFile(fileAccess);	//操作文件格式转换
		return map;
	}
}
