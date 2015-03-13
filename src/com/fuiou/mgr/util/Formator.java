package com.fuiou.mgr.util;

import java.util.Calendar;

public class Formator {
	/**
     * 格式化成两位小数的串（xxxxxxxx.xx）
     * @param amt 待格式化的数值
     * @return 格式化后的串
     */    
    public static String decimal(double amt) {
        long result1 = 0;
        if(amt>0) {
            result1 = (long)(amt * 100 + 0.5);
        } else {
            result1 = (long)(amt * 100 - 0.5);
        }
        
        if(result1==0) {
            return "0.00";
        }
        
        String result;
        if(result1>0) {     // 正数
            result = ""+ result1;
            // 把字符串补满3位
            result=fill(result, 3);
            result = result.substring(0, result.length()-2) + "." + result.substring(result.length()-2);
        }else {     // 负数
            result = ""+ Math.abs(result1);
            result=fill(result, 3);
            result = "-" + result.substring(0, result.length()-2) + "." + result.substring(result.length()-2);
        }
        return result;
    }

    /**
     * 把数字串变为指定长度的串，不足位的话前面补0，超长原样返回
     * @param str 要补足的串
     * @param length 指定的长度
     * @return 变化后的串
     */
    public static String fill(String str,int length) {
        StringBuffer buffer=new StringBuffer("");
        
        if(str==null) {
            str="";
        }
        
        if(length<=str.length()) {       // 串超长原样返回
            return str;
        } else {
            int strLen=length-str.length();
            for(int i=0;i<strLen;i++) {
                buffer.append("0");
            }
        }
        buffer.append(str);
        return buffer.toString();
    }

    /**
     * 把数字串填冲为前面补0的指定长度的串，指定长度位数不足返回原数字串。
     * @param number 待补足位数的数字
     * @param length 指定的长度
     * @return 变化后的串，指定长度位数不足返回原数字串。
     */
    public static String fill(int number,int length) {
        //StringBuffer buffer=new StringBuffer("");
        String str = "" + number;
        return fill(str,length);
    }

    /**
     * 将输入两个数相除，结果向上取整，除数为0返回1
     * @param num1 被除数
     * @param num2 除数
     * @return 向上取整后的商
     */   
    public static int fixUp(int num1, int num2) {
        if(num2==0)
            return 1;
        
        if(num1 % num2 == 0) {
            return (num1/num2);
        }else{
            return (num1/num2+1);
        }
    }
    
    /**
     * 把null变为""，否则不变
     * @param str 要赋值的串
     * @return 变化后的串
     */
    public static String fill(String str) {
        if(str==null) {
            return "";
        }else {
            return str;
        }        
    }
    
    /**
     * 拼接一个yyyymmdd的字符串
     * @param date 待拼接日期
     * @return 拼接完成的字符串
     */
    public static String formatDate(Calendar date) {
        if (date==null) {
            return null;
        }
     
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        
        return "" + year + fill(month,2) + fill(day,2);
    }

    /**
     * 拼接一个hhmmss的字符串
     * @param date 待拼接日期
     * @return 拼接完成的字符串
     */
    public static String formatTime(Calendar date) {
        if (date==null) {
            return null;
        }
     
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        int second = date.get(Calendar.SECOND);
        
        return fill(hour,2) + fill(minute,2) + fill(second,2);
    }
        
    /**
     * 格式化日期时间字符串为"yyyy-mm-dd hh:mm:ss"
     * @param dataTime 待格式化的字符串（yyyymmddhhmmss）
     * @return 格式化后的字符串，若参数字符串不符合规范，则返回null
     */
    public static String formatDateTimeStr(String dataTime) {
        if(dataTime.length()!=14) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(dataTime.substring(0,4)).append("-").append(dataTime.substring(4,6)).append("-").append(dataTime.substring(6,8));
        buffer.append(" ").append(dataTime.substring(8,10)).append(":").append(dataTime.substring(10,12)).append(":").append(dataTime.substring(12,14));
        return buffer.toString();
    }
    
    /**
     * 把元为单位的数字转化为分为单位
     * @param num 待格式化的数值
     * @return 转换后的值
     */
    public static long yuan2Fen(String num) {
        long fen=0L;
        String fenStr ="";
        int pos = num.indexOf(".");
        
        if( pos == -1 ) {
            fenStr = num + "00";
        } else {
            // 取小数点前的值
            fenStr = num.substring(0,pos);
            if( num.length() - pos >= 3 ) {     // 小数点后面大于等于2位
                fenStr += num.substring( pos+1,pos+3 );
            } else if( num.length() == pos ){   // 小数点后面没有值
                fenStr += "00";
            }else {                              // 小数点后面1位
                fenStr += num.substring( pos+1 ) + "0";
            }
        }
        try {
            fen = Long.parseLong(fenStr);
        }catch(Exception e) {
            return 0L;
        }
        return fen;
    }
}
