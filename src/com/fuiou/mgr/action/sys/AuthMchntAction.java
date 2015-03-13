package com.fuiou.mgr.action.sys;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.InsInfCacheBean;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.util.StringUtils;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.ActionContext;

public class AuthMchntAction {
	public String suggestComboBox(){
		ActionContext context = ActionContext.getContext();
		HttpServletRequest req = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse res = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		OutputStream out = null;
		String outputStr = "[";
		int cacheSize = 0;
		int cnt = 0;
		int iLineCnt = 0;
		try {
			out = res.getOutputStream();
			String wildcard = req.getParameter("wildcard");
			String lineCnt = req.getParameter("lineCnt");
			iLineCnt = Integer.parseInt(lineCnt);
			String busiType = req.getParameter("busiType");
			// String value = req.getParameter("value");
			String relate = req.getParameter("relate");
			String keyword = req.getParameter("keyword");
			keyword = keyword.trim();
			String keywords[] = { keyword, keyword };

			if (StringUtils.isNotEmpty(keyword)) {
				if (keyword.indexOf("：") >= 0) {
					String[] keywordsTemp = keyword.split("：");
					keywords[0] = keywordsTemp[0];
					keywords[1] = keywordsTemp[0];
				}

			}
			keywords[0] = keywords[0].toLowerCase();
			keywords[1] = keywords[1].toLowerCase();
			boolean needsRun = true;
			// 通配符
			if (StringUtils.isNotEmpty(wildcard)) {
				int wcNum = Integer.valueOf(wildcard).intValue();
				if (wcNum > 0) {
					if (keywords[0].indexOf('*') >= 0 || "通配".indexOf(keywords[1]) >= 0) {

						String wcStr = "";
						for (int i = 0; i < wcNum; i++) {
							wcStr += "*";
						}
						outputStr += "['" + wcStr + "：通配',''],";
						needsRun = false;
					}
				}
			}

			if (needsRun) {
				String[] busiTypes = busiType.split("\\|");
				if (StringUtils.isNotEmpty(busiTypes[0])) {
					if (busiTypes[0].equals("ins") || busiTypes[0].equals("mchnt")) {
						//
						if (StringUtils.isEmpty(relate)) {
							relate = "";
						}
						Map map = SystemParams.get(tOperatorInf.getMCHNT_CD());
						if (map != null) {
							//String[] insMchntTps = getParams(busiTypes);
							// 遍历map
							Iterator it = map.entrySet().iterator();
							for (cnt = 0; it.hasNext();) {
								Map.Entry entry = (Map.Entry) it.next();
								// String key = (String) entry.getKey(); //
								// mchntCd
								InsInfCacheBean val = (InsInfCacheBean) entry.getValue(); // InsInfCacheBean
								String key = "";
								if (busiTypes[0].equals("ins")) {
									key = val.getINS_CD();
								} else {
									key = val.getMCHNT_CD();
								}

//								if (isExistsInsMchntTp(insMchntTps, val.getMCHNT_TPS() + "&" + val.getINS_TP())) {
								if (key.toLowerCase().indexOf(keywords[0]) >= 0
										|| val.getINS_NAME_CN().toLowerCase().indexOf(keywords[1]) >= 0) {
									if (relate.equals("") || relate.equals("cups")
											&& relate.equals("relIns")
											&& relate.equals("acq")) {
										if (cnt < iLineCnt) {
											outputStr += "['" + key + "：" + val.getINS_NAME_CN() + "',''],";
										}
										cnt++;
									}
								}
//									cacheSize++;
//								}

							}
						}

					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (cnt <= iLineCnt)
				outputStr += "['','" + cnt + "/" + cacheSize + "条记录']]";
			else
				outputStr += "['••••••','" + cnt + "/" + cacheSize + "条记录']]";
			out.write(outputStr.getBytes("UTF-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
//	private String[] getParams(String[] strIn){
//		if (strIn.length > 1) {
//			return strIn[1].split(",");// like: F101,F102
//		} else {
//			return null;
//		}
//	}
//	private boolean isExistsInsMchntTp(String[] strIn, String insMchntTps){
//		// insMchntTps = xx&xx&
//		if (strIn != null) {
//			for (int i = 0; i < strIn.length; i++) {
//				if (insMchntTps.indexOf(strIn[i]) >= 0) {
//					return true;
//				}
//			}
//			return false;
//		} else {
//			return true;
//		}
//	}
}
