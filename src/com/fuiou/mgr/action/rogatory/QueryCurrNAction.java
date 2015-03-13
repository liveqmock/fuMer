package com.fuiou.mgr.action.rogatory;

import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.page.PagerUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class QueryCurrNAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private TApsTxnLog txnlog;
	private List<TApsTxnLog> dataset;
	private TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	ActionContext context = ActionContext.getContext();
	HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
	TInsMchntInf tInsInf = (TInsMchntInf) context.getSession().get(TDataDictConst.INS_INF);
	ResourceBundle rb = (ResourceBundle) context.getSession().get("resourceBundle");
	private HSSFWorkbook book = null;
	public TApsTxnLog getTxnlog() {
		return txnlog;
	}
	public void setTxnlog(TApsTxnLog txnlog) {
		this.txnlog = txnlog;
	}
	public List<TApsTxnLog> getDataset() {
		return dataset;
	}
	public void setDataset(List<TApsTxnLog> dataset) {
		this.dataset = dataset;
	}

	public String query(){
		txnlog.setSRC_MCHNT_CD(tInsInf.getMCHNT_CD());
		int totalCount = tApsTxnLogService.countTxnLogs(txnlog);
		if(totalCount>0){
			int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCount);
			dataset = tApsTxnLogService.pageQuery(txnlog,pageInfo[0],pageInfo[1]);
			for(TApsTxnLog log:dataset){
				String busiCd=log.getBUSI_CD();
				String destTxnSt=log.getDEST_TXN_ST();
				log.setMinAmt(FuMerUtil.formatFenToYuan(log.getDEST_TXN_AMT()));//交易源金额
				log.setTRACK_2_DATA(FuMerUtil.formatFenToYuan(log.getTXN_FEE_AMT()));//手续费
				log.setTRACK_3_DATA(FuMerUtil.date2String(log.getTXN_RCV_TS(), "yyyy-MM-dd HH:mm:ss"));
				log.setMSG_TP(StringUtils.isEmpty(log.getSRC_ORDER_NO())?rb.getString("global.single"):rb.getString("global.batch"));//单笔 or 批量
				String[] items=log.getDETAIL_INQR_DATA().split(TDataDictConst.FILE_CONTENT_APART, 3);
				log.setDEST_TXN_ST(TDataDictConst.getStatusDescption(busiCd, destTxnSt, log.getCUSTMR_TP()));
				if(items!=null){
					if(items.length>2){
						TRootBankInf tRootBankInf=SystemParams.bankMap.get(items[1]);
						if(tRootBankInf!=null){
							log.setDEST_INS_CD(tRootBankInf.getBANK_NM());
						}
					}
				}
				if((TDataDictConst.DESTTXNST_SUCC.equals(destTxnSt) || TDataDictConst.DESTTXNST_TIMEOUT.equals(destTxnSt) || TDataDictConst.DESTTXNST_FAIL.equals(destTxnSt))  && TDataDictConst.BUSI_CD_PAYFOR.equals(busiCd)){
					log.setACQ_AUTH_RSP_CD("1");
				}
			}
		}else{
			request.setAttribute("msg", "未查询到数据!");
		}
		request.setAttribute("totalCount", totalCount);
		return "res";
	}
	
	public String download(){
		initExcel();
		txnlog.setSRC_MCHNT_CD(tInsInf.getMCHNT_CD());
		int totalCount = tApsTxnLogService.countTxnLogs(txnlog);
		int pages = totalCount % 2000 == 0?totalCount/2000 : totalCount/2000+1; 
		int startIndex = 0,endIndex=0;
		for(int i=0;i<pages;i++){
			startIndex = i*2000+1;
			endIndex = (i+1)*2000;
			dataset = tApsTxnLogService.pageQuery(txnlog,startIndex,endIndex);
			write2Excel(startIndex,endIndex);
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		try{
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="+ new String((rb.getString("txnQuery.form.detailQuery")).getBytes("gb2312"), "ISO8859-1")+ ".xls");// 设置下载头信息
			response.setContentType("application/vnd.ms-excel; charset=utf-8");
			book.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void initExcel() {
		book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet(rb.getString("txnQuery.form.detailQuery"));
		String[] rowTitle = null;
		if(TDataDictConst.BUSI_CD_REFUND_TICKET.equals(txnlog.getBUSI_CD())){
			rowTitle = new String[]{rb.getString("txnQuery.form.txnRcvTs"), rb.getString("txnQuery.form.serialNum"), rb.getString("txnQuery.form.fileDetailNum"), rb.getString("txnQuery.form.busiCd"), rb.getString("txnQuery.form.filename"), rb.getString("txnQuery.form.amtTitle"),rb.getString("txnQuery.form.bankNm"),rb.getString("txnQuery.form.acntNm"), rb.getString("txnQuery.form.account"), rb.getString("txnQuery.form.status"),rb.getString("txnQuery.form.additional"),rb.getString("txnQuery.form.entpSerialNum"), rb.getString("txnQuery.form.remark"), rb.getString("txnQuery.form.mobile"),rb.getString("txnQuery.form.origTxnDate"), rb.getString("txnQuery.form.origSerialNum"), rb.getString("txnQuery.form.origTxnAmt")};
		}else if(TDataDictConst.BUSI_CD_PAYFOR.equals(txnlog.getBUSI_CD())){
			rowTitle = new String[]{rb.getString("txnQuery.form.txnRcvTs"), rb.getString("txnQuery.form.serialNum"), rb.getString("txnQuery.form.fileDetailNum"), rb.getString("txnQuery.form.busiCd"), rb.getString("txnQuery.form.filename"), rb.getString("txnQuery.form.amtTitle"),rb.getString("txnQuery.form.bankNm"),rb.getString("txnQuery.form.acntNm"), rb.getString("txnQuery.form.account"), rb.getString("txnQuery.form.status"),rb.getString("txnQuery.form.additional"),rb.getString("txnQuery.form.entpSerialNum"), rb.getString("txnQuery.form.remark"), rb.getString("txnQuery.form.mobile"),rb.getString("txnQuery.form.refund")};
		}else{
			rowTitle = new String[]{rb.getString("txnQuery.form.txnRcvTs"), rb.getString("txnQuery.form.serialNum"), rb.getString("txnQuery.form.fileDetailNum"), rb.getString("txnQuery.form.busiCd"), rb.getString("txnQuery.form.filename"), rb.getString("txnQuery.form.amtTitle"),rb.getString("txnQuery.form.bankNm"),rb.getString("txnQuery.form.acntNm"), rb.getString("txnQuery.form.account"), rb.getString("txnQuery.form.status"),rb.getString("txnQuery.form.additional"),rb.getString("txnQuery.form.entpSerialNum"), rb.getString("txnQuery.form.remark"), rb.getString("txnQuery.form.mobile") };
		}
		HSSFRow headRow = sheet.createRow(0);
		for(int i=0;i<rowTitle.length;i++){
			headRow.createCell(i).setCellValue(rowTitle[i]);
		}
	}
	
	private void write2Excel(int startIndex,int endIndex) {
		HSSFSheet sheet = book.getSheetAt(0);
		HSSFRow row = null;
		for(TApsTxnLog log:dataset){
			int lastRow = sheet.getLastRowNum();
			row = sheet.createRow(lastRow+1);
			//明细序列|扣款人开户行代码(总行代码)|扣款人银行帐号|户名|金额|企业流水号|备注|手机号......
			String[] items = log.getDETAIL_INQR_DATA().split(TDataDictConst.FILE_CONTENT_APART);
			String[] flds =  null;
			if(StringUtils.isEmpty(log.getIC_FLDS())){
				flds = new String[3];
				flds[0] = "--";
				flds[1] = "--";
				flds[2] = "--";
			}else{
				flds = log.getIC_FLDS().split(TDataDictConst.FILE_CONTENT_APART, 3);
			}
			row.createCell(0).setCellValue(FuMerUtil.date2String(log.getTXN_RCV_TS(), "yyyy-MM-dd HH:mm:ss"));
			row.createCell(1).setCellValue(log.getKBPS_TRACE_NO());
			row.createCell(2).setCellValue(items[0]);
			row.createCell(3).setCellValue(StringUtils.isEmpty(log.getSRC_ORDER_NO())?rb.getString("global.single"):rb.getString("global.batch"));
			row.createCell(4).setCellValue(log.getSRC_ORDER_NO().trim());
			row.createCell(5).setCellValue(FuMerUtil.formatFenToYuan(log.getDEST_TXN_AMT()));
			row.createCell(6).setCellValue(SystemParams.bankMap.get(items[1]).getBANK_NM());
			row.createCell(7).setCellValue(log.getID_NO().trim());
			row.createCell(8).setCellValue(log.getDEBIT_ACNT_NO().substring(2).trim());
			row.createCell(9).setCellValue(TDataDictConst.getStatusDescption(log.getBUSI_CD(), log.getDEST_TXN_ST(), log.getCUSTMR_TP()));
			row.createCell(10).setCellValue(log.getADDN_PRIV_DATA().trim());
			if(!TDataDictConst.BUSI_CD_VERIFY.equals(log.getBUSI_CD())){
				row.createCell(11).setCellValue(flds[0]);
				row.createCell(12).setCellValue(flds[1]);
				row.createCell(13).setCellValue(flds[2]);
			}else{
				row.createCell(11).setCellValue("--");
				row.createCell(12).setCellValue("--");
				row.createCell(13).setCellValue("--");
			}
			if(TDataDictConst.BUSI_CD_PAYFOR.equals(log.getBUSI_CD())){
				if(null!=log.getCANCEL_IND() && 1==log.getCANCEL_IND()){
					row.createCell(14).setCellValue(rb.getString("txnQuery.form.refundYes"));
				}else{
					row.createCell(14).setCellValue(rb.getString("txnQuery.form.refundNo"));
				}
			}
			if(TDataDictConst.BUSI_CD_REFUND_TICKET.equals(txnlog.getBUSI_CD())){
				row.createCell(14).setCellValue(log.getRLAT_KBPS_SRC_SETTLE_DT());
				row.createCell(15).setCellValue(log.getKBPS_TRACE_NO());
				row.createCell(16).setCellValue(FuMerUtil.formatFenToYuan(log.getSRC_TXN_AMT()));
			}
		}
	}
}
