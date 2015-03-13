package com.fuiou.mgr.adapter.payfor.huaanfileProcess.excel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;

public class ExcelXLSAnalysis extends ExcelAnalysis{
	private static Logger logger = LoggerFactory.getLogger(ExcelXLSAnalysis.class);
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource, String txnDataType){
		payForTxnInfBean=new PayForTxnInfBean();
		payForTxnInfSumBean=payForTxnInfBean.getPayForTxnInfSumBean();
		payForTxnInfDetailRowBeanList=payForTxnInfBean.getPayForTxnInfDetailRowBeanList();
		FileAccess fileAccess = (FileAccess)accessBean;
		File convertFile = fileAccess.getFile();
		
		if(null != convertFile){
			logger.debug("开始处理华安的文件,文件名为:"+convertFile.getName());
			try {
				// 转换poi对象
				HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(convertFile));
				
				// 获取第一个sheet
				HSSFSheet firstSheet=book.getSheetAt(0);
				if(null != firstSheet){
					Integer sumRows = firstSheet.getLastRowNum();//总行数
					if(sumRows<=0){
						return null;
					}else{
						int rowSumFor = 0;
						HSSFRow row0 = firstSheet.getRow(0);
						try {
							String businType = getCellValue(row0.getCell(0));
							String payCon = getCellValue(row0.getCell(1));
							String account = getCellValue(row0.getCell(5));
							if(!"业务类型".equals(businType)||!"付款公司名称".equals(payCon)||!"收款方账号".equals(account)){
								return null;
							}
						} catch (Exception e) {
							return null;
						}
						// 循环设置明细行
						int actualRowNum = 0;
						for(int i = 1;i <= sumRows; i++){
							actualRowNum ++;
							PayForTxnInfDetailRowBean payForTxnInfDetailRowBean = new PayForTxnInfDetailRowBean();
							HSSFRow row = firstSheet.getRow(i);
							
							String bankNameHw = bankNm(getCellValue(row.getCell(7)));//收款人开户行，excel数据
							String bankId = bankConvert(bankNameHw,mchntCd);//收款人开户行代码(总行代码)
							
							String cityNameHw = getCellValue(row.getCell(9));//收款人开户行城市，excel数据
							String cityId = cityConvert(cityNameHw,mchntCd);//收款人开户行城市代码
//							String bankName = getCellValue(row.getCell(7));//收款人开户行支行名称
							String count = getCellValue(row.getCell(5));//收款人银行帐号
							String ownerName = getCellValue(row.getCell(6));//户名
							String amt = getCellValue(row.getCell(14));//金额
							String memo = getCellValue(row.getCell(15));//备注
							if("".equals(bankNameHw)&&"".equals(cityNameHw)&&"".equals(bankNameHw)&&"".equals(count)&&"".equals(ownerName)&&"".equals(amt)&&"".equals(memo)){
								break;
							}
							rowSumFor ++;
							payForTxnInfDetailRowBean.setDetailSeriaNo(getRowSeriaNo(i));
							payForTxnInfDetailRowBean.setBankCd(bankId);
							payForTxnInfDetailRowBean.setCity(cityId);
							payForTxnInfDetailRowBean.setBankNo(bankNameHw);
							payForTxnInfDetailRowBean.setBankAccount(count.replaceAll("-", ""));
							payForTxnInfDetailRowBean.setAccountName(ownerName);
							try {
								payForTxnInfDetailRowBean.setDetailAmt(FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(amt)));
							} catch (Exception e) {
								payForTxnInfDetailRowBean.setDetailAmt(amt);
							}
							payForTxnInfDetailRowBean.setMemo("华安" + memo);
							String stimedStringAmt = "";
							try {
								stimedStringAmt = FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(amt));
							} catch (Exception e) {
								stimedStringAmt = amt;
							}
							String trimedString = getRowSeriaNo(i) + "|" + bankId + "|" + cityId + "|" + bankNameHw + "|" + count.replaceAll("-", "") + "|" + ownerName + "|" + stimedStringAmt + "|" + "" + "|" + memo + "|" + "";
							payForTxnInfDetailRowBean.setLineStr(trimedString);
							payForTxnInfDetailRowBean.setActualRowNum(actualRowNum);
							payForTxnInfDetailRowBeanList.add(payForTxnInfDetailRowBean);
						}
						//System.out.println(getPayForFileBean().getPayForDetailRows().size());
						if(rowSumFor == 0){
							return null;
						}else{
							// 设置汇总行
							setMchntCdGotoBean(mchntCd);//设置商户号
							setBusiCdGotoBean();//设置交易类型
							setDate();//设置交易日期
							setFileSerialNum();//设置文件序号
							setFileRowsAndSum();//设置文件总行数和金额
							payForTxnInfSumBean.setSumRow(payForTxnInfSumBean.getMchntCd()+"|"+payForTxnInfSumBean.getBusiCd()+"|"+payForTxnInfSumBean.getTxnDate()+"|"+payForTxnInfSumBean.getDayNo()+"|"+payForTxnInfSumBean.getSumDetails()+"|"+payForTxnInfSumBean.getSumAmt());
							// 设置文件信息
							payForTxnInfBean.setFileName(payForTxnInfSumBean.getBusiCd()+"_"+payForTxnInfSumBean.getTxnDate()+"_"+payForTxnInfSumBean.getDayNo()+".txt");
							payForTxnInfBean.setFile(convertFile);
							payForTxnInfBean.setMchntCd(mchntCd);
							payForTxnInfBean.setTxnInfType(busiCd);	
							payForTxnInfBean.setOprUsrId(accessBean.getOprUsrId());
						}
					}
				}else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
	            logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP)+convertFile.getName());
	            return null;
			} 
		}else{
            logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP));
            return null;
		}
		return payForTxnInfBean;
	}
}
