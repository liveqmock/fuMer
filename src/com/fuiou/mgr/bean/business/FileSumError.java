package com.fuiou.mgr.bean.business;

public class FileSumError{
    private String errCode;	//错误码
    private String errMemo;	//错误描述
    private String row; //原汇总行

    public String getRow() {
		return row;
	}

	public void setRow(String row) {
	    if(this.row == null){
	        this.row = row;
	    }
	}

	public String getErrCode(){
        return errCode;
    }

	/**
	 * 汇总错误代码在本对象上只能被赋一次值
	 * @param errCode
	 */
    public void setErrCode(String errCode){
        if(this.errCode == null){
            this.errCode = errCode;
        }
    }

    public String getErrMemo(){
        return errMemo;
    }

    /**
     * 汇总错误描述在本对象上只能被赋一次值
     * @param errMemo
     */
    public void setErrMemo(String errMemo){
        if(this.errMemo == null){
            this.errMemo = errMemo;
        }
    }

}