package com.fuiou.mgr.http.connector.incomefor;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.req.incomefor.InComeForReqType;
import com.fuiou.mer.model.rsp.incomefor.InComeForRspCoder;
import com.fuiou.mer.model.rsp.incomefor.InComeForRspType;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.AccessAdapter;
import com.fuiou.mgr.adapter.AccessAdapterFactory;
import com.fuiou.mgr.adapter.BusiAdapter;
import com.fuiou.mgr.adapter.BusiAdapterFactory;
import com.fuiou.mgr.bean.access.InComeForInfAccess;
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

/**
 * http直连接口收款交易处理类
 * yangliehui
 *
 */
public class HttpInComeForReqProcess implements HttpReqProcessInterface{
	private static Logger logger = LoggerFactory.getLogger(HttpInComeForReqProcess.class);
	@Override
	public String httpProcess(Object o,String mchnt,String busiCd) {
		InComeForRspType inComeForRspType = new InComeForRspType();
		String rspXml = "";
		try {
			InComeForReqType inComeForReqType = (InComeForReqType)o;
			String ver = inComeForReqType.getVer()==null?"":inComeForReqType.getVer().trim();			// 版本号
			String merdt = inComeForReqType.getMerdt()==null?"":inComeForReqType.getMerdt().trim();		// 请求日期 c(8)yyyyMMdd
			String orderno = inComeForReqType.getOrderno()==null?"":inComeForReqType.getOrderno().trim();	// 请求流水 c(10,30)  数字串，当天必须唯一
			String bankno = inComeForReqType.getBankno()==null?"":inComeForReqType.getBankno().trim();		// 总行代码 c(4) 参见总行代码表
			String accntno = inComeForReqType.getAccntno()==null?"":inComeForReqType.getAccntno().trim();	// 账号	c(10,28) 用户账号
			String accntnm = inComeForReqType.getAccntnm()==null?"":inComeForReqType.getAccntnm().trim();	// 账户名称	c(30) 	用户账户名称
			String amt = inComeForReqType.getAmt()==null?"": inComeForReqType.getAmt().trim();			// 金额	n(1,12)	单位：分
			String entseq = inComeForReqType.getEntseq()==null?"":inComeForReqType.getEntseq().trim();// 企业流水号    填写后，系统体现在交易查询和外部通知中
			String memo = inComeForReqType.getMemo()==null?"":inComeForReqType.getMemo().trim();// 备注   填写后，系统体现在交易查询和外部通知中
			String mobile = inComeForReqType.getMobile()==null?"":inComeForReqType.getMobile().trim();//手机号 为将来短信通知时使用 
			String certtp = inComeForReqType.getCerttp()==null?"":inComeForReqType.getCerttp().trim();//证件类型
			String certno = inComeForReqType.getCertno()==null?"":inComeForReqType.getCertno().trim();//证件号
			if(ver==null||"".equals(ver)){
				inComeForRspType.setRet(TDataDictConst.HTTP_VER_IS_NULL);
				inComeForRspType.setMemo("版本号为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(merdt == null || "".equals(merdt)){
				inComeForRspType.setRet(TDataDictConst.HTTP_MERDT_ER);
				inComeForRspType.setMemo("请求日期为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(orderno == null || "".equals(orderno)){
				inComeForRspType.setRet(TDataDictConst.HTTP_ORDERNO_ER);
				inComeForRspType.setMemo("请求流水为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(bankno==null||"".equals(bankno)){
				inComeForRspType.setRet(TDataDictConst.HTTP_BANKNO_ER);
				inComeForRspType.setMemo("总行代码为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(accntno==null||"".equals(accntno)){
				inComeForRspType.setRet(TDataDictConst.HTTP_ACCNTNO_ER);
				inComeForRspType.setMemo("账号为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(accntnm==null||"".equals(accntnm)){
				inComeForRspType.setRet(TDataDictConst.HTTP_ACCNTNM_ER);
				inComeForRspType.setMemo("账户名为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			if(amt==null||"".equals(amt)){
				inComeForRspType.setRet(TDataDictConst.HTTP_AMT_ER);
				inComeForRspType.setMemo("金额为空");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			String resulter=HttpValidate.validate(mchnt, merdt, orderno, bankno, null, null, accntno, accntnm, entseq, amt, memo, mobile, certtp, certno, 20);
			String[] items = resulter.split(TDataDictConst.FILE_CONTENT_APART, 2);
			if(items!=null&&items.length==2){
				inComeForRspType.setRet(items[0]);
				inComeForRspType.setMemo(items[1]);
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
			}
			amt=String.valueOf(FuMerUtil.formatFenToYuan(amt));// 金额--元
			
			TxnInfBean txnInfBean = null;
			BusiBean busiBean = null;
			FileError fileError = null;
			String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB;
			String txnDataType = "DATA";
			// 接入对象
			InComeForInfAccess accessBean = new InComeForInfAccess();
			accessBean.setBankCd(bankno);
			accessBean.setBankAccount(accntno);
			accessBean.setAccountName(accntnm);
			accessBean.setAmount(amt);
			accessBean.setEnpSeriaNo(entseq);
			accessBean.setMemo(memo);
			accessBean.setMobile(mobile);
			accessBean.setMchntCd(mchnt);
			accessBean.setOprUsrId("");
			accessBean.setMerdt(merdt);
			accessBean.setOrderno(orderno);
			accessBean.setCertificateTp(certtp);//2.04版新增证件类型
			accessBean.setCertificateNo(certno);//2.04版新增证件类号
			// 通过接入适配器 传入接入对象 返回中间对象
			AccessAdapter accessAdapter = AccessAdapterFactory.getAccessAdapter(busiCd);
			txnInfBean = accessAdapter.analysisTxn(mchnt, accessBean, srcModuleCd, txnDataType);
			// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
			BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
			busiAdapter.analysisTxnInfBean(mchnt, txnInfBean, srcModuleCd, txnDataType);
			busiBean = busiAdapter.getBusiBean();
			fileError = busiAdapter.getFileError();
			// 检查银行代码
	        if (SystemParams.bankMap.get(bankno)==null || SystemParams.getProperty("BANK_CDS_INCOME_FOR_GZ_UNION_PAY").indexOf(bankno)==-1) {
	        	inComeForRspType.setRet(TDataDictConst.INTER_BANK_CD_ER);
				inComeForRspType.setMemo("银行代码失败,不支持此行");
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
				logger.error("return xml[" + rspXml + "]");
				return rspXml;
	        }
	        // 检查银行账号
	        TCardBin tCardBin = CardUtil.getTCardBinByCardNo(accntno, SystemParams.cardBinMap);
	        //获取发卡机构号issCd，校验开户行代码substring(2,6)与总行代码是否一致,
	        String issCd = CardUtil.getIssCdByCardNo(accntno, SystemParams.cardBinMap);
	        if(tCardBin!=null){ //卡BIN不为空，需要校验用户总行代号和卡BIN表里的是否一致
	            if(null == issCd || issCd.length()!=10){
	            	inComeForRspType.setRet(TDataDictConst.FILE_BANK_ACCOUNTS_ER);
					inComeForRspType.setMemo("未获取到卡bin值");
					rspXml = InComeForRspCoder.marshal(inComeForRspType);
					logger.error("return xml[" + rspXml + "]");
					return rspXml;
	            }
	        }else{ //卡BIN为空，找到总行代号对应的开户行代号 4位转10位
	            TRootBankInf tRootBankInf = SystemParams.bankMap.get(bankno);
	            if(tRootBankInf==null){
	            	inComeForRspType.setRet(TDataDictConst.INTER_BANK_CD_ER);
					inComeForRspType.setMemo("匹配银行代码失败,不支持此行");
					rspXml = InComeForRspCoder.marshal(inComeForRspType);
					logger.error("return xml[" + rspXml + "]");
					return rspXml;
	            }
	        }
	    
	   
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
			txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType, false);
			FileError fileErrorFromDbService = txnDbService.getFileError();
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
										String errMemo = rowColumnError.getErrMemo();
										String errCode = rowColumnError.getErrCode();
										inComeForRspType.setRet(errCode);
										inComeForRspType.setMemo(errMemo);
										rspXml = InComeForRspCoder.marshal(inComeForRspType);
										logger.error("return xml[" + rspXml + "]");
										return rspXml;
									}
								}
							}
						}
					}
					logger.error(fileError.getFileSumError().getErrCode()+" "+fileError.getFileSumError().getErrMemo());
					inComeForRspType.setRet(fileError.getFileSumError().getErrCode());
					inComeForRspType.setMemo(fileErrorFromDbService.getFileSumError().getErrMemo());
					rspXml = InComeForRspCoder.marshal(inComeForRspType);
					logger.error("return xml[" + rspXml + "]");
					return rspXml;
				}
			}
			inComeForRspType.setRet(TDataDictConst.HTTP_SUCCEED);
			inComeForRspType.setMemo("富友受理成功");
			rspXml = InComeForRspCoder.marshal(inComeForRspType);
			logger.info("return xml[" + rspXml + "]");
			return rspXml;
		} catch (Exception e) {
			e.printStackTrace();
			inComeForRspType.setRet(TDataDictConst.UNSUCCESSFUL);
			inComeForRspType.setMemo("系统异常");
			try {
				rspXml = InComeForRspCoder.marshal(inComeForRspType);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			logger.error("return xml[" + rspXml + "]");
			return rspXml;
		}
	}
}
