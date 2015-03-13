package com.fuiou.mgr.action.payfor;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.util.fileconvert.payfor.huaan.HuaAnPayForFileConvert;
import com.opensymphony.xwork2.ActionContext;

/** 
 * @auth caoyongxing
 * time:2011-4-22 上午11:39:00       
 * 
 */
public class UploadTemp {
	
	private static Logger logger = LoggerFactory.getLogger(UploadTemp.class);
	private File upload;
	/** 文件名称 */
	private String uploadFileName;
	private String uploadContentType;
	private String message;
	private TFileInfService tFileInfService = new TFileInfService();
	private LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	private TDataDictService tDataDictService = new TDataDictService();
	private String btnType;
	private String saveFileExcel;
	private String isDisplayFeeMsg;
	
	public String getSaveFileExcel() {
		return saveFileExcel;
	}
	public void setSaveFileExcel(String saveFileExcel) {
		this.saveFileExcel = saveFileExcel;
	}
	public String getBtnType() {
		return btnType;
	}
	public void setBtnType(String btnType) {
		this.btnType = btnType;
	}
	public String getIsDisplayFeeMsg() {
		return isDisplayFeeMsg;
	}
	public void setIsDisplayFeeMsg(String isDisplayFeeMsg) {
		this.isDisplayFeeMsg = isDisplayFeeMsg;
	}
	/**   
     * 删除文件   
     * @param fileName String 文件名，包含文件路径   
     * @return int 成功 >0；失败 -1  
     */   
    private int deleteFile(String fileName) {   
        int results = 0;   
        File file = new File(fileName);   
        if (file.exists()) {   
            if (file.delete()) {   
                results = results + 1;   
            }   
        }   
        return results;   
    }

	public String executePayForBtnType() throws Exception {
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		// 商户信息
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);

		TFileInf fileInf = tFileInfService.selectByMchntCdAndFileName(tInsInf.getMCHNT_CD(), uploadFileName);
		if (null == fileInf) {
			errorMap.put(TDataDictConst.SELECT_DATA_NULL_ER, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SELECT_DATA_NULL_ER));
			logger.error(TDataDictConst.SELECT_DATA_NULL_ER + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.SELECT_DATA_NULL_ER) + uploadFileName);
			ActionContext.getContext().put("message", message);
			ActionContext.getContext().put("errorMap", errorMap);
			return "error";
		}
		// ********文件状态是用户未确认
		if ("OK".equals(btnType)) {// 确认提交，并忽略错误记录，
			if(tFileInfService.updateTFileInfAndTApsTxnLogByPayFor(TDataDictConst.FILE_ST_MCHNT_CONFIRM ,tOperatorInf.getMCHNT_CD(),fileInf.getFILE_NM() , TDataDictConst.FILE_ST_MCHNT_INIT) > 0){
				errorMap.put(TDataDictConst.SUCCEED, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + " 批量付款文件确认提交");
			}else{
				errorMap.put(TDataDictConst.SUCCEED, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + " 批量付款文件确认提交");
			}
			ActionContext.getContext().put("errorMap", errorMap);
			return "error";
		}
		if ("NO".equals(btnType)) {// 取消，删除文件信息与记录
			//删除文件
			if(tFileInfService.deleteTFileInfAndTApsTxnLogByWait(tOperatorInf.getMCHNT_CD(),fileInf.getFILE_NM(),TDataDictConst.BUSI_CD_PAYFOR) > 0){
				deleteFile(fileInf.getFILE_PATH()+fileInf.getFILE_NM()+fileInf.getFILE_NM_SFFX());
//				deleteFile(saveFileExcel);
				errorMap.put(TDataDictConst.SUCCEED, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.SUCCEED));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + "取消批量付款文件");
			}else{
				errorMap.put(TDataDictConst.UNSUCCESSFUL, "文件名:" + uploadFileName + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.UNSUCCESSFUL));
				logger.info("操作员" + tOperatorInf.getLOGIN_ID() + " 商户号:" + tOperatorInf.getMCHNT_CD() + uploadFileName + "取消批量付款文件失败");
			}
			ActionContext.getContext().put("errorMap", errorMap);
			return "error";
		}
		ActionContext.getContext().put("message", "系统错误!");
		return "error";
	}
	
	
	public String executeUpload()throws Exception  {
		ActionContext ac= ActionContext.getContext();
		if(upload==null||!upload.exists()) {
			message="文件上传失败";
			return "error";
		}
		logger.info(upload.getAbsoluteFile().getName());
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		String mchntCd = tOperatorInf.getMCHNT_CD();
		// 机构
		TInsMchntInf tInsInf = (TInsMchntInf) ActionContext.getContext().getSession().get(TDataDictConst.INS_INF);
		// 1保存路径
		logger.info("商户号："+mchntCd+"操作员："+tOperatorInf.getLOGIN_ID());
		Date currDbTime=new Date();
		
		// 查询出商户类型
		TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
		TInsMchntInf tInsMchntInf = tInsMchntInfService.getTInsMchntInfByMchntAndRowTp(mchntCd, "1");
		isDisplayFeeMsg  = "1";
		if(null != tInsMchntInf){
			String mchntTp = tInsMchntInf.getMCHNT_TP();
			if(mchntTp.length()>=2){
				if("D4".equals(mchntTp.substring(0, 2))){
					isDisplayFeeMsg = "0";
				}
			}
		}
		
		HuaAnPayForFileConvert fileConvert = new HuaAnPayForFileConvert();
		//文件接入对象
		FileAccess fileAccess=new FileAccess();
		fileAccess.setFile(upload);
		fileAccess.setFileName(uploadFileName);
		Map<String, String> map = fileConvert.doConvert(fileAccess, mchntCd,tOperatorInf,tInsInf);
		if(map == null){
			String date=FuMerUtil.date2String(currDbTime, "yyyyMMdd");
			String savePath = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.HUAAN_EXCEL_FILE_PATH);
			savePath =savePath+File.separator+mchntCd+File.separator+date;
			File savefile = new File(new File(savePath), uploadFileName);
			if (!savefile.getParentFile().exists()) {
				savefile.getParentFile().mkdirs();
			}
			logger.info(savefile.getAbsoluteFile().getName());
			FileUtils.copyFile(upload, savefile);
			savefile.setExecutable(false);
			savefile.setWritable(true, false);
			ac.put("message", "文件上传成功");
			ActionContext.getContext().put("saveFileExcel", savefile.getPath());
			return "payforHuaAn_succeed_error";
		}
		ActionContext.getContext().put("errorMap", map);
		
		return "error";
	}
	public File getUpload() {
		return upload;
	}
	public void setUpload(File upload) {
		this.upload = upload;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUploadContentType() {
		return uploadContentType;
	}
	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

}
