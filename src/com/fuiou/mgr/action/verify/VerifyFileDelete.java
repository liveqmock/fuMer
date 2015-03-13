package com.fuiou.mgr.action.verify;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class VerifyFileDelete extends ActionSupport {
	private static Logger logger = LoggerFactory.getLogger(VerifyFileDelete.class);
	private String fileName;
	private String fileBusiTp;
	private String message;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileBusiTp() {
		return fileBusiTp;
	}
	public void setFileBusiTp(String fileBusiTp) {
		this.fileBusiTp = fileBusiTp;
	}
	
	@Override
	public String execute() throws Exception {
		HttpServletRequest request = (HttpServletRequest)ActionContext
		.getContext().get(ServletActionContext.HTTP_REQUEST); 
		//商户信息
		TInsMchntInf tInsInf = (TInsMchntInf)ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		String merId = tInsInf.getMCHNT_CD();
		if((fileName == null || "".equals(fileName))
				|| (fileBusiTp == null || "".equals(fileBusiTp))) {
			logger.error("参数错误：" + "文件名：" + fileName +  "业务类型：" + fileBusiTp);
			message = "系统异常";
			request.setAttribute("message", message);
			return ERROR;
		}
		
		if(merId == null || "".equals(merId)) {
			logger.error("参数错误：" + "文件名：" + fileName +  "业务类型：" + fileBusiTp);
			message = "系统异常";
			request.setAttribute("message", message);
			return ERROR;
		}
		
		TFileInfService tFService = new TFileInfService();
		int ret = tFService.deleteByMchntCdAndFileNmAndFileStOrTApsTxnLog(merId, fileName);
		if(ret != 1) {
			message = "系统异常,文件删除失败";
			request.setAttribute("message", message);
		}
		message = "文件及相应记录删除成功！";
		request.setAttribute("message", message);
		return SUCCESS;
	}
}
