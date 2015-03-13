package com.fuiou.mgr.adapter;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.incomefor.InComeForAccessAdapter;

import com.fuiou.mgr.adapter.payfor.HuaAnPayForAccessAdapter;
import com.fuiou.mgr.adapter.payfor.PayForAccessAdapter;
import com.fuiou.mgr.adapter.verify.VerifyAccessAdapter;
/**
 * 接入适配器工厂类
 * yangliehui
 *
 */
public class AccessAdapterFactory {

	/**
	 * 获取接入适配器
	 * @param busiCd		业务类型
	 * @return
	 */
	public static AccessAdapter getAccessAdapter(String busiCd) {
		if (busiCd.equals(TDataDictConst.BUSI_CD_INCOMEFOR)) {
			return new InComeForAccessAdapter();
		} else if(busiCd.equals(TDataDictConst.BUSI_CD_PAYFOR)){
			return new PayForAccessAdapter();
		} else if(busiCd.equals(TDataDictConst.BUSI_CD_VERIFY)){
			return new VerifyAccessAdapter();
		} else if(busiCd.equals("HAAP01")){
			return new HuaAnPayForAccessAdapter();
		}
		return null;
	}
}
