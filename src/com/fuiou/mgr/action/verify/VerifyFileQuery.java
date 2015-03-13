package com.fuiou.mgr.action.verify;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.util.page.PagerUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 账户验证文件查询
 * 
 * zx
 * 
 */
public class VerifyFileQuery extends ActionSupport {

	private static final long serialVersionUID = 8374126994004224457L;
	private HttpServletRequest request;
	private TDataDictService tDataDictService = new TDataDictService();
	private TFileInfService fileInfService = new TFileInfService();
	String startDate;
	String endDate;
	String stateSel;
	String fileName;
	String fileBusiTp;

	

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStateSel() {
		return stateSel;
	}

	public void setStateSel(String stateSel) {
		this.stateSel = stateSel;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileBusiTp() {
		return fileBusiTp;
	}

	public void setFileBusiTp(String fileBusiTp) {
		this.fileBusiTp = fileBusiTp;
	}
	
	@Override
	public String execute() throws Exception {
		request = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		//判断起始日期和截止日期是否为空
		if("".equals(startDate) || null ==  startDate || "".equals(endDate)|| null ==endDate){
			request.setAttribute("message", "日期不能为空");
			request.setAttribute("fileCount", 0);
			return SUCCESS;
		}
		//在2个日期都不为空的情况下判断起始日期是否在截止日期之前
		if ((startDate != null && !"".equals(startDate)) && (endDate != null && !"".equals(endDate))) {
			if ((endDate.compareTo(startDate) < 0)) {
				request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_WRON_FILEDT));
				request.setAttribute("fileCount", 0);
				return SUCCESS;
			}
		}
		
		// 商户信息
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		String merId = tInsInf.getMCHNT_CD();
		
		int totalCount = 0; // 返回记录数
		
		totalCount = fileInfService.countUploadFiles(startDate,endDate,merId,fileBusiTp,fileName,null);//20140113文件查询改造
		if (totalCount == 0) {
			// 没有符合条件的记录
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_QUERY_NO_RECORD));
			request.setAttribute("fileCount", 0);
			return SUCCESS;
		}
		int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCount);
		List<TFileInf> files = fileInfService.getUploadFiles(startDate,endDate,merId,fileBusiTp,fileName,null, pageInfo[0], pageInfo[1]);

		request.setAttribute("fileCount", totalCount);
		request.setAttribute("fileList", files);
		request.setAttribute("fileBusiTp", fileBusiTp);

		if (fileBusiTp.equals(TDataDictConst.BUSI_CD_INCOMEFOR)) {
			return "incomeSuc";
		} else if (fileBusiTp.equals(TDataDictConst.BUSI_CD_PAYFOR)) {
			return "payforSuc";
		} else if (fileBusiTp.equals(TDataDictConst.BUSI_CD_VERIFY)) {
			return SUCCESS;
		}
		return SUCCESS;
	}
}
