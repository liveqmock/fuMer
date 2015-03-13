package com.fuiou.mgr.ftp;

//明细错误原因
public class RowColumnError{
    private String errCode;//行错误号
    private String errMemo;//行错误描述

    public String getErrCode(){
        return errCode;
    }

    public void setErrCode(String errCode){
        this.errCode = errCode;
    }

    public String getErrMemo(){
        return errMemo;
    }

    public void setErrMemo(String errMemo){
        this.errMemo = errMemo;
    }

}
