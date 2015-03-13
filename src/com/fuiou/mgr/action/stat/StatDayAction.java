package com.fuiou.mgr.action.stat;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings(value = { "rawtypes" })
public class StatDayAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private String startDate;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	private TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();

	public String init() throws Exception {
		// ActionContext context = context;
		// HttpServletRequest request = (HttpServletRequest) context
		// .get(ServletActionContext.HTTP_REQUEST);
		// TRootBankInfService tRootBankInfService=new TRootBankInfService();
		// List<TRootBankInf>
		// list=tRootBankInfService.selectTRootBankInfForDaystat();
		//
		// context.put("tRootBankInf", list);
		return "inqu";
	}

	@SuppressWarnings("unused")
	public String stat() throws Exception {
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(tOperatorInf.getMCHNT_CD(), "1");
		ActionContext context = ActionContext.getContext();
		TInsMchntInf tInsInf = (TInsMchntInf) context.getSession().get(TDataDictConst.INS_INF);
		TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();

		// 代收
		Integer incomeforSuccessCount = 0;// 成功笔数
		long incomefor_src_SuccessAmt = 0;// 应收金额
		long incomefor_dest_SuccessAmt = 0;// 实收金额
		long incomeforSuccessFeeAtm = 0;//交易手续费（实时结算的才有）
		
		Integer incomeforfailuerCount = 0;// 失败笔数
		long incomefor_src_FailureAmt = 0;// 应收金额
		long incomefor_dest_FailureAmt = 0;// 实收金额
		long incomeforFailureFeeAtm = 0;//交易手续费（实时结算的才有）
		
		// 付款交易（付款）
		Integer payforSuccessCount = 0;// 成功笔数
		long payfor_inSrc_SuccessAmt=0;//应收金额
		long payfor_inDest_SuccessAmt=0;//实收金额
		long payfor_src_SuccessAmt = 0;// 应付金额
		long payfor_dest_SuccessAmt = 0;// 实付金额
		long payforFeeAtm = 0;//交易手续费（实时结算的才有）
		
		Integer payforFailureCount = 0;// 失败笔数
		long payfor_inSrc_FailureAmt=0;//应收金额
		long payfor_inDest_FailureAmt=0;//实收金额
		long payfor_src_FailureAmt = 0;// 应付金额
		long payfor_dest_FailureAmt = 0;// 实付金额
		long payforFeeFailureAtm = 0;//交易手续费（实时结算的才有）
		
		// 代付交易（代付）
		Integer rpayforSuccessCount = 0;// 成功笔数
		long rpayfor_inSrc_SuccessAmt=0;//应收金额
		long rpayfor_inDest_SuccessAmt=0;//实收金额
		long rpayfor_src_SuccessAmt = 0;// 应付金额
		long rpayfor_dest_SuccessAmt = 0;// 实付金额
		long rpayforFeeAtm = 0;//交易手续费（实时结算的才有）
		
		Integer rpayforFailureCount = 0;// 失败笔数
		long rpayfor_inSrc_FailureAmt=0;//应收金额
		long rpayfor_inDest_FailureAmt=0;//实收金额
		long rpayfor_src_FailureAmt = 0;// 应付金额
		long rpayfor_dest_FailureAmt = 0;// 实付金额
		long rpayforFeeFailureAtm = 0;//交易手续费（实时结算的才有）
		
//		// 实时代付交易（代付）
//		Integer spayforSuccessCount = 0;// 成功笔数
//		long spayfor_inSrc_SuccessAmt=0;//应收金额
//		long spayfor_inDest_SuccessAmt=0;//实收金额
//		long spayfor_src_SuccessAmt = 0;// 应付金额
//		long spayfor_dest_SuccessAmt = 0;// 实付金额
//		long spayforFeeAtm = 0;//交易手续费（实时结算的才有）
		// 借记交易（退票）REFUND_TICKET
		Integer refTkSuccessCount = 0;// 成功笔数
		long refTk_src_SuccessAmt = 0;// 应收金额易额
		long refTk_dest_SuccessAmt = 0;// 实收金额
		long refundFeeAtm = 0;//手续费（实时结算才有）
		
//		//退票重发交易（退票重发）
//		Integer refundRenewSuccessCount = 0;// 成功笔数
//		long refundRenew_src_SuccessAmt = 0;// 应付金额易额
//		long refundRenew_dest_SuccessAmt = 0;// 实付金额
//		long refundRenewFeeAtm = 0;//交易手续费（实时结算的才有）
//		
//		//退票退款交易（退票退款）
//		Integer refundMendSuccessCount = 0;// 成功笔数
//		long refundMend_src_SuccessAmt = 0;// 应付金额易额
//		long refundMend_dest_SuccessAmt = 0;// 实付金额
//		long refundMendFeeAtm = 0;//交易手续费（实时结算的才有）
		
		// 合计
		Integer allCount = 0;// 合计笔数
		long all_inSrc_SuccessAmt = 0;// 应收金额
		long all_inDest_SuccessAmt = 0;// 实收金额
		long all_src_SuccessAmt = 0;// 应付金额
		long all_dest_SuccessAmt = 0;// 实付金额
		long all_fee_ant = 0;//手续费合计
		//AC01当日成功的笔数
		List listAC01S = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "1", null, TDataDictConst.CAPITAL_DIR_INCOMEFOR, TDataDictConst.BUSI_CD_INCOMEFOR,startDate, tInsInf.getMCHNT_CD());
		//AC01当日失败的笔数
		List listAC01F = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "2", null, TDataDictConst.CAPITAL_DIR_INCOMEFOR, TDataDictConst.BUSI_CD_INCOMEFOR,startDate, tInsInf.getMCHNT_CD());
		
		//AP01当日成功的笔数(D方向)
		List listAP01SD = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "1", null, TDataDictConst.CAPITAL_DIR_INCOMEFOR, TDataDictConst.BUSI_CD_PAYFOR,startDate, tInsInf.getMCHNT_CD());
		//AP01当日成功的笔数(C方向)
		List listAP01SC = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "1", null, TDataDictConst.CAPITAL_DIR_PAYFOR, TDataDictConst.BUSI_CD_PAYFOR,startDate, tInsInf.getMCHNT_CD());
		
		//AP01当日失败的笔数(D方向)
		List listAP01FD = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "2", null, TDataDictConst.CAPITAL_DIR_INCOMEFOR, TDataDictConst.BUSI_CD_PAYFOR,startDate, tInsInf.getMCHNT_CD());
		//AP01当日失败的笔数(C方向)
		List listAP01FC = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "2", null, TDataDictConst.CAPITAL_DIR_PAYFOR, TDataDictConst.BUSI_CD_PAYFOR,startDate, tInsInf.getMCHNT_CD());
		
		//TP01当日成功的笔数
		List listTP01S = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "1", null, TDataDictConst.CAPITAL_DIR_INCOMEFOR, TDataDictConst.BUSI_CD_REFUND_TICKET,startDate, tInsInf.getMCHNT_CD());
		
//		//TR01当日成功的笔数
//		List listTR01S = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "1", null, TDataDictConst.CAPITAL_DIR_PAYFOR, TDataDictConst.BUSI_CD_REFUND_RENEW,startDate, tInsInf.getMCHNT_CD());
//		
//		//TT01当日成功的笔数
//		List listTT01S = tApsTxnLogService.selectTApsTxnLogForDaystat("1", "1", null, TDataDictConst.CAPITAL_DIR_PAYFOR, TDataDictConst.BUSI_CD_REFUNDMEND,startDate, tInsInf.getMCHNT_CD());
		
		if(listAC01S.size()>0){
			Map map=(Map) listAC01S.get(0);
			incomeforSuccessCount=Integer.parseInt(map.get("COUNT")+"");
			if(incomeforSuccessCount>0){	
				incomefor_src_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
				incomefor_dest_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
				incomeforSuccessFeeAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
			}
		}
		if(listAC01F.size()>0){
			Map map=(Map) listAC01F.get(0);
			incomeforfailuerCount=Integer.parseInt(map.get("COUNT")+"");
			if(incomeforfailuerCount>0){
				incomefor_src_FailureAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
				incomefor_dest_FailureAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
				incomeforFailureFeeAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
			}
		}
		int ap=0;
		if(listAP01SD.size()>0){
			Map map=(Map) listAP01SD.get(0);
			ap=Integer.parseInt(map.get("COUNT")+"");
			if(ap>0){
				payfor_inSrc_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
				payfor_inDest_SuccessAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
			}
		}
		if(ap>0){
			if(listAP01SC.size()>0){
				Map map=(Map) listAP01SC.get(0);
				payforSuccessCount=Integer.parseInt(map.get("COUNT")+"");
				if(payforSuccessCount>0){
					payfor_src_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
					payfor_dest_SuccessAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
					payforFeeAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
				}else{
					payfor_inSrc_SuccessAmt=0;
					payfor_inDest_SuccessAmt=0;
				}
			}
		}
		
		int ap2=0;
		if(listAP01SD.size()>0){
			Map map=(Map) listAP01FD.get(0);
			ap2=Integer.parseInt(map.get("COUNT")+"");
			if(ap2>0){
				payfor_inSrc_FailureAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
				payfor_inDest_FailureAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
			}
		}
		if(ap2>0){
			if(listAP01FC.size()>0){
				Map map=(Map) listAP01FC.get(0);
				payforFailureCount=Integer.parseInt(map.get("COUNT")+"");
				if(payforFailureCount>0){
					payfor_src_FailureAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
					payfor_dest_FailureAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
					payforFeeFailureAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
				}else{
					payfor_inSrc_FailureAmt=0;
					payfor_inDest_FailureAmt=0;
				}
			}
		}
		
		int d =0;
		
		if(listTP01S.size()>0){
			Map map=(Map) listTP01S.get(0);
			refTkSuccessCount=Integer.parseInt(map.get("COUNT")+"");
			if(refTkSuccessCount>0){
				refTk_src_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
				refTk_dest_SuccessAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
				refundFeeAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
			}
		}
//		if(listTR01S.size()>0){
//			Map map=(Map) listTR01S.get(0);
//			refundRenewSuccessCount=Integer.parseInt(map.get("COUNT")+"");
//			if(refundRenewSuccessCount>0){
//				
//				refundRenew_src_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
//				refundRenew_dest_SuccessAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
//				refundRenewFeeAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
//			}
//		}
//		if(listTT01S.size()>0){
//			Map map=(Map) listTT01S.get(0);
//			refundMendSuccessCount=Integer.parseInt(map.get("COUNT")+"");
//			if(refundMendSuccessCount>0){
//				refundMend_src_SuccessAmt=Long.parseLong(map.get("SRC_TXN_AMT")+"");
//				refundMend_dest_SuccessAmt=Long.parseLong(map.get("DEST_TXN_AMT")+"");
//				refundMendFeeAtm=Long.parseLong(map.get("TXN_FEE_AMT")+"");
//			}
//		}
		
		allCount=incomeforSuccessCount+incomeforfailuerCount+payforSuccessCount+payforFailureCount+rpayforSuccessCount+rpayforFailureCount+refTkSuccessCount;//+refundRenewSuccessCount+refundMendSuccessCount
		all_inSrc_SuccessAmt=incomefor_src_SuccessAmt+incomefor_src_FailureAmt+payfor_inSrc_SuccessAmt+payfor_inSrc_FailureAmt+rpayfor_inSrc_SuccessAmt+rpayfor_inSrc_FailureAmt+refTk_src_SuccessAmt;
		all_inDest_SuccessAmt=incomefor_dest_SuccessAmt+incomefor_dest_FailureAmt+payfor_inDest_SuccessAmt+payfor_inDest_FailureAmt+rpayfor_inDest_SuccessAmt+rpayfor_inDest_FailureAmt+refTk_dest_SuccessAmt;
		all_src_SuccessAmt=payfor_src_SuccessAmt+payfor_src_FailureAmt+rpayfor_src_SuccessAmt+rpayfor_src_FailureAmt;//+refundRenew_src_SuccessAmt+refundMend_src_SuccessAmt
		all_dest_SuccessAmt=payfor_dest_SuccessAmt+payfor_dest_FailureAmt+rpayfor_dest_SuccessAmt+rpayfor_dest_FailureAmt;//+refundRenew_dest_SuccessAmt+refundMend_dest_SuccessAmt

		context.put("companyName", tInsInf.getINS_NAME_CN());// tInsInf.getCOMPANY_NAME());
		context.put("fromDate", FuMerUtil.StringDateToFormatType2(startDate, "yyyyMMdd", "yyyy/MM/dd"));
		//代收
		context.put("incomeforSuccessCount",incomeforSuccessCount);
		context.put("incomefor_src_SuccessAmt",FuMerUtil.formatFenToYuan(incomefor_src_SuccessAmt));
		context.put("incomefor_dest_SuccessAmt",FuMerUtil.formatFenToYuan(incomefor_dest_SuccessAmt));
		context.put("incomeforfailuerCount",incomeforfailuerCount);
		context.put("incomefor_src_FailureAmt",FuMerUtil.formatFenToYuan(incomefor_src_FailureAmt));
		context.put("incomefor_dest_FailureAmt",FuMerUtil.formatFenToYuan(incomefor_dest_FailureAmt));
		//付款交易（付款)
		context.put("payforSuccessCount",payforSuccessCount);
		context.put("payfor_inSrc_SuccessAmt",FuMerUtil.formatFenToYuan(payfor_inSrc_SuccessAmt));
		context.put("payfor_inDest_SuccessAmt",FuMerUtil.formatFenToYuan(payfor_inDest_SuccessAmt));
		context.put("payfor_src_SuccessAmt",FuMerUtil.formatFenToYuan(payfor_src_SuccessAmt));
		context.put("payfor_dest_SuccessAmt",FuMerUtil.formatFenToYuan(payfor_dest_SuccessAmt));

		context.put("payforFailureCount",payforFailureCount);
		context.put("payfor_inSrc_FailureAmt",FuMerUtil.formatFenToYuan(payfor_inSrc_FailureAmt));
		context.put("payfor_inDest_FailureAmt",FuMerUtil.formatFenToYuan(payfor_inDest_FailureAmt));
		context.put("payfor_src_FailureAmt",FuMerUtil.formatFenToYuan(payfor_src_FailureAmt));
		context.put("payfor_dest_FailureAmt",FuMerUtil.formatFenToYuan(payfor_dest_FailureAmt));
		
		//付款交易（代付)
		context.put("rpayforSuccessCount",rpayforSuccessCount);
		context.put("rpayfor_inSrc_SuccessAmt",FuMerUtil.formatFenToYuan(rpayfor_inSrc_SuccessAmt));
		context.put("rpayfor_inDest_SuccessAmt",FuMerUtil.formatFenToYuan(rpayfor_inDest_SuccessAmt));
		context.put("rpayfor_src_SuccessAmt",FuMerUtil.formatFenToYuan(rpayfor_src_SuccessAmt));
		context.put("rpayfor_dest_SuccessAmt",FuMerUtil.formatFenToYuan(rpayfor_dest_SuccessAmt));
		
		context.put("rpayforFailureCount",rpayforFailureCount);
		context.put("rpayfor_inSrc_FailureAmt",FuMerUtil.formatFenToYuan(rpayfor_inSrc_FailureAmt));
		context.put("rpayfor_inDest_FailureAmt",FuMerUtil.formatFenToYuan(rpayfor_inDest_FailureAmt));
		context.put("rpayfor_src_FailureAmt",FuMerUtil.formatFenToYuan(rpayfor_src_FailureAmt));
		context.put("rpayfor_dest_FailureAmt",FuMerUtil.formatFenToYuan(rpayfor_dest_FailureAmt));
		
//		//付款交易（实时代付)
//		context.put("spayforSuccessCount",spayforSuccessCount);
//		context.put("spayfor_inSrc_SuccessAmt",FuMerUtil.formatFenToYuan(spayfor_inSrc_SuccessAmt));
//		context.put("spayfor_inDest_SuccessAmt",FuMerUtil.formatFenToYuan(spayfor_inDest_SuccessAmt));
//		context.put("spayfor_src_SuccessAmt",FuMerUtil.formatFenToYuan(spayfor_src_SuccessAmt));
//		context.put("spayfor_dest_SuccessAmt",FuMerUtil.formatFenToYuan(spayfor_dest_SuccessAmt));
		//借记交易（退票）REFUND_TICKET
		context.put("refTkSuccessCount",refTkSuccessCount);
		context.put("refTk_src_SuccessAmt",FuMerUtil.formatFenToYuan(refTk_src_SuccessAmt));
		context.put("refTk_dest_SuccessAmt",FuMerUtil.formatFenToYuan(refTk_dest_SuccessAmt));
		//退票重发交易（退票重发）
//		context.put("refundRenewSuccessCount",refundRenewSuccessCount);
//		context.put("refundRenew_src_SuccessAmt",FuMerUtil.formatFenToYuan(refundRenew_src_SuccessAmt));
//		context.put("refundRenew_dest_SuccessAmt",FuMerUtil.formatFenToYuan(refundRenew_dest_SuccessAmt));
//		//退票重发交易（退票退款）
//		context.put("refundMendSuccessCount",refundMendSuccessCount);
//		context.put("refundMend_src_SuccessAmt",FuMerUtil.formatFenToYuan(refundMend_src_SuccessAmt));
//		context.put("refundMend_dest_SuccessAmt",FuMerUtil.formatFenToYuan(refundMend_dest_SuccessAmt));
		//各种交易的手续费
		context.put("incomeforSuccessFeeAtm",incomeforSuccessFeeAtm);
		context.put("incomeforFailureFeeAtm",incomeforFailureFeeAtm);
		context.put("payforFeeAtm",payforFeeAtm);
		context.put("payforFeeFailureAtm",payforFeeFailureAtm);
		context.put("rpayforFeeAtm",rpayforFeeAtm);
		context.put("rpayforFeeFailureAtm",rpayforFeeFailureAtm);
		context.put("refundFeeAtm",refundFeeAtm);
//		context.put("refundRenewFeeAtm",refundRenewFeeAtm);
//		context.put("refundMendFeeAtm",refundMendFeeAtm);
		
		//合计
		context.put("allCount",allCount);
		context.put("all_inSrc_SuccessAmt",FuMerUtil.formatFenToYuan(all_inSrc_SuccessAmt));
		context.put("all_inDest_SuccessAmt",FuMerUtil.formatFenToYuan(all_inDest_SuccessAmt));
		context.put("all_src_SuccessAmt",FuMerUtil.formatFenToYuan(all_src_SuccessAmt));
		context.put("all_dest_SuccessAmt",FuMerUtil.formatFenToYuan(all_dest_SuccessAmt));
		context.put("all_fee_ant",all_fee_ant);
		context.put("MCHNT_TP",tInsMchntInf.getMCHNT_TP());
		return "result";
	}
}
