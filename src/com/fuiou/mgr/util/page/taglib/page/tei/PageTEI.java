package com.fuiou.mgr.util.page.taglib.page.tei;

import javax.servlet.jsp.tagext.*;

/**
 */
public class PageTEI extends TagExtraInfo {
  @Override
public VariableInfo[] getVariableInfo(TagData data) {
    return new VariableInfo[] {
      new VariableInfo("pageSize","java.lang.Integer",true,VariableInfo.NESTED),
      new VariableInfo("pageNo","java.lang.Integer",true,VariableInfo.NESTED),
      new VariableInfo("pages","java.lang.Integer",true,VariableInfo.NESTED),
      new VariableInfo("index","java.lang.Integer",true,VariableInfo.NESTED),
      new VariableInfo("total","java.lang.Integer",true,VariableInfo.NESTED)
    };
  }
}