package com.fuiou.mgr.adapter.payfor.fileuploadtype;

import java.util.List;

import com.fuiou.mgr.bean.access.FileAccess;

/**
 * 文件上传的类型（有xls格式、xlsx格式、csv格式）
 * dev_db
 *
 */
public interface IFileUploadType {
	public List<String> analysisTxn(FileAccess file);
}
