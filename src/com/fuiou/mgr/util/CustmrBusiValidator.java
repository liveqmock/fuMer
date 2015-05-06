package com.fuiou.mgr.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.http.util.SignatureUtil;

public class CustmrBusiValidator {
	
	public static Logger logger=LoggerFactory.getLogger(CustmrBusiValidator.class);

	//错误码
	public static final String SUCCESS_CODE = "0000";
	public static final String FORMAT_ERR = "300001";
	public static final String SIGNATURE_ERR = "300002";
	public static final String RISK_LEVEL_ERR = "300003";
	public static final String SRC_CHNL_ERR = "300004";
	public static final String MCHNT_NOT_EXIST = "300005";
	public static final String MOBILE_NO_ERR = "300013";
	public static final String VALIDATE_CNT_TOO_MUCH = "300014";
	public static final String CONTRACT_TYPE_LOWER = "300015";
	public static final String CONTRACT_IS_VALID = "300016";
	public static final String PIN_IS_PASS = "300017";
	//签约来源
	public static final String SRC_CHNL_POS = "POS";//POS签约
	public static final String SRC_CHNL_APP = "APP";//APP签约
	public static final String SRC_CHNL_IVR = "IVR";//IVR签约
	public static final String SRC_CHNL_JZH = "JZH";//金账户签约
	public static final String SRC_CHNL_DSF = "DSF";//代收付接口签约
	public static final String SRC_CHNL_WEB = "WEB";//代收付系统签约
	
	public static String[] creditTps = {"0","1","2","3","4","5","6","7"};
	public static Map<String,String> errorCodeMap = new HashMap<String,String>();
	public static Map<String,Integer> srcChnlMap = new HashMap<String,Integer>();
	
	static{
		errorCodeMap.put(SUCCESS_CODE, "受理成功");
		errorCodeMap.put(FORMAT_ERR, "报文格式错");
		errorCodeMap.put(SIGNATURE_ERR, "报文验签错");
		errorCodeMap.put(RISK_LEVEL_ERR, "低风险商户无需导入");
		errorCodeMap.put(MCHNT_NOT_EXIST, "商户不存在");
		errorCodeMap.put(SRC_CHNL_ERR, "签约来源未定义");
		errorCodeMap.put(MOBILE_NO_ERR, "手机号已绑定多张银行卡");
		errorCodeMap.put(VALIDATE_CNT_TOO_MUCH, "当日验证次数超限");
		errorCodeMap.put(CONTRACT_TYPE_LOWER, "已存在更高级别的签约记录");
		errorCodeMap.put(CONTRACT_IS_VALID, "签约记录已生效");
		errorCodeMap.put(PIN_IS_PASS, "卡密已经生效,不允许APP签约");
		
		srcChnlMap.put(SRC_CHNL_POS, 5);
		srcChnlMap.put(SRC_CHNL_APP, 15);
		srcChnlMap.put(SRC_CHNL_IVR, 10);
		srcChnlMap.put(SRC_CHNL_JZH, 5);
		srcChnlMap.put(SRC_CHNL_DSF, 5);
		srcChnlMap.put(SRC_CHNL_WEB, 5);
	}
	
	public static TCustmrBusiService custmrBusiService = new TCustmrBusiService();
	
	public static Map<String,String> validate(TCustmrBusi custmrBusi){
		Map<String,String> resultMap = new HashMap<String, String>();
		//验签
		if(!validateSignature(custmrBusi)){
			resultMap.put(SIGNATURE_ERR, errorCodeMap.get(CustmrBusiValidator.SIGNATURE_ERR));
		}
		TCustmrBusi busi = custmrBusiService.selectByAcntAndBusiCd(custmrBusi.getMCHNT_CD(),custmrBusi.getBUSI_CD(), custmrBusi.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(custmrBusi.getSrcChnl()));
		if(null!=busi&&CustmrBusiContractUtil.CONTRACT_ST_VALID.equals(busi.getCONTRACT_ST())){
			resultMap.put(CONTRACT_IS_VALID, errorCodeMap.get(CustmrBusiValidator.CONTRACT_IS_VALID));
			return resultMap;
		}
		//如果APP签约则判断卡密是否已经通过,如果已经通过不允许签约
		if(SRC_CHNL_APP.equals(custmrBusi.getSrcChnl()) && busi!=null && busi.getACNT_IS_VERIFY_2().equals(CustmrBusiContractUtil.VERIFY_PASS)){
			resultMap.put(PIN_IS_PASS, errorCodeMap.get(PIN_IS_PASS));
			return resultMap;
		}
		if(!TDataDictConst.BUSI_CD_INCOMEFOR.equals(custmrBusi.getBUSI_CD())){
			resultMap.put(FORMAT_ERR, "业务代码"+custmrBusi.getBUSI_CD()+"不被支持");
			return resultMap;
		}
		if(null==SystemParams.bankMap.get(custmrBusi.getBANK_CD())){
			resultMap.put(FORMAT_ERR, "银行代码"+custmrBusi.getBANK_CD()+"不被支持");
			return resultMap;
		}
		if(StringUtils.isEmpty(custmrBusi.getUSER_NM())  && !SRC_CHNL_POS.equals(custmrBusi.getSrcChnl())){
			resultMap.put(FORMAT_ERR, "客户姓名不能为空");
			return resultMap;
		}
		String mchntCd = custmrBusi.getMCHNT_CD();
		if(StringUtils.isEmpty(mchntCd)){
			resultMap.put(FORMAT_ERR, "商户号为空");
			return resultMap;
		}
		if(null==SystemParams.getInsMchntInf(mchntCd)){
			resultMap.put(MCHNT_NOT_EXIST, "商户号不存在");
			return resultMap;
		}
		//低风险商户不让在平台或者接口签约
		if(CustmrBusiContractUtil.LOW_RISK.equals(MemcacheUtil.getRiskLevel(mchntCd)) && (SRC_CHNL_WEB.equals(custmrBusi.getSrcChnl()) || SRC_CHNL_DSF.equals(custmrBusi.getSrcChnl()))){
			resultMap.put(RISK_LEVEL_ERR, errorCodeMap.get(RISK_LEVEL_ERR));
			return resultMap;
		}
		if(!Arrays.asList(creditTps).contains(custmrBusi.getCREDT_TP()) && !SRC_CHNL_POS.equals(custmrBusi.getSrcChnl())){
			resultMap.put(FORMAT_ERR, "证件类型"+custmrBusi.getCREDT_TP()+"不合法");
			return resultMap;
		}
		if(StringUtils.isEmpty(custmrBusi.getCREDT_NO()) && !SRC_CHNL_POS.equals(custmrBusi.getSrcChnl())){
			resultMap.put(FORMAT_ERR, "证件号码不能为空");
			return resultMap;
		}
		//如果证件号不为空的情况则需要验证证件号的合法性
		if(StringUtils.isNotEmpty(custmrBusi.getCREDT_NO())){
			if((creditTps[0].equals(custmrBusi.getCREDT_TP()) && !"YES".equals(IDCardValidateUtil.IDCardValidate(custmrBusi.getCREDT_NO()))) || (!creditTps[0].equals(custmrBusi.getCREDT_TP()) && !RegexCheckUtil.checkZhengjian(custmrBusi.getCREDT_NO()))){
				resultMap.put(FORMAT_ERR, "证件号码"+custmrBusi.getCREDT_NO()+"不合法");
				return resultMap;
			}
		}
		if(!RegexCheckUtil.checkACount(custmrBusi.getACNT_NO())){
			resultMap.put(FORMAT_ERR, "卡号格式不对");
			return resultMap;
		}
		TCardBin cardBin = CardUtil.getTCardBinByCardNo(custmrBusi.getACNT_NO(), SystemParams.cardBinMap);
		if(cardBin==null){
			resultMap.put(FORMAT_ERR, "未找到卡bin");
			return resultMap;
		}else{
			String insCd = CardUtil.getIssCdByCardNo(custmrBusi.getACNT_NO(), SystemParams.cardBinMap);
			if(!"105".equals(insCd.substring(3, 6))){//非建行卡要做每日验证次数校验
				if(!MemcacheUtil.cupsVerifyCnt(custmrBusi.getMCHNT_CD(), custmrBusi.getACNT_NO())){
					resultMap.put(VALIDATE_CNT_TOO_MUCH, errorCodeMap.get(VALIDATE_CNT_TOO_MUCH));
					return resultMap;
				}
			}
		}
		if(!"01".equals(cardBin.getCARD_ATTR())){
			resultMap.put(FORMAT_ERR, "暂不支持非借记卡");
			return resultMap;
		}
		if(!RegexCheckUtil.checkMobile(custmrBusi.getMOBILE_NO()) && !SRC_CHNL_POS.equals(custmrBusi.getSrcChnl())){
			resultMap.put(FORMAT_ERR, "手机号码"+custmrBusi.getMOBILE_NO()+"不合法");
			return resultMap;
		}
		if(StringUtils.isNotEmpty(custmrBusi.getMOBILE_NO())){
			Integer cnt = custmrBusiService.findByMobile(custmrBusi.getMCHNT_CD(), custmrBusi.getMOBILE_NO(), custmrBusi.getBUSI_CD(), Arrays.asList(custmrBusi.getACNT_NO()));
			if(cnt>2){
				resultMap.put(MOBILE_NO_ERR, "手机号"+custmrBusi.getMOBILE_NO()+"已绑定多张卡");
				return resultMap;
			}
		}
		return resultMap;
   }
	
	
	/**
	 * 验签
	 * @param custmrBusi
	 * @return
	 */
	private static boolean validateSignature(TCustmrBusi custmrBusi){
		if(SRC_CHNL_DSF.equals(custmrBusi.getSrcChnl())){//商户由接口发起的
			String keyValue = MemcacheUtil.getMchntKey(custmrBusi.getMCHNT_CD());
			return SignatureUtil.validate(custmrBusi, keyValue);//验签
		}else if(SRC_CHNL_WEB.equals(custmrBusi.getSrcChnl())){//页面发起的直接过
			return true;
		}else{
			return SignatureUtil.validate(custmrBusi, "2ba02c706f01d0a3a4f9e5c30a8f75a6");//验签
		}
	}
}
