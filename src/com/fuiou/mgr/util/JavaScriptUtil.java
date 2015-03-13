package com.fuiou.mgr.util;

public class JavaScriptUtil {

	// 将java字符串中的\和"字符替换成\\和\"，用于给javascript变量赋值
	public static String javaStr2JavaScriptStr(String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
