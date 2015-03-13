package com.fuiou.mgr.checkout;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.FileNameInf;
import com.fuiou.mer.model.TFileInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TFileInfService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TPmsBankInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;


public class CheckOutBase {

	private static final Logger logger = LoggerFactory.getLogger(CheckOutBase.class);
	protected  TFileInfService tFileInfService = null;
	protected  TInsMchntInfService tInsMchntInfService = null;
	protected  TDataDictService tDataDictService = null;
	protected TPmsBankInfService tPmsBankInfService = null;
	private static Pattern AMT_PATTERN = Pattern.compile("^(0\\.[1-9]\\d?|0\\.[0][1-9]|[1-9]\\d{0,13}(\\.\\d{1,2})?)$");
	
	public CheckOutBase(){
		tFileInfService = new TFileInfService();
		tInsMchntInfService = new TInsMchntInfService();
		tDataDictService = new TDataDictService();
		tPmsBankInfService = new TPmsBankInfService();
	}
	
	/**
	 * 汇总信息
	 * <br>key: fileNam 文件名
	 * <br>key: fileBusiTp 业务代码
	 * <br>key: fileTotalNumber 明细总行数
	 * <br>key: fileAmt 总额
	 */
	public HashMap<String, String> collectInfo = new HashMap<String,String>();
	
	/**
	 * 文件名验证，数据库中是否存在该文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param fileBusiTp
	 *            业务类型
	 * @return 是/否
	 */
	public boolean hasSameFileNameInDb(String mchntCd, String fileName) {
		if (null != tFileInfService.selectByMchntCdAndFileName(mchntCd, fileName)) {
			return true;
		}
		return false;
	}

	/**
	 * 文件相同笔数金额验证
	 * 
	 * @param mechnt 商户
	 * @param busiCd 业务类型
	 * @param fileAmt 总金额
	 * @return TFileInf <br>
	 */
	public TFileInf SameFileName(String mchnt, String busiCd, Long fileAmt, Integer fileRows) {
		String days = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.FILE_UNIFORM_DAYS);
		if(null == days){//默认是查询7天的
			days = "7";
		}
		//return fileInfService.selectDuplicateFile(mechnt,TDataDictConst.BUSI_CD_PAYFOR, fileAmt,Integer.valueOf(days));
		return tFileInfService.selectDuplicateFile(mchnt, busiCd, fileAmt, fileRows, Integer.valueOf(days));
	}

	/**
	 * 验证商户号
	 * 
	 * @param mchntCd
	 *            商户编号
	 * @return 0 停用 <br>
	 *         1 正常商户 <br>
	 *         2已注销<br>
	 *         3无此商户<br>
	 */
	public HashMap<String,String> MerchantCdCheckOut(String merchant) {
		HashMap<String,String> map = new HashMap<String,String>();
		if (null == merchant || "".equals(merchant)) {
			map.put(TDataDictConst.MCHNT_NULL_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.MCHNT_NULL_ER));
			logger.error(TDataDictConst.MCHNT_NULL_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.MCHNT_NULL_ER)+merchant);
			return map;
		}
		TInsMchntInf inf = tInsMchntInfService.MchntCdCheckOut(merchant, TDataDictConst.INS_ST_USABLE);
		if (null == inf ) {
			map.put(TDataDictConst.MCHNT_NULL_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.MCHNT_NULL_ER));
			logger.error(TDataDictConst.MCHNT_NULL_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.MCHNT_NULL_ER)+merchant);
			return map;
		}
		
		if (TDataDictConst.INS_ST_LOGOUT.equals(inf.getINS_ST())) {
			map.put(TDataDictConst.MCHNT_LOGOUT_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.MCHNT_LOGOUT_ER));
			logger.error(TDataDictConst.MCHNT_LOGOUT_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.MCHNT_LOGOUT_ER)+merchant);
			return map;
		}
		if (TDataDictConst.INS_ST_STOP.equals(inf.getINS_ST())) {
			map.put(TDataDictConst.MCHNT_STOP_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.MCHNT_STOP_ER)+merchant);
			return map;
		}
		return map;
	}

	/**
	 * 文件名格式验证 <br>
	 * @param fileName 文件名
	 * @param mchntCd 商户号
	 * @param busiCd 需要校验业务代码
	 * @param fileNameInf 传出文件名信息
	 * @return HashMap<String,String>
	 */
	public LinkedHashMap<String,String> fileNameFormatCheckOut(String fileName,String mchntCd,String busiCd, FileNameInf fileNameInf) {
		// 业务代码_交易日期（YYYYMMDD）_当日序号.txt
		//非空验证
		LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
		if (null == fileName || "".equals(fileName.trim())) {
			map.put(TDataDictConst.FILE_NULL_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_NULL_ER));
			logger.error(TDataDictConst.FILE_NULL_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_NULL_ER)+fileName);
			return map;
		}
		//后缀验证
		String[] fileNamePostFix = fileName.split("\\.");
		if(fileNamePostFix.length != 2){
			map.put(TDataDictConst.FILE_FORMAT_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_FORMAT_ER));
			logger.error(TDataDictConst.FILE_FORMAT_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_FORMAT_ER)+fileName);
			return map;
		}
		if(null == TDataDictConst.selectPostFix(fileNamePostFix[1])){
			map.put(TDataDictConst.FILE_POSTFIX_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB,TDataDictConst.FILE_POSTFIX_ER));
			logger.error(TDataDictConst.FILE_POSTFIX_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_POSTFIX_ER)+fileName);
			return map;
		}
		//格式验证
		String[] fileNames = fileNamePostFix[0].split(TDataDictConst.FILE_TITLE_APART);
        if(fileNames.length != 3){
            map.put(TDataDictConst.FILE_FORMAT_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_FORMAT_ER));
            logger.error(TDataDictConst.FILE_FORMAT_ER + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_FORMAT_ER) + fileName);
            return map;
        }
        //业务代码验证 
        if(!busiCd.equals(fileNames[0])){
            map.put(TDataDictConst.FILE_BUSI_TP_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_BUSI_TP_ER));
            logger.error(TDataDictConst.FILE_BUSI_TP_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_BUSI_TP_ER)+fileName);
            return map;
        }
        //交易日期
        if(!FuMerUtil.getCurrTime(TDataDictConst.FILE_NAM_DATE_FORMAT).equals(fileNames[1].toString())){
            map.put(TDataDictConst.FILE_NAME_DATE_FORMAT_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER));
            logger.error(TDataDictConst.FILE_NAME_DATE_FORMAT_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_NAME_DATE_FORMAT_ER)+fileName);
            return map;
        }
        //当日序号为四位数字
		if(!fileNames[2].matches("\\d{4}")){
			map.put(TDataDictConst.FILE_NAME_SEQ_ER, tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_SEQ_ER));
			logger.error(TDataDictConst.FILE_NAME_SEQ_ER+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_NAME_SEQ_ER)+fileName);
			return map;
		}
		fileNameInf.setFileName(fileName);
		fileNameInf.setFileNamePrefix(fileName.substring(0, fileName.indexOf(".")));
		fileNameInf.setFileNameSuffix(fileName.substring(fileName.indexOf(".")));
		fileNameInf.setBusiCd(fileNames[0]);
		fileNameInf.setDt(fileNames[1]);
		fileNameInf.setSeq(fileNames[2]);
		return map;
	}
	
	/**
	 * 查询相同文件
	 * @param map
	 * @param fileRows 笔数
	 * @param fileAmt 金额
	 */
	public void selectUniformFile(HashMap<String, String> map,String mchntCd,long fileAmt, int fileRows,String busiCd){
		TFileInf fileInf =  SameFileName(mchntCd, busiCd, fileAmt, fileRows);
		if(null != fileInf){
			map.put("fileNam", fileInf.getFILE_NM());
			map.put("filaDate", FuMerUtil.date2String(fileInf.getROW_CRT_TS(), "yyyy-MM-dd HH:mm:ss"));
		}
	}
	
	/**
	 * 支行名称掐头去尾，仅用于中行、建行、广发
	 * @param bankName 支行名称
	 * @return 掐头去尾后的支行名称
	 */
	public String subBankName(String bankName) {
		String[] startsNames = {"中国银行（香港）有限公司", "中国银行股份有限公司", "中国银行", "中行", "中国建设银行股份有限公司", "建设银行股份有限公司", "中国建设银行", "建设银行", "建行", "广东发展银行股份有限公司", "广东发展银行", "广发银行", "广发行", "广发"};
		String[] endNames = {"支行营业部", "分行营业部", "支行", "分行", "营业部", "营业厅", "分理处", "储蓄所"};
		for (int i = 0; i < startsNames.length; i++) {
			if (bankName.startsWith(startsNames[i])) {
				bankName = bankName.substring(startsNames[i].length());
			}
		}
		for (int i = 0; i < endNames.length; i++) {
			if (bankName.endsWith(endNames[i])) {
			    bankName = bankName.substring(0,  bankName.length() - endNames[i].length());
			}
		}
		return bankName;
	}
	/**
	 * 字符串截取出银行名称
	 *cyx
	 */
	
	public String subBankName2(String bankName) {
		int i=bankName.indexOf("银行");
		if(i==-1)
		{
			return null;
			
		}else
			return bankName.substring(0,i+2);
	}
	/**
	 * 字符串截取出银前面的
	 *cyx
	 */
	
	public String subBankName3(String bankName) {
		int i=bankName.indexOf("银");
		if(i==-1)
		{
			return null;
			
		}else
			return bankName.substring(0,i);
	}
	
	/**
	 * 检查字符串是否超过数据库字段长度
	 * @param string 待检查字符串
	 * @param dbLengh 数据库字段长度
	 * @return true：超限，false：未超限
	 */
	public static boolean exceedDbLenth(String string, int dbLengh){
	    int stringLen = Integer.MAX_VALUE;
	    try{
            stringLen = string.getBytes(TDataDictConst.DB_CHARSET).length;
        }catch(UnsupportedEncodingException e){
        }
        return stringLen>dbLengh;
	}
	
	
	public static boolean exceedDbNameLenth(String string, int dbLengh){
	    int stringLen = Integer.MAX_VALUE;
	    try{
            stringLen = string.getBytes(TDataDictConst.FILE_CODE).length;
        }catch(UnsupportedEncodingException e){
        }
        return stringLen>dbLengh;
	}
	
	
	/**
	 * 检查金额格式，元占14个字符，最多两位分，0元有效
	 * @param money 金额字符串
	 * @return
	 */
	public static boolean validMoney(String money){
	    Matcher matcher = AMT_PATTERN.matcher(money);
	    return matcher.matches();
	} 
	
	/**
	 * 获取代收单笔限额
	 * @return 单位：分
	 */
	public long getMaxAmtIncomeFor(){
//	    String dictValue = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.MAX_AMT_INCOME_FOR);
		 String dictValue = SystemParams.sysConfig.get(TDataDictConst.MAX_AMT_INCOME_FOR);
	    try{
            long amt = Long.parseLong(dictValue);
            return amt;
        }catch(Exception e){
            return 1000000;
        }
	}
	
	/**
	 * 获取付款单笔限额
	 * @return 单位：分
	 */
	public long getMaxAmtPayFor(){
//	    String dictValue = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.MAX_AMT_PAY_FOR);
	    String dictValue = SystemParams.sysConfig.get(TDataDictConst.MAX_AMT_PAY_FOR);
        try{
            long amt = Long.parseLong(dictValue);
            return amt;
        }catch(Exception e){
            return 1000000;
        }
    }
	
	public Date getCurrDbTime(){
	    return null;
	}
	
	
	public static void main(String[] args)
	{
		String s=new CheckOutBase().subBankName3("上海银行长沙支行");
		System.out.println(s);
	}
}
