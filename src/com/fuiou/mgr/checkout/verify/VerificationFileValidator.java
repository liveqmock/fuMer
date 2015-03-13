package com.fuiou.mgr.checkout.verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.CorrectDetInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.RegexCheckUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.util.DetailRow;
import com.fuiou.mgr.util.StringUtil;

/**
 * 账户验证文件格式验证
 * zx
 *
 */
public class VerificationFileValidator extends CheckOutBase{
	private static Logger logger = LoggerFactory.getLogger(VerificationFileValidator.class);
	private TDataDictService tDataDictService = new TDataDictService();
	
	/**
	 * 验证商户上传的文件名格式
	 * 
	 * 格式：商户号_业务代码_交易日期（YYYYMMDD）_当日序号.txt
	 * 
	 * @param fileName
	 * @return 
	 */
//	public String fileNameCheckOut(String fileName, String fileBusiTp) {
//		StringBuffer errMsg = new StringBuffer("");
//		if(fileName == null || "".equals(fileName)) {
//			logger.error("文件名为空！");
//			errMsg.append("系统异常！");
//			vFileErrMsg.setFileNameErrMsg(errMsg.toString());
//			return false;
//		}
//		
//		fileName = fileName.substring(0,fileName.indexOf(".txt"));
//		String[] nameSplit = fileName.split("_");
//		
//		
//		// 文件名组成元素个数验证
//		if(nameSplit.length != 4) {
//			logger.error("文件名结构错误！");
//			errMsg.append("文件名结构错误！");
//			vFileErrMsg.setFileNameErrMsg(errMsg.toString());
//			return false;
//		}
//		// 商户号验证
//		String mer_id = getMerId();
//		if(!nameSplit[0].equals(mer_id)) {
//			logger.error("文件名中的商户号与登录商户号不符！");
//			errMsg.append("文件名中的商户号与登录商户号不符！");
//			vFileErrMsg.setFileNameErrMsg(errMsg.toString());
//			return false;
//		}
//		// 业务代码验证
//		String bus_cd = "8003";	// 8003为批量代收（暂定）
//		if(!nameSplit[1].equals(bus_cd)){
//			logger.error("文件名中的业务代码错误！");
//			errMsg.append("文件名中的业务代码错误！");
//			vFileErrMsg.setFileNameErrMsg(errMsg.toString());
//			return false;
//		}
//		// 交易日期验证
//		if(!nameSplit[2].equals(date)) {
//			logger.error("文件名中的交易日期错误！");
//			errMsg.append("文件名中的交易日期错误！");
//			vFileErrMsg.setFileNameErrMsg(errMsg.toString());
//			return false;
//		}
//		// 当日序号验证
//		Pattern pattern = Pattern.compile("[0-9]");   
//		Matcher matcher = pattern.matcher(nameSplit[3]);   
//		if(!matcher.matches()) {
//			logger.error("文件名中的当日序号必须为数字！");
//			errMsg.append("文件名中的当日序号必须为数字！");
//			vFileErrMsg.setFileNameErrMsg(errMsg.toString());
//			return false;
//		}
//		
//		// 检索DB，验证文件名是否重复验证
//		///////////////////////////////////
//		///////////////////////////////////
//		
//		return true;
//	}
	
	/**
	 * 验证文件内容格式
	 * 
	 * 1.汇总信息验证(出现错误便停止)
	 * 2.逐行验证明细信息
	 * 4.保存格式错误的数据至列表
	 * 5.验证汇总信息中的明细数目
	 * 
	 * @param inMap 验证所需参数
	 * @return 验证结果
	 */
	@SuppressWarnings("rawtypes")
    public HashMap fileCheckOut(File checkOut_file, String mchntCd, String fileDate, String fileSeq) {
        HashMap outMap = new HashMap();
		String checkOut_result; // 验证结果 0：正确  1：汇总信息格式错误  2：明细行格式错误  3:文件内容为空  4:系统异常
		String checkOut_sumInfErrMsg; // 汇总行错误信息
		String checkOut_excErrMsg = null; // 异常信息
		DetailRow detInfErr; // 明细行格式错误类
		StringBuffer detInfErrMsg = null; // 明细行格式错误信息
		CorrectDetInf correctDet = null; // 格式正确的明细行
		List<DetailRow> checkOut_detInfErrList = new ArrayList<DetailRow>();
		List<CorrectDetInf> checkOut_correctDetList = new ArrayList<CorrectDetInf>();
		Set<String> indexSet = new HashSet<String>();
		int sumDetNo = 0; // 汇总信息中的明细数目
		int corRowCount = 0; // 明细行正确行数
		int errRowCount = 0; // 明细行错误行数
		int rowCount = 0; // 行数
		int avaRowCount = 0; // 有效行数
		int detRowCount = 0; // 明细行数
		String[] sumInfo; // 汇总信息
		String[] detInfo; // 明细信息
		String line;
		BufferedReader reader = null;
		try {
			logger.info("文件验证开始...");
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(checkOut_file), "GBK"));
			while((line = reader.readLine()) != null) {
				rowCount++;
				line = line.trim();
				// 跳过空行
				if("".equals(line)) {
					continue;
				}
				avaRowCount++;
				// 验证汇总信息
				if(avaRowCount == 1) {
					logger.info("汇总信息行："+ line);
					sumInfo = line.split(TDataDictConst.FILE_CONTENT_APART, 5);
					if(sumInfo.length != 5) {
                        logger.error("汇总信息格式错误或缺少汇总信息！");
                        checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_FORMAT_EP);
                        checkOut_result = "1";
                        this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
                        return outMap;
                    }
					for(int i=0;i<sumInfo.length;i++) {
						sumInfo[i] = sumInfo[i].trim();
					}
					if(!mchntCd.equals(sumInfo[0])) {
						logger.error("汇总信息中的商户号与登录商户号不符！");
						checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_MCHNT_CD_ER);
						checkOut_result = "1";
						this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
						return outMap;
					}
					if(!TDataDictConst.BUSI_CD_VERIFY.equals(sumInfo[1])){
						logger.error("汇总信息中的业务代码错误！");
						checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_BUSI_TP_ER);
						checkOut_result = "1";
						this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
						return outMap;
					}
					if(!fileDate.equals(sumInfo[2])) {
						logger.error("汇总信息中的交易日期错误！");
						checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_DATE_FORMAT_ER);
						checkOut_result = "1";
						this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
						return outMap;
					}
					if(!fileSeq.equals(sumInfo[3])) {
						logger.error("汇总信息中的当日序号与文件名中的当日序号不符！");
						checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_SEQ_ER);
						checkOut_result = "1";
						this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
						return outMap;
					}
					if("".equals(sumInfo[4]) || !(RegexCheckUtil.checkIsDigital(sumInfo[4]))) {
						logger.error("汇总信息中的明细数目格式错误！");
						checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
						checkOut_result = "1";
						this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
						return outMap;
					}
					sumDetNo = Integer.parseInt(sumInfo[4]);
					if(sumDetNo<=0){
		                logger.error(TDataDictConst.FILE_COLLECT_TOTAL_EP + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_TOTAL_EP) + checkOut_file.getName());
		                checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
                        checkOut_result = "1";
                        this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
                        return outMap;
		            }else if(sumDetNo>TDataDictConst.FILE_CONTENT_MAX_ROWS){
		                logger.error(TDataDictConst.FILE_CONTENT_MAXROWS_EP + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_CONTENT_MAXROWS_EP) + checkOut_file.getName());
		                checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_MAXROWS_EP);
                        checkOut_result = "1";
                        this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
                        return outMap;
		            }
					continue;
				}
				if(avaRowCount > TDataDictConst.FILE_CONTENT_MAX_ROWS + 1){
                    logger.error(TDataDictConst.FILE_CONTENT_MAXROWS_EP + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_CONTENT_MAXROWS_EP) + checkOut_file.getName());
                    checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_MAXROWS_EP);
                    checkOut_result = "1";
                    this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
                    return outMap;
				}
				// 验证明细行
				detInfErrMsg = new StringBuffer();
				detInfo = line.split(TDataDictConst.FILE_CONTENT_APART, 6);
				for(int i=0; i<detInfo.length; i++) {
					detInfo[i] = detInfo[i].trim();
				}
				if(detInfo.length != 6) {
					detInfErrMsg.append(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
					errRowCount++;
				} else if(StringUtil.isEmpty(detInfo[0])
						|| (detInfo[0].length() != TDataDictConst.FILE_CONTENT_SEQ_LENTTH) ) { // 明细序号校验
					// 明细序号格式错误
					detInfErrMsg.append("明细序列号："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
					errRowCount++;
				} else if( !(RegexCheckUtil.checkIsDigital(detInfo[0]))) { // 明细序号校验
					// 明细序号格式错误
					detInfErrMsg.append("明细序列号："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_FORMAT_ER));
					errRowCount++;
				}else if(indexSet.contains(detInfo[0])) { // 明细序号重复
					detInfErrMsg.append("明细序列号："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ITERANT));
					errRowCount++;
				} else if(StringUtil.isEmpty(detInfo[1])
						|| (detInfo[1].length() != 4)) { // 行别校验
					// 行别格式错误
					detInfErrMsg.append("行别："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
					errRowCount++;
				} else if(SystemParams.getProperty("BANK_CDS_VERIFY_SZ_STL_CNTR").indexOf(detInfo[1]) == -1) {
                    // 无效总行代码
                    detInfErrMsg.append("行别："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.INTER_BANK_CD_ER));
                    errRowCount++;
                } else if(StringUtil.isEmpty(detInfo[2])
						|| detInfo[2].length() < 10 || CheckOutBase.exceedDbLenth(detInfo[2], 28)) { // 账号/卡号校验
					// 账号/卡号格式错误
					detInfErrMsg.append("帐号:"+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
					errRowCount++;
				} else if(StringUtil.isEmpty(detInfo[3])) {// 账户名称校验
					// 账户名称格式错误
					detInfErrMsg.append("账户名："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_ER));
					errRowCount++;
				}else if(CheckOutBase.exceedDbLenth(detInfo[3], 30))
				{
					detInfErrMsg.append("账户名："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
					errRowCount++;	
				}
//				else if(!RegexCheckUtil.checkIsDigitAndChinese(detInfo[3]))
//                {
//                    detInfErrMsg.append("账户名："+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_ROW_FORMAT_ER));
//                    errRowCount++;  
//                }
				else if(StringUtil.isEmpty(detInfo[4])
						|| (detInfo[4].length() != 1)) {// 证件类型校验
					// 证件类型格式错误
					detInfErrMsg.append(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_WRONG_ID_TYPE));
					errRowCount++;
				} else if(TDataDictConst.ID_TYPES_SZ_STL_CNTR.indexOf(detInfo[4]) == -1) {
					// 无效证件类型
					detInfErrMsg.append(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FIlE_DETAIL_ROW_INVALID_ID_TYPE));
					errRowCount++;
				} else if(StringUtil.isEmpty(detInfo[5])
						|| (CheckOutBase.exceedDbLenth(detInfo[5], 20))) {// 证件号码校验	
					// 证件号码格式错误
					detInfErrMsg.append(tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_ROWS_LENGTH_ER));
					errRowCount++;
				}else{
				    indexSet.add(detInfo[0]);
				}
				// 错误行处理，保存错误行信息，界面显示用
				if(detInfErrMsg.length() != 0) {
					// 向错误行list添加DetailRow 明细序列|账户行别|帐号/卡号|账户名称|证件类型|证件号码
					detInfErr = new DetailRow();
					detInfErr.setRowNo(rowCount);
					detInfErr.setLine(line);
					detInfErr.setErrMsg(detInfErrMsg.toString());
					
					checkOut_result = "2";
					
					this.setOutMap(outMap, checkOut_result, null, checkOut_detInfErrList, detInfErr, null, null, null);
				// 正确行处理
				} else {
					// 到当前行为止还未出现错误，保存正确行数据(交易日志入库用)
					if(errRowCount == 0) {
						correctDet = new CorrectDetInf();
						correctDet.setIndex(detInfo[0]);
						correctDet.setCardNo(detInfo[2]);
						correctDet.setAccountName(detInfo[3]);
						correctDet.setBankNo(detInfo[1]);
						correctDet.setIdNo(detInfo[5]);
						correctDet.setIdType(detInfo[4]);
						correctDet.setLine(line);
						this.setOutMap(outMap, null, null, null, null, checkOut_correctDetList, correctDet, null);
					}
				}
				
			}
			
			if(avaRowCount<2){
			    logger.error(TDataDictConst.FILE_CONTENT_MINROWS_EP + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_CONTENT_MINROWS_EP) + checkOut_file.getName());
                checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_CONTENT_MINROWS_EP);
                checkOut_result = "3";
                this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
                return outMap;
			}
			
			// 明细行数 = 有效行数 - 1
			detRowCount = avaRowCount - 1;
			outMap.put("checkOut_detRowCount", detRowCount);
			// 正确明细行数 = 明细行数 - 错误明细行数
			corRowCount = detRowCount - errRowCount;
			outMap.put("checkOut_corRowCount", corRowCount);
			outMap.put("checkOut_errRowCount", errRowCount);
			
			// 汇总信息中的明细数目不等于实际行数
			if(sumDetNo != detRowCount) {
				
				logger.error("汇总信息中的明细数目不等于实际行数");
				logger.error(TDataDictConst.FILE_COLLECT_TOTAL_EP + " " + tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS, TDataDictConst.FILE_COLLECT_TOTAL_EP) + checkOut_file.getName());
                checkOut_sumInfErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_COLLECT_TOTAL_EP);
                checkOut_result = "1";
                this.setOutMap(outMap, checkOut_result, checkOut_sumInfErrMsg, null, null, null, null, null);
                return outMap;
			}
			
			// 文件验证结束，无错误
			if(errRowCount == 0) {
				checkOut_result = "0";
				this.setOutMap(outMap, checkOut_result, null, null, null, null, null, null);
			}
			
			outMap.put("checkOut_correctDetList", checkOut_correctDetList);
			outMap.put("checkOut_detInfErrList", checkOut_detInfErrList);
			
			logger.info("文件验证结束，明细行数：" + detRowCount + " 错误行数：" 
					+ errRowCount + " 正确行数：" + corRowCount);
		} catch (FileNotFoundException e) {
			logger.error("文件不存在：" + e);
			checkOut_result = "4";
			checkOut_excErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_NAME_CONTENT);
			this.setOutMap(outMap, null, checkOut_result, null, null, null, null, checkOut_excErrMsg);
		} catch (IOException e) {
			logger.error("文件读取错误：" + e);
			checkOut_result = "4";
			checkOut_excErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP);
			this.setOutMap(outMap, null, checkOut_result, null, null, null, null, checkOut_excErrMsg);
		} catch (Exception e) {
			logger.error("文件读取错误：" + e);
			checkOut_result = "4";
			checkOut_excErrMsg = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_WEB, TDataDictConst.FILE_READ_EP);
			this.setOutMap(outMap, null, checkOut_result, null, null, null, null, checkOut_excErrMsg);
		}finally{
		    if(reader != null){
                try{
                    reader.close();
                }catch(Exception e){
                }
            }
		}
		return outMap;
	}
	
	/**
	 * 返回信息设置
	 * @param outMap
	 * @param checkOut_result 验证结果
	 * @param checkOut_sumInfErrMsg	汇总行错误信息
	 * @param checkOut_detInfErrList 错误行信息List
	 * @param detInfErr 错误行
	 * @param checkOut_correctDetList 正确行List
	 * @param correctDet 正确行
	 * @param checkOut_excErrMsg 异常信息
	 */
	private void setOutMap(HashMap outMap, String checkOut_result, String checkOut_sumInfErrMsg,
			List checkOut_detInfErrList, DetailRow detInfErr, 
			List<CorrectDetInf> checkOut_correctDetList, CorrectDetInf correctDet,
			String checkOut_excErrMsg) {
		if(checkOut_result != null && !"".equals(checkOut_result)) {
			outMap.put("checkOut_result", checkOut_result);
		}
		if(checkOut_sumInfErrMsg != null && !"".equals(checkOut_sumInfErrMsg)) {
			outMap.put("checkOut_sumInfErrMsg", checkOut_sumInfErrMsg);
		}
		if(checkOut_detInfErrList != null && detInfErr != null) {
			checkOut_detInfErrList.add(detInfErr);
		}
		if(checkOut_correctDetList != null && correctDet != null) {
			checkOut_correctDetList.add(correctDet);
		}
		if(checkOut_excErrMsg != null && !"".equals(checkOut_excErrMsg)) {
			outMap.put("checkOut_excErrMsg", checkOut_excErrMsg);
		}
	}
	
}
