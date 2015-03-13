package com.fuiou.mgr.busi.dbservice;

import com.fuiou.mer.util.TDataDictConst;


/**
 * 业务处理器工厂类
 * yangliehui
 *
 */
public class TxnDbServiceFactory {
	public static TxnDbService getDataOperate(String busiCd) {
		if (busiCd.equals(TDataDictConst.BUSI_CD_INCOMEFOR)) {
			return new InComeForTxnDbService();
		} else if(busiCd.equals(TDataDictConst.BUSI_CD_PAYFOR)){
			return new PayForTxnDbService();
		} else if(busiCd.equals(TDataDictConst.BUSI_CD_VERIFY)){
			return new VerifyTxnDbService();
		}
		return null;
	}
}
