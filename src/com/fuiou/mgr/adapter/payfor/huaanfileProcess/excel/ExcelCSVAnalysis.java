package com.fuiou.mgr.adapter.payfor.huaanfileProcess.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;

public class ExcelCSVAnalysis extends ExcelAnalysis{
	private static Logger logger = LoggerFactory.getLogger(ExcelCSVAnalysis.class);
	private CsvListReader reader = null;//解析csv的对象
	private List<String> row = null;//存放每一行信息的结合
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource, String txnDataType){
		payForTxnInfBean=new PayForTxnInfBean();
		payForTxnInfSumBean=payForTxnInfBean.getPayForTxnInfSumBean();
		payForTxnInfDetailRowBeanList=payForTxnInfBean.getPayForTxnInfDetailRowBeanList();
		FileAccess fileAccess = (FileAccess)accessBean;
		File convertFile = fileAccess.getFile();
		if(convertFile==null){
			logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP));
			return null;
		}else{
			try {
				reader = new CsvListReader(new InputStreamReader(
						new FileInputStream(convertFile), "GBK"),
						CsvPreference.EXCEL_PREFERENCE);
				String businType="";
				String payCon="";
				String account="";
				if(reader.getLineNumber()==0){
					if(this.hasNext()){
						List<String> row0=this.next();
						if(row.size()>6){
							businType = row0.get(0);
							payCon = row0.get(1);
							account = row0.get(5);
						}
					}
				}
				if(!"业务类型".equals(businType)||!"付款公司名称".equals(payCon)||!"收款方账号".equals(account)){
					return null;
				}
				// 循环设置明细行
				int actualRowNum = 0;
				//循环的有效行
				int rowSumFor = 0;
				while(this.hasNext()){
					actualRowNum++;
					row=this.next();
					PayForTxnInfDetailRowBean payForTxnInfDetailRowBean = new PayForTxnInfDetailRowBean();
					String bankNameHw = row.get(7);//收款人开户行，excel数据
					String bankId = bankConvert(bankNameHw,mchntCd);//收款人开户行代码(总行代码)
					
					String cityNameHw = row.get(9);//收款人开户行城市，excel数据
					String cityId = cityConvert(cityNameHw,mchntCd);//收款人开户行城市代码
					String count = row.get(5);//收款人银行帐号
					String ownerName = row.get(6);//户名
					String amt = row.get(14);//金额
					String memo = row.get(15);//备注
					if("".equals(bankNameHw)&&"".equals(cityNameHw)&&"".equals(bankNameHw)&&"".equals(count)&&"".equals(ownerName)&&"".equals(amt)&&"".equals(memo)){
						break;
					}
					rowSumFor ++;
					payForTxnInfDetailRowBean.setDetailSeriaNo(getRowSeriaNo(actualRowNum));
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
					String trimedString = getRowSeriaNo(actualRowNum) + "|" + bankId + "|" + cityId + "|" + bankNameHw + "|" + count.replaceAll("-", "") + "|" + ownerName + "|" + stimedStringAmt + "|" + "" + "|" + memo + "|" + "";
					payForTxnInfDetailRowBean.setLineStr(trimedString);
					payForTxnInfDetailRowBean.setActualRowNum(actualRowNum);
					payForTxnInfDetailRowBeanList.add(payForTxnInfDetailRowBean);
				}
				if(rowSumFor==0){
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
					this.close();
				}
			} catch (Exception e) {
				 logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP)+convertFile.getName());
			}
		}
		return payForTxnInfBean;
	}
	/**
	 * 判断是否有下一条信息
	 * @return
	 */
	public boolean hasNext() {
		try {
//			if (reader.getLineNumber() == 0) {//
//				row = reader.read();
//			}
			row = reader.read();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return row != null;
	}

	public List<String> next() {
		return row;
	}
	
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

//	/**
//	 * 当前行号,从1开始
//	 * 
//	 * @return int
//	 */
//	public int getLineNumber() {
//		return reader.getLineNumber() - 1;
//	}
}
