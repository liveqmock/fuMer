package com.fuiou.mgr.http.connector;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.http.connector.incomefor.HttpContractReqProcess;
import com.fuiou.mgr.http.connector.incomefor.HttpInComeForReqProcess;
import com.fuiou.mgr.http.connector.incomefor.HttpSInComeForReqProcess;
import com.fuiou.mgr.http.connector.payfor.HttpPayForReqProcess;
import com.fuiou.mgr.http.connector.qrytransreq.HttpQrytransReqProcess;
import com.fuiou.mgr.http.connector.verify.HttpVerifyReqProcess;
/**
 * http直连接口交易处理工厂类
 * yangliehui
 *
 */
public class HttpReqFactory {
	/**
	 * 获取处理对象
	 * @param reqtype	请求类型
	 * @return
	 */
	public static HttpReqProcessInterface getTxnProcessClass(String reqtype){
		if(reqtype.equals(TDataDictConst.REQTP_INCOMEFORREQ)){
			return new HttpInComeForReqProcess();
		}else if(reqtype.equals(TDataDictConst.REQTP_SINCOMEFORREQ)){
			return new HttpSInComeForReqProcess();
		}else if(reqtype.equals(TDataDictConst.REQTP_PAYFORREQ)){
			return new HttpPayForReqProcess();
		}else if(reqtype.equals(TDataDictConst.REQTP_VERIFYREQ)){
			return new HttpVerifyReqProcess();
		}else if(reqtype.equals(TDataDictConst.REQTP_QRYTRANSREQ)){
			return new HttpQrytransReqProcess();
		}else if(reqtype.equals(TDataDictConst.REQTP_ADDCUSTMRBUSI)){
			return new HttpContractReqProcess();
		}
		return null;
	}
}
