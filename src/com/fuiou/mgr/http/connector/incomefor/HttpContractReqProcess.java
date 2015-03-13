package com.fuiou.mgr.http.connector.incomefor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.Object2Xml;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.http.connector.HttpReqProcessInterface;
import com.fuiou.mgr.util.CustmrBusiValidator;
import com.fuiou.mgr.util.IDCardValidateUtil;

/**
 * http批量导入协议库处理
 * @author Jerry
 */
public class HttpContractReqProcess implements HttpReqProcessInterface {
	
	private CustmrBusiContractUtil custmrBusiContractUtil=new CustmrBusiContractUtil();
	private List<TCustmrBusi> verifyAbledList = new ArrayList<TCustmrBusi>();
	private List<String> msgs = new ArrayList<String>();
	private Map<String,Set<String>> map = new HashMap<String, Set<String>>();
	

	@Override
	public String httpProcess(Object o, String mchntCd, String busiCd) {
		try{
			String riskLevel = MemcacheUtil.getRiskLevel(mchntCd);
			if(CustmrBusiContractUtil.LOW_RISK.equals(riskLevel)){
				msgs.add("无需导入协议库");
				return msgs2Xml(TDataDictConst.CUSTMR_BUSI_INVALID);
			}
			String xml = o.toString();
			List<TCustmrBusi> custmrBusis = (List<TCustmrBusi>) Object2Xml.xml2List(xml, TCustmrBusi.class);
			if(custmrBusis.size()>10){
				msgs.add("同一批次不允许超过10条");
				return msgs2Xml(TDataDictConst.CUSTMR_BUSI_INVALID);
			}
			for(TCustmrBusi custmrBusi:custmrBusis){
				custmrBusi.setMCHNT_CD(mchntCd);
				validateFeilds(custmrBusi,mchntCd,busiCd);
			}
			if(msgs.size()>0){//校验失败
				return msgs2Xml(TDataDictConst.CUSTMR_BUSI_INVALID);
			}else{
				for(TCustmrBusi custmrBusi:verifyAbledList){
					custmrBusi = custmrBusiContractUtil.addCustmrBusi(custmrBusi);
					msgs.add(custmrBusiContractUtil.getResultInfo(custmrBusi));
				}
				return msgs2Xml(TDataDictConst.HTTP_SUCCEED);
			}
		}catch (Exception e) {
			e.printStackTrace();
			msgs.add("协议库导入失败");
		}
		return msgs2Xml(TDataDictConst.CUSTMR_BUSI_INVALID);
	}

	
	private String msgs2Xml(String rspCd) {
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		xml.append("<custmrrsp>")
		.append("<ret>"+rspCd+"</ret>").append("<memo>");
		for(String msg:msgs){
			xml.append(msg+"|");
		}
		xml.append("</memo>").append("</custmrrsp>");
		return xml.toString();
	}


	private void validateFeilds(TCustmrBusi custmrBusis, String mchnt,String busiCd) {
		boolean flag = true;
		if(!TDataDictConst.BUSI_CD_INCOMEFOR.equals(custmrBusis.getBUSI_CD())){
			msgs.add("业务代码"+custmrBusis.getBUSI_CD()+"不被支持");
			flag = false;
		}
		if(null==SystemParams.bankMap.get(custmrBusis.getBANK_CD())){
			msgs.add("银行代码"+custmrBusis.getBANK_CD()+"不被支持");
			flag = false;
		}
		if(StringUtils.isEmpty(custmrBusis.getUSER_NM())){
			msgs.add("客户姓名不能为空");
			flag = false;
		}
		if(!Arrays.asList(CustmrBusiValidator.creditTps).contains(custmrBusis.getCREDT_TP())){
			msgs.add("证件类型"+custmrBusis.getCREDT_TP()+"不合法");
			flag = false;
		}
		if(StringUtils.isEmpty(custmrBusis.getCREDT_NO())){
			msgs.add("证件号码不能为空");
			flag = false;
		}
		if((CustmrBusiValidator.creditTps[0].equals(custmrBusis.getCREDT_TP()) && !"YES".equals(IDCardValidateUtil.IDCardValidate(custmrBusis.getCREDT_NO()))) || (!CustmrBusiValidator.creditTps[0].equals(custmrBusis.getCREDT_TP()) && !RegexCheckUtil.checkZhengjian(custmrBusis.getCREDT_NO()))){
			msgs.add("证件号码"+custmrBusis.getCREDT_NO()+"不合法");
			flag = false;
		}
		if(!RegexCheckUtil.checkMobile(custmrBusis.getMOBILE_NO())){
			msgs.add("手机号码"+custmrBusis.getMOBILE_NO()+"不合法");
			flag = false;
		}else{
			Set<String> set = map.get(custmrBusis.getMOBILE_NO());
			if(set==null){
				set = new HashSet<String>();
				set.add(custmrBusis.getACNT_NO());
				map.put(custmrBusis.getMOBILE_NO(), set);	
			}else{
				set.add(custmrBusis.getACNT_NO());
			}
			if(set.size()>2){
				msgs.add("手机号"+custmrBusis.getMOBILE_NO()+"重复出现2次以上");
			}
		}
		if(flag)
			verifyAbledList.add(custmrBusis);
	}

}
