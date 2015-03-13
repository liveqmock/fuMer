package com.fuiou.mgr.checkout.verify;

import java.util.ArrayList;

import com.fuiou.mgr.util.DetailRow;

/**
 * 账户验证文件错误信息
 * zx
 *
 */
public class VerificationFileErrMsg {
	private String fileNameErrMsg;   // 文件名错误信息
	private String sumInfErrMsg;     // 汇总行错误信息
	private ArrayList<DetailRow> detInfErrList; // 明细行错误信息
	
	public String getFileNameErrMsg() {
		return fileNameErrMsg;
	}
	public void setFileNameErrMsg(String fileNameErrMsg) {
		this.fileNameErrMsg = fileNameErrMsg;
	}
	public String getSunInfErrMsg() {
		return sumInfErrMsg;
	}
	public void setSunInfErrMsg(String sunInfErrMsg) {
		this.sumInfErrMsg = sunInfErrMsg;
	}
	public ArrayList<DetailRow> getDetInfErrList() {
		return detInfErrList;
	}
	public void setDetInfErrList(ArrayList<DetailRow> detInfErrList) {
		this.detInfErrList = detInfErrList;
	}
}
