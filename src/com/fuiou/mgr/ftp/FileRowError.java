package com.fuiou.mgr.ftp;

import java.util.List;

//明细错误信息
public class FileRowError{

    private int rowNo;// 行号
    private String row; // 原明细行
    private List<RowColumnError> columnErrors;// 明细错误原因

    public List<RowColumnError> getColumnErrors(){
        return columnErrors;
    }

    public void setColumnErrors(List<RowColumnError> columnErrors){
        this.columnErrors = columnErrors;
    }

    public String getRow(){
        return row;
    }

    public void setRow(String row){
        if(this.row == null){
            this.row = row;
        }
    }

    public int getRowNo(){
        return rowNo;
    }

    public void setRowNo(int rowNo){
        if(this.rowNo == 0){
            this.rowNo = rowNo;
        }
    }
}
