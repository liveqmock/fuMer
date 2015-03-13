package com.fuiou.mgr.http.connector.verify;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.req.verify.VerifyReqType;
import com.fuiou.mer.model.rsp.verify.VerifyRspCoder;
import com.fuiou.mer.model.rsp.verify.VerifyRspType;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.VerifyInfAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.fuiou.mgr.http.connector.HttpReqProcessInterface;
import com.fuiou.mgr.http.util.HttpValidate;
import com.fuiou.mgr.http.util.SocketClient;

/**
 * http直连接口验证交易处理类
 * yangliehui
 *
 */
public class HttpVerifyReqProcess implements HttpReqProcessInterface{
	private static Logger logger = LoggerFactory.getLogger(HttpVerifyReqProcess.class);
	private TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
	@Override
	public String httpProcess(Object o,String mchnt,String busiCd) {
		VerifyRspType verifyRspType = new VerifyRspType();
		String rspXml = "";
		try {
			VerifyReqType verifyReqType = (VerifyReqType)o;
			String ver = verifyReqType.getVer()==null?"":verifyReqType.getVer().trim();			// 版本号
			String merdt = verifyReqType.getMerdt()==null?"":verifyReqType.getMerdt().trim();		// 请求日期 c(8)yyyyMMdd
			String orderno = verifyReqType.getOrderno()==null?"":verifyReqType.getOrderno().trim();	// 请求流水 c(10,30)  数字串，当天必须唯一
			String bankno = verifyReqType.getBankno()==null?"":verifyReqType.getBankno().trim();		// 总行代码 c(4) 参见总行代码表
			String accntno = verifyReqType.getAccntno()==null?"":verifyReqType.getAccntno().trim();	// 账号	c(10,28) 用户账号
			String accntnm = verifyReqType.getAccntnm()==null?"":verifyReqType.getAccntnm().trim();	// 账户名称	c(30) 	用户账户名称
			String certtp = verifyReqType.getCerttp()==null?"":verifyReqType.getCerttp().trim();		// 证件类型
			String certno = verifyReqType.getCertno()==null?"":verifyReqType.getCertno().trim();		// 证件名称
			
			if(ver==null||"".equals(ver)){
				verifyRspType.setRet(TDataDictConst.HTTP_VER_IS_NULL);
				verifyRspType.setMemo("版本号为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(merdt == null || "".equals(merdt)){
				verifyRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
				verifyRspType.setMemo("请求日期为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(orderno == null || "".equals(orderno)){
				verifyRspType.setRet(TDataDictConst.HTTP_ORDERNO_ER);
				verifyRspType.setMemo("请求流水为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(bankno==null||"".equals(bankno)){
				verifyRspType.setRet(TDataDictConst.HTTP_BANKNO_ER);
				verifyRspType.setMemo("总行代码为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(accntno==null||"".equals(accntno)){
				verifyRspType.setRet(TDataDictConst.HTTP_ACCNTNO_ER);
				verifyRspType.setMemo("账号为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(accntnm==null||"".equals(accntnm)){
				verifyRspType.setRet(TDataDictConst.HTTP_ACCNTNM_ER);
				verifyRspType.setMemo("账户名为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(certtp==null||"".equals(certtp)){
				verifyRspType.setRet(TDataDictConst.HTTP_CERTTP_ER);
				verifyRspType.setMemo("证件类型为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(certno==null||"".equals(certno)){
				verifyRspType.setRet(TDataDictConst.HTTP_CARD_ER);
				verifyRspType.setMemo("证件号为空");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			String resulter=HttpValidate.validate(mchnt, merdt, orderno, bankno, null, null, accntno, accntnm, null, null, null, null, certtp, certno, 20);
			String[] items = resulter.split(TDataDictConst.FILE_CONTENT_APART, 2);
			if(items!=null&&items.length==2){
				verifyRspType.setRet(items[0]);
				verifyRspType.setMemo(items[1]);
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(mchnt, "1");
			String mchntTp = tInsMchntInf.getMCHNT_TP();
			// 判断商户是否为----手续费实时结算商户
			if (mchntTp.startsWith("D6")) {
				verifyRspType.setRet(TDataDictConst.HTTP_TXN_FEE_AMT_ER);
				verifyRspType.setMemo("该商户为手续费实时结算商户，不能进行实名验证");
				rspXml = VerifyRspCoder.marshal(verifyRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			TxnInfBean txnInfBean = null;
			BusiBean busiBean = null;
			FileError fileError = null;
			String srcModuleCd = TDataDictConst.SRC_MODULE_CD_HTTP;
			String txnDataType = "DATA";
			// 接入对象
			VerifyInfAccess verifyInfAccess = new VerifyInfAccess();
			verifyInfAccess.setAccntnm(accntnm);
			verifyInfAccess.setAccntno(accntno);
			verifyInfAccess.setBankno(bankno);
			verifyInfAccess.setBusiCd(busiCd);
			verifyInfAccess.setCertno(certno);
			verifyInfAccess.setCerttp(certtp);
			verifyInfAccess.setMchntCd(mchnt);
			verifyInfAccess.setOprUsrId("");
			verifyInfAccess.setMerdt(merdt);
			verifyInfAccess.setOrderno(orderno);
			// 通过接入适配器 传入接入对象 返回中间对象
			AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
			txnInfBean = accessAdapter.analysisTxn(mchnt, verifyInfAccess, srcModuleCd, txnDataType);
			// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
			BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
			busiAdapter.analysisTxnInfBean(mchnt, txnInfBean, srcModuleCd, txnDataType);
			busiBean = busiAdapter.getBusiBean();
			fileError = busiAdapter.getFileError();
			
			List<FileRowError> fileRowErrorList = fileError.getFileRowErrors();
			if(fileRowErrorList != null){
				for(int i = 0;i<fileRowErrorList.size();i++){
					FileRowError fileRowError = fileRowErrorList.get(i);
					if(fileRowError.getColumnErrors().size() > 0){
						RowColumnError rowColumnError = fileRowError.getColumnErrors().get(0);
						verifyRspType.setRet(rowColumnError.getErrCode());
						verifyRspType.setMemo(rowColumnError.getErrMemo());
						rspXml = VerifyRspCoder.marshal(verifyRspType);
						return rspXml;
					}
				}
			}
			// 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
			// 拆分交易
			SplitTrade splitTrade = new SplitTrade();
			splitTrade.splitTrade(busiBean);
			ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean = splitTrade.getForTxnFeeAmtBean();
			BusiBean operationVerifyErrorBusibean = splitTrade.getOperationVerifyErrorBusibean();
			
			// 计算手续费、路由
			TradeCalculate tradeCalculate = new TradeCalculate();
			ForAccessTxnFeeAmtRutBean calculateForTxnFeeAmtBean = tradeCalculate.doCalculate(forTxnFeeAmtBean);
			
			// 组合业务对象
			PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
			BusiBean packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
			
			TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
			txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType,true);
			
			List<FileRowError> fileRowOperateErrors = txnDbService.getFileRowOperateErrors();
			if(fileRowOperateErrors.size() != 0){
				for(int i = 0;i<fileRowOperateErrors.size();i++){
					FileRowError fileRowError = fileRowOperateErrors.get(i);
					if(fileRowError.getColumnErrors().size() > 0){
						RowColumnError rowColumnError = fileRowError.getColumnErrors().get(0);
						verifyRspType.setRet(rowColumnError.getErrCode());
						verifyRspType.setMemo(rowColumnError.getErrMemo());
						rspXml = VerifyRspCoder.marshal(verifyRspType);
						return rspXml;
					}
				}
			}
			FileError fileErrorFromDbService = txnDbService.getFileError();
			if(fileErrorFromDbService.getFileSumError() != null){
				if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
					fileRowErrorList = fileErrorFromDbService.getFileRowErrors();
					if(fileRowErrorList != null){
						if(fileRowErrorList.size()>0){
							for(FileRowError fileRowError : fileRowErrorList){
								List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
								if(rowColumnErrorList != null){
									if(rowColumnErrorList.size() > 0){
										RowColumnError rowColumnError = rowColumnErrorList.get(0);
										String errMemo = rowColumnError.getErrMemo();
										String errCode = rowColumnError.getErrCode();
										verifyRspType.setRet(errCode);
										verifyRspType.setMemo(errMemo);
										rspXml = VerifyRspCoder.marshal(verifyRspType);
										logger.error("return xml[" + rspXml + "]");
										return rspXml;
									}
								}
							}
						}
					}
					logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
					verifyRspType.setRet(fileError.getFileSumError().getErrCode());
					verifyRspType.setMemo(fileErrorFromDbService.getFileSumError().getErrMemo());
					rspXml = VerifyRspCoder.marshal(verifyRspType);
					logger.error("return xml[" + rspXml + "]");
					return rspXml;
				}
			}
			rspXml = SocketClient.sendToAps(mchnt, merdt, orderno);
			logger.info("return xml[" + rspXml + "]");
			return rspXml;
		} catch (Exception e) {
			e.printStackTrace();
			verifyRspType.setRet(TDataDictConst.UNSUCCESSFUL);
			verifyRspType.setMemo("系统异常");
			try {
				rspXml = VerifyRspCoder.marshal(verifyRspType);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			logger.error("return xml[" + rspXml + "]");
			return rspXml;
		}
	}
}
