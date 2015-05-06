package com.fuiou.mgr.action.http.req;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fuiou.mer.model.IvrContractReqBean;
import com.fuiou.mer.model.IvrContractRespBean;
import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.model.TIvrOrderInf;
import com.fuiou.mer.service.IvrOrderInfService;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.util.Object2Xml;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.BaseAction;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.http.util.SignatureUtil;
import com.fuiou.mgr.http.util.SocketClient;
import com.fuiou.mgr.util.CustmrBusiValidator;
import com.fuiou.mgr.util.VPCDecodeUtil;

/**
 * 提供给IVR厂商的协议库相关接口
 * 
 * @author Jerry
 * 
 */
public class IvrContractReqAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(CustmrBusiContractUtil.class);
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();
	private IvrOrderInfService orderService = new IvrOrderInfService();
	private IvrContractRespBean respBean = new IvrContractRespBean();
	private IvrContractReqBean reqBean;
	private String xml;// 请求报文

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	/**
	 * 根据卡号获取客户待生效的协议所对应的商户号
	 */
	public void getMhcntListByCard() {
		reqBean = this.received();
		if (reqBean != null) {
			String acntNo = reqBean.getAcntNo();
			if (!RegexCheckUtil.startCheck(acntNo, "^[0-9]{8,30}$")) {// 验证卡号格式
				respBean.setRespCd("202001");
				respBean.setRespDesc("卡号格式错");
			} else {
				List<String> mchntList = custmrBusiService.getMhcntListByCard(acntNo, reqBean.getFlag());// 根据卡号获取签约商户列表
				if (mchntList != null && mchntList.size() > 0) {// 如果找到了
					respBean.setRespCd("202000");
					respBean.setRespDesc("处理成功");
					respBean.setMchnts(mchntList);
					this.writeMsg(Object2Xml.object2xml(respBean,IvrContractRespBean.class));
				} else {
					respBean.setRespCd("202007");
					respBean.setRespDesc("找不到待签约记录");
				}
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, IvrContractRespBean.class));
	}

	/**
	 * 判断呼入手机号是否为签约手机号
	 */
	public void isContractMobile() {
		reqBean = received();
		if (reqBean != null) {
			String mchntCd = reqBean.getMchntCd();
			String mobile = reqBean.getMobile();
			boolean flag = true;
			if (!RegexCheckUtil.startCheck(mchntCd, "^[0-9]{7}[A-Z][0-9]{7}$")) {// 验证商户号
				flag = false;
				respBean.setRespCd("202001");
				respBean.setRespDesc("商户号格式错");
			}
			if (flag && !RegexCheckUtil.startCheck(mobile, "^1[0-9]{10}$")) {// 验证手机号
				flag = false;
				respBean.setRespCd("202001");
				respBean.setRespDesc("手机号格式错");
			}
			if (flag) {
				TCustmrBusi busi = custmrBusiService.selectByAcntAndBusiCd(reqBean.getMchntCd(), TDataDictConst.BUSI_CD_INCOMEFOR,reqBean.getAcntNo(),CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_WEB));
				if (mobile.equals(busi.getMOBILE_NO())) {
					respBean.setRespCd("202000");
					respBean.setRespDesc("呼入手机号与签约手机号一致");
				} else {
					respBean.setRespCd("202012");
					respBean.setRespDesc("呼入手机号与签约手机号不一致");
				}
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, IvrContractRespBean.class));
	}

	/**
	 * IVR请求获取并且将订单信息推送给VPC
	 */
	public void sendOrderToVpc() {
		reqBean = received();
		if (reqBean != null) {
			String flag = reqBean.getFlag();//签约标志,如果是签约则协议库状态必须为未生效状态,如果是解约协议库则必须处于生效状态,否则拒绝
			TCustmrBusi custmrBusi = custmrBusiService.selectByAcntAndBusiCd(reqBean.getMchntCd(), TDataDictConst.BUSI_CD_INCOMEFOR,reqBean.getAcntNo(),CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_WEB));
			if("C".equals(flag) && CustmrBusiContractUtil.CONTRACT_ST_VALID.equals(custmrBusi.getACNT_IS_VERIFY_2())){
				respBean.setRespCd("202012");
				respBean.setRespDesc("签约已生效");
			}else if("D".equals(flag) && !CustmrBusiContractUtil.CONTRACT_ST_VALID.equals(custmrBusi.getACNT_IS_VERIFY_2())){
				respBean.setRespCd("202012");
				respBean.setRespDesc("签约未生效");
			}else{
				TIvrOrderInf order = orderService.getOrderByMobile(reqBean.getMobile());//根据手机号码获取订单信息
				if(order==null || order.getVERIFY_CNT()==0){
					IvrOrderInfService orderService = new IvrOrderInfService();
					order = orderService.insertNewOrder(reqBean,custmrBusi);
				}
				boolean isSentOk = sendToVpc(order);//将订单信息封装并且发送给VPC
				if(isSentOk){
					respBean.setRespCd("202000");
					respBean.setRespDesc("订单信息发送成功");
				}
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, IvrContractRespBean.class));
	}

	/**
	 * 将订单信息封装并且发送给VPC
	 * @param order
	 * @return
	 */
	private boolean sendToVpc(TIvrOrderInf order) {
		StringBuilder params = new StringBuilder();
		params.append("Version=1.0.0&").append("TranTp=82&").append("TranSubTp=00&")
		.append("ChkFlg=1010000110000000&").append("TranAmtPos=000000001000&")
		.append("TranCurrCd=156&").append("VPCId=00168400cd2c&")
		.append("MchntCd="+SystemParams.getProperty("fuiouMchntCd")+"&").append("OdrId="+order.getORDER_NO()+"&")
		.append("OdrPhoneNum="+order.getMOBILE_NO()+"&").append("PriAcct="+order.getACNT_NO()+"&")
		.append("OdrDtTm="+order.getORDER_DT()+order.getORDER_TM()+"&").append("TransSsn="+order.getTRANS_NO()+"&")
		.append("CstmInfo={01|"+order.getCERT_NO()+"|"+order.getUSER_NM()+"|"+order.getMOBILE_NO()+"|||}");
		String vpcInfo = VPCDecodeUtil.doCrypt("C", params.toString());
		vpcInfo = "User-Agent: Donjin Http 0.1\r\nCache-Control: no-cache\r\nContent-Type: text/xml;charset=UTF-8\r\nAccept: */*\r\nContent-Length: "+vpcInfo.length()+"\r\n\r\n"+vpcInfo;
		String respStr = SocketClient.sendToVpc(SystemParams.getProperty("vpcIp"), Integer.valueOf(SystemParams.getProperty("vpcPort")), vpcInfo, "UTF-8");
		try{
			if(StringUtils.isNotEmpty(respStr)){
				int index = respStr.indexOf("\r\n\r\n");
				respStr = respStr.substring(index);
				respStr = VPCDecodeUtil.doCrypt("D", respStr);
				System.out.println(respStr);
				String[] strs = respStr.split("&");
				Map<String,String> map = new HashMap<String, String>();
				for(String s:strs){
					map.put(s.split("=")[0], s.split("=")[1]);
				}
				if(map.get("RspCd")!=null && "DJ0000".equals(map.get("RspCd"))){
					return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 根据手机号获取订单
	 */
	public void getOrderByMobile(){
		reqBean = received();
		if (reqBean != null) {
			respBean.setRespCd("202000");
			respBean.setRespDesc("处理成功");
			TIvrOrderInf orderInf = orderService.getOrderByMobile(reqBean.getMobile());
			if(null!=orderInf){
				String status = orderInf.getORDER_ST();
				respBean.setOrderSt(status);
				if(orderInf.getVERIFY_CNT()>0){
					respBean.setContFlag("Y");
					respBean.setFlag(orderInf.getCONTRACT_FLAG());
					respBean.setAcntNo(orderInf.getACNT_NO());
					respBean.setMchntCd(orderInf.getMCHNT_CD());
				}else{
					respBean.setContFlag("N");
					respBean.setFlag(orderInf.getCONTRACT_FLAG());
					respBean.setAcntNo(orderInf.getACNT_NO());
					respBean.setMchntCd(orderInf.getMCHNT_CD());
				}
			}else{
				respBean.setOrderSt("-1");
				respBean.setContFlag("Y");
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, IvrContractRespBean.class));
	}
	
	/**
	 * 挂断通知
	 */
	public void hangup(){
		reqBean = received();
		if (reqBean != null) {
			TIvrOrderInf orderInf = orderService.getOrderByMobile(reqBean.getMobile());
			if(null!=orderInf){
				int i = orderService.updateOrderSt(orderInf.getROW_ID(), TDataDictConst.IVR_ORDER_PAY_OVER, 0);
				if(1==i){
					respBean.setRespCd("202000");
					respBean.setRespDesc("处理成功");
				}else{
					respBean.setRespCd("202001");
					respBean.setRespDesc("挂断失败");
				}
			}else{
				respBean.setRespCd("202001");
				respBean.setRespDesc("未找到订单");
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, IvrContractRespBean.class));
	}

	/**
	 * 订单支付
	 * @throws Exception
	 */
	public void orderPay() throws Exception {
	}
	
	/**
	 * 接收请求并且验签
	 * 
	 * @return
	 */
	private IvrContractReqBean received() {
		try {
			logger.debug("accept contract request ,xml = " + xml);
			reqBean = (IvrContractReqBean) Object2Xml.xml2object(xml,IvrContractReqBean.class);
			String reqInsCd = reqBean.getReqInsCd();
			String key = SystemParams.getProperty(reqInsCd + "_Key");
			boolean flag = SignatureUtil.validate(reqBean, key);// 校验签名是否正确
			if (!flag) {
				respBean.setRespCd("202002");
				respBean.setRespDesc("请求签名不对");
			} else {
				return reqBean;
			}
		} catch (Exception e) {
			respBean.setRespCd("202001");
			respBean.setRespDesc("请求报文异常");
		}
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
			printWriter = new PrintWriter(new OutputStreamWriter(
					response.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.write(rspXml);
		printWriter.close();
	}
	
	
}
