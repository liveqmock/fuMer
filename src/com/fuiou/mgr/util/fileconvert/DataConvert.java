package com.fuiou.mgr.util.fileconvert;

import java.util.List;

import com.fuiou.mer.model.TBankAlias;
import com.fuiou.mer.model.TCityAlias;
import com.fuiou.mer.service.TBankAliasService;
import com.fuiou.mer.service.TCityAliasService;

/**
 * 银行、城市转换
 * yangliehui
 *
 */
public class DataConvert {
	
	public String bankConvert(String bankName,String mchntCd){
	    TBankAlias tBankAlias = new TBankAliasService().bankConvert(bankName, mchntCd);
	    if(tBankAlias==null){
	        return "";
	    }else{
	        return tBankAlias.getBANK_CD();
	    }
	}
	
	/**
	 * 城市转换为ID(与数据库对应)
	 * @param cityName 城市名称
	 * @param mchntCd 商户号
	 * @return 城市id
	 */
	public String cityConvert(String cityName,String mchntCd){
		List<TCityAlias> list = new TCityAliasService().selectByExample("", cityName);
		if(list.size() == 0){
			cityName = cityName.substring(0, cityName.length()-1);
			List<TCityAlias> list2 = new TCityAliasService().selectByExample("", cityName);
			if(list2.size() == 0){
				return "";
			}else{
				TCityAlias tCityAlias2 = list2.get(0);
				return tCityAlias2.getCITY_CD();
			}
		}else{
			TCityAlias tCityAlias = list.get(0);
			return tCityAlias.getCITY_CD();
		}
	}
}
