package com.fuiou.mgr.action.contract;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.util.BpsUtilBean;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.DateUtils;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.bps.BpsTransaction;
import com.fuiou.mgr.util.CustmrBusiValidator;

public class CustmrBusiContractUtil{
	
	private static final Logger logger = Logger.getLogger(CustmrBusiContractUtil.class);

	public static final String LOW_RISK = "00";//低风险商户
	public static final String MID_RISK = "01";//中险商户
	public static final String HIGH_RISK = "02";//高风险商户
	public static final String OTHER_RISK = "03";//特殊商户
	
	public static final String CONTRACT_ST_INIT = "2";//协议待验证
	public static final String CONTRACT_ST_VALID = "1";//协议库已生效
	public static final String CONTRACT_ST_INVALID = "0";//协议库未生效
	
	public static final String VERIFY_PASS = "1";//验证通过
	public static final String VERIFY_UNPASS = "0";//验证不通过
	
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();
	
	
	
	/**
	 * @param custmrBusi:原始数据对象
	 */
	public TCustmrBusi addCustmrBusi(TCustmrBusi custmrBusi) {
		if(CustmrBusiValidator.SRC_CHNL_POS.equals(custmrBusi.getSrcChnl())){
			doPosContract(custmrBusi);
		}else if(CustmrBusiValidator.SRC_CHNL_APP.equals(custmrBusi.getSrcChnl())){
			doAppContract(custmrBusi);
		}else{
			doWebContract(custmrBusi);
		}
		TCustmrBusi busi = custmrBusiService.selectByAcntAndBusiCd(custmrBusi.getMCHNT_CD(),custmrBusi.getBUSI_CD(), custmrBusi.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(custmrBusi.getSrcChnl()));
		return busi;
	}
	
	/**
	 * 处理接口、页面、金账户过来的签约请求
	 * @param custmrBusi
	 * @return
	 */
	private void doWebContract(TCustmrBusi custmrBusi) {
		boolean flag = send2Bps(custmrBusi);
//		boolean flag = true;
		if(flag){
			custmrBusi.setACNT_IS_VERIFY_1(VERIFY_PASS);
			custmrBusi.setACNT_IS_VERIFY_3(VERIFY_PASS);
		}
		// 获取签约记录
		TCustmrBusi busi = custmrBusiService.selectByAcntAndBusiCd(custmrBusi.getMCHNT_CD(),custmrBusi.getBUSI_CD(), custmrBusi.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(custmrBusi.getSrcChnl()));
		boolean flag2 = (busi!=null&&VERIFY_PASS.equals(busi.getACNT_IS_VERIFY_2()));
		//获取商户风险等级
		String riskLevel = MemcacheUtil.getRiskLevel(custmrBusi.getMCHNT_CD());
		if((LOW_RISK.equals(riskLevel)&&flag) || (MID_RISK.equals(riskLevel)&&flag) || (HIGH_RISK.equals(riskLevel)&&flag&&flag2) || (OTHER_RISK.equals(riskLevel)&&flag&&flag2)){
			custmrBusi.setCONTRACT_ST(CONTRACT_ST_VALID);
		}else{
			custmrBusi.setCONTRACT_ST(CONTRACT_ST_INVALID);
		}
		if(busi!=null){
			custmrBusi.setROW_ID(busi.getROW_ID());
			custmrBusi.setREC_UPD_USR("FHT");
			custmrBusi.setREC_UPD_TS(new Date());
			custmrBusi.setRESERVED2("0");
			custmrBusiService.updateByRowId(custmrBusi);
			//把其他低级别签约方式置位失效
			if(CONTRACT_ST_VALID.equals(custmrBusi.getCONTRACT_ST())){
				int rows = custmrBusiService.updateRowTp(busi);
				logger.debug(custmrBusi + "  "+ rows +" updated");
			}
		}else{
			insertToDB(custmrBusi);
		}
	}


	/**
	 * 处理APP过来的签约记录
	 * @param custmrBusi
	 * @return
	 */
	private void doAppContract(TCustmrBusi custmrBusi) {
		TCustmrBusi busi = custmrBusiService.selectByAcntAndBusiCd(custmrBusi.getMCHNT_CD(),custmrBusi.getBUSI_CD(), custmrBusi.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_POS));
		boolean flag = send2Bps(custmrBusi);
//		boolean flag = true;
		if(flag){
			custmrBusi.setACNT_IS_VERIFY_1(VERIFY_PASS);
			custmrBusi.setACNT_IS_VERIFY_3(VERIFY_PASS);
			custmrBusi.setCONTRACT_ST(CONTRACT_ST_VALID);
		}
		if(busi!=null){
			custmrBusi.setROW_ID(busi.getROW_ID());
			custmrBusi.setRESERVED2("0");
			custmrBusi.setREC_UPD_USR("FHT");
			custmrBusi.setREC_UPD_TS(new Date());
			custmrBusiService.updateByRowId(custmrBusi);
		}else{
			insertToDB(custmrBusi);
		}
	}


	/**
	 * 处理POS过来的协议库记录
	 * @param custmrBusi
	 * @return
	 */
	private void  doPosContract(TCustmrBusi custmrBusi) {
		TCustmrBusi busi = custmrBusiService.selectByAcntAndBusiCd(custmrBusi.getMCHNT_CD(),custmrBusi.getBUSI_CD(), custmrBusi.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_POS));
		custmrBusi.setACNT_IS_VERIFY_2(VERIFY_PASS);
		if(busi!=null){
			custmrBusi.setROW_ID(busi.getROW_ID());
			custmrBusi.setGROUP_ID(CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_POS));
			custmrBusi.setREC_UPD_USR("FHT");
			custmrBusi.setREC_UPD_TS(new Date());
			custmrBusi.setRESERVED2("0");
			if(VERIFY_PASS.equals(busi.getACNT_IS_VERIFY_1())||VERIFY_PASS.equals(busi.getACNT_IS_VERIFY_3())){
				custmrBusi.setCONTRACT_ST(CONTRACT_ST_VALID);
			}
			int rows = custmrBusiService.updateByRowId(custmrBusi);
			if(1==rows)
				logger.debug("custmrBusi rowId="+busi.getROW_ID()+" was modified");
			if(CONTRACT_ST_VALID.equals(custmrBusi.getCONTRACT_ST())){
				//把其他低级别签约方式置位失效
				rows = custmrBusiService.updateRowTp(custmrBusi);
				logger.debug(custmrBusi + "  "+ rows +" updated");
			}
		}else{
			insertToDB(custmrBusi);
		}
	}



	/**
	 * 将新的协议库插入数据库
	 * @param mchntCd:商户代码
	 * @param loginUser:当前登陆用户
	 * @param custmrBusi：待入库的协议库对象
	 * @param contractSt：生效标志
	 * @param flag1：卡号户名是否验证标志,0:验证未通过,1:验证已通过
	 * @param flag2：卡号密码验证,0:验证未通过,1:验证已通过
	 * @param flag3：户名证件号验证,0:验证未通过,1:验证已通过
	 * @param flag4：卡号手机号验证,0:验证未通过,1:验证已通过
	 */
	private void insertToDB(TCustmrBusi custmrBusi){
		String contractNo = new TSeqService().getId("contractNo", 12, "");// 获取协议号
		custmrBusi.setGROUP_ID(CustmrBusiValidator.srcChnlMap.get(custmrBusi.getSrcChnl()));
		custmrBusi.setCONTRACT_NO(contractNo);
		custmrBusi.setROW_CRT_TS(new Date());
		custmrBusi.setREC_UPD_TS(new Date());
		custmrBusi.setRESERVED2("0");// 标示未解约
		custmrBusi.setROW_TP("1");
		custmrBusi.setROW_ST("1");
		Calendar calendar = Calendar.getInstance();
		custmrBusi.setCONTRACT_SIGN_DT(FuMerUtil.date2String(calendar.getTime(), "yyyyMMdd"));
		calendar.add(Calendar.YEAR, 1);
		custmrBusi.setCONTRACT_EXPIRE_DT(FuMerUtil.date2String(calendar.getTime(), "yyyyMMdd"));
		try {
			//插库
			int rows = custmrBusiService.saveCustmrBusiInfos(Arrays.asList(custmrBusi));
			if(1==rows){
				logger.debug("contractNo="+contractNo+" insert into db success");
			}else{
				logger.debug("contractNo="+contractNo+" insert into db fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getResultInfo(TCustmrBusi custmrBusi){
		StringBuffer info = new StringBuffer(custmrBusi.getACNT_NO());
		if(VERIFY_PASS.equals(custmrBusi.getACNT_IS_VERIFY_1())){
			info.append("卡号户名验证成功,");
		}else{
			info.append("卡号户名验证失败,");
		}
		if(VERIFY_PASS.equals(custmrBusi.getACNT_IS_VERIFY_2())){
			info.append("卡号密码验证成功,");
		}else{
			info.append("卡号密码验证失败,");
		}
		if(VERIFY_PASS.equals(custmrBusi.getACNT_IS_VERIFY_3())){
			info.append("户名证件号验证成功,");
		}else{
			info.append("户名证件号验证失败,");
		}
		if(CONTRACT_ST_VALID.equals(custmrBusi.getCONTRACT_ST())){
			info.append("签约已生效");
		}else{
			info.append("签约未生效");
		}
		return info.toString();
	}
	
	public static boolean send2Bps(TCustmrBusi custmrBusi){
		String insCd = CardUtil.getIssCdByCardNo(custmrBusi.getACNT_NO(), SystemParams.cardBinMap);
		if(StringUtils.isNotEmpty(insCd)&&insCd.length()>3&&"105".equals(insCd.substring(3, 6))){//建行卡走PA01签约接口
			return BpsTransaction.ccbValidate(custmrBusi);//建行签约接口
		}else{
			BpsUtilBean ressultBean = BpsTransaction.cupsPayment(custmrBusi,"1");//代发一分钱
			if(BpsTransaction.SUCC_CODE.equals(ressultBean.getRespCode())){
				SystemParams.verifyCnt.get(DateUtils.getCurrentDate()).incrementAndGet();
				return true;
			}else{
				return false;
			}
		}
	}
	

}
