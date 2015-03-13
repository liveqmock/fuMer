package com.fuiou.mgr.adapter.payfor.huaanfileProcess.excel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import com.fuiou.mer.model.TBankAlias;
import com.fuiou.mer.model.TCityAlias;
import com.fuiou.mer.model.TPmsBankAlias;
import com.fuiou.mer.service.TBankAliasService;
import com.fuiou.mer.service.TCityAliasService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TPmsBankAliasService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.payfor.huaanfileProcess.FileAnalysisAdapter;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.checkout.CheckOutBase;

public class ExcelAnalysis extends FileAnalysisAdapter{
	TDataDictService tDataDictService = new TDataDictService();
	TSeqService tSeqService = new TSeqService();
	String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
	PayForTxnInfBean payForTxnInfBean;
	PayForTxnInfSumBean payForTxnInfSumBean;
	List<PayForTxnInfDetailRowBean> payForTxnInfDetailRowBeanList;
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource, String txnDataType){
		FileAccess fileaccess=(FileAccess)accessBean;
		String postfixName=fileaccess.getFileName().substring(fileaccess.getFileName().lastIndexOf(".")+1);
		if("xls".equals(postfixName)){
			return new ExcelXLSAnalysis().analysisTxn(mchntCd, accessBean, txnInfSource, txnDataType);
		}else if("xlsx".equals(postfixName)){
			return new ExcelXLSXAnalysis().analysisTxn(mchntCd, accessBean, txnInfSource, txnDataType);
		}else if("csv".equals(postfixName)){
			return new ExcelCSVAnalysis().analysisTxn(mchntCd, accessBean, txnInfSource, txnDataType);
		}else{
			return null;
		}
	}
	/**
	 * 得到解析的列并转换类型
	 * @param cell
	 * @return
	 */
	public String getCellValue(Cell cell) {
		String value = "";
		if (null == cell) {
			return value;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数值型
			if (DateUtil.isCellDateFormatted(cell)) {
				// 如果是date类型则 ，获取该cell的date值
				value = DateUtil.getJavaDate(cell.getNumericCellValue())
						.toString();
				java.util.Date date1 = new Date(value);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				value = format.format(date1);
			} else {// 纯数字
				DecimalFormat df = new DecimalFormat(); 
				 Number num=null;
                try {
               	 num = df.parse(cell.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				if(num!=null){
					value = num.toString();
				}else{
					value = String.valueOf(cell.getNumericCellValue());;
				}
			}
			break;
		/* 此行表示单元格的内容为string类型 */
		case Cell.CELL_TYPE_STRING: // 字符串型
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:// 公式型
			// 读公式计算值
			value = String.valueOf(cell.getNumericCellValue());
			if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
				value = cell.getStringCellValue().toString();
			}
			cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_BOOLEAN:// 布尔
			value = " " + cell.getBooleanCellValue();
			break;
		/* 此行表示该单元格值为空 */
		case Cell.CELL_TYPE_BLANK: // 空值
			value = "";
			break;
		case Cell.CELL_TYPE_ERROR: // 故障
			value = "";
			break;
		default:
			value = cell.getStringCellValue().toString();
		}
		return value;
	}
	/**
	 * 银行转换为ID(与数据库对应)
	 * @param bankName 银行名称
	 * @param mchntCdn 商户号
	 * @return 银行id
	 */
	public String bankConvert(String bankName,String mchntCd){
	    TBankAlias tBankAlias = new TBankAliasService().bankConvert(bankName, mchntCd);
	    if(tBankAlias==null){
	        return "";
	    }else{
	        return tBankAlias.getBANK_CD();
	    }
	}
	/**
	 * 文件上传的开户行名称转换成标准名称
	 * @param bankNmOfFile 文件上传的名称
	 * @return 标准名称
	 */
	public String bankNm(String bankNmOfFile){
		TPmsBankAlias tPmsBankAlias = new TPmsBankAliasService().pmsBankConvert(bankNmOfFile);
		if(tPmsBankAlias == null){
			return bankNmOfFile;
		}else{
			return tPmsBankAlias.getPMS_BANK_NM();
		}
	}
	/**
	 * 城市转换为ID(与数据库对应)
	 * @param cityName 城市名称
	 * @param mchntCd 商户号
	 * @return 城市id
	 */
	public String cityConvert(String cityName,String mchntCd){
		List<TCityAlias> list = new TCityAliasService().selectByExample("", cityName);
		if(list.size() == 0){
			if(cityName.length() != 0){
				cityName = cityName.substring(0, cityName.length()-1);
				List<TCityAlias> list2 = new TCityAliasService().selectByExample("", cityName);
				if(list2.size() == 0){
					return "";
				}else{
					TCityAlias tCityAlias2 = list2.get(0);
					return tCityAlias2.getCITY_CD();
				}
			} else {
				return "";
			}
		}else{
			TCityAlias tCityAlias = list.get(0);
			return tCityAlias.getCITY_CD();
		}
	}
	/**
	 * 获取行号
	 * @param rowNo 当前行
	 * @return
	 */
	public String getRowSeriaNo(int rowNo){
		if(rowNo >= 1 && rowNo < 10){
			return "00000"+rowNo;
		}else if(rowNo >= 10 && rowNo < 100){
			return "0000"+rowNo;
		}else if(rowNo >= 100 && rowNo < 1000){
			return "000"+rowNo;
		}else {
			return "00"+rowNo;
		}
	}
	/**
	 * set文件序号
	 * @return 序号
	 */
	public String setFileSerialNum(){
		String fileSerialId = tSeqService.huaAnFileSerialNum();
		if(null != fileSerialId && !"".equals(fileSerialId)){
			payForTxnInfSumBean.setDayNo(fileSerialId);
			return fileSerialId;
		}
		return null;
	}
	
	/**
	 * set文件总行数和金额
	 * @param payForFileBean
	 * @return
	 */
	public int setFileRowsAndSum(){
		int rows = 0;
		double sums = 0;
		if(null != payForTxnInfDetailRowBeanList){
			for(PayForTxnInfDetailRowBean payForTxnInfDetailRowBean:payForTxnInfDetailRowBeanList){
				rows ++;
				try {
					String amtRow = FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(payForTxnInfDetailRowBean.getDetailAmt()));
					if (CheckOutBase.validMoney(amtRow)) {
						sums += Double.parseDouble(amtRow);
					} else {
						sums += 0l;
					}
				} catch (Exception e) {
					sums += 0l;
				}
			}
			long sumFen = Long.valueOf(FuMerUtil.formatYuanToFen(Double.valueOf(sums)));
			payForTxnInfSumBean.setSumAmt(FuMerUtil.formatFenToYuan(sumFen));
			payForTxnInfSumBean.setSumDetails(Integer.valueOf(rows).toString());
			return rows;
		}
		return 0;
	}
	
	/**
	 * set交易日期
	 * @return
	 */
	public String setDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String dateStr = format.format(date);
		payForTxnInfSumBean.setTxnDate(dateStr);
		return dateStr;
	}
	
	/**
	 * set交易类型至文件Bean
	 * @return
	 */
	public String setBusiCdGotoBean(){
		if(null != TDataDictConst.BUSI_CD_PAYFOR && !"".equals(TDataDictConst.BUSI_CD_PAYFOR)){
			payForTxnInfSumBean.setBusiCd(TDataDictConst.BUSI_CD_PAYFOR);
			return TDataDictConst.BUSI_CD_PAYFOR;
		}
		return null;
	}
	
	/**
	 * set商户号至文件Bean
	 * @return
	 */
	public String setMchntCdGotoBean(String mchntCdStr){
		payForTxnInfSumBean.setMchntCd(mchntCdStr);
		return null;
	}
}
