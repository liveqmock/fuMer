package com.fuiou.mgr.adapter.treaty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.service.TRootBankInfService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.util.CustmrBusiValidator;
import com.fuiou.mgr.util.IDCardValidateUtil;
import com.fuiou.mgr.util.StringUtil;

public class Excelvilidate {
	private Logger logger = LoggerFactory.getLogger(Excelvilidate.class);
	//存放结果集的集合
	private Map<String,Object> resultMap=new HashMap<String, Object>();

	public void  getResultMap(List<String> errorStr,List<TInsMchntInf> tInsMchntInfs){
		resultMap.put("errorStr", errorStr);
		resultMap.put("tInsMchntInfs", tInsMchntInfs);
	}
	public void getResultMap2(List<String> errorStr,List<TCustmrBusi> tCustmrBusis){
		resultMap.put("errorStr", errorStr);
		resultMap.put("tCustmrBusis", tCustmrBusis);
	}
	public Map<String,Object> vilidateCustmr(List<String> listStr,AccessBean bean,String fileSuffix){
		FileAccess fileAccess=(FileAccess)bean;
		String filename = fileAccess.getFileName();
		String mchntCd=fileAccess.getMchntCd();
		TRootBankInfService tRootBankInfService=new TRootBankInfService();
		List<String> errorStr=new ArrayList<String>();
		List<TCustmrBusi> tCustmrBusis=new ArrayList<TCustmrBusi>();
		TCustmrBusiService custmrBusiService = new TCustmrBusiService();
		try {
			int row=1;
			if("txt".equals(fileSuffix)){
				row=0;
			}
			List<String> acntNos=new ArrayList<String>();
			Map<String,Set<String>> mobileNos=new HashMap<String,Set<String>>();
			Calendar calendar = Calendar.getInstance();
			String startDt = FuMerUtil.date2String(calendar.getTime(), "yyyyMMdd");
			calendar.add(Calendar.YEAR, 1);
			String expireDt = FuMerUtil.date2String(calendar.getTime(), "yyyyMMdd");
			for(String strRow:listStr){
				row++;
				String[] items = strRow.split(TDataDictConst.FILE_CONTENT_APART, 10);
				String BUSI_CD=items[0].replaceAll("\n", "").trim();//业务类型
				String USER_NM=items[1].replaceAll("\n", "").trim();//姓名
				String MOBILE_NO=items[2].replaceAll("\n", "").trim();//手机号码
				String CREDT_TP=items[3].replaceAll("\n", "").trim();//证件类型
				String CREDT_NO=items[4].replaceAll("\n", "").trim();//证件号码
				String ACNT_NO=items[5].replaceAll("\n", "").trim();//账号
				String ACNT_TP=items[6].replaceAll("\n", "").trim();//账户属性
				String BANK_CD=items[7].replaceAll("\n", "").trim();//行别
				String IS_CALLBACK=items[8].replaceAll("\n", "").trim();//语音回拨标志
				String RESERVED1=items[9].replaceAll("\n", "").trim();//备注
				
				if(StringUtil.isEmpty(BUSI_CD)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行业务类型为空");
				}else{
					BUSI_CD=BUSI_CD.substring(0,4).trim();
					if(!TDataDictConst.BUSI_CD_INCOMEFOR.equals(BUSI_CD)){
						errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行业务类型错误");
					}
				}
				
				if(StringUtil.isEmpty(USER_NM)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行姓名内容为空");
				}
				if(USER_NM.length()>30){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER+"|"+"第"+row+"行姓名内容过长");
				}
				if(!RegexCheckUtil.checkMobile(MOBILE_NO)){
					errorStr.add(TDataDictConst.FIlE_ROW_FORMAT_ER+"|"+"第"+row+"行手机号码内容格式不正确");
				}else{
					Set<String> set = mobileNos.get(MOBILE_NO);
					if(set==null){
						set = new HashSet<String>();
						set.add(ACNT_NO);
						mobileNos.put(MOBILE_NO, set);
					}else{
						set.add(ACNT_NO);
					}
					if(set.size()>2){
						errorStr.add("手机号"+MOBILE_NO+"重复出现2次以上");
					}else{
						Integer cnt = custmrBusiService.findByMobile(bean.getMchntCd(),MOBILE_NO,BUSI_CD,new ArrayList<String>(set));//判断手机号在数据库中是否已经绑定了多个卡号
						if(cnt + set.size() > 2){
							errorStr.add("手机号"+MOBILE_NO+"已绑定多张卡号");
						}
					}
				}
				if(StringUtil.isEmpty(CREDT_TP)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行证件类型内容为空");
				}
				if("身份证".equals(CREDT_TP)){
					CREDT_TP="0";
				}else if("护照".equals(CREDT_TP)){
					CREDT_TP="1";
				}else if("军官证".equals(CREDT_TP)){
					CREDT_TP="2";
				}else if("士兵证".equals(CREDT_TP)){
					CREDT_TP="3";
				}else if("回乡证".equals(CREDT_TP)){
					CREDT_TP="4";
				}else if("户口本".equals(CREDT_TP)){
					CREDT_TP="5";
				}else if("外国护照".equals(CREDT_TP)){
					CREDT_TP="6";
				}else if("其他".equals(CREDT_TP)){
					CREDT_TP="7";
				}else{
					errorStr.add(TDataDictConst.FIlE_ROW_FORMAT_ER+"|"+"第"+row+"行证件类型内容类型不正确");
				}
				if(StringUtil.isEmpty(CREDT_NO)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER+"|"+"第"+row+"行证件号码为空");
				}
				if(("0".equals(CREDT_TP)&&!"YES".equals(IDCardValidateUtil.IDCardValidate(CREDT_NO))) ||(!"0".equals(CREDT_TP)&&!RegexCheckUtil.checkZhengjian(CREDT_NO))){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行证件号码格式不正确");
				}
				
				if(StringUtil.isEmpty(ACNT_NO)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行账号内容为空");
				}
				if(!RegexCheckUtil.checkACount(ACNT_NO)){
					errorStr.add(TDataDictConst.FIlE_ROW_FORMAT_ER+"|"+"第"+row+"行账号内容格式不正确（必须为10-30位数字）");
				}
				
				if(StringUtil.isEmpty(ACNT_TP)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行账户属性内容为空");
				}
				if("无需验证".equals(ACNT_TP)){
					ACNT_TP="*";
				}else if("借记卡".equals(ACNT_TP)){
					ACNT_TP="01";
				}else if("贷记卡".equals(ACNT_TP)){
					ACNT_TP="02";
				}else if("准贷记卡".equals(ACNT_TP)){
					ACNT_TP="03";
				}else{
					errorStr.add(TDataDictConst.FIlE_ROW_FORMAT_ER+"|"+"第"+row+"行账户属性类型不正确");
				}
				
				if(BANK_CD==null||"".equals(BANK_CD)){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_ER+"|"+"第"+row+"行行别内容为空");
				}
				if("是".equals(IS_CALLBACK)){
					IS_CALLBACK = "1";
				}else if("否".equals(IS_CALLBACK)){
					IS_CALLBACK = "0";
				}else{
					errorStr.add("第"+row+"行语音回拨标志符无法识别");
				}
				TRootBankInf tRootBankInf = tRootBankInfService.selectByTBank_Nm(BANK_CD);
				if(tRootBankInf!=null){
					BANK_CD=tRootBankInf.getBANK_CD();
				}else{
					if(RegexCheckUtil.checkNum(BANK_CD)){
						tRootBankInf=tRootBankInfService.selectByTBank_Cd(BANK_CD);
						if(tRootBankInf!=null){
							BANK_CD=tRootBankInf.getBANK_CD();
						}else{
							BANK_CD="";
						}
					}else{
						BANK_CD="";
					}
				}
				if("".equals(BANK_CD)){
					errorStr.add(TDataDictConst.INTER_BANK_CD_ER+"|"+"第"+row+"行匹配银行代码失败,不支持此行");
				}
				
				if(!"".equals(RESERVED1)&&RESERVED1.length()>60){
					errorStr.add(TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER+"|"+"第"+row+"行备注内容过长");
				}
				
				String busiName=TDataDictConst.getBusiCdName(BUSI_CD);
				//判断卡号是否文件内重复
				if(acntNos.contains((ACNT_NO+BUSI_CD))){
					errorStr.add(TDataDictConst.FIlE_NUMBER_ER+"|"+"第"+row+"行账号"+ACNT_NO+"的"+busiName+"协议库信息在文件中已存在，可能还有其他与之相同的，请仔细检查");
				}else{
					acntNos.add(ACNT_NO+BUSI_CD);
				}
				//组装数据
				TCustmrBusi tCustmrBusi=new TCustmrBusi();
				tCustmrBusi.setBUSI_CD(BUSI_CD);
				tCustmrBusi.setCONTRACT_SIGN_DT(startDt);
				tCustmrBusi.setCONTRACT_EXPIRE_DT(expireDt);
				tCustmrBusi.setUSER_NM(USER_NM);
				tCustmrBusi.setMOBILE_NO(MOBILE_NO);
				tCustmrBusi.setCREDT_TP(CREDT_TP);
				tCustmrBusi.setCREDT_NO(CREDT_NO);
				tCustmrBusi.setACNT_NO(ACNT_NO);
				tCustmrBusi.setACNT_TP(ACNT_TP);
				tCustmrBusi.setBANK_CD(BANK_CD);
				tCustmrBusi.setRESERVED1(RESERVED1);
				tCustmrBusi.setRESERVED3(filename);
				String user = fileAccess.getOprUsrId();
				String contractNo = new TSeqService().getId("contractNo", 12, "");// 获取协议号
				tCustmrBusi.setGROUP_ID(CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_WEB));
				tCustmrBusi.setCONTRACT_NO(contractNo);
				tCustmrBusi.setREC_UPD_USR(user);
				tCustmrBusi.setMCHNT_CD(mchntCd);
				tCustmrBusi.setROW_TP("1");
				tCustmrBusi.setROW_ST("1");
				tCustmrBusi.setRESERVED2("0");//标示未解约
				tCustmrBusi.setROW_CRT_TS(new Date());
				tCustmrBusi.setREC_CMT_TS(new Date());
				tCustmrBusi.setREC_UPD_TS(new Date());
				tCustmrBusi.setACNT_IS_VERIFY_1(CustmrBusiContractUtil.VERIFY_UNPASS);
				tCustmrBusi.setACNT_IS_VERIFY_2(CustmrBusiContractUtil.VERIFY_UNPASS);
				tCustmrBusi.setACNT_IS_VERIFY_3(CustmrBusiContractUtil.VERIFY_UNPASS);
				tCustmrBusi.setACNT_IS_VERIFY_4(CustmrBusiContractUtil.VERIFY_UNPASS);
				tCustmrBusi.setCONTRACT_ST(CustmrBusiContractUtil.CONTRACT_ST_INIT);//待验证
				tCustmrBusis.add(tCustmrBusi);
			}
		} catch (Exception e) {
			errorStr.add(TDataDictConst.UNSUCCESSFUL+"|"+"操作失败");
			logger.error(TDataDictConst.UNSUCCESSFUL+"|"+"操作失败");
			e.printStackTrace();
		}
		getResultMap2(errorStr, tCustmrBusis);
		return resultMap;
	}
}
