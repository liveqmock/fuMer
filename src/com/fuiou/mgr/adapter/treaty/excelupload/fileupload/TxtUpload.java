package com.fuiou.mgr.adapter.treaty.excelupload.fileupload;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.AccessBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mgr.adapter.treaty.excelupload.TreatyFileUploadInterface;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.util.StringUtil;

public class TxtUpload implements TreatyFileUploadInterface{
	private static Logger logger = LoggerFactory.getLogger(TxtUpload.class);
	private List<String> strings=null;
	@Override
	public List<String> analysisTxn(AccessBean file) {
		TDataDictService tDataDictService = new TDataDictService();
		// TODO Auto-generated method stub
		FileAccess fileaccess=(FileAccess)file;
		logger.debug("开始处理文件,文件名为:"+fileaccess.getFileName());
		strings=new ArrayList<String>();
		List<String> timpStrings;
		try {
			timpStrings = FileUtils.readLines(fileaccess.getFile(), TDataDictConst.FILE_CODE);
			String trimedString = null;
			for (int i = 0; i < timpStrings.size(); i++) {
				// 将文件去掉空白行
				if (StringUtil.isEmpty(timpStrings.get(i))) {
					continue;
				}
				trimedString = timpStrings.get(i).trim().replaceAll("\r", "");
				trimedString = trimedString.replaceAll("\n", "");
				strings.add(trimedString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(TDataDictConst.FILE_READ_EP
					+ " "
					+ tDataDictService.selectTDataDictByClassAndKey(
							TDataDictConst.RET_CODE_SYS,
							TDataDictConst.FILE_READ_EP) + fileaccess.getFileName());
			e.printStackTrace();
			e.printStackTrace();
		}
		return strings;
	}

}
