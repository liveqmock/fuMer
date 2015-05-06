package com.fuiou.mgr.util;


import org.apache.log4j.Logger;


import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;

/**
 * 协议库代发验证
 * @author Jerry
 */
public class CustmrBusiVerifyWoker implements Runnable {
	
	private static final Logger logger = Logger.getLogger(CustmrBusiVerifyWoker.class);
	
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();

	private TCustmrBusi custmrBusi;

	public CustmrBusiVerifyWoker(TCustmrBusi custmrBusi) {
		this.custmrBusi = custmrBusi;
	}
	
	@Override
	public void run() {
		logger.debug("user_nm:"+custmrBusi.getUSER_NM()+";acnt_no:"+custmrBusi.getACNT_NO()+";cert_no:"+custmrBusi.getCREDT_NO()+";acnt_is_verify_1:"+custmrBusi.getACNT_IS_VERIFY_1()+";acnt_is_verify_2:"+custmrBusi.getACNT_IS_VERIFY_2()+";acnt_is_verify_3:"+custmrBusi.getACNT_IS_VERIFY_3());
		if(!MemcacheUtil.cupsVerifyCnt(custmrBusi.getMCHNT_CD(), custmrBusi.getACNT_NO())){
			return ;
		}
		String riskLevel = MemcacheUtil.getRiskLevel(custmrBusi.getMCHNT_CD());
		boolean flag = CustmrBusiContractUtil.send2Bps(custmrBusi);
		if(flag){
			custmrBusi.setACNT_IS_VERIFY_1(CustmrBusiContractUtil.VERIFY_PASS);
			custmrBusi.setACNT_IS_VERIFY_3(CustmrBusiContractUtil.VERIFY_PASS);
		}else{
			custmrBusi.setACNT_IS_VERIFY_1(CustmrBusiContractUtil.VERIFY_UNPASS);
			custmrBusi.setACNT_IS_VERIFY_3(CustmrBusiContractUtil.VERIFY_UNPASS);
		}
		String acntIsVerify2 = custmrBusiService.selectById(custmrBusi).getACNT_IS_VERIFY_2();//为了解决与SWT并发的问题在修改的时候再去查一把库
		if((CustmrBusiContractUtil.LOW_RISK.equals(riskLevel)&&flag) || 
				(CustmrBusiContractUtil.MID_RISK.equals(riskLevel)&&flag) || 
				(CustmrBusiContractUtil.HIGH_RISK.equals(riskLevel)&&flag&&CustmrBusiContractUtil.VERIFY_PASS.equals(acntIsVerify2))|| 
				(CustmrBusiContractUtil.OTHER_RISK.equals(riskLevel)&&flag&&CustmrBusiContractUtil.VERIFY_PASS.equals(acntIsVerify2))){
			custmrBusi.setCONTRACT_ST(CustmrBusiContractUtil.CONTRACT_ST_VALID);
		}else{
			custmrBusi.setCONTRACT_ST(CustmrBusiContractUtil.CONTRACT_ST_INVALID);
		}
		custmrBusiService.updateContractSt(custmrBusi);
	}



	
}
