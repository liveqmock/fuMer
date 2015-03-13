package com.fuiou.mgr.adapter.payfor.fileuploadtype;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.FileAccess;
/**
 * csv格式的上传
 * dev_db
 *
 */
public class ExcelCSVUpload implements IFileUploadType{
	TDataDictService tDataDictService = new TDataDictService();
	private static Logger logger = LoggerFactory
	.getLogger(ExcelCSVUpload.class);
	private CsvListReader reader = null;//解析csv的对象
	private List<String> row = null;//存放每一行信息的结合
	List<String> list = new ArrayList<String>();//返回的集合
	String mchntCd="";//商户号
	@Override
	public List<String> analysisTxn(FileAccess fileAccess) {
		logger.debug("开始处理文件,文件名为:"+fileAccess.getFileName());
		mchntCd=fileAccess.getMchntCd();
		// TODO Auto-generated method stub
		File file=fileAccess.getFile();
		if(file==null){
			logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP));
			return null;
		}
		try {
			reader = new CsvListReader(new InputStreamReader(
					new FileInputStream(file), "GBK"),
					CsvPreference.EXCEL_PREFERENCE);
			double sum = 0;
			int detailNum = 0;// 设置明细行
			List<String> listTemp = new ArrayList<String>();//临时集合
			if(reader.getLineNumber()==0){
				this.hasNext();
			}
			if(TDataDictConst.BUSI_CD_PAYFOR.equals(fileAccess.getBusiCd())){//付款,代付
				while(this.hasNext()){
					String rowValue="";
					row=this.next();
					String rowId = "";// 序号
					String cityName = "";// 开户市/县
					String bankName = "";// 开户银行
					String bankDetailName = "";// 开户行支行全称
					String account = "";//收款人银行账号
					String accountName = "";// 户名
					String money = "";// 金额
					String serialNum = "";// 企业流水账号
					String remark = "";// 备注
					String mobilePhone = "";// 手机
					
					if(row.size()>0)rowId=Utils.getRowSeriaNo(row.get(0));
					if(row.size()>1)bankName=row.get(1);
					if(row.size()>2)cityName=row.get(2);
					if(row.size()>3)bankDetailName=row.get(3);
					if(row.size()>4)account=row.get(4);
					if(row.size()>5)accountName=row.get(5);
					if(row.size()>6)money=row.get(6);
					if(row.size()>7)serialNum=row.get(7);
					if(row.size()>8)remark=row.get(8);
					if(row.size()>9)mobilePhone=row.get(9);
					
					String bankId = Utils.bankConvert(bankName,mchntCd);//收款人开户行代码(总行)
					String cityId = Utils.cityConvert(cityName,mchntCd);//收款人开户行城市代码
					//转换类型计算总金额
					double tempMoney=0;
					try {
						tempMoney=Double.parseDouble(money);
					} catch (Exception e) {
						tempMoney=0;
					}
					sum+=tempMoney;
					rowValue=rowId+"|"+bankId+"|"+cityId+"|"+bankDetailName+"|"+account+"|"+accountName+"|"+money+"|"+serialNum+"|"+remark+"|"+mobilePhone;
					detailNum+=1;
					System.out.println(rowValue);
					listTemp.add(rowValue);
				}
				if (listTemp.size() == 0) {
					return null;
				}
				String fileName=fileAccess.getFileName();
				// 业务代码
				String busiCd=Utils.getBusiCd(fileName);
				// 交易日期
				String dateStr=Utils.getDateStr(fileName);
				// 当日序列号
				String beforeNum=Utils.getBeforeNum(fileName);
				// 汇总行
				String sumRow = fileAccess.getMchntCd() + "|" + busiCd + "|"
						+ dateStr + "|" + beforeNum + "|" + detailNum + "|"
						+ Utils.doubleToString(sum);
				list.add(sumRow);
				for (String str : listTemp) {
					list.add(str);
				}
			}else if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(fileAccess.getBusiCd())){//代收
				while(this.hasNext()){
					row=this.next();
					String rowId = "";// 序号
					String bankName = "";// 开户银行
					String account = "";//扣款人银行账号
					String accountName = "";// 户名
					String money = "";// 金额
					String serialNum = "";// 企业流水账号
					String remark = "";// 备注
					String mobilePhone = "";// 手机
					String certificateTp = "";//2.04版本新加的证件类型
					String certificateNo = "";//2.04版本新加的证件号
					
					if(row.size()>0)rowId=Utils.getRowSeriaNo(row.get(0));
					if(row.size()>1)bankName=row.get(1);
					if(row.size()>2)account=row.get(2);
					if(row.size()>3)accountName=row.get(3);
					if(row.size()>4)money=row.get(4);
					if(row.size()>5)serialNum=row.get(5);
					if(row.size()>6)remark=row.get(6);
					if(row.size()>7)mobilePhone=row.get(7);
					if(row.size()>8)certificateTp=row.get(8);
					if(row.size()>9)certificateNo=row.get(9);
					String cdId = Utils.getCardId(TDataDictConst.MAP_ID_TP_STL_CNTR, certificateTp);
					
					String bankId = Utils.bankConvert(bankName,mchntCd);//收款人开户行代码(总行)
					//转换类型计算总金额
					double tempMoney=0;
					try {
						tempMoney=Double.parseDouble(money);
					} catch (Exception e) {
						tempMoney=0;
					}
					sum+=tempMoney;
					String rowValue=rowId+"|"+bankId+"|"+account+"|"+accountName+"|"+money+"|"+serialNum+"|"+remark+"|"+mobilePhone+"|"+cdId+"|"+certificateNo;
					detailNum+=1;
					System.out.println(rowValue);
					listTemp.add(rowValue);
				}
				if (listTemp.size() == 0) {
					return null;
				}
				String fileName=fileAccess.getFileName();
				// 业务代码
				String busiCd=Utils.getBusiCd(fileName);
				// 交易日期
				String dateStr=Utils.getDateStr(fileName);
				// 当日序列号
				String beforeNum=Utils.getBeforeNum(fileName);
				//汇总行
				String sumRow=fileAccess.getMchntCd()+"|"+busiCd+"|"+dateStr+"|"+beforeNum+"|"+detailNum+"|"+Utils.doubleToString(sum);
				list.add(sumRow);
				for(String str:listTemp){
					list.add(str);
				}
			}else if(TDataDictConst.BUSI_CD_VERIFY.equals(fileAccess.getBusiCd())){//实名验证
				while(this.hasNext()){
					row=this.next();
					String rowId = "";// 序号
					String bankName = "";// 开户银行
					String account = "";//扣款人银行账号
					String accountName = "";// 户名
					String cdType = "";// 证件类型
					String idCard = "";// 身份证号码
					
					if(row.size()>0)rowId=Utils.getRowSeriaNo(row.get(0));
					if(row.size()>1)bankName=row.get(1);
					if(row.size()>2)account=row.get(2);
					if(row.size()>3)accountName=row.get(3);
					if(row.size()>4)cdType=row.get(4);
					if(row.size()>5)idCard=row.get(5);
					
					String bankId = Utils.bankConvert(bankName,mchntCd);//收款人开户行代码(总行)
					String cdId=Utils.getCardId(TDataDictConst.MAP_ID_TP_STL_CNTR, cdType);//证件对应的编号
					String rowValue=rowId+"|"+bankId+"|"+account+"|"+accountName+"|"+cdId+"|"+idCard;
					detailNum+=1;
					System.out.println(rowValue);
					listTemp.add(rowValue);
				}
				if (listTemp.size() == 0) {
					return null;
				}
				String fileName=fileAccess.getFileName();
				// 业务代码
				String busiCd=Utils.getBusiCd(fileName);
				// 交易日期
				String dateStr=Utils.getDateStr(fileName);
				// 当日序列号
				String beforeNum=Utils.getBeforeNum(fileName);
				//汇总行
				String sumRow=fileAccess.getMchntCd()+"|"+busiCd+"|"+dateStr+"|"+beforeNum+"|"+detailNum;
				list.add(sumRow);
				for(String str:listTemp){
					list.add(str);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 判断是否有下一条信息
	 * @return
	 */
	public boolean hasNext() {
		try {
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
//	 * 转换成num类型避免科学计数法
//	 * @return
//	 */
//	public String getNum(String numStr){
//		DecimalFormat df = new DecimalFormat(); 
//		String num="";
//		try {
//			num=df.parse(numStr).toString();
//		} catch (Exception e) {
//			num=numStr;
//		}
//		return num;
//	}
}
