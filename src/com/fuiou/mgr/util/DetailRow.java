package com.fuiou.mgr.util;

/**
 * 明细行错误信息
 * 
 * zx
 * 
 */
public class DetailRow{
    private int rowNo; // 明细行号
    private String line; // 明细行
    private String errMsg; // 错误信息

    public int getRowNo(){
        return rowNo;
    }

    public void setRowNo(int rowNo){
        this.rowNo = rowNo;
    }

    public String getLine(){
        return line;
    }

    public void setLine(String line){
        this.line = line;
    }

    public String getErrMsg(){
        return errMsg;
    }

    public void setErrMsg(String errMsg){
        this.errMsg = errMsg;
    }
}
