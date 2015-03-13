package com.fuiou.mgr.http.connector.payfor;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.req.payfor.PayForReqType;
import com.fuiou.mer.model.rsp.incomefor.InComeForRspCoder;
import com.fuiou.mer.model.rsp.payfor.PayForRspCoder;
import com.fuiou.mer.model.rsp.payfor.PayForRspType;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.PayForInfAccess;
import com.fuiou.mgr.bean.business.BusiBean;
import com.fuiou.mgr.bean.business.FileError;
import com.fuiou.mgr.bean.business.FileRowError;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
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

/**
 * http直连接口付款交易处理类
 * yangliehui
 *
 */
public class HttpPayForReqProcess implements HttpReqProcessInterface{
	private static Logger logger = LoggerFactory.getLogger(HttpPayForReqProcess.class);
	@Override
	public String httpProcess(Object o,String mchnt,String busiCd) {
		PayForRspType payForRspType = new PayForRspType();
		String rspXml = "";
		try {
			PayForReqType payForReqType = (PayForReqType)o;
			String ver = payForReqType.getVer()==null?"":payForReqType.getVer().trim();			// 版本号
			String merdt = payForReqType.getMerdt()==null?"":payForReqType.getMerdt().trim();		// 请求日期 c(8)yyyyMMdd
			String orderno = payForReqType.getOrderno()==null?"":payForReqType.getOrderno().trim();	// 请求流水 c(10,30)  数字串，当天必须唯一
			String bankno = payForReqType.getBankno()==null?"":payForReqType.getBankno().trim();		// 总行代码 c(4) 参见总行代码表
			String cityno = payForReqType.getCityno()==null?"":payForReqType.getCityno().trim();		// 城市代码 c(4) 参见地市代码表
			String branchnm = payForReqType.getBranchnm()==null?"":payForReqType.getBranchnm().trim();	// 支行名称c(100) 支行名称，中行、建行、广发必填
			String accntno = payForReqType.getAccntno()==null?"":payForReqType.getAccntno().trim();	// 账号	c(10,28) 用户账号
			String accntnm = payForReqType.getAccntnm()==null?"":payForReqType.getAccntnm().trim();	// 账户名称	c(30) 	用户账户名称
			String amt = payForReqType.getAmt()==null?"": payForReqType.getAmt().trim();			// 金额	n(1,12)	单位：分
			String entseq = payForReqType.getEntseq()==null?"":payForReqType.getEntseq().trim();// 企业流水号    填写后，系统体现在交易查询和外部通知中
			String memo = payForReqType.getMemo()==null?"":payForReqType.getMemo().trim();// 备注   填写后，系统体现在交易查询和外部通知中
			String mobile = payForReqType.getMobile()==null?"":payForReqType.getMobile().trim();//手机号 为将来短信通知时使用 
			String amtFen = "0";
			if(ver==null||"".equals(ver)){
				payForRspType.setRet(TDataDictConst.HTTP_VER_IS_NULL);
				payForRspType.setMemo("版本号为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(merdt == null || "".equals(merdt)){
				payForRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
				payForRspType.setMemo("请求日期为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(orderno == null || "".equals(orderno)){
				payForRspType.setRet(TDataDictConst.HTTP_ORDERNO_ER);
				payForRspType.setMemo("请求流水为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(bankno==null||"".equals(bankno)){
				payForRspType.setRet(TDataDictConst.HTTP_BANKNO_ER);
				payForRspType.setMemo("总行代码为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(cityno==null||"".equals(cityno)){
				payForRspType.setRet(TDataDictConst.HTTP_CITY_ER);
				payForRspType.setMemo("城市代码为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(accntno==null||"".equals(accntno)){
				payForRspType.setRet(TDataDictConst.HTTP_ACCNTNO_ER);
				payForRspType.setMemo("账号为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(accntnm==null||"".equals(accntnm)){
				payForRspType.setRet(TDataDictConst.HTTP_ACCNTNM_ER);
				payForRspType.setMemo("账户名为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(amt==null||"".equals(amt)){
				payForRspType.setRet(TDataDictConst.HTTP_AMT_ER);
				payForRspType.setMemo("金额为空");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			String resulter=HttpValidate.validate(mchnt, merdt, orderno, bankno, cityno, branchnm, accntno, accntnm, entseq, amt, memo, mobile, null, null, 0);
			String[] items = resulter.split(TDataDictConst.FILE_CONTENT_APART, 2);
			if(items!=null&&items.length==2){
				payForRspType.setRet(items[0]);
				payForRspType.setMemo(items[1]);
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			amt=String.valueOf(FuMerUtil.formatFenToYuan(amt));// 金额--元
			
			BusiBean busiBean = null;
			FileError fileError = null;
			TxnInfBean txnInfBean = null;
			String srcModuleCd = TDataDictConst.SRC_MODULE_CD_HTTP;
			String txnDataType = "DATA";
			try {
				// 接入对象
				PayForInfAccess accessBean = new PayForInfAccess();
				accessBean.setBankCd(bankno.trim());
				accessBean.setBankAccount(accntno.trim());
				accessBean.setAccountName(accntnm.trim());
				accessBean.setAmount(amt.trim());
				accessBean.setEnpSeriaNo(entseq.trim());
				accessBean.setMemo(memo.trim());
				accessBean.setMobile(mobile.trim());
				accessBean.setBankNam(branchnm.trim());
				accessBean.setCityCode(cityno.trim());
				accessBean.setMchntCd(mchnt.trim());
				accessBean.setBusiCd(busiCd.trim());
				accessBean.setOprUsrId("");
				accessBean.setMerdt(merdt.trim());
				accessBean.setOrderno(orderno.trim());
				// 通过接入适配器 传入接入对象 返回中间对象
				AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
				txnInfBean = accessAdapter.analysisTxn(mchnt, accessBean, srcModuleCd, txnDataType);
				// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
				BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
				busiAdapter.analysisTxnInfBean(mchnt, txnInfBean, srcModuleCd, txnDataType);
				busiBean = busiAdapter.getBusiBean();
				fileError = busiAdapter.getFileError();
			} catch (Exception e) {
				e.printStackTrace();
				payForRspType.setRet(TDataDictConst.UNSUCCESSFUL);
				payForRspType.setMemo("系统异常");
				try {
					rspXml = PayForRspCoder.marshal(payForRspType);
				} catch (JAXBException e1) {
					e1.printStackTrace();
				}
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			TRootBankInf tRootBankInf = SystemParams.bankMap.get(bankno);
			if(tRootBankInf==null){
				payForRspType.setRet(TDataDictConst.INTER_BANK_CD_ER);
				payForRspType.setMemo("匹配银行代码失败,不支持此行");
				try {
					rspXml = PayForRspCoder.marshal(payForRspType);
				} catch (JAXBException e1) {
					e1.printStackTrace();
				}
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			// 拆分交易
			SplitTrade splitTrade = new SplitTrade();
			splitTrade.splitTrade(busiBean);
			ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean = splitTrade.getForTxnFeeAmtBean();
			if(forTxnFeeAmtBean.getPayForList().size()==0){
//				payForRspType.setRet(TDataDictConst.BUSI_ERROR);
				payForRspType.setRet("111169");
				payForRspType.setMemo("业务校验失败");
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			BusiBean operationVerifyErrorBusibean = splitTrade.getOperationVerifyErrorBusibean();
			// 计算手续费、路由
			TradeCalculate tradeCalculate = new TradeCalculate();
			ForAccessTxnFeeAmtRutBean calculateForTxnFeeAmtBean = tradeCalculate.doCalculate(forTxnFeeAmtBean);
			List<PayForBusiDetailRowBean> payForBusiDetailRowBeanList = calculateForTxnFeeAmtBean.getPayForList();
			
			String resultER=HttpValidate.validate2(payForBusiDetailRowBeanList);
			String[] items2 = resultER.split(TDataDictConst.FILE_CONTENT_APART, 2);
			if(items2!=null&&items2.length==2){
				payForRspType.setRet(items2[0]);
				payForRspType.setMemo(items2[1]);
				rspXml = PayForRspCoder.marshal(payForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			// 组合业务对象
			PackagingBusiBean packagingBusiBean = new PackagingBusiBean();
			BusiBean packBusiBean = packagingBusiBean.toPackaging(operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);
			//判余额，并预授权交易
			tradeCalculate.checkAccountBalance(packBusiBean, fileError,srcModuleCd);
			TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
			txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType, false);
			FileError fileErrorFromDbService = txnDbService.getFileError();
			// 响应用户
			if(fileErrorFromDbService.getFileSumError() != null){
				if(fileErrorFromDbService.getFileSumError().getErrCode() != null || fileErrorFromDbService.getFileSumError().getErrMemo() != null){
					List<FileRowError> fileRowErrorList = fileErrorFromDbService.getFileRowErrors();
					if(fileRowErrorList != null){
						if(fileRowErrorList.size()>0){
							for(FileRowError fileRowError : fileRowErrorList){
								List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
								if(rowColumnErrorList != null){
									if(rowColumnErrorList.size() > 0){
										RowColumnError rowColumnError = rowColumnErrorList.get(0);
										String errCode = rowColumnError.getErrCode();
										String errMemo = rowColumnError.getErrMemo();
										payForRspType.setRet(errCode);
										payForRspType.setMemo(errMemo);
										try {
											rspXml = PayForRspCoder.marshal(payForRspType);
										} catch (JAXBException e1) {
											e1.printStackTrace();
										}
										logger.error("return xml[" + rspXml + "]");
										return rspXml;
									}
								}
							}
						}
					}
					logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
					payForRspType.setRet(fileError.getFileSumError().getErrCode());
					payForRspType.setMemo(fileError.getFileSumError().getErrMemo());
					try {
						rspXml = PayForRspCoder.marshal(payForRspType);
					} catch (JAXBException e1) {
						e1.printStackTrace();
					}
					logger.error("return xml[" + rspXml + "]");
					return rspXml;
				}
			}
			payForRspType.setRet(TDataDictConst.HTTP_SUCCEED);
			payForRspType.setMemo("成功");
			try {
				rspXml = PayForRspCoder.marshal(payForRspType);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			logger.info("return xml[" + rspXml + "]");
			return rspXml;
		} catch (Exception e) {
			e.printStackTrace();
			payForRspType.setRet(TDataDictConst.UNSUCCESSFUL);
			payForRspType.setMemo("系统异常");
			try {
				rspXml = PayForRspCoder.marshal(payForRspType);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			logger.error("return xml[" + rspXml + "]");
			return rspXml;
		}
	}
}
