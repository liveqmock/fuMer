package com.fuiou.mgr.action.withdeposit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TCustCwdAcnt;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TCustCwdAcntService;
import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.StringUtils;
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
import com.fuiou.mgr.bean.business.FileSumError;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.OperationBean;
import com.fuiou.mgr.bean.business.PayForBusiBean;
import com.fuiou.mgr.bean.business.RowColumnError;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.busi.dbservice.TxnDbService;
import com.fuiou.mgr.busi.dbservice.TxnDbServiceFactory;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.PackagingBusiBean;
import com.fuiou.mgr.doTransaction.Access.SplitTrade;
import com.fuiou.mgr.doTransaction.Access.TradeCalculate;
import com.fuiou.mgr.util.Formator;
import com.fuiou.mgr.util.VirtAcntUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class WithDePositAction extends ActionSupport {
	private static final long serialVersionUID = 573961637625441677L;
	private static Logger logger = LoggerFactory
			.getLogger(WithDePositAction.class);

	private PayForInfAccess icfia = new PayForInfAccess();
	private String srcModuleCd = TDataDictConst.SRC_MODULE_CD_WEB; // 文件来源
	private String busiCd = TDataDictConst.BUSI_CD_PAYFOR;// 业务代码
	private String txnDataType = "DATA"; // 交易数据类型
	private TxnInfBean txnInfBean;
	private BusiBean busiBean;
	private FileError fileError;
	
	/** 错误信息 */
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();

	public LinkedHashMap<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(LinkedHashMap<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	private String feeAmt;// 手续费
	private String srcAmt;// 原金额
	private String destAmt;// 目标金额
	private String feeMsg;
	private String isDisplayFeeMsg;
	private String msg = "";

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	private String forward;
	private String frame;
	private String issBankName;
	private String sumAmt;//可用余额
	
	private List<String> acntNos;//虚拟账户

	public String getSumAmt() {
		return sumAmt;
	}

	public void setSumAmt(String sumAmt) {
		this.sumAmt = sumAmt;
	}

	public String getIssBankName() {
		return issBankName;
	}

	public void setIssBankName(String issBankName) {
		this.issBankName = issBankName;
	}

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	private BusiBean packBusiBean;
	private BusiBean busiBeanProcess;
	private TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	
	private TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();

	public PayForInfAccess getIcfia() {
		return icfia;
	}

	public void setIcfia(PayForInfAccess icfia) {
		this.icfia = icfia;
	}
	public String submitWith(){
		TRootBankInf tRootBankInf=SystemParams.bankMap.get(icfia.getBankCd());
		if(tRootBankInf!=null){
			ActionContext.getContext().put("BANK_NM", tRootBankInf.getBANK_NM());
		}
		ActionContext.getContext().put("issBankName", issBankName);
		ActionContext.getContext().put("icfia", icfia);
		ActionContext.getContext().put("sumAmt", sumAmt);
		return "confirmWith";
	}
	public String validater() {
		// 商户信息
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(ServletActionContext.HTTP_REQUEST);
		HttpSession session = request.getSession();
		TOperatorInf tOperatorInf = (TOperatorInf) session
				.getAttribute(TDataDictConst.OPERATOR_INF);
		String mchntCd = tOperatorInf.getMCHNT_CD();
		TInsMchntInf tInsMchntInf = tInsMchntInfService.selectTInsInfByMchntCd(mchntCd);
//		TCustCwdAcnt tCustCwdAcnt = tCustCwdAcntService.selectTCustCwdAcntByCustAcntNo(acnt);
		icfia.setMchntCd(mchntCd);
		icfia.setOprUsrId(tOperatorInf.getLOGIN_ID());
//		icfia.setMemo("");
//		icfia.setEnpSeriaNo("");
//		icfia.setMerdt("");
//		icfia.setMobile("");
//		icfia.setBankCd(tCustCwdAcnt.getBANK_ROOT_CD());
//		icfia.setBankAccount(tCustCwdAcnt.getOUT_ACNT_NO());
//		icfia.setAccountName(tCustCwdAcnt.getOUT_ACNT_NM());
//		icfia.setMobile(tInsMchntInf.getBIND_MOBILE());
//		icfia.setCityCode(tCustCwdAcnt.getCITY_CD());
//		icfia.setBankNam(tCustCwdAcnt.getINTER_BANK_NO());
		icfia.setBusiCd(busiCd);
		icfia.setFlag(true);
		// 通过接入适配器 传入接入对象 返回中间对象
		AccessAdapter accessAdapter = AccessAdapterFactory
				.getAccessAdapter(busiCd);
		txnInfBean = accessAdapter.analysisTxn(mchntCd, icfia, srcModuleCd,txnDataType);//组装基本的交易信息
		//txnInfBean.setFileName("提现_"+new SimpleDateFormat("yyyyMMdd").format(new Date())+"0001");
		// 调用业务适配器将中间对象交给校验器，对中间对象进行校验，业务适配器返回业务对象和错误信息。
		BusiAdapter busiAdapter = BusiAdapterFactory.getFileBusiAdapter(busiCd);
		busiAdapter.analysisTxnInfBean(mchntCd, txnInfBean, srcModuleCd,txnDataType);
		busiBean = busiAdapter.getBusiBean();
		PayForBusiBean payForBusiBean = (PayForBusiBean) busiBean;
		OperationBean operationBean =null;
		if(payForBusiBean.getPayForBusiDetailRowBeanList()!=null && payForBusiBean.getPayForBusiDetailRowBeanList().size()>0){
			operationBean =payForBusiBean.getPayForBusiDetailRowBeanList().get(0).getOperationBean();
		}
		if(operationBean!=null&&operationBean.getFileRowErrro()!=null){
			FileRowError fileRowErrro = operationBean.getFileRowErrro();
			List<RowColumnError> detailErrors = fileRowErrro.getColumnErrors();
			if(detailErrors!=null&&detailErrors.size()>0){
				for(RowColumnError detailError:detailErrors){
					msg = msg + detailError.getErrMemo()+" ;  ";
				}
				errorMap.put(TDataDictConst.SUCCEED, msg);
				return "result";
			}
		}
		fileError = busiAdapter.getFileError();
		
		long money = Formator.yuan2Fen(icfia.getAmount());
		// 将业务对象交给业务服务处理器，路由匹配、手续费计算等...
		// 拆分交易
		try {
			SplitTrade splitTrade = new SplitTrade();
			splitTrade.splitTrade(busiBean);
			ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean = splitTrade
					.getForTxnFeeAmtBean();
			BusiBean operationVerifyErrorBusibean = splitTrade
					.getOperationVerifyErrorBusibean();
			// 计算手续费、路由
			TradeCalculate tradeCalculate = new TradeCalculate();
			ForAccessTxnFeeAmtRutBean calculateForTxnFeeAmtBean = tradeCalculate
					.doCalculate(forTxnFeeAmtBean);

			List<InComeForBusiDetailRowBean> inComeForBusiDetailRowBeanList = calculateForTxnFeeAmtBean
					.getInComeForList();
			feeAmt = "0";
			srcAmt = "0";
			destAmt = "0";
			int feeAmtInt = 0;
			int srcAmtInt = 0;
			int destAmtInt = 0;
			for (InComeForBusiDetailRowBean inComeForBusiDetailRowBean : inComeForBusiDetailRowBeanList) {
				feeAmtInt += inComeForBusiDetailRowBean.getOperationBean()
						.getTxnFeeAmt();
				srcAmtInt += inComeForBusiDetailRowBean.getOperationBean()
						.getDestTxnAmt();
				destAmtInt += (inComeForBusiDetailRowBean.getOperationBean()
						.getDestTxnAmt() - inComeForBusiDetailRowBean
						.getOperationBean().getTxnFeeAmt());
			}

			feeMsg = "";
			if (destAmtInt < 0) {
				feeMsg = "目标金额小于手续费,交易失败";
			}

			feeAmt = FuMerUtil.formatFenToYuan(feeAmtInt);
			srcAmt = FuMerUtil.formatFenToYuan(srcAmtInt);
			destAmt = FuMerUtil.formatFenToYuan(destAmtInt);
			
			TCustStlInfService custStlInfService = new TCustStlInfService();
			acntNos = custStlInfService.getAcntNosByIndCd(tInsInf.getINS_CD());
			String acnt = "";
			if(acntNos.size() <= 0){
				logger.error("商户:"+tOperatorInf.getMCHNT_CD()+",没有开通虚拟账户");
				msg = "商户:"+tOperatorInf.getMCHNT_CD()+",没有开通虚拟账户";
				errorMap.put(TDataDictConst.SUCCEED, msg);
				return "result";
			}else{
				acnt = acntNos.get(0);
				String blacAmt = tradeCalculate.queryBlac(acnt, tInsInf.getINS_CD());
				String[] amts = blacAmt.split("\\|");
				if(StringUtils.isEmpty(blacAmt)){
					logger.info("余额查询失败");
					msg = "余额查询失败";
					errorMap.put(TDataDictConst.SUCCEED, msg);
					return "result";
				}else{
					double amt = Double.parseDouble(amts[1]);
					if(amt<srcAmtInt){	
						logger.info("余额不足！提现金额："+money+"加手续费："+(srcAmtInt-money)+"大于可用余额："+amt);
						msg = "余额不足！提现金额："+icfia.getAmount()+"加手续费："+(FuMerUtil.formatFenToYuan(srcAmtInt-money))+"大于可用余额："+FuMerUtil.formatFenToYuan(amt);
						errorMap.put(TDataDictConst.SUCCEED, msg);
						return "result";
					}
				}
			}
			
			isDisplayFeeMsg = "1";
			if (null != tInsMchntInf) {
				String mchntTp = tInsMchntInf.getMCHNT_TP();
				if (mchntTp.length() >= 2) {
					if ("D4".equals(mchntTp.substring(0, 2))) {
						isDisplayFeeMsg = "0";
					}
				}
			}

			// 组合业务对象
			PackagingBusiBean packagingBusiBean = new PackagingBusiBean();

			packBusiBean = packagingBusiBean.toPackaging(
					operationVerifyErrorBusibean, calculateForTxnFeeAmtBean);

			busiBeanProcess = packBusiBean;
			packBusiBean.setOprUsrId(tOperatorInf.getLOGIN_ID());//操作员
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("业务对象拆分处理计算操作有误");
			msg = "提现失败，系统异常";
			errorMap.put(TDataDictConst.SUCCEED, msg);
			return "result";
		}
		return request();
	}
	public String init(){
		HttpServletRequest request  = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		TOperatorInf tOperatorInf = (TOperatorInf)request.getSession().getAttribute(TDataDictConst.OPERATOR_INF);
		String mngInsCd = tOperatorInf.getINS_CD();
		String src_mchnt_cd = tOperatorInf.getMCHNT_CD();
		TCustStlInfService custStlInfService = new TCustStlInfService();
		List<String> acntNos = custStlInfService.getAcntNosByIndCd(mngInsCd);
		
		TCustCwdAcntService tCustCwdAcntService = new TCustCwdAcntService();
		TCustCwdAcnt tCustCwdAcnt = tCustCwdAcntService.selectTCustCwdAcntByCustAcntNo(acntNos.get(0));
		TRootBankInf tRootBankInf = null;
		if(tCustCwdAcnt != null){
			tRootBankInf =SystemParams.bankMap.get(tCustCwdAcnt.getBANK_ROOT_CD());
		}else{
			msg = "未找到提现账户对应的信息";
		}
		if(tRootBankInf!=null){
			ActionContext.getContext().put("BANK_NM", tRootBankInf.getBANK_NM());
		}
		ActionContext.getContext().put("acntNo", acntNos.get(0));
		ActionContext.getContext().put("msg", "");
		ActionContext.getContext().put("tCustCwdAcnt", tCustCwdAcnt);
		return "withdeposit";
	}
	
	public String request(){
		TxnDbService txnDbService = TxnDbServiceFactory.getDataOperate(busiCd);
		
		txnDbService.dataProcess(packBusiBean, fileError, txnInfBean, busiCd, srcModuleCd, txnDataType, false);
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		// 商户信息
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		//获取错误信息
		FileError fileErrorFromDbService = txnDbService.getFileError();
		FileSumError fileSumError = fileErrorFromDbService.getFileSumError() ;//汇总错误
		if(fileSumError!=null){
			errorMap.put(TDataDictConst.UNSUCCESSFUL, fileSumError.getErrCode()+";"+fileSumError.getErrMemo());
			return "result";
		}
		List<FileRowError> fileRowErrorList = fileErrorFromDbService.getFileRowErrors();//明细错误
		if(fileRowErrorList != null && fileRowErrorList.size()>0){
			for(FileRowError fileRowError : fileRowErrorList){
				List<RowColumnError> rowColumnErrorList = fileRowError.getColumnErrors();
				if(rowColumnErrorList != null && rowColumnErrorList.size() > 0){
						String memo = "";
						String code = "";
						for(RowColumnError rowColumnError : rowColumnErrorList){
							code += rowColumnError.getErrCode()+";";
							memo += rowColumnError.getErrMemo()+";";
						}
						msg = memo;
				}
			}
			errorMap.put(TDataDictConst.UNSUCCESSFUL, msg);
			return "result";
	}
		// 修改交易状态为商户已复核
		Date currDate = busiBeanProcess.getCurrDbTime();
		TApsTxnLog tApsTxnLogD = tApsTxnLogService.getTapsTxnLogByTime(busiCd, currDate,TDataDictConst.CAPITALDIR_D);
//		int a = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLogD, TDataDictConst.TXN_CUSTMR_CAPITAL_AUC_SURE);//修改D方向的资金为：商户资金已到帐，已复核
//		if(a>0){
//			TApsTxnLog tApsTxnLog = tApsTxnLogService.getTapsTxnLogByTime(busiCd, currDate,TDataDictConst.CAPITALDIR_C);
//			int i = tApsTxnLogService.updateTapsTxnLogSure(tApsTxnLog, TDataDictConst.TXN_CUSTMR_SECOND_SURE);
//			if(i > 0){
//				msg = "提现成功";
//				errorMap.put(TDataDictConst.SUCCEED, msg);
//			}else{
//				msg = "修改提现状态失败";
//				errorMap.put(TDataDictConst.UNSUCCESSFUL, msg);
//			}
//		}else{
//			msg = "修改提现状态失败";
//			errorMap.put(TDataDictConst.UNSUCCESSFUL, msg);
//		}
		TApsTxnLog tApsTxnLogC = tApsTxnLogService.getTapsTxnLogByTime(busiCd, currDate,TDataDictConst.CAPITALDIR_C);
		if(tApsTxnLogC != null){
			String rlatKbpsSrcSettleDt = tApsTxnLogC.getRLAT_KBPS_SRC_SETTLE_DT();
			String rlatSrcModuleCd = tApsTxnLogC.getRLAT_SRC_MODULE_CD();
			String rlatKbpsTraceNo = tApsTxnLogC.getRLAT_KBPS_TRACE_NO();
			short rlatSubTxnSeq = tApsTxnLogC.getRLAT_SUB_TXN_SEQ();
			
			String KBPS_SRC_SETTLE_DT = tApsTxnLogC.getKBPS_SRC_SETTLE_DT();
			String KBPS_TRACE_NO = tApsTxnLogC.getKBPS_TRACE_NO();
		
			if(tApsTxnLogD != null){
				long payAuthAmt = 0;//需要预授权的总金额
				int payAuthCount = 0;//预授权的总笔数
				String isMchntCapital = tApsTxnLogD.getTXN_MD();				// 商户资金到账
				String isChnlCapital = tApsTxnLogC.getPOS_ENTRY_MD_CD();			// 渠道资金到账
				if("0".equals(isMchntCapital)){	// 不需要确认商户资金到账
					// 修改收款的交易为商户资金已到帐
					int ii = tApsTxnLogService.updateTapsTxnLogByRlat(rlatKbpsSrcSettleDt, rlatSrcModuleCd, rlatKbpsTraceNo, rlatSubTxnSeq,TDataDictConst.TXN_CUSTMR_CAPITAL_AUC_SURE,"",TDataDictConst.TXN_DEST_RES_SUC,(short)1,"","","");
					if(ii > 0){
						if(TDataDictConst.TXN_DEST_RES_SUC.equals(tApsTxnLogC.getSRC_TXN_ST())&&TDataDictConst.TXN_DEST_RECORD_SUC.equals(tApsTxnLogC.getDEST_TXN_ST())){
							payAuthAmt += tApsTxnLogC.getSRC_TXN_AMT();
							payAuthCount ++;
						}
						logger.info("付款交易修改关联的一笔收款的状态为：商户已到账，修改成功");
					}else{
						logger.info("付款交易修改关联的一笔收款的状态为：商户已到账，修改失败");
					}
					String custmr_tp = "B1";//默认渠道资金到账已复核
					String dest_rsp_cd = "";//响应码
					String addn_priv_data = "";//描述
					String retri_ref_no = "";//预授权码
					if(payAuthCount > 0){
						String acnt = "";
						if(acntNos.size() <= 0){
							logger.error("商户:"+tOperatorInf.getMCHNT_CD()+",没有开通虚拟账户");
							custmr_tp = TDataDictConst.TXN_CUSTMR_PAYAUTH;
							dest_rsp_cd = TDataDictConst.UNOPEN_ACCOUNT;
							addn_priv_data = "没有开通虚拟账户";
						}else{
							boolean payAuthIsError = false;//预授权是否失败
							String[] result = null;
							acnt = acntNos.get(0);
							logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
							.format(new Date())+"调用预授权交易开始");
							try {
								result = FasService.payAuth(tInsInf.getINS_CD(), acnt, payAuthAmt+"", VirtAcntUtil.getSsn(), "", tInsInf.getMCHNT_CD(), tInsInf.getMCHNT_TP(),"AP01","S32");
								if(result != null){
									if("0000".equals(result[2])){
										logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
										.format(new Date())+"预授权交易成功");
										retri_ref_no=result[6]!=null?result[6]:"";//预授权标示码
									}else{
										logger.error(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
										.format(new Date())+"预授权交易失败");
										payAuthIsError = true;
									}
								}
							} catch (Exception e) {
									// TODO Auto-generated catch block
								payAuthIsError = true;
								logger.error("预授权超时");
								logger.error("Exception:",e);
							}
							if(payAuthIsError){
								custmr_tp = TDataDictConst.TXN_CUSTMR_PAYAUTH;
								dest_rsp_cd = TDataDictConst.PAYAUTH_HAS_ERROR;
								addn_priv_data = "预授权失败";
							}
						}
					}else{
						logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
						.format(new Date())+"修改D方向的交易状态失败，无需预授权");
					}
					if("0".equals(isChnlCapital)){// 不需要确认渠道资金到账
						// 修改付款交易为渠道资金到账已复核
						int res=tApsTxnLogService.updateTApsTxnLog2(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, "",custmr_tp,dest_rsp_cd,addn_priv_data,retri_ref_no);
						if(res==1) {
							msg = "提现成功";
							errorMap.put(TDataDictConst.SUCCEED, msg);
							logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改C方向交易状态成功！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
						}
						else {
							msg = "提现失败";
							logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改C方向交易状态失败！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
						}
					}else{						// 需要确认渠道资金到账
						// 修改付款交易为商户商户资金已到账
						int res=tApsTxnLogService.updateTApsTxnLog(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, "",TDataDictConst.TXN_CUSTMR_CAPITAL_AUC_SURE);
						if(res==1) {
							msg = "提现成功";
							errorMap.put(TDataDictConst.SUCCEED, msg);
							logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改C方向交易状态成功！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
						}
						else {
							msg = "提现失败";
							errorMap.put(TDataDictConst.SUCCEED, msg);
							logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改C方向交易状态失败！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
						}
					}
				}else{							// 需要确认商户资金到账
					// 修改付款交易为商户已复核
					int res=tApsTxnLogService.updateTApsTxnLog(KBPS_SRC_SETTLE_DT, "WMP", KBPS_TRACE_NO, "",TDataDictConst.CUSTMR_TP_REVIEWED);
					if(res==1) {
						msg = "提现成功";
						errorMap.put(TDataDictConst.SUCCEED, msg);
						logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改C方向交易状态成功！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
					}else {
						logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改C方向交易状态失败！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
					}
					// 修改收款的交易为商户已复核
					int ii = tApsTxnLogService.updateTapsTxnLogByRlat(rlatKbpsSrcSettleDt, rlatSrcModuleCd, rlatKbpsTraceNo, rlatSubTxnSeq,TDataDictConst.TXN_CUSTMR_SECOND_SURE,"",null,(short)-1,"","","");
					if(ii > 0){
						msg = "提现成功";
						errorMap.put(TDataDictConst.SUCCEED, msg);
						logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改D方向交易状态成功！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
					}else{
						msg = "提现失败";
						errorMap.put(TDataDictConst.SUCCEED, msg);
						logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + "提现修改D方向交易状态失败！"+"流水号:" +KBPS_TRACE_NO+"交易日期："+KBPS_SRC_SETTLE_DT);
					}
				}
			}
		}
		return "result";
	}
	public String review(){
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		String[] param = forward.split("\\|");
		String src=param[0].trim();
		if(param.length>1) frame=param[1].trim().split("=")[1];
		if(null==frame || "".equals(frame)){//没有指定则采用默认的frame页面
			setFrame("/WEB-INF/pages/stru/StruDefault.jsp");
		}
		if(!src.contains("action")){
			src="toJSP.action?toJSP="+src;
		}
		request.setAttribute("src", src);
		return "redirectURL";
	}
}
