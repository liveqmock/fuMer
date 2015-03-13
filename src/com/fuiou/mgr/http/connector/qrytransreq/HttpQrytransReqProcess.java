package com.fuiou.mgr.http.connector.qrytransreq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.req.qrytrans.QryTransReqType;
import com.fuiou.mer.model.rsp.qrytrans.QryTransRspCoder;
import com.fuiou.mer.model.rsp.qrytrans.QryTransRspType;
import com.fuiou.mer.model.rsp.qrytrans.Trans;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.http.connector.HttpReqProcessInterface;

public class HttpQrytransReqProcess implements HttpReqProcessInterface{
	private static Logger logger = LoggerFactory.getLogger(HttpQrytransReqProcess.class);
	@Override
	public String httpProcess(Object o, String mchnt, String busiCd) {
		TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
		QryTransRspType qryTransRspType = new QryTransRspType();
		String rspXml = "";
		try {
			List<Trans> trans = qryTransRspType.getTrans();
			QryTransReqType qryTransReqType = (QryTransReqType)o;
			String ver = (qryTransReqType.getVer()==null)?"":qryTransReqType.getVer().trim();				// 版本号
			String busicd = (qryTransReqType.getBusicd()==null)?"":qryTransReqType.getBusicd().trim();		// 业务代码
			String orderno = (qryTransReqType.getOrderno()==null)?"":qryTransReqType.getOrderno().trim();	// 原请求流水
			String startdt = (qryTransReqType.getStartdt()==null)?"":qryTransReqType.getStartdt().trim();	// 开始日期
			String enddt = (qryTransReqType.getEnddt()==null)?"":qryTransReqType.getEnddt().trim();			// 结束日期
			String transst = (qryTransReqType.getTransst()==null)?"":qryTransReqType.getTransst().trim();	// 交易状态
			if(StringUtils.isEmpty(ver)){
				qryTransRspType.setRet(TDataDictConst.HTTP_VER_IS_NULL);
				qryTransRspType.setMemo("版本号为空");
				rspXml = QryTransRspCoder.marshal(qryTransRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(StringUtils.isEmpty(busiCd)){
				qryTransRspType.setRet(TDataDictConst.HTTP_BUSICD_ER);
				qryTransRspType.setMemo("业务代码为空");
				rspXml = QryTransRspCoder.marshal(qryTransRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(StringUtils.isEmpty(startdt)){
				qryTransRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
				qryTransRspType.setMemo("起始日期为空");
				rspXml = QryTransRspCoder.marshal(qryTransRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(StringUtils.isEmpty(enddt)){
				qryTransRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
				qryTransRspType.setMemo("截止日期为空");
				rspXml = QryTransRspCoder.marshal(qryTransRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			try{
				Date start = FuMerUtil.string2date(startdt, "yyyyMMdd");
				Date end = FuMerUtil.string2date(enddt, "yyyyMMdd");
				if(start.after(end)){
					qryTransRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
					qryTransRspType.setMemo("截止日期不能小于起始日期");
					rspXml = QryTransRspCoder.marshal(qryTransRspType);
					logger.error("return xml[" + rspXml + "]");
					return rspXml;
				}
				if(end.after(new Date())){
					qryTransRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
					qryTransRspType.setMemo("截止日期不能大于当前时间");
					rspXml = QryTransRspCoder.marshal(qryTransRspType);
					logger.error("return xml[" + rspXml + "]");
				}
				if(end.getTime() - start.getTime() > 15*24*3600*1000){//不能超过15天
					qryTransRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
					qryTransRspType.setMemo("日期范围不能超过15天");
					rspXml = QryTransRspCoder.marshal(qryTransRspType);
					logger.error("return xml[" + rspXml + "]");
				}
			}catch (Exception e) {
				e.printStackTrace();
				logger.debug("date format error");
			}
			List<TApsTxnLog> tApsTxnLogList = tApsTxnLogService.getHttpTApsTxnLogs(mchnt, busicd, orderno, startdt, enddt, transst);
			List<Trans> transList = this.getTrans(tApsTxnLogList, busicd);
			for(Trans tran : transList){
				trans.add(tran);
			}
			if(transList.size()<=0){
				qryTransRspType.setRet(TDataDictConst.SELECT_DATA_NULL_ER);
				qryTransRspType.setMemo("未查询到符合条件的信息");
				rspXml = QryTransRspCoder.marshal(qryTransRspType);
				return rspXml;
			}
			qryTransRspType.setRet(TDataDictConst.HTTP_SUCCEED);
			qryTransRspType.setMemo("成功");
			rspXml = QryTransRspCoder.marshal(qryTransRspType);
			return rspXml;
		} catch (Exception e) {
			qryTransRspType.setRet(TDataDictConst.UNSUCCESSFUL);
			qryTransRspType.setMemo("系统异常");
			try {
				rspXml = QryTransRspCoder.marshal(qryTransRspType);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			return rspXml;
		}
	}

	/**
	 * 获取交易结果集合
	 * @param tApsTxnLogs
	 * @param busiCd
	 * @return
	 */
	public List<Trans> getTrans(List<TApsTxnLog> tApsTxnLogs,String busiCd){
		List<Trans> transList = new ArrayList<Trans>();
		if(tApsTxnLogs != null && tApsTxnLogs.size() > 0){
			for(TApsTxnLog tApsTxnLog : tApsTxnLogs){
				Trans trans = new Trans();
				trans.setAccntnm(tApsTxnLog.getID_NO());
				String accntno = "";
				if(tApsTxnLog.getBUSI_CD().trim().equals(TDataDictConst.BUSI_CD_INCOMEFOR)){
					try {
						accntno = tApsTxnLog.getDEBIT_ACNT_NO().substring(2);
					} catch (Exception e) {
						accntno = "";
						e.printStackTrace();
					}
				}else if(tApsTxnLog.getBUSI_CD().trim().equals(TDataDictConst.BUSI_CD_PAYFOR) ||
						tApsTxnLog.getBUSI_CD().trim().equals(TDataDictConst.BUSI_CD_REFUND_TICKET)){
					try {
						accntno = tApsTxnLog.getCREDIT_ACNT_NO().substring(2);
					} catch (Exception e) {
						accntno = "";
						e.printStackTrace();
					}
				}else if(tApsTxnLog.getBUSI_CD().trim().equals(TDataDictConst.BUSI_CD_VERIFY)){
					try {
						accntno = tApsTxnLog.getDEBIT_ACNT_NO().substring(2);
					} catch (Exception e) {
						accntno = "";
						e.printStackTrace();
					}
				}
				trans.setAccntno(accntno);
				String feeMd = tApsTxnLog.getAUTH_RSP_CD();
				Long amt = 0l;
				if("0".equals(feeMd)){
					amt = tApsTxnLog.getSRC_TXN_AMT() - tApsTxnLog.getTXN_FEE_AMT();
				}else {
					amt = tApsTxnLog.getSRC_TXN_AMT();
				}
				trans.setAmt(amt.toString());
				String icflds = tApsTxnLog.getIC_FLDS();
				String entseq = "";
				String memo = "";
				String[] icfldsArray = null;
				try {
					icfldsArray = icflds.split(TDataDictConst.FILE_CONTENT_APART, 3);
					entseq = icfldsArray[0];
				} catch (Exception e) {
					entseq = "";
					e.printStackTrace();
				}
				try {
					memo = icfldsArray[1];
				} catch (Exception e) {
					memo = "";
					e.printStackTrace();
				}
				trans.setEntseq(entseq);
				trans.setMemo(memo);
				trans.setMerdt(tApsTxnLog.getORIG_ORDER_DT());
				trans.setOrderno(tApsTxnLog.getORIG_SRC_ORDER_NO());
				trans.setReason(tApsTxnLog.getADDN_PRIV_DATA());
				
				String resultSt = "";
				String custmrTp =tApsTxnLog.getCUSTMR_TP();
				String destTxnSt = tApsTxnLog.getDEST_TXN_ST();
				resultSt=TDataDictConst.getStatusDescption(busiCd, destTxnSt, custmrTp);
				trans.setResult(resultSt);
				if(TDataDictConst.BUSI_CD_PAYFOR.equals(tApsTxnLog.getBUSI_CD()) && (TDataDictConst.TXN_DEST_RES_FAIL.equals(destTxnSt)||TDataDictConst.TXN_DEST_TIMEOUT.equals(destTxnSt))){//如果是付款，则至告诉商户0、3、1三种状态
					trans.setState(TDataDictConst.TXN_DEST_RES_SUC);//交易状态
				}else{
					trans.setState(destTxnSt);
				}
				transList.add(trans);
			}
		}
		return transList;
	}
	
	
	public static void main(String[] args) {
		Date start = FuMerUtil.string2date("20140409", "yyyyMMdd");
		Date end = FuMerUtil.string2date("20140409", "yyyyMMdd");
		System.out.println(start.after(end));
	}
}
