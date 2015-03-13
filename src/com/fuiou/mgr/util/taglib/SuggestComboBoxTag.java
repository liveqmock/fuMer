package com.fuiou.mgr.util.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.fuiou.mer.model.InsInfCacheBean;
import com.fuiou.mer.util.StringUtils;
import com.fuiou.mer.util.SystemParams;

public class SuggestComboBoxTag extends TagSupport {

	private static final long serialVersionUID = 5916223971232131780L;

	private String wildcard = "0";
	private String lineCnt = "10";
	private String busiType;// 必填参数
	private String value = "";

	private String allowNull = "true";
	private String valiType = "";
	private String label = "";
	private String name;// 必填参数
	private String id;
	private String size = "68";
	private String style = "input";
	private String blur = "";
	private String relate = ""; // cups,relIns,acq

	private String showType = "all"; // 1.全显，2.只显input，3只显script

	public int doStartTag() throws JspException {

		if (StringUtils.isEmpty(id)) {
			id = name;
		}

		if (StringUtils.isNotEmpty(value) && !showType.equals("script")) {
			// 根据busiType获取value
			String[] busiTypes = busiType.split("\\|");
			if (busiTypes[0].equals("ins") || busiTypes[0].equals("mchnt")) {
				InsInfCacheBean insInfCacheBean = getInsInfByCd(value);
				if (insInfCacheBean != null) {
					value = value + "：" + insInfCacheBean.getINS_NAME_CN();
				}
			}
		}
		String input = "";
		if (!showType.equals("script")) {
			// 生成input
			input = "<input type=\"text\" name=\"" + name + "\" id=\"" + name
					+ "\" allowNull=\"" + allowNull + "\" valiType=\""
					+ valiType + "\" label=\"" + label + "\" value=\"" + value
					+ "\"" + " class=\"" + style + "\" size=\"" + size
					+ "\" onblur=\"" + blur + "\"/>";
		}

		// 准备好ajax参数
		String postParams = "wildcard=" + wildcard + "&lineCnt=" + lineCnt
				+ "&busiType=" + busiType + "&value=" + value + "&relate="
				+ relate;

		// 生成js,页面必须引用jquery.js以及suggest.js
		String javaScript = "";

		if (showType.equals("script")) {
			javaScript = "mySuggest.add_suggest('" + name
					+ "', 'suggestCombox.fuiou', 'post', '" + postParams
					+ "');";

		} else {
			javaScript = "<script>" + "$(function(){"
					+ "mySuggest.add_suggest('" + name
					+ "', 'authMchnt_suggestComboBox.action', 'post', '" + postParams
					+ "');" + "});</script>";
		}

		String sb = "";
		if (showType.equals("all")) {
			sb = input + javaScript;
		} else if (showType.equals("input")) {
			sb = input;
		} else if (showType.equals("script")) {
			sb = javaScript;
		}

		try {
			pageContext.getOut().write(sb);
		} catch (IOException e) {
			throw new JspException(e);
		}

		return super.doStartTag();
	}

	public String getWildcard() {
		return wildcard;
	}

	public void setWildcard(String wildcard) {
		this.wildcard = wildcard;
	}

	public String getLineCnt() {
		return lineCnt;
	}

	public void setLineCnt(String lineCnt) {
		this.lineCnt = lineCnt;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getAllowNull() {
		return allowNull;
	}

	public void setAllowNull(String allowNull) {
		this.allowNull = allowNull;
	}

	public String getValiType() {
		return valiType;
	}

	public void setValiType(String valiType) {
		this.valiType = valiType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getBlur() {
		return blur;
	}

	public void setBlur(String blur) {
		this.blur = blur;
	}

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}
	public static InsInfCacheBean getInsInfByCd(String cd){
		try{
			if (cd.length()<15){
				String mchntCd =  (String)SystemParams.get(SystemParams.KEY_INS_MCHNT_CD).get(cd);
				Object obj =  SystemParams.get(SystemParams.KEY_INS_INF).get(mchntCd);
				if(obj==null)
					return null;
				else{
					return (InsInfCacheBean)obj;
				}
			}else{
				Object obj = SystemParams.get(SystemParams.KEY_INS_INF).get(cd);
				if(obj==null)
					return null;
				else{
					return (InsInfCacheBean)obj;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}
