package com.fuiou.mgr.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;

public class VirtAcntUtil {

	/**
	 * 判断商户的可用余额是否比上送的交易金额大
	 * @param insCd
	 * @param sumAmt
	 * @return
	 */
	public static String verifyVirtBalance(String insCd,double sumAmt){
		TCustStlInfService custStlInfService = new TCustStlInfService();
		List<String> acntNos = custStlInfService.getAcntNosByIndCd(insCd);//获取账户信息
		if(acntNos == null || acntNos.size() != 1){
			return TDataDictConst.UNOPEN_ACCOUNT;//没有开通虚拟账户
		}
		try {
			String[] strs = FasService.queryBalance(insCd, acntNos.get(0), getSsn());
			String balance = FuMerUtil.formatFenToYuan(strs[4]);//可用余额
			if(Double.valueOf(balance) < sumAmt){
				return TDataDictConst.BALANCE_NOT_ENOUGH;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return TDataDictConst.QUERY_BALANCE_HAS_ERROR;
		}
		return null;
	}

	
	public static String getSsn(){
		Calendar cal = GregorianCalendar.getInstance();
		String mills = String.valueOf(cal.getTimeInMillis());
		int length = mills.length();
		String ssn = mills.substring(length-6, length);
		return ssn;
	}
	
}
