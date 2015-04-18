package com.fuiou.mgr.http.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.model.TIvrOrderInf;
import com.fuiou.mer.model.TWebLog;
import com.fuiou.mer.service.IvrOrderInfService;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.service.TWebLogService;
import com.fuiou.mer.util.BpsUtilBean;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.action.contract.CustmrBusiContractUtil;
import com.fuiou.mgr.bps.BpsTransaction;
import com.fuiou.mgr.util.CustmrBusiValidator;
import com.fuiou.mgr.util.VPCDecodeUtil;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class OrderPayWorker implements Runnable {
	
	private IvrOrderInfService orderService = new IvrOrderInfService();
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();
	private TWebLogService webLogService = new TWebLogService();

	Socket socket;

	public OrderPayWorker(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		String str = null;
		try {
			BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
			byte[] buffer = new byte[2048];
			int len = inputStream.read(buffer);
			str = new String(buffer, 0, len);
			inputStream.close();
			doPay(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doPay(String respStr) throws Exception {
		if (StringUtils.isNotEmpty(respStr)) {
			int index = respStr.indexOf("\r\n\r\n");
			respStr = respStr.substring(index);
			respStr = VPCDecodeUtil.doCrypt("D", respStr);
			System.out.println(respStr);
			String[] strs = respStr.split("&");
			Map<String, String> map = new HashMap<String, String>();
			for (String s : strs) {
				map.put(s.split("=")[0], s.split("=")[1]);
			}
			if (map.get("RspCd") != null && "DJ0000".equals(map.get("RspCd"))) {
				String custInfo = map.get("CstmInfo");
				Base64Encoder base64Encoder = new Base64Encoder();
				byte[] b = base64Encoder.decode(custInfo);
				String tempStr = new String(b);
				String pinStr =  tempStr.substring(0, tempStr.length()-1)+"|}";
				String pin = new String(base64Encoder.encode(pinStr.getBytes()));
				pin = pin.replaceAll("\r\n", "");
				pin = pin.replaceAll("\n", "");
				TIvrOrderInf order = orderService.getOrder(map.get("OdrDtTm").substring(0, 8),map.get("OdrId"));
				TCustmrBusi custmrBusi = custmrBusiService.selectByAcntAndBusiCd(order.getMCHNT_CD(), TDataDictConst.BUSI_CD_INCOMEFOR,order.getACNT_NO(),CustmrBusiValidator.srcChnlMap.get(CustmrBusiValidator.SRC_CHNL_WEB));
				BpsUtilBean deductionResultBean = null,paymentResultBean = null;
				deductionResultBean = BpsTransaction.sendToUpmp(order,pin);//扣款结果
				if(BpsTransaction.SUCC_CODE.equals(deductionResultBean.getRespCode())){
					 //如果扣款成功则立即发一笔付款
					paymentResultBean = BpsTransaction.cupsPayment(custmrBusi,order.getORDER_AMT()+"");
					if(BpsTransaction.SUCC_CODE.equals(paymentResultBean.getRespCode())){
						//扣款结果入库
						TWebLog webLogDBean = webLogService.saveWebLogD(order, deductionResultBean);//扣款结果入库
						if (webLogDBean == null)
							throw new Exception("insert order fail,order num is " + order.getORDER_NO());
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
					}
				}

			}
		}
	}

	
	public static void main(String[] args) {
		String s = "e3x8fHx8WVNxeVRaZlRHOHR6UkdVcFpmWm5MSS9aVmJBaWltcTI0bXRWOTd5OG1NWFlXaWczWnR3b0JZaUlORjRsdkJaUnk2ZnZpK1ZBZWthRnpsT1BhTjFQSnhETzRGdHV6M0VQY2o0SVlhQUhmUmoyTVZNYVBTaFpNeE96cmExVCtsNWNpYXFTeGxKMXNhVFNROXBZOGx2cGpXSVJMek9DVnpYLzNWMy9wN2tCeWdvPXx9";
		Base64Encoder base64Encoder = new Base64Encoder();
		byte[] b = base64Encoder.decode(s);
		System.out.println(new String(b));
//		String[] strsa = new String(b).split("\\|"); 
//		String pin = strsa[5];
//		System.out.println(pin);
	}
}
