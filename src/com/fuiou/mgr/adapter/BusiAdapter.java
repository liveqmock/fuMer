package com.fuiou.mgr.adapter;

import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.convert.TxnInfBean;

public interface BusiAdapter {
	/**
	 * 解析交易数据
	 * @param mchntCd 		商户号
	 * @param txnInfBean 	中间对象
	 * @param txnInfSource	交易信息来源：WEB；FTP；HTTP...
	 * @param txnDataType	交易信息类型：FILE；DATA
	 * @return				中间对象
	 */
	public void analysisTxnInfBean(String mchntCd,TxnInfBean txnInfBean,String txnInfSource,String txnDataType);
	
	public BusiBean getBusiBean();
	
	public FileError getFileError();
}
