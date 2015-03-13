package com.fuiou.mgr.adapter.payfor.huaanfileProcess;

import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;

public interface IFileAnalysis {
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource, String txnDataType);
}
