package com.fuiou.mgr.ftp;

import java.util.List;

//拒绝文件信息
public class FileError{
	//错误汇总信息
    private FileSumError fileSumError;
    //明细错误信息
    private List<FileRowError> fileRowErrors;

    public FileSumError getFileSumError(){
        return fileSumError;
    }

    public void setFileSumError(FileSumError fileSumError){
        this.fileSumError = fileSumError;
    }

    public List<FileRowError> getFileRowErrors(){
        return fileRowErrors;
    }

    public void setFileRowErrors(List<FileRowError> fileRowErrors){
        this.fileRowErrors = fileRowErrors;
    }

}
