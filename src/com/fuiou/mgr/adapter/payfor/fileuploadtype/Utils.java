package com.fuiou.mgr.adapter.payfor.fileuploadtype;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.VCityInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TRootBankInfService;
import com.fuiou.mer.service.VCityInfService;
import com.fuiou.mer.util.StringUtils;

public class Utils {
	/**
	 * 得到解析的列并转换类型
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell) {
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
	public static String bankConvert(String bankName,String mchntCd){
		//平安银行的把他归类到0313中
		if(StringUtils.isNotEmpty(bankName)){
			bankName = bankName.replace(" ", "");
			if(bankName.contains("平安银行")){
				return "0307";
			}
		}
	    TRootBankInf tRootBankInf = new TRootBankInfService().selectByTBank_Nm(bankName);
	    if(tRootBankInf==null){
	        return "";
	    }else{
	        return tRootBankInf.getBANK_CD();
	    }
	}
	/**
	 * 城市转换为ID(与数据库对应)
	 * @param cityName 城市名称
	 * @param mchntCd 商户号
	 * @return 城市id
	 */
	public static String cityConvert(String cityName,String mchntCd){
		List<VCityInf> list = new VCityInfService().selectProvCdByCityName(cityName);
		if(list.size() == 0){
			if(cityName.length() != 0){
				cityName = cityName.substring(0, cityName.length()-1);
				List<VCityInf> list2 = new VCityInfService().selectProvCdByCityName(cityName);
				if(list2.size() == 0){
					return "";
				}else{
					VCityInf vCityInf2 = list2.get(0);
					return vCityInf2.getCITY_CD();
				}
			} else {
				return "";
			}
		}else{
			VCityInf vCityInf = list.get(0);
			return vCityInf.getCITY_CD();
		}
	}
	/**
	 * 获取行号
	 * @param rowNo 当前行
	 * @return
	 */
	public static String getRowSeriaNo(String rowNum){
		int rowNo=0;
		try {
			rowNo=Integer.parseInt(rowNum);
			if(rowNo >= 1 && rowNo < 10){
				return "00000"+rowNo;
			}else if(rowNo >= 10 && rowNo < 100){
				return "0000"+rowNo;
			}else if(rowNo >= 100 && rowNo < 1000){
				return "000"+rowNo;
			}else {
				return "00"+rowNo;
			}
		} catch (Exception e) {
			return rowNum;
		}
	}
	public static String getBusiCd(String fileName){
		// 业务代码
		String busiCd="";
		//如果在这里发生了异常，则说明文件名有误
		try {
			busiCd = fileName.substring(0, fileName.indexOf("_"));
		} catch (Exception e) {
			busiCd="";
		}
		return busiCd;
	}
	public static String getDateStr(String fileName){
		// 交易日期
		String dateStr="";
		//如果在这里发生了异常，则说明文件名有误
		try {
			dateStr = fileName.substring(fileName.indexOf("_") + 1,
					fileName.lastIndexOf("_"));
		} catch (Exception e) {
			dateStr="";
		}
		return dateStr;
	}
	public static String getBeforeNum(String fileName){
		// 当日序列号
		String beforeNum="";
		//如果在这里发生了异常，则说明文件名有误
		try {
			beforeNum = fileName.substring(
					fileName.lastIndexOf("_") + 1,
					fileName.lastIndexOf("."));
		} catch (Exception e) {
			beforeNum="";
		}
		return beforeNum;
	}
	/**
	 * 证件转换为ID(与数据库对应)
	 * @param dictClass证件的标示（map_id_tp_stl_cntr）固定的
	 * @param dictKey证件类型
	 * @return
	 */
	public static String getCardId(String dictClass,String dictKey){
		TDataDictService tDataDictService=new TDataDictService();
		String cardId=tDataDictService.selectTDataDictByClassAndKey(dictClass,dictKey);
		if(cardId!=null){
			return cardId;
		}
		return "";
	}
	/**
	 * 把double类型的数据转换成2为小数
	 * @param num
	 * @return
	 */
	public static String doubleToString(double num){
        DecimalFormat   df   =   new   DecimalFormat( "##.00 "); 
        double value   =   Double.parseDouble(df.format(num)); 
        System.out.println(value);
		return value+"";
	}
	public static String convert(String convert){
		String result="";
		if("是".equals(convert)){
			result="1";
		}else if("否".equals(convert)){
			result="0";
		}else{
			result="0";
		}
		return result;
	}
}
