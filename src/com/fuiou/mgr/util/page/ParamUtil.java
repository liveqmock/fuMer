package com.fuiou.mgr.util.page;
import javax.servlet.*;

public class ParamUtil {
	
    public static String getParameter( ServletRequest request, String paramName ) {
    	try{
    		request.setCharacterEncoding("GBK");
    		String temp = request.getParameter(paramName);
    		if (temp != null && !temp.equals("")) {
    			return temp;
    		} else {
    			return "";
    		}
    	}catch(Exception e){
    		return "";
    	}
    }

    public static int getIntParameter( ServletRequest request, String paramName, int defaultNum ) {
        String temp = request.getParameter(paramName);
        if (temp != null && !temp.equals("")) {
            int num = defaultNum;
            try {
                num = Integer.parseInt(temp);
            }
            catch (Exception ignored) {
            }
            return num;
        }
        else {
            return defaultNum;
        }
    }
  }///////////
