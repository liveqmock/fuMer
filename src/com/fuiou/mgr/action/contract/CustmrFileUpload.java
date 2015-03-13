package com.fuiou.mgr.action.contract;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.BaseAction;
import com.fuiou.mgr.adapter.treaty.TreatyAdapter;
import com.fuiou.mgr.adapter.treaty.TreatyInterface;
import com.fuiou.mgr.bean.access.FileAccess;

public class CustmrFileUpload extends BaseAction{
	private static final long serialVersionUID = -6509503025976083614L;
	private static Logger logger = LoggerFactory.getLogger(CustmrFileUpload.class);
	/**文件*/
	private File upload;
	/** 文件名称 */
	private String uploadFileName;
	/** 文件类型 */
	private String uploadContentType;
	private List<String> messages;
	
	//信息
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public File getUpload() {
		return upload;
	}
	public void setUpload(File upload) {
		this.upload = upload;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getUploadContentType() {
		return uploadContentType;
	}
	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}
	public String batCustmr(){
		return "batCustmr";
	}
	
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	/**
	 * 客户协议库批量上传
	 * @return
	 */
	public String uploadFile(){
		//操作员
		String riskLevel = MemcacheUtil.getRiskLevel(tOperatorInf.getMCHNT_CD());
		if(CustmrBusiContractUtil.LOW_RISK.equals(riskLevel)){
			messages = Arrays.asList(new String[]{"低风险商户无需导入协议库"});
		}else{
			FileAccess fileAccess=new FileAccess();
			fileAccess.setFile(upload);
			fileAccess.setFileName(uploadFileName);
			fileAccess.setBusiCd(TDataDictConst.CUSTMR_TREATY);
			fileAccess.setMchntCd(tOperatorInf.getMCHNT_CD());
			fileAccess.setOprUsrId(tOperatorInf.getLOGIN_ID());
			//获取接入的适配器
			TreatyInterface accessAdapter = TreatyAdapter.getAccessAdapter(TDataDictConst.CUSTMR_TREATY);
			//解析文件并入库
			Map<String, Object> resultMap = accessAdapter.analysisTxn(fileAccess);
			messages= (List<String>) resultMap.get("errorStr");
		}
		return "message";
	}
}
