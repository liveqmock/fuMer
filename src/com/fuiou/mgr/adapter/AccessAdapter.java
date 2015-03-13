package com.fuiou.mgr.adapter;

import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;


public interface AccessAdapter {
	/**
	 * 解析交易数据
	 * @param mchntCd 		商户号
	 * @param accessBean 	接入对象
	 * @param txnInfSource	交易信息来源：WEB；FTP；HTTP...
	 * @param txnDataType	交易信息类型：FILE；DATA
	 * @return				中间对象
	 */
	TxnInfBean analysisTxn(String mchntCd,AccessBean accessBean,String txnInfSource,String txnDataType);
}
