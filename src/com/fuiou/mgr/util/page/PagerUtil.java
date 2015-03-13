package com.fuiou.mgr.util.page;

import javax.servlet.http.HttpServletRequest;

import com.fuiou.mgr.util.page.taglib.page.PagerTag;

public class PagerUtil {
	public static int[] getStartEndIndex(HttpServletRequest request, int listCount){
        //获取每页记录数,若没指定,使用默认值
        int pageSize = ParamUtil.getIntParameter(request, "pageSize", PagerTag.defaultPageSize);
        int pageNo = ParamUtil.getIntParameter(request, "pageNo", 1);
        String choice = ParamUtil.getParameter(request, "choice");

        int index = getPageStartPos(listCount, pageSize, choice, pageNo);

        int total = index + pageSize -1;
        if (total > listCount)
        	total = listCount;

        int[] rtn = {index,total};

        return rtn;
	}

    public static int[] getPagerParameter(HttpServletRequest request){
        //获取每页记录数,若没指定,使用默认值
        int pageSize = ParamUtil.getIntParameter(request, "pageSize", PagerTag.defaultPageSize);
        int pageNo = ParamUtil.getIntParameter(request, "pageNo", 1);
        String choice = ParamUtil.getParameter(request, "choice");
        int listCount = ParamUtil.getIntParameter(request, "totleSize", 0);

        int index = getPageStartPos(listCount, pageSize, choice, pageNo);

        int total = index + pageSize -1;
        if (total > listCount)
        	total = listCount;

        return new int[]{index,total,pageSize};
	}

    public static int[] getStartEndIndex(HttpServletRequest request){
        //获取每页记录数,若没指定,使用默认值
        int pageSize = ParamUtil.getIntParameter(request, "pageSize", PagerTag.defaultPageSize);
        int pageNo = ParamUtil.getIntParameter(request, "pageNo", 1);
        String choice = ParamUtil.getParameter(request, "choice");
        int listCount = ParamUtil.getIntParameter(request, "totleSize", 0);

        int index = getPageStartPos(listCount, pageSize, choice, pageNo);

        int total = index + pageSize -1;
        if (total > listCount)
        	total = listCount;

        int[] rtn = {index,total};

        return rtn;
	}

    /**
	 * 获取当前页的开始序号，通常用于显示序号
	 * @param request
	 * @return
	 */
	public static int getPageStartIndex(HttpServletRequest request, int totalCount){
        //获取每页记录数,若没指定,使用默认值
        int pageSize = ParamUtil.getIntParameter(request, "pageSize", PagerTag.defaultPageSize);
        int pageNo = ParamUtil.getIntParameter(request, "pageNo", 1);
        String choice = ParamUtil.getParameter(request, "choice");
        
        int index = getPageStartPos(totalCount, pageSize, choice, pageNo);
        
        return index;
	}
	
	private static int getPageStartPos(int total, int pageSize, String choice, int pageNo){
        //计算总页数
        int pages = (total % pageSize == 0) ? total / pageSize : total / pageSize + 1;

        //根据操作,重新确定当前页号
        if (choice.equals("next"))
            pageNo++;
        if (choice.equals("prev"))
            pageNo--;
        if (choice.equals("first"))
            pageNo = 1;
        if (choice.equals("last"))
            pageNo = pages;

        //页号越界处理
        if (pageNo > pages)
            pageNo = pages;
        if (pageNo <= 0)
            pageNo = 1;

        //起始记录号
        int index = (pageNo - 1) * pageSize + 1;
        return index;
	}
}
