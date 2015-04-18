package com.fuiou.mgr.bps;

import org.apache.commons.lang.StringUtils;

import com.fuiou.key.SysKeyLoaderUtil;
import com.fuiou.mer.model.IvrContractReqBean;
import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.model.TIvrOrderInf;
import com.fuiou.mer.util.BpsUtilBean;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.MD5Util;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.http.util.SocketClient;
import com.fuiou.ssn.service.SsnGenerator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class BpsTransaction {
	
	public static final String SUCC_CODE = "0000";//网关验证通过响应代码
	public static final String CHANNEL_ID = "FHT";

	/**
	 * 建行PA01签约验证接口
	 * @param custmrBusi
	 * @return
	 */
	public static boolean ccbValidate(TCustmrBusi custmrBusi) {
		try{
			String currentDt = FuMerUtil.getCurrTime("yyyyMMdd");
			BpsUtilBean bean = new BpsUtilBean();
			bean.setTranCd("PA01");
			bean.setDestInsCd(SystemParams.getProperty("ccbInsCd"));//发往建行
			bean.setChannelId(CHANNEL_ID);
			bean.setPriority("3");
			bean.setTransNo(SsnGenerator.getSsn(bean.getDestInsCd()));
			bean.setReqDate(currentDt);
			bean.setAgreeNo("");//签约号
			bean.setDestSsn(bean.getTransNo());
			bean.setDebitAccNo(custmrBusi.getACNT_NO().length()+custmrBusi.getACNT_NO());
			bean.setDebitAccName(custmrBusi.getUSER_NM());
			bean.setCertType(custmrBusi.getCREDT_TP());
			bean.setCertNo(custmrBusi.getCREDT_NO());
			bean.setCVN2("");
			bean.setVaildDate("");
			bean.setMobileNo(custmrBusi.getMOBILE_NO());
			setMd5Content(bean);
			XStream stream = new XStream();
			stream.processAnnotations(BpsUtilBean.class);
			String xml = stream.toXML(bean);
			String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			String result = SocketClient.send(SystemParams.getProperty("bpsIp"), Integer.valueOf(SystemParams.getProperty("bpsPort")), xmlHead+xml, "utf-8", 4);
			if(StringUtils.isNotEmpty(result)){
				BpsUtilBean resultBean = (BpsUtilBean) stream.fromXML(result);
				if(SUCC_CODE.equals(resultBean.getRespCode())){
					return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * BPS代发户名验证
	 * @param custmrBusi
	 * @return
	 */
	public static BpsUtilBean cupsPayment(TCustmrBusi custmrBusi,String amt){
		BpsUtilBean bean = new BpsUtilBean();
		BpsUtilBean resultBean = null;
		try{
			String currentDay = FuMerUtil.getCurrTime("yyyyMMdd");
			bean.setTranCd("DF");
			bean.setDestInsCd(SystemParams.getProperty("cupsInsCd"));//发往银联cups
			bean.setChannelId(CHANNEL_ID);
			bean.setPriority("3");
			bean.setTransNo(SsnGenerator.getSsn(bean.getDestInsCd()));
			bean.setReqDate(currentDay);
			bean.setDestSsn(bean.getTransNo());
			bean.setCreditAccName(custmrBusi.getUSER_NM());
			bean.setCreditAccNo(custmrBusi.getACNT_NO().length()+custmrBusi.getACNT_NO());
			bean.setCreditAccBankName("");//开户行名称
			bean.setTranAmt(amt);//代发一分钱
			bean.setCertType(custmrBusi.getCREDT_TP());
			bean.setCertNo(custmrBusi.getCREDT_NO());
			bean.setCardFlag("");//0：卡 1：存折 2 对公帐号
			bean.setMobileNo(custmrBusi.getMOBILE_NO());
			bean.setInterBankNo("");//开户行联行号
			bean.setNote("");//
			bean.setAddrFlag("");//同城异地标志(1—同城(跨行) 2—异地(跨行) 0–行内(异地) 3–行内(同城) 不填默认行内 目前只有深发,民生支持跨行)
//			String md5Src = bean.getDestInsCd()+bean.getReqDate()+bean.getDestSsn()+bean.getTranAmt()+SystemParams.getProperty("bpsKey");
//			bean.setMD5(MD5Util.encode(md5Src, null));
			setMd5Content(bean);
			XStream stream = new XStream();
			stream.processAnnotations(BpsUtilBean.class);
			String xml = stream.toXML(bean);
			String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			String result = SocketClient.send(SystemParams.getProperty("bpsIp"), Integer.valueOf(SystemParams.getProperty("bpsPort")), xmlHead+xml, "utf-8", 4);
			if(StringUtils.isNotEmpty(result)){
				resultBean = (BpsUtilBean) stream.fromXML(result);
			}else{
				resultBean = new BpsUtilBean();
				resultBean.setTransNo(bean.getTransNo());
				resultBean.setRespCode("999999");
				resultBean.setRespInfo("接收应答超时");
			}
		}catch (Exception e) {
			e.printStackTrace();
			resultBean =  new BpsUtilBean();
			resultBean.setTransNo(bean.getTransNo());
			resultBean.setRespCode("999999");
			resultBean.setRespInfo("未知错误");
		}
		return resultBean;
	}
	
	/**
	 * 银联无磁有密代扣接口
	 * @param custmrBusi
	 * @return
	 */
	public static BpsUtilBean sendToUpmp(TIvrOrderInf order,String pin) {
		BpsUtilBean bean = new BpsUtilBean();
		BpsUtilBean resultBean = null;
		String tempStr = "XXXXXXXXXXXXX";
		try{
			order.setACNT_NO(order.getACNT_NO());
			String currentDt = FuMerUtil.getCurrTime("yyyyMMdd");
			bean.setTranCd("YMDK");
			bean.setDestInsCd(SystemParams.getProperty("upmpInsCd"));//发往银联
			bean.setChannelId(CHANNEL_ID);
			bean.setPriority("3");
			bean.setTransNo(SsnGenerator.getSsn(bean.getDestInsCd()));
//			bean.setTranAmt(order.getORDER_AMT()+"");
			bean.setTranAmt("1");//测试金额
			bean.setReqDate(currentDt);
			bean.setAgreeNo("");//签约号
			bean.setDestSsn(bean.getTransNo());
			bean.setDebitAccNo(order.getACNT_NO().length()+order.getACNT_NO());
			bean.setDebitAccName(order.getUSER_NM());
			bean.setCertType(order.getCERT_TP());
			bean.setCertNo(order.getCERT_NO());
			bean.setCVN2("");
			bean.setVaildDate("");
			bean.setMobileNo(order.getMOBILE_NO());
			bean.setInf(tempStr);//密码
//正确		bean.setInf("ezAxfDM0MDgwMjE5NzgxMDIyMDAxMHzmn6XmmZPls7B8MTU4MDE4MDgwODR8fHhZYWF1bklBeS9qM2hnNlZmTUR2bGY3YjRsUEtJVi9iaE9zbzNjS0kzMmVGRGtvbHJDb01ha3VEVTdnc3BWbDVTZ2ZZTEFyOUtRUXYyWDdaTm9DaGhiZTM1Z2NUc3RsQWFCaXI5bWZUVkpNSFE3NFE5SnN5dUFGa3pNM3BZNjVkTm1WOEl5MHJKS2ZHcm1OZlhONTVzWjZSZE1aWVAyWUFYUUV1dGxpNjRBQT18fH0=");
			setMd5Content(bean);
			XStream stream = new XStream();
			stream.processAnnotations(BpsUtilBean.class);
			String xml = stream.toXML(bean);
			xml = xml.replace(tempStr, pin);
			String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			String result = SocketClient.send(SystemParams.getProperty("bpsIp"), Integer.valueOf(SystemParams.getProperty("bpsPort")), xmlHead+xml, "utf-8", 4);
			if(StringUtils.isNotEmpty(result)){
				resultBean = (BpsUtilBean) stream.fromXML(result);
			}else{
				resultBean = new BpsUtilBean();
				resultBean.setTransNo(bean.getTransNo());
				resultBean.setRespCode("999999");
				resultBean.setRespInfo("接收应答超时");
			}
		}catch (Exception e) {
			e.printStackTrace();
			resultBean =  new BpsUtilBean();
			resultBean.setTransNo(bean.getTransNo());
			resultBean.setRespCode("999999");
			resultBean.setRespInfo("未知错误");
		}
		return resultBean;
	}
	
	private static void setMd5Content(BpsUtilBean bean){
		StringBuilder md5Source = new StringBuilder();
		if(StringUtils.isNotEmpty(bean.getDestInsCd())){
			md5Source.append(bean.getDestInsCd());
		}
		if(StringUtils.isNotEmpty(bean.getReqDate())){
			md5Source.append(bean.getReqDate());
		}
		if(StringUtils.isNotEmpty(bean.getDestSsn())){
			md5Source.append(bean.getDestSsn());
		}
		if(StringUtils.isNotEmpty(bean.getDebitAccNo())){
			md5Source.append(bean.getDebitAccNo());
		}
		if(StringUtils.isNotEmpty(bean.getCreditAccNo())){
			md5Source.append(bean.getCreditAccNo());
		}
		if(StringUtils.isNotEmpty(bean.getTranAmt())){
			md5Source.append(bean.getTranAmt());
		}
//		String  key = SysKeyLoaderUtil.loadSysKey("http://192.168.8.22:8086/kms/key/sysKeyLoaderService?wsdl",CHANNEL_ID.toLowerCase(), "BPS_COMM");
//		md5Source.append(key);
		md5Source.append("123456");
		bean.setMD5(MD5Util.encode(md5Source.toString(), null));
	}
	
	
}
