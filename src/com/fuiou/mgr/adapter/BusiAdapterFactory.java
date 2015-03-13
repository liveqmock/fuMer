package com.fuiou.mgr.adapter;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.incomefor.InComeForBusiAdapter;
import com.fuiou.mgr.adapter.payfor.PayForBusiAdapter;
import com.fuiou.mgr.adapter.verify.VerifyBusiAdapter;
/**
 * 业务适配器工厂类
 * yangliehui
 *
 */
public class BusiAdapterFactory {
	/**
	 * 获取业务适配器
	 * @param busiCd		业务类型
	 * @return
	 */
	public static BusiAdapter getFileBusiAdapter(String busiCd){
		if (busiCd.equals(TDataDictConst.BUSI_CD_INCOMEFOR)) {
			return new InComeForBusiAdapter();
		}else if(busiCd.equals(TDataDictConst.BUSI_CD_PAYFOR)){
			return new PayForBusiAdapter();
		}else if(busiCd.equals(TDataDictConst.BUSI_CD_VERIFY)){
			return new VerifyBusiAdapter();
		}
		return null;
	}
}
