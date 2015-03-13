package com.fuiou.mgr.http.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TRootBankInfService;
import com.fuiou.mer.service.VCityInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.util.Formator;

public class HttpValidate {
	private static TRootBankInfService tRootBankInfService=new TRootBankInfService();
	private static VCityInfService vCityInfService=new VCityInfService();
	private static TDataDictService tDataDictService=new TDataDictService();
	private static TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	private static TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
	/**
	 * 直连接口的请求参数的验证
	 * @param mchnt 商户号
	 * @param merdt 请求日期 
	 * @param orderno 请求流水
	 * @param bankno 总行代码
	 * @param cityno 城市代码
	 * @param branchnm 支行名称
	 * @param accntno 账号
	 * @param accntnm 账户名称	
	 * @param entseq 企业流水号
	 * @param amount 金额
	 * @param memo 备注
	 * @param mobile 手机号
	 * @param certtp 证件类型
	 * @param certno 证件号码
	 * @param length 证件的限制长度
	 * @return
	 */
	public static String validate(String mchnt,String merdt,String orderno,String bankno,String cityno,String branchnm,
			String accntno,String accntnm,String entseq,String amount,String memo,String mobile,String certtp,String certno,int length){
		String result="";
		if(mchnt!=null){
			if("".equals(mchnt.trim())){
				result=TDataDictConst.HTTP_MCHNT_ER+"|"+"商户号为空";
				return result;
			}
			if(mchnt.trim().length()>15){
				result=TDataDictConst.HTTP_MCHNT_ER2+"|"+"商户号过长";
				return result;
			}
			TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(mchnt, "1");
			if(tInsMchntInf==null){
				result=TDataDictConst.HTTP_MCHNT_ER3+"|"+"没有找到对应的商户";
				return result;
			}
		}
        if(merdt!=null){
        	if(!RegexCheckUtil.checkDate(merdt.trim())){
        		result=TDataDictConst.HTTP_MERDT2_ER+"|"+"日期格式不正确";
        		return result;
        	}
        	if(!FuMerUtil.getCurrTime(TDataDictConst.FILE_NAM_DATE_FORMAT).equals(merdt.trim())){
        		result=TDataDictConst.HTTP_MERDT3_ER+"|"+"日期不等于当前日期";
        		return result;
        	}
        }
        if(orderno!=null){
        	if(!"".equals(orderno.trim())&&!RegexCheckUtil.checkIsDigital(orderno.trim())){
        		result=TDataDictConst.HTTP_ORDERNO_ER2 +"|"+"请求流水号只能为数字";
        		return result;
        	}
        	if(!"".equals(orderno.trim())&&orderno.trim().length()<10){
        		result=TDataDictConst.HTTP_ORDERNO_ER3 +"|"+"请求流水号小于10位";
        		return result;
        	}
        	if(!"".equals(orderno.trim())&&orderno.trim().length()>30){
        		result=TDataDictConst.HTTP_ORDERNO_ER4 +"|"+"请求流水号大于30位";
        		return result;
        	}
        	int repeatTxnCount = tApsTxnLogService.selectHttpRepeatTxn(merdt.trim(), orderno.trim(),mchnt);
			if(repeatTxnCount>0){
				result=TDataDictConst.HTTP_TXN_REPEAT_ER +"|"+"当天交易重复";
        		return result;
			}
        }
        if(bankno!=null){
        	if(!"".equals(bankno.trim())&&bankno.trim().length()<4){
        		result=TDataDictConst.HTTP_BANKNO4_ER +"|"+"总行代码小于4位";
        		return result;
        	}
        	if(!"".equals(bankno.trim())&&bankno.trim().length()>4){
        		result=TDataDictConst.HTTP_BANKNO5_ER +"|"+"总行代码大于4位";
        		return result;
        	}
        	if(!RegexCheckUtil.checkIsDigital(bankno.trim())){
        		result=TDataDictConst.HTTP_BANKNO3_ER +"|"+"总行代码非数字";
        		return result;
        	}
        	String[] bankCds={bankno.trim()};
        	List<TRootBankInf> banks = tRootBankInfService.selectByBankCds(bankCds);
        	if(banks==null){
        		result=TDataDictConst.HTTP_BANKNO2_ER +"|"+"总行代码不存在";
        		return result;
        	}
        	if(banks.size()<=0){
        		result=TDataDictConst.HTTP_BANKNO2_ER +"|"+"总行代码不存在";
        		return result;
        	}
        }
        if(cityno!=null){
        	if(cityno.trim().length()!=4){
        		result=TDataDictConst.HTTP_CITY_ER3 +"|"+"城市代码不等于4位";
        		return result;
        	}
        	String cityNo=vCityInfService.selectProvCdByCityId(cityno.trim());
        	if(cityNo==null||"".equals(cityNo)){
        		result=TDataDictConst.HTTP_CITY_ER2 +"|"+"城市代码不正确";
        		return result;
        	}
        }
        if(branchnm!=null){
        	if(!"".equals(branchnm.trim())&&branchnm.trim().length()>100){
        		result=TDataDictConst.HTTP_BRANCHNM_ER +"|"+"支行名称的长度超长";
        		return result;
        	}
        }
        if(accntno!=null){
        	if(!RegexCheckUtil.checkIsDigital(accntno.trim())){
        		result=TDataDictConst.HTTP_ACCNTNO4_ER +"|"+"账号有非数字字符";
        		return result;
        	}
        	if(!"".equals(accntno.trim())&&accntno.trim().length()<10){
        		result=TDataDictConst.HTTP_ACCNTNO2_ER +"|"+"账号小于10位";
        		return result;
        	}
        	if(!"".equals(accntno.trim())&&accntno.trim().length()>28){
        		result=TDataDictConst.HTTP_ACCNTNO3_ER +"|"+"账号大于28位";
        		return result;
        	}
        }
        if(accntnm!=null){
        	if(!"".equals(accntnm.trim())&&accntnm.trim().length()>30){
        		result=TDataDictConst.HTTP_ACCNTNM3_ER +"|"+"账户名称大于30位";
        		return result;
        	}
        }
		if(entseq!=null){
			if(!"".equals(entseq.trim())&&entseq.trim().length()>32){
				result=TDataDictConst.HTTP_ENTSEQ2_ER+"|"+"企业流水号过长";
				return result;
			}
		}
		// 检查金额
		if(amount!=null){
			if("0".equals(amount.trim())){
				result=TDataDictConst.HTTP_AMT2_ER  +"|"+"金额为0";
				return result;
			}
			long money=0;
			if(!HttpValidate.checkMoney(amount)){
				result=TDataDictConst.HTTP_AMT6_ER  +"|"+"金额输入有误";
				return result;
			}
			if(amount.length()>12){
				result=TDataDictConst.HTTP_AMT7_ER +"|"+"金额超限";
				return result;
			}
			money = Formator.yuan2Fen(amount.trim());
			if(money<0){
				result=TDataDictConst.HTTP_AMT3_ER +"|"+"金额为负数";
				return result;
			}
		}
		if(memo!=null){		
			if(!"".equals(memo.trim())&&memo.trim().length()>60){
				result=TDataDictConst.HTTP_REMARK_ER+"|"+"备注过长";
				return result;
			}
		}
		if(mobile!=null){
			if(!"".equals(mobile.trim())&&!RegexCheckUtil.checkMobile(mobile.trim())){
				result=TDataDictConst.HTTP_MOBILE_ER+"|"+"手机格式不正确";
				return result;
			}
		}
		if(certtp!=null&&!"".equals(certtp)){
			String cardId=tDataDictService.selectTDataDictByClassAndValue(TDataDictConst.MAP_ID_TP_STL_CNTR,certtp.trim());
			if("".equals(cardId)||cardId==null){
				result=TDataDictConst.HTTP_CERTTP_ER2+"|"+"证件类型不正确";
				return result;
			}
		}
		if(certtp!=null&&!"".equals(certtp)){
			if("".equals(certno)||certno==null){
				result=TDataDictConst.HTTP_CARD_ER+"|"+"证件号不能为空";
				return result;
			}
			if(certno!=null&&!"".equals(certno)){
				if(certno.trim().length()>length){
					result=TDataDictConst.HTTP_CARD_ER2+"|"+"证件号过长";
					return result;
				}
			}
		}
		return result;
	}
	/**
	 * 验证目标金额是否小于或等于手续费
	 * @param payForBusiDetailRowBeanList
	 * @return
	 */
	public static String validate2(List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList){
		String result="";
		int feeAmtInt = 0;
		int srcAmtInt = 0;
		int destAmtInt = 0;
		for(PayForBusiDetailRowBean payForBusiDetailRowBean:payForBusiDetailRowBeanList){
			feeAmtInt += payForBusiDetailRowBean.getOperationBean().getTxnFeeAmt();
			srcAmtInt += payForBusiDetailRowBean.getDetailAmt();
			destAmtInt += (payForBusiDetailRowBean.getOperationBean().getDestTxnAmt());
		}
		if(destAmtInt<0){
			result=TDataDictConst.HTTP_AMT4_ER+"|"+"金额小于手续费";
		}
		if(srcAmtInt<feeAmtInt){
			result=TDataDictConst.HTTP_AMT4_ER+"|"+"金额小于手续费";
		}
		if(srcAmtInt==feeAmtInt){
			result=TDataDictConst.HTTP_AMT5_ER+"|"+"金额等于手续费";
		}
		return result;
	}
	public static String validate3(String srcmerdt,String srcorderno){
		String result="";
		if(srcmerdt!=null){
        	if(!RegexCheckUtil.checkDate(srcmerdt.trim())){
        		result=TDataDictConst.HTTP_SRCMERDT_ER2+"|"+"原请求时间格式不正确";
        		return result;
        	}
		}
		if(srcorderno!=null){
			if(srcorderno.trim().length()>30){
				if(!RegexCheckUtil.checkDate(srcorderno.trim())){
	        		result=TDataDictConst.HTTP_SRCORDERNO_ER2+"|"+"原请求流水长度大于30位";
	        		return result;
	        	}
			}
		}
		return result;
	}
	
	
	/**
	 * 正则校验公共类
	 * @param str 被校验的字符串
	 * @param regex 正则表达式
	 * @return
	 */
	public static boolean startCheck(String str, String regex) {
		if (null != str) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		}
		return false;
	}
	 /**
     * 检查金额输入是否正确
     * @param money
     * @return
     */
    public static boolean checkMoney(String money){
    	String regex="^[1-9]\\d*$";
    	return startCheck(money, regex);
    }
}
