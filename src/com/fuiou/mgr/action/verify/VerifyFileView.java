package com.fuiou.mgr.action.verify;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 账户验证文件查看
 * zx
 *
 */
public class VerifyFileView extends ActionSupport {
	private static final Logger logger = LoggerFactory.getLogger(VerifyFileView.class);
	HashMap<String,String> stateMap = new HashMap<String,String>();
	HashMap<String,String> resultStaMap = new HashMap<String,String>();
	private TDataDictService tDataDictService = new TDataDictService();
	String fileName;
	String fileBusiTp;
	public String getFileBusiTp() {
		return fileBusiTp;
	}

	public void setFileBusiTp(String fileBusiTp) {
		this.fileBusiTp = fileBusiTp;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String execute() throws Exception {
		//商户信息
		TInsMchntInf tInsInf = (TInsMchntInf)ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		String merId = tInsInf.getMCHNT_CD();
		HttpServletRequest request = (HttpServletRequest)ActionContext
			.getContext().get(ServletActionContext.HTTP_REQUEST);
//		String fileBusiTp = TDataDictConst.BUSI_CD_VERIFY;
		stateMap.put("0", "商户未确认（默认状态）");
		stateMap.put("1", "商户已确认");
		stateMap.put("2", "运营人员已确认");
		stateMap.put("3", "运营人员退回");
		stateMap.put("4", "超时退回");
		stateMap.put("5", "正在处理");
		stateMap.put("6", "银行文件生成失败");
		stateMap.put("7", "银行文件生成成功");
		stateMap.put("8", "处理失败");
		stateMap.put("9", "处理成功");
		resultStaMap.put("0", "未生成");
		resultStaMap.put("1", "生成中");
		resultStaMap.put("2", "已生成");
		
		if(tInsInf == null) {
			logger.error("商户信息取得异常");
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.MCHNT_NULL_ER));
			return ERROR;
		}
		if(fileName == null || "".equals(fileName)) {
			logger.error("查询参数错误:文件名为空");
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NULL_ER));
			return ERROR;
		}
		if(fileBusiTp == null || "".equals(fileBusiTp)) {
			logger.error("查询参数错误:业务类型为空");
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_BUSI_TP_ER));
			return ERROR;
		}
		
		TFileInfService service = new TFileInfService();
		TFileInf tFileInf = service.selectByMchntCdAndFileName(merId, fileName);

		if(tFileInf == null) {
			request.setAttribute("message", tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SELECT_DATA_NULL_ER));
			return ERROR;
		}
		request.setAttribute("fileName", tFileInf.getFILE_NM());
		request.setAttribute("fileMerNo", tFileInf.getFILE_MCHNT_CD());
		request.setAttribute("fileDate", tFileInf.getFILE_DT());
		request.setAttribute("fileBusiTp", tFileInf.getFILE_BUSI_TP());
		request.setAttribute("fileSeq", tFileInf.getFILE_SEQ());
		request.setAttribute("fileSize", tFileInf.getFILE_SIZE());
		request.setAttribute("rowCount", tFileInf.getFILE_ROWS());
		request.setAttribute("fileAmt", FuMerUtil.formatFenToYuan(tFileInf.getFILE_AMT()));
		request.setAttribute("fileState", stateMap.get(tFileInf.getFILE_ST()+""));
		request.setAttribute("resultFileState", resultStaMap.get(tFileInf.getFILE_RSLT_ST()+""));
		request.setAttribute("resultFileName", tFileInf.getFILE_RSLT_NM());
		request.setAttribute("fileOperId", tFileInf.getOPR_USR_ID());
		
		return SUCCESS;
	}
}
