package com.fuiou.mgr.adapter.treaty;

import com.fuiou.mer.util.TDataDictConst;

public class TreatyAdapter {
	public static TreatyInterface getAccessAdapter(String treatyType){
		if(treatyType.equals(TDataDictConst.CUSTMR_TREATY)){
			return new CustmrTreatyAdapter();
		}else{
			return null;
		}
	}
}
