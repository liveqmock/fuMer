package com.fuiou.mgr.bean.access;

import java.io.File;

/**
 * 文件接入对象
 * yangliehui
 *
 */
public class FileAccess extends AccessBean{
	private File file;			// 文件
	private String fileName;	// 文件名称
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
