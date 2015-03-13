package com.fuiou.mgr.adapter;

import java.io.File;

import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;

/**
 * 接入适配器
 * yangliehui
 *
 */
public abstract class FileAccessAdapter implements AccessAdapter {
	protected String mchntCd;			// 商户号
	protected String txnInfSource;		// 交易信息来源：FTP；WEB；HTTP...
	protected String txnDataType;		// 交易信息类型：FILE；DATA...
	/**
	 * 解析交易
	 */
	@Override
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource,String txnDataType) {
		this.mchntCd = mchntCd;
		this.txnInfSource = txnInfSource;
		this.txnDataType = txnDataType;
		return null;
	}
}
