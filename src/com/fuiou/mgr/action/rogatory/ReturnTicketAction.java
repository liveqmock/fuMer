package com.fuiou.mgr.action.rogatory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TInsBankAcnt;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TInsBankAcntService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TPmsBankInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.Number2Chinese;
import com.fuiou.mgr.util.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReturnTicketAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private TApsTxnLog txnLog;
	public TApsTxnLog getTxnLog() {
		return txnLog;
	}
	public void setTxnLog(TApsTxnLog txnLog) {
		this.txnLog = txnLog;
	}
	public String queryCurrDetail() throws Exception {
		TApsTxnLogService apsTxnLogService = new TApsTxnLogService();
		TApsTxnLog apsTxnLog=apsTxnLogService.selectByPrimaryKey(txnLog.getKBPS_SRC_SETTLE_DT(), txnLog.getSRC_MODULE_CD(), txnLog.getKBPS_TRACE_NO(), (short)0);
		if (null == apsTxnLog) {
			ActionContext.getContext().put("tApsTxnLogMap", "");
			return "detail";
		}
		Map tApsTxnLogMap = new HashMap();
		tApsTxnLogMap.put("KBPS_SRC_SETTLE_DT", apsTxnLog.getKBPS_SRC_SETTLE_DT());
		tApsTxnLogMap.put("KBPS_TRACE_NO", apsTxnLog.getKBPS_TRACE_NO());
		tApsTxnLogMap.put("SRC_SSN", apsTxnLog.getSRC_SSN());
		tApsTxnLogMap.put("ID_NO", apsTxnLog.getID_NO());
		tApsTxnLogMap.put("ADDN_PRIV_DATA", apsTxnLog.getADDN_PRIV_DATA());
		tApsTxnLogMap.put("TXN_RCV_TS", FuMerUtil.date2String(apsTxnLog.getTXN_RCV_TS(), "yyyy-MM-dd HH:mm:ss"));
		tApsTxnLogMap.put("BUSI_CD", apsTxnLog.getBUSI_CD());
		
		if ("".equals(apsTxnLog.getSRC_ORDER_NO())) {// 交易类型
			tApsTxnLogMap.put("bargaining", "单笔");
		} else {
			tApsTxnLogMap.put("bargaining", "批量");
		}
		tApsTxnLogMap.put("SRC_ORDER_NO", apsTxnLog.getSRC_ORDER_NO());
		tApsTxnLogMap.put("SRC_TXN_AMT", FuMerUtil.formatFenToYuan(apsTxnLog.getSRC_TXN_AMT()));
		if (!"".equals(apsTxnLog.getDEBIT_ACNT_NO())) {
			tApsTxnLogMap.put("DEBIT_ACNT_NO", apsTxnLog.getDEBIT_ACNT_NO().substring(2, apsTxnLog.getDEBIT_ACNT_NO().length()));// 扣款账号
		}
		if (!"".equals(apsTxnLog.getCREDIT_ACNT_NO())) {
			tApsTxnLogMap.put("CREDIT_ACNT_NO", apsTxnLog.getCREDIT_ACNT_NO().substring(2, apsTxnLog.getCREDIT_ACNT_NO().length()));// 收款账号
		}
		String destTxnSt = apsTxnLog.getDEST_TXN_ST();
		String custmrTp = apsTxnLog.getCUSTMR_TP();
		String opSt = TDataDictConst.getStatusDescption(apsTxnLog.getBUSI_CD() + "",destTxnSt + "", custmrTp + "");
		tApsTxnLogMap.put("OP_ST", opSt);
		if (!"".equals(apsTxnLog.getIC_FLDS())) {
			String str[] = apsTxnLog.getIC_FLDS().split(TDataDictConst.FILE_CONTENT_APART,3);
			if (str.length == 3) {
				tApsTxnLogMap.put("enSerialNum_nm", str[0]);
				tApsTxnLogMap.put("remark_nm", str[1]);
				tApsTxnLogMap.put("phoneNum_nm", str[2]);
			}
		}
		tApsTxnLogMap.put("mchnt_review_ts", apsTxnLog.getRESRV_DATA_1());//商户复核时间
		if(!TDataDictConst.DESTTXNST_INIT.equals(apsTxnLog.getDEST_TXN_ST())){
			tApsTxnLogMap.put("to_ts", FuMerUtil.date2String(apsTxnLog.getTO_TS(), "yyyy-MM-dd HH:mm:ss"));//交易发送时间
		}
		String trackData3 = apsTxnLog.getTRACK_3_DATA();
		if(TDataDictConst.BUSI_CD_PAYFOR.equals(apsTxnLog.getBUSI_CD()) && StringUtil.isNotEmpty(trackData3)){
			String chnlReviewTs = trackData3.split("&")[1].substring(4, 12);
			tApsTxnLogMap.put("chnl_review_ts", chnlReviewTs);//渠道复核时间
		}
		if (TDataDictConst.BUSI_CD_REFUND_TICKET.equals(apsTxnLog.getBUSI_CD())) {
			tApsTxnLogMap.put("TP_TXN_RCV_TS", FuMerUtil.date2String(apsTxnLog.getTXN_RCV_TS(), "yyyy-MM-dd HH:mm:ss")); // 借记时间
			tApsTxnLogMap.put("TP_DEST_TXN_AMT", FuMerUtil.formatFenToYuan(apsTxnLog.getDEST_TXN_AMT())); // 目标交易金额
			tApsTxnLogMap.put("TP_ADDN_PRIV_DATA", apsTxnLog.getADDN_PRIV_DATA());// 附言
			tApsTxnLogMap.put("TP_KBPS_TRACE_NO", apsTxnLog.getKBPS_TRACE_NO());// 流水号
		}
		ActionContext.getContext().put("tApsTxnLogMap", tApsTxnLogMap);
		return "detail";
	}
	
	
	
	/**
	 * 回执打印
	 * @return
	 */
	public String initPrint(){
		TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
		TInsMchntInfService insMchntInfService = new TInsMchntInfService();
		TInsBankAcntService insBankAcntService = new TInsBankAcntService();
		TPmsBankInfService pmsBankInfService = new TPmsBankInfService();
		String KBPS_SRC_SETTLE_DT=txnLog.getKBPS_SRC_SETTLE_DT();
		txnLog = tApsTxnLogService.getTapsTxnLogByKey(KBPS_SRC_SETTLE_DT, txnLog.getSRC_MODULE_CD(), txnLog.getKBPS_TRACE_NO(), txnLog.getSUB_TXN_SEQ());
		TInsMchntInf insMchntInf = insMchntInfService.getTInsMchntInfByMchntAndRowTp(txnLog.getSRC_MCHNT_CD(), "1");
		txnLog.setACQ_AUTH_RSP_CD(KBPS_SRC_SETTLE_DT.substring(0, 4)+"年"+KBPS_SRC_SETTLE_DT.substring(4, 6)+"月"+KBPS_SRC_SETTLE_DT.substring(6)+"日");//发起日期
		txnLog.setSRC_MCHNT_CD(insMchntInf.getINS_NAME_CN());//商户名称
		TInsBankAcnt payAcnt = insBankAcntService.selectByKey(txnLog.getDEST_INS_CD(), "1", "1", "1"); //付款账户
		if(payAcnt == null){
			txnLog.setACQ_RSP_CD("");//付款人账号
			txnLog.setADDN_PRIV_DATA("");//付款人开户行名称
			txnLog.setADDN_RSP_DAT("");//付款人名称
		}else{
			if("08A0015840".equals(payAcnt.getINS_CD().trim())){//结算中心
				txnLog.setACQ_RSP_CD("58400020003");//付款人账号
				txnLog.setADDN_PRIV_DATA("行业支付专户");//付款人开户行名称
				txnLog.setADDN_RSP_DAT("中行深圳分行");//付款人名称
			}else if("08A0011100".equals(payAcnt.getINS_CD().trim())){//天津代收付
				txnLog.setACQ_RSP_CD("02170001040020887");//付款人账号
				txnLog.setADDN_PRIV_DATA("中国农业银行天津光明支行");//付款人开户行名称
				txnLog.setADDN_RSP_DAT("上海富友支付服务有限公司");//付款人名称
			}else{
				txnLog.setACQ_RSP_CD(payAcnt.getACNT_NO());//付款人账号
				txnLog.setADDN_PRIV_DATA(pmsBankInfService.selectBankNameByInterBankNoAndSt(payAcnt.getINTER_BANK_NO(), "1", "1", "00"));//付款人开户行名称
				txnLog.setADDN_RSP_DAT(payAcnt.getACNT_NM());//付款人名称
			}
		}
		
		//明细序列|扣款人开户行代码(总行代码)|扣款人银行帐号|户名|金额|企业流水号|备注|手机号
		String[] detailData = txnLog.getDETAIL_INQR_DATA().split("\\|",10);//明细数据
		txnLog.setDEST_SSN(detailData[4]);//收款人银行账号
		txnLog.setADDN_POS_INF(detailData[3]);//收款人开户行名称
		txnLog.setACQ_INS_CD(FuMerUtil.formatFenToYuan(txnLog.getSRC_TXN_AMT()));//金额
		txnLog.setADV_PROC_ST(Number2Chinese.amountToChinese(Double.valueOf(FuMerUtil.formatFenToYuan(txnLog.getSRC_TXN_AMT()))));
		txnLog.setACQ_INS_RES(detailData[8]);//备注信息
		txnLog.setADJ_CAPITAL_MD(FuMerUtil.date2String(new Date(), "yyyyMMdd"));
		return "print";
	}
}