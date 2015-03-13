package com.fuiou.mgr.action.virtacct;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.InsInfCacheBean;
import com.fuiou.mer.model.TAcntBookDtlHis;
import com.fuiou.mer.model.TCustCwdAcnt;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.service.TAcntBookDtlHisService;
import com.fuiou.mer.service.TBasicAcntService;
import com.fuiou.mer.service.TCustCwdAcntService;
import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.DateUtils;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.VirtAcntUtil;
import com.fuiou.mgr.util.page.PagerUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class VirtualAcountAction extends ActionSupport {
	private static Logger logger = LoggerFactory
			.getLogger(VirtualAcountAction.class);
	private static final long serialVersionUID = 1L;
	private List<String> acntNos;
	private String acnt;
	private String balance;
	private String month;
	private Integer totalCnt;
	private Integer currentPage;
	private List<TAcntBookDtlHis> details;
	private String cminAmt;
	private String startDt;
	private String endDt;
	private String realBookInd;
	private String selectTp;// 查询类型
	private String mchntCd_tag;
	private String mchntCd;

	public String getSelectTp() {
		return selectTp;
	}

	public void setSelectTp(String selectTp) {
		this.selectTp = selectTp;
	}

	public String getMchntCd_tag() {
		return mchntCd_tag;
	}

	public void setMchntCd_tag(String mchntCd_tag) {
		this.mchntCd_tag = mchntCd_tag;
	}

	public String getMchntCd() {
		return mchntCd;
	}

	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}

	public String getRealBookInd() {
		return realBookInd;
	}

	public void setRealBookInd(String realBookInd) {
		this.realBookInd = realBookInd;
	}

	public String getStartDt() {
		return startDt;
	}

	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}

	public String getEndDt() {
		return endDt;
	}

	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}

	public String getCminAmt() {
		return cminAmt;
	}

	public void setCminAmt(String cminAmt) {
		this.cminAmt = cminAmt;
	}

	public String getCmaxAmt() {
		return cmaxAmt;
	}

	public void setCmaxAmt(String cmaxAmt) {
		this.cmaxAmt = cmaxAmt;
	}

	public String getDminAmt() {
		return dminAmt;
	}

	public void setDminAmt(String dminAmt) {
		this.dminAmt = dminAmt;
	}

	public String getDmaxAmt() {
		return dmaxAmt;
	}

	public void setDmaxAmt(String dmaxAmt) {
		this.dmaxAmt = dmaxAmt;
	}

	private String cmaxAmt;
	private String dminAmt;
	private String dmaxAmt;

	public Integer getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(Integer totalCnt) {
		this.totalCnt = totalCnt;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public List<TAcntBookDtlHis> getDetails() {
		return details;
	}

	public void setDetails(List<TAcntBookDtlHis> details) {
		this.details = details;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getAcnt() {
		return acnt;
	}

	public void setAcnt(String acnt) {
		this.acnt = acnt;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public List<String> getAcntNos() {
		return acntNos;
	}

	public void setAcntNos(List<String> acntNos) {
		this.acntNos = acntNos;
	}

	public String ini() {
		String[] bankCds = SystemParams.getProperty(
				"BANK_CDS_INCOME_FOR_GZ_UNION_PAY").split("_");
		List<TRootBankInf> tRootBankInfs = new ArrayList<TRootBankInf>();
		for (String bankCd : bankCds) {
			tRootBankInfs.add(SystemParams.bankMap.get(bankCd));
		}
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		request.setAttribute("bankList", tRootBankInfs);
		return "init";
	}

	public String queryBalanceIni() {
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		TOperatorInf tOperatorInf = (TOperatorInf) request.getSession()
				.getAttribute(TDataDictConst.OPERATOR_INF);
		String mngInsCd = tOperatorInf.getINS_CD();
		TCustStlInfService custStlInfService = new TCustStlInfService();
		acntNos = custStlInfService.getAcntNosByIndCd(mngInsCd);
		return "queryBalanceIni";
	}

	/**
	 * 余额查询
	 * 
	 * @return
	 */
	public String queryBlac() {
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		TOperatorInf tOperatorInf = (TOperatorInf) request.getSession()
				.getAttribute(TDataDictConst.OPERATOR_INF);
		String mngInsCd = tOperatorInf.getINS_CD().trim();
		String[] strs = null;
		try {
			try {
				strs = FasService.queryBalance(mngInsCd, acnt.trim(),
						VirtAcntUtil.getSsn());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String balance0 = FuMerUtil
					.formatFenToYuan(Double.valueOf(strs[3]));// 账面余额
			String balance1 = FuMerUtil
					.formatFenToYuan(Double.valueOf(strs[4]));// 可用余额
			String balance2 = FuMerUtil
					.formatFenToYuan(Double.valueOf(strs[5]));// 未结余额
			String balance3 = FuMerUtil
					.formatFenToYuan(Double.valueOf(strs[6]));// 冻结余额
			balance = balance0 + "|" + balance1 + "|" + balance2 + "|"
					+ balance3;
		} catch (Exception e) {
			e.printStackTrace();
			balance = "-1";
		}// 查询余额
		return "queryBalance";
	}

	/**
	 * 明细查询
	 * 
	 * @return
	 */
	public String queryDetails() {
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		TAcntBookDtlHisService acntBookDtlService = new TAcntBookDtlHisService();
		acnt = acnt.trim();// 获取账户
		// 转换账户为子账户：t_basic_acnt --->>cust_acnt_no='主账户' and in_acnt_idx=1
		// 获取basic_acnt_no即为子账户
		TBasicAcntService basicAcntService = new TBasicAcntService();
		String subAcnt = basicAcntService.getSubAcnt(acnt);
		totalCnt = acntBookDtlService.count(subAcnt, startDt, endDt, cminAmt,
				cmaxAmt, dminAmt, dmaxAmt);
		if (totalCnt > 0) {
			int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCnt);// 分页信息
			details = acntBookDtlService.queryTxnDetails(subAcnt, startDt,
					endDt, cminAmt, cmaxAmt, dminAmt, dmaxAmt, pageInfo[0],
					pageInfo[1]);
			if (details != null && details.size() > 0) {
				for (TAcntBookDtlHis detail : details) {
					detail.setAmt2(FuMerUtil.formatFenToYuan(detail
							.getCT_DEBIT_AMT() == null ? 0 : detail
							.getCT_DEBIT_AMT()));
					detail.setAmt1(FuMerUtil.formatFenToYuan(detail
							.getCT_CREDIT_AMT() == null ? 0 : detail
							.getCT_CREDIT_AMT()));
					detail.setAmt3(FuMerUtil.formatFenToYuan(detail
							.getCR_ACCU_AMT_C() == null ? 0 : detail
							.getCR_ACCU_AMT_C()));// CT_BALANCE
				}
			}
		}
		return "details";
	}

	/**
	 * 下载交易日明细
	 * 
	 * @return
	 */
	public String downloadDetails() {
		HttpServletResponse response = (HttpServletResponse) ActionContext
				.getContext().get(ServletActionContext.HTTP_RESPONSE);
		ResourceBundle rb = (ResourceBundle) ActionContext.getContext().getSession().get("resourceBundle");
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet(startDt + "-" + endDt + rb.getString("acntManagement.form.accountDetail"));
		HSSFRow header = sheet.createRow(0);
		header.createCell(0).setCellValue(rb.getString("acntManagement.form.serialNum"));
		header.createCell(1).setCellValue(rb.getString("acntManagement.form.txnDate"));
		header.createCell(2).setCellValue(rb.getString("acntManagement.form.bookDate"));
		header.createCell(3).setCellValue(rb.getString("acntManagement.form.credit"));
		header.createCell(4).setCellValue(rb.getString("acntManagement.form.debit"));
		header.createCell(5).setCellValue(rb.getString("acntManagement.form.balance"));
		header.createCell(6).setCellValue(rb.getString("acntManagement.form.remark"));
		TAcntBookDtlHisService acntBookDtlService = new TAcntBookDtlHisService();
		acnt = acnt.trim();// 获取账户
		// 转换账户为子账户：t_basic_acnt --->>cust_acnt_no='主账户' and in_acnt_idx=1
		// 获取basic_acnt_no即为子账户
		TBasicAcntService basicAcntService = new TBasicAcntService();
		String subAcnt = basicAcntService.getSubAcnt(acnt);
		totalCnt = acntBookDtlService.count(subAcnt, startDt, endDt, cminAmt,
				cmaxAmt, dminAmt, dmaxAmt);
		if (totalCnt > 0) {
			HSSFRow detailRow = null;
			details = acntBookDtlService.queryTxnDetails(subAcnt, startDt,
					endDt, cminAmt, cmaxAmt, dminAmt, dmaxAmt, 1,
					totalCnt);
			if (details != null && details.size() > 0) {
				for (TAcntBookDtlHis detail : details) {
					detailRow = sheet.createRow(details.indexOf(detail) + 1);
					detailRow.createCell(0).setCellValue(detail.getTRACE_NO());
					detailRow.createCell(1).setCellValue(
							detail.getFAS_SETTLE_DT());
					detailRow.createCell(2).setCellValue(detail.getBOOK_DT());
					detailRow.createCell(3)
							.setCellValue(
									FuMerUtil.formatFenToYuan(detail
											.getCT_CREDIT_AMT() == null ? 0
											: detail.getCT_CREDIT_AMT()));
					detailRow.createCell(4)
							.setCellValue(
									FuMerUtil.formatFenToYuan(detail
											.getCT_DEBIT_AMT() == null ? 0
											: detail.getCT_DEBIT_AMT()));
					detailRow.createCell(5)
							.setCellValue(
									FuMerUtil.formatFenToYuan(detail
											.getCR_ACCU_AMT_C() == null ? 0
											: detail.getCR_ACCU_AMT_C()));
					detailRow.createCell(6).setCellValue(
							detail.getBOOK_DIGEST());
				}
			}
		}
		try {
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ new String((startDt+"-"+endDt + rb.getString("acntManagement.form.accountDetail")).getBytes("gb2312"),
							"ISO8859-1") + ".xls");
			// 设置下载头信息
			response.setContentType("application/vnd.ms-excel; charset=utf-8");
			book.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String showMonthRptDownload() {
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		TOperatorInf tOperatorInf = (TOperatorInf) request.getSession()
				.getAttribute(TDataDictConst.OPERATOR_INF);
		String mngInsCd = tOperatorInf.getINS_CD();
		TCustStlInfService custStlInfService = new TCustStlInfService();
		acntNos = custStlInfService.getAcntNosByIndCd(mngInsCd);
		return "showMonthDownload";
	}

	public String queryFasTxnLog() {
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		TAcntBookDtlHisService acntBookDtlService = new TAcntBookDtlHisService();
		month = StringUtils.isEmpty(month) ? DateUtils.getCurrentDate() : month
				.trim();// 获取查询的日期
		acnt = acnt.trim();// 获取账户
		// 转换账户为子账户：t_basic_acnt --->>cust_acnt_no='主账户' and in_acnt_idx=1
		// 获取basic_acnt_no即为子账户
		TBasicAcntService basicAcntService = new TBasicAcntService();
		String subAcnt = basicAcntService.getSubAcnt(acnt);
		totalCnt = acntBookDtlService.countDetail(subAcnt.trim(), startDt,
				endDt, realBookInd);
		if (totalCnt > 0) {
			int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCnt);// 分页信息
			details = acntBookDtlService.queryDetails(subAcnt.trim(), startDt,
					endDt, realBookInd, pageInfo[0], pageInfo[1]);
			TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
			for (TAcntBookDtlHis detail : details) {
				detail.setStlObjCd(tInsInf.getINS_NM_JC_CN());
				detail.setAmt1(FuMerUtil.formatFenToYuan(detail
						.getCT_DEBIT_AMT()));
				detail.setAmt2(FuMerUtil.formatFenToYuan(detail
						.getCT_CREDIT_AMT()));
				detail.setAmt3(FuMerUtil.formatFenToYuan(detail
						.getCA_DEBIT_AMT()));
				detail.setAmt4(FuMerUtil.formatFenToYuan(detail
						.getCA_CREDIT_AMT()));
				detail.setAmt5(FuMerUtil.formatFenToYuan(detail.getCA_BALANCE()));
				detail.setAmt6(FuMerUtil.formatFenToYuan(detail.getCU_BALANCE()));
			}
		}
		return "fasTxnDetails";
	}

	public String expMonthRpt() {
		HttpServletResponse response = (HttpServletResponse) ActionContext
				.getContext().get(ServletActionContext.HTTP_RESPONSE);
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("交易记录详情");
		HSSFRow header = sheet.createRow(0);
		header.createCell(0).setCellValue("流水");
		header.createCell(1).setCellValue("交易日期");
		header.createCell(2).setCellValue("记账日期");
		header.createCell(3).setCellValue("账面余额出账金额");
		header.createCell(4).setCellValue("账面余额入账金额");
		header.createCell(5).setCellValue("可用余额出账金额");
		header.createCell(6).setCellValue("可用余额入账金额");
		header.createCell(7).setCellValue("可用余额");
		header.createCell(8).setCellValue("未转结余额");
		header.createCell(9).setCellValue("备注");
		TAcntBookDtlHisService acntBookDtlService = new TAcntBookDtlHisService();
		acnt = acnt.trim();// 获取账户
		// 转换账户为子账户：t_basic_acnt --->>cust_acnt_no='主账户' and in_acnt_idx=1
		// 获取basic_acnt_no即为子账户
		TBasicAcntService basicAcntService = new TBasicAcntService();
		String subAcnt = basicAcntService.getSubAcnt(acnt);
		totalCnt = acntBookDtlService.countDetail(subAcnt.trim(), startDt,
				endDt, realBookInd);
		if (totalCnt > 0) {
			HSSFRow detailRow = null;
			int pages = totalCnt % 1000 == 0 ? totalCnt / 1000
					: totalCnt / 1000 + 1;
			for (int i = 0; i < pages; i++) {
				details = acntBookDtlService.queryDetails(subAcnt.trim(),
						startDt, endDt, realBookInd, i * 1000, (i + 1) * 1000);
				detailRow = sheet.createRow(i * 1000 + i + 1);
				detailRow.createCell(0).setCellValue(
						details.get(i).getTRACE_NO());
				detailRow.createCell(1).setCellValue(
						details.get(i).getFAS_SETTLE_DT());
				detailRow.createCell(2).setCellValue(
						details.get(i).getBOOK_DT());
				detailRow.createCell(3).setCellValue(
						FuMerUtil.formatFenToYuan(details.get(i)
								.getCT_DEBIT_AMT()));
				detailRow.createCell(4).setCellValue(
						FuMerUtil.formatFenToYuan(details.get(i)
								.getCT_CREDIT_AMT()));
				detailRow.createCell(5).setCellValue(
						FuMerUtil.formatFenToYuan(details.get(i)
								.getCA_DEBIT_AMT()));
				detailRow.createCell(6).setCellValue(
						FuMerUtil.formatFenToYuan(details.get(i)
								.getCA_CREDIT_AMT()));
				detailRow.createCell(7).setCellValue(
						FuMerUtil.formatFenToYuan(details.get(i)
								.getCA_BALANCE()));
				detailRow.createCell(8).setCellValue(
						FuMerUtil.formatFenToYuan(details.get(i)
								.getCU_BALANCE()));
				detailRow.createCell(9).setCellValue(
						details.get(i).getBOOK_DIGEST());
			}
		}
		try {
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ new String(("日交易记录详情").getBytes("gb2312"), "ISO8859-1")
					+ ".xls");
			// 设置下载头信息
			response.setContentType("application/vnd.ms-excel; charset=utf-8");
			book.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public String huaAnQueryBalanceIni() {
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext()
				.getSession().get(TDataDictConst.OPERATOR_INF);
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		List<InsInfCacheBean> insInfCacheBeans = new ArrayList<InsInfCacheBean>();
		Map map = (Map) SystemParams.getCache(tOperatorInf.getMCHNT_CD());
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String mchntCd = it.next().toString();
			InsInfCacheBean insInfCacheBean = (InsInfCacheBean) map
					.get(mchntCd);
			insInfCacheBeans.add(insInfCacheBean);
		}
		ActionContext.getContext().put("insInfCacheBeans", insInfCacheBeans);
		return "huaAnQueryBalanceIni";
	}

	public String getMchntRlatBlac() {
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext()
				.getSession().get(TDataDictConst.OPERATOR_INF);
		List<InsInfCacheBean> mchntRlatBlacs = new ArrayList<InsInfCacheBean>();
		TCustStlInfService custStlInfService = new TCustStlInfService();
		Map map = (Map) SystemParams.getCache(tOperatorInf.getMCHNT_CD());
		if ("1".equals(selectTp)) {
			if ("".equals(mchntCd)) {
				Iterator it = map.keySet().iterator();
				while (it.hasNext()) {
					String mchntCd = it.next().toString();
					InsInfCacheBean insInfCacheBean = (InsInfCacheBean) map
							.get(mchntCd);
					mchntRlatBlacs.add(insInfCacheBean);
				}
			} else {
				TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
				TInsMchntInf tInsInf = tInsMchntInfService
						.selectTInsInfByMchntCd(mchntCd);
				InsInfCacheBean insInfCacheBean = SystemParams
						.getInsInfCacheBean(tInsInf);
				mchntRlatBlacs.add(insInfCacheBean);
			}
		} else if ("2".equals(selectTp)) {
			String mchntNm = (mchntCd_tag == null ? "" : mchntCd_tag);
			try {
				mchntNm = URLDecoder.decode(mchntNm, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] mchntInf = mchntNm.split("：");
			String cd = "";
			String nm = "";
			if (mchntInf.length >= 2) {
				cd = mchntInf[0];
				nm = mchntInf[1];
			} else {
				cd = mchntInf[0];
				nm = mchntInf[0];
			}
			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				String mchntCd = it.next().toString();
				InsInfCacheBean insInfCacheBean = (InsInfCacheBean) map
						.get(mchntCd);
				if (insInfCacheBean.getMCHNT_CD().toUpperCase()
						.indexOf(cd.toUpperCase()) >= 0
						|| insInfCacheBean.getINS_NAME_CN().toUpperCase()
								.indexOf(nm.toUpperCase()) >= 0) {
					mchntRlatBlacs.add(insInfCacheBean);
				}
			}
		}
		for (InsInfCacheBean insInfCacheBean : mchntRlatBlacs) {
			List<String> mchntRlatAcnts = custStlInfService
					.getAcntNosByIndCd(insInfCacheBean.getINS_CD());
			String[] strs = null;
			if (mchntRlatAcnts.size() > 0) {
				String acnt = mchntRlatAcnts.get(0).trim();
				TCustCwdAcntService tCustCwdAcntService = new TCustCwdAcntService();
				TCustCwdAcnt tCustCwdAcnt = tCustCwdAcntService
						.selectTCustCwdAcntByCustAcntNo(acnt);
				if (tCustCwdAcnt != null) {
					insInfCacheBean.setOutAcntNm(tCustCwdAcnt.getOUT_ACNT_NM());
					insInfCacheBean.setOutAcntNo(tCustCwdAcnt.getOUT_ACNT_NO());
					insInfCacheBean.setBankNm(tCustCwdAcnt.getISS_BANK_NM());
				}
				try {
					logger.info("=========余额查询开始========");
					strs = FasService.queryBalance(insInfCacheBean.getINS_CD(),
							acnt, VirtAcntUtil.getSsn());
					if (strs != null) {
						String balance0 = FuMerUtil.formatFenToYuan(Double
								.valueOf(strs[3]));// 账面余额
						String balance1 = FuMerUtil.formatFenToYuan(Double
								.valueOf(strs[4]));// 可用余额
						String balance2 = FuMerUtil.formatFenToYuan(Double
								.valueOf(strs[5]));// 未结余额
						String balance3 = FuMerUtil.formatFenToYuan(Double
								.valueOf(strs[6]));// 冻结余额
						insInfCacheBean.setBalance0(balance0);
						insInfCacheBean.setBalance1(balance1);
						insInfCacheBean.setBalance2(balance2);
						insInfCacheBean.setBalance3(balance3);
					}
				} catch (Exception e) {
					logger.error("余额查询出错", e);
				}// 查询余额
			}
		}
		ActionContext.getContext().put("mchntRlatBlacs", mchntRlatBlacs);
		return "huaAnDetail";
	}
}
