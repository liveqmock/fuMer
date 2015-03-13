package com.fuiou.mgr.bps;

import org.apache.commons.lang.StringUtils;

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

public class BpsTransaction {
	
	public static final String SUCC_CODE = "0000";//网关验证通过响应代码

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
			bean.setChannelId("APS");
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
			String md5Src = bean.getDestInsCd()+bean.getReqDate()+bean.getDestSsn()+bean.getTranAmt()+SystemParams.getProperty("bpsKey");
			bean.setMD5(MD5Util.encode(md5Src, null));
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
			bean.setChannelId("APS");
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
			String md5Src = bean.getDestInsCd()+bean.getReqDate()+bean.getDestSsn()+bean.getTranAmt()+SystemParams.getProperty("bpsKey");
			bean.setMD5(MD5Util.encode(md5Src, null));
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
	public static BpsUtilBean sendToUpmp(TIvrOrderInf order,IvrContractReqBean reqBean) {
		BpsUtilBean bean = new BpsUtilBean();
		BpsUtilBean resultBean = null;
		try{
			order.setACNT_NO("6226650600901588");//测试卡号
			String currentDt = FuMerUtil.getCurrTime("yyyyMMdd");
			bean.setTranCd("YMDK");
			bean.setDestInsCd(SystemParams.getProperty("upmpInsCd"));//发往银联
			bean.setChannelId("APS");
			bean.setPriority("3");
			bean.setTransNo(SsnGenerator.getSsn(bean.getDestInsCd()));
			bean.setTranAmt(order.getORDER_AMT()+"");
//			bean.setTranAmt("1");//测试金额
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
			bean.setInf(reqBean.getPin());//密码
//			bean.setInf("ezAxfDM3MDY4MTE5ODkxMjAxMTgxMnzpgITplJ98MTg1MDE2MjU5NTB8fEt6cmpFYzFCbUtsb1JvUkdNK1EwWk5hVWNFOVVpWFdUVFh0NlhYTU1qN25NbGxLUE9NNFZwN0ZtOHlrZkRVTHNxTDV2TVpQanNYZmV2Y1Vta21JKzBzN2RzTmRkTDNoY0RNbmN1YzNRbzhxdFUrLzI2Rk1uTVpXaTNxTVo3OXVlYWJKZDlhZ0ZwVVlWRlRleEl2TllXSFF2NDN2N2tKdE5WN1FUcmhKVjkrbz18fH0=");//测试密码
			String md5Src = bean.getDestInsCd()+bean.getReqDate()+bean.getDestSsn()+bean.getTranAmt()+SystemParams.getProperty("bpsKey");
			bean.setMD5(MD5Util.encode(md5Src, null));
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
}
