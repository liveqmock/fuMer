package com.fuiou.mgr.action.contract;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.util.Object2Xml;
import com.fuiou.mgr.action.BaseAction;
import com.fuiou.mgr.util.CustmrBusiValidator;

public class ApiContractAction extends BaseAction {
	
	private static final long serialVersionUID = -3043493391478101006L;
	private static final Logger logger = Logger.getLogger(CustmrBusiContractUtil.class);
	
	private CustmrBusiContractUtil custmrBusiContractUtil=new CustmrBusiContractUtil();
	public String xml;
	private TCustmrBusi reqBean;
	private TCustmrBusi respBean = new TCustmrBusi();
	
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String contract(){
		logger.debug("received xml:"+xml);
		if(StringUtils.isNotEmpty(xml)){
			try{
				reqBean = (TCustmrBusi) Object2Xml.xml2object(xml, TCustmrBusi.class);
				Map<String,String> resultMap = CustmrBusiValidator.validate(reqBean);
				if(resultMap.size()>0){
					for(Map.Entry<String, String> entry:resultMap.entrySet()){
						respBean.setRespCd(entry.getKey());
						respBean.setRespDesc(entry.getValue());
					}
				}else{
					reqBean = custmrBusiContractUtil.addCustmrBusi(reqBean);
					respBean.setRespCd(CustmrBusiValidator.SUCCESS_CODE);
					respBean.setRespDesc(custmrBusiContractUtil.getResultInfo(reqBean));
					respBean.setCONTRACT_NO(reqBean.getCONTRACT_NO());
					respBean.setCONTRACT_ST(reqBean.getCONTRACT_ST());
					respBean.setACNT_IS_VERIFY_1(reqBean.getACNT_IS_VERIFY_1());
					respBean.setACNT_IS_VERIFY_2(reqBean.getACNT_IS_VERIFY_2());
					respBean.setACNT_IS_VERIFY_3(reqBean.getACNT_IS_VERIFY_3());
				}
			}catch (Exception e) {
				e.printStackTrace();
				respBean.setRespCd(CustmrBusiValidator.FORMAT_ERR);
				respBean.setRespDesc(CustmrBusiValidator.errorCodeMap.get(CustmrBusiValidator.FORMAT_ERR));
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, TCustmrBusi.class));
		return null;
	}
	
   
   
   /**
	 * 响应客户端
	 * 
	 * @param rspXml
	 */
	private void writeMsg(String rspXml) {
		rspXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + rspXml;
		logger.debug("response xml = " + rspXml);
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.write(rspXml);
		printWriter.close();
	}
}
