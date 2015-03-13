package com.fuiou.mgr.adapter.payfor.fileuploadtype;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.access.FileAccess;

/**
 * xlsx格式的上传
 * 
 * dev_db
 * 
 */
public class ExcelXLSXUpload implements IFileUploadType {

	private static Logger logger = LoggerFactory
			.getLogger(ExcelXLSXUpload.class);
	String mchntCd="";
	TDataDictService tDataDictService = new TDataDictService();
	@Override
	public List<String> analysisTxn(FileAccess fileAccess) {
		logger.debug("开始处理文件,文件名为:"+fileAccess.getFileName());
		mchntCd=fileAccess.getMchntCd();
		File file = fileAccess.getFile();
		if(file==null){
			logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP));
			return null;
		}
		// 存放每行信息的结合
		List<String> list = new ArrayList<String>();
		// TODO Auto-generated method stub
		try {
			// 转换poi对象
			XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(file));
			// 获取第一个sheet
			XSSFSheet firstSheet = book.getSheetAt(0);
			if (firstSheet != null) {
				int sumRows = firstSheet.getLastRowNum();// 总行数
				if (sumRows == 0) {
					return null;
				}
				if(TDataDictConst.BUSI_CD_PAYFOR.equals(fileAccess.getBusiCd())){//付款，代付
					double sum = 0;
					List<String> tempList = new ArrayList<String>();
					int detailNum = 0;// 设置明细行
					for (int i = 1; i <= sumRows; i++) {
						String rowValue = "";
						XSSFRow row = firstSheet.getRow(i);
						String rowId = rowId=Utils.getRowSeriaNo(Utils.getCellValue(row.getCell(0)));// 序号
						String bankName = Utils.getCellValue(row.getCell(1));// 开户银行
						String bankId = Utils.bankConvert(bankName,mchntCd);//收款人开户行代码(总行代码)
						String cityName = Utils.getCellValue(row.getCell(2));// 开户市/县
						String cityId = Utils.cityConvert(cityName,mchntCd);//收款人开户行城市代码
						String bankDetailName = Utils.getCellValue(row
								.getCell(3));// 开户行支行全称

						String account = Utils.getCellValue(row.getCell(4));// 收款人银行账号
						String accountName = Utils.getCellValue(row
								.getCell(5));// 户名
						String money=Utils.getCellValue(row.getCell(6));// 金额
						// 转换类型计算总金额
						double tempMoney = 0;
						try {
							tempMoney = Double.parseDouble(money);
						} catch (Exception e) {
							tempMoney = 0;
						}
						sum += tempMoney;
						String serialNum = Utils
								.getCellValue(row.getCell(7));// 企业流水账号
						String remark = Utils.getCellValue(row.getCell(8));// 备注
						String mobilePhone = Utils.getCellValue(row
								.getCell(9));// 手机
						rowValue = rowId + "|" + bankId + "|" + cityId + "|"
								+ bankDetailName + "|" + account + "|"
								+ accountName + "|" + money + "|" + serialNum + "|"
								+ remark + "|" + mobilePhone;
						detailNum += 1;
						tempList.add(rowValue);
					}
					if (tempList.size() == 0) {
						return null;
					}
					String fileName = fileAccess.getFileName();
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
					for (String str : tempList) {
						list.add(str);
					}
				}else if(TDataDictConst.BUSI_CD_INCOMEFOR.equals(fileAccess.getBusiCd())){//代收
					double sum=0;
					List<String> tempList = new ArrayList<String>();
					int detailNum=0;//设置明细行
					for(int i=1;i<=sumRows;i++){
						XSSFRow row = firstSheet.getRow(i);
						String rowId = Utils.getRowSeriaNo(Utils.getCellValue(row.getCell(0)));// 序号
						String bankName = Utils.getCellValue(row.getCell(1));// 开户银行
						String bankId = Utils.bankConvert(bankName,mchntCd);//扣款人开户行代码(总行代码)
						String account = Utils.getCellValue(row.getCell(2));//扣款人银行账号
						String accountName = Utils.getCellValue(row.getCell(3));// 户名
						String money=Utils.getCellValue(row.getCell(4));// 金额
						String serialNum = Utils.getCellValue(row.getCell(5));// 企业流水账号
						String remark = Utils.getCellValue(row.getCell(6));// 备注
						String mobilePhone = Utils.getCellValue(row.getCell(7));// 手机
						String certificateTp = Utils.getCellValue(row.getCell(8));//2.04版本新加的证件类型
						String cdId = Utils.getCardId(TDataDictConst.MAP_ID_TP_STL_CNTR, certificateTp);
						String certificateNo = Utils.getCellValue(row.getCell(9));//2.04版本新加的证件号
						String rowValue=rowId+"|"+bankId+"|"+account+"|"+accountName+"|"+money+"|"+serialNum+"|"+remark+"|"+mobilePhone+"|"+cdId+"|"+certificateNo;
						detailNum++;
						//转换类型计算总金额
						double tempMoney=0;
						try {
							tempMoney=Double.parseDouble(money);
						} catch (Exception e) {
							tempMoney=0;
						}
						sum+=tempMoney;
						tempList.add(rowValue);
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
					for(String str:tempList){
						list.add(str);
					}
				}else if(TDataDictConst.BUSI_CD_VERIFY.equals(fileAccess.getBusiCd())){//实名验证
					List<String> tempList = new ArrayList<String>();
					int detailNum=0;//设置明细行
					for(int i=1;i<=sumRows;i++){
						XSSFRow row = firstSheet.getRow(i);
						String rowId = rowId=Utils.getRowSeriaNo(Utils.getCellValue(row.getCell(0)));// 序号
						String bankName = Utils.getCellValue(row.getCell(1));// 开户银行
						String bankId = Utils.bankConvert(bankName,mchntCd);//开户行代码(总行代码)
						String account = Utils.getCellValue(row.getCell(2));//开户银行账号
						String accountName = Utils.getCellValue(row.getCell(3));// 户名
						String cdType=Utils.getCellValue(row.getCell(4));// 证件类型
						String cdId=Utils.getCardId(TDataDictConst.MAP_ID_TP_STL_CNTR, cdType);
						String idCard = Utils.getCellValue(row.getCell(5));// 身份证号码
						String rowValue=rowId+"|"+bankId+"|"+account+"|"+accountName+"|"+cdId+"|"+idCard;
						detailNum++;
						tempList.add(rowValue);
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
					for(String str:tempList){
						list.add(str);
					}
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(TDataDictConst.FILE_READ_EP
					+ " "
					+ tDataDictService.selectTDataDictByClassAndKey(
							TDataDictConst.RET_CODE_SYS,
							TDataDictConst.FILE_READ_EP) + file.getName());
			e.printStackTrace();
		}
		return list;
	}
	public static void main(String[] args) {
		String str="9.5555E+15";
		double ds=Double.parseDouble(str);
		System.out.println(ds);
	}
}
