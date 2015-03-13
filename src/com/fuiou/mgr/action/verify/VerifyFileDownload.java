package com.fuiou.mgr.action.verify;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
/**
 * 账户验证文件下载
 * zx
 *
 */
public class VerifyFileDownload extends ActionSupport {
	/**
     * 
     */
    private static final long serialVersionUID = 8272501173134297794L;
    private static Logger logger = LoggerFactory.getLogger(VerifyFileDownload.class);
	String fileName;
	String savePath;
	String fileSffx;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getFileSffx() {
		return fileSffx;
	}

	public void setFileSffx(String fileSffx) {
		this.fileSffx = fileSffx;
	}
	public InputStream getDownloadFile() throws FileNotFoundException {
		String file = getSavePath() + getFileName() + getFileSffx();
		setFileName(getFileName() + getFileSffx());
		logger.info("下载文件：" + file);
		return new FileInputStream(file);
	}
	
	@Override
	public String execute() throws Exception {
		
		
		
		return SUCCESS;
	}
}
