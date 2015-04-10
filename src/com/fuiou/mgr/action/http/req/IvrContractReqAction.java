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
import com.fuiou.mer.model.TWebLog;
import com.fuiou.mer.service.IvrOrderInfService;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.service.TWebLogService;
import com.fuiou.mer.util.BpsUtilBean;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.Object2Xml;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.BaseAction;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.bps.BpsTransaction;
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
	private TWebLogService webLogService = new TWebLogService();
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
		String vpcMsg = request.getParameter("");
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
				IvrOrderInfService orderService = new IvrOrderInfService();
				TIvrOrderInf order = orderService.insertNewOrder(reqBean,custmrBusi);
				if(null!=order){
					boolean isSentOk = sendToVpc(order);//将订单信息封装并且发送给VPC
					if(isSentOk){
						respBean.setRespCd("202000");
						respBean.setRespDesc("订单信息发送成功");
					}
				}else{
					respBean.setRespCd("202012");
					respBean.setRespDesc("订单信息发送失败");
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
		.append("ChkFlg=1000000000000000&").append("TranAmtPos=000000001000&")
		.append("TranCurrCd=156&").append("VPCId=00168400cd2c&")
		.append("MchntCd="+SystemParams.getProperty("fuiouMchntCd")+"&").append("OdrId="+order.getORDER_NO()+"&")
		.append("OdrPhoneNum="+order.getMOBILE_NO()+"&").append("PriAcct="+order.getACNT_NO()+"&")
		.append("OdrDtTm="+order.getORDER_DT()+order.getORDER_TM()+"&").append("TransSsn="+order.getTRANS_NO());
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
				if(TDataDictConst.IVR_ORDER_PAY_SUCCESS.equals(status)||TDataDictConst.IVR_ORDER_PAY_FAIL.equals(status)){
					orderService.updateOrderSt(orderInf.getROW_ID(),TDataDictConst.IVR_ORDER_PAY_OVER);//修改订单至结束状态
				}
			}else{
				respBean.setOrderSt("-1");
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean, IvrContractRespBean.class));
	}

	/**
	 * 订单支付
	 * @throws Exception
	 */
	public void orderPay() throws Exception {
		reqBean = received();
		if (reqBean != null) {
			TIvrOrderInf order = orderService.getOrder(reqBean.getOrderDt(),reqBean.getOrderNo());
			TCustmrBusi custmrBusi = custmrBusiService.selectByAcntAndBusiCd(order.getMCHNT_CD(), TDataDictConst.BUSI_CD_INCOMEFOR,order.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_WEB));
			if (null == custmrBusi) {
				respBean.setRespCd("202007");
				respBean.setRespDesc("找不到待签约记录");
			} else {
				BpsUtilBean deductionResultBean = null,paymentResultBean = null;
				deductionResultBean = BpsTransaction.sendToUpmp(order,reqBean);//扣款结果
				if(BpsTransaction.SUCC_CODE.equals(deductionResultBean.getRespCode())){
					 //如果扣款成功则立即发一笔付款
					paymentResultBean = BpsTransaction.cupsPayment(custmrBusi,order.getORDER_AMT()+"");
					if(BpsTransaction.SUCC_CODE.equals(paymentResultBean.getRespCode())){
						//扣款结果入库
						TWebLog webLogDBean = webLogService.saveWebLogD(order, deductionResultBean);//扣款结果入库
						if (webLogDBean == null)
							throw new Exception("insert order fail,order num is " + reqBean.getOrderNo());
						//付款结果入库
						webLogService.saveWebLogC(webLogDBean,paymentResultBean);
						//根据支付结果修改协议库状态
						 boolean flag = CustmrBusiContractUtil.VERIFY_PASS.equals(custmrBusi.getACNT_IS_VERIFY_1());//户名证件号验证状态
						 custmrBusi.setACNT_IS_VERIFY_2(CustmrBusiContractUtil.VERIFY_PASS);//卡密验证通过
						 custmrBusi.setGROUP_ID(CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_IVR));//更改签约方式
						 String riskLevel = MemcacheUtil.getRiskLevel(custmrBusi.getMCHNT_CD());//低风险不需要卡密验证
						 //在卡密验证成功的情况下只需要判定户名卡号是否验证通过
						 if((CustmrBusiContractUtil.HIGH_RISK.equals(riskLevel)&&flag) || (CustmrBusiContractUtil.OTHER_RISK.equals(riskLevel)&&flag)){
							 custmrBusi.setCONTRACT_ST(CustmrBusiContractUtil.CONTRACT_ST_VALID);
						 }
						 int rows = custmrBusiService.updateByRowId(custmrBusi);//修改协议库
						 if(1!=rows){
							 throw new
							 	Exception("custmr busi update fail,custmrbuis acnt_no="+custmrBusi.getACNT_NO());
						 }
						 //修改掉低级别的签约状态
						 rows = custmrBusiService.updateRowTp(custmrBusi);
						 logger.debug(custmrBusi + "  "+ rows +" updated");
					}else{
						respBean.setRespCd("202012");
						respBean.setRespDesc("卡密验证成功,退款出现异常,请联系富友人员处理");
					}
				}else{
					respBean.setRespCd("202012");
					respBean.setRespDesc("订单支付失败");
				}
			}
		}
		this.writeMsg(Object2Xml.object2xml(respBean,IvrContractRespBean.class));
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
