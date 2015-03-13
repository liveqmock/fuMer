package com.fuiou.mgr.directReturnPayResult;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 返回商户回盘文件
 * 测试系统
 * yangliehui
 *
 */
public class ReturnPayResult {
	/**
	 * 读取配置文件，配置要测试的商户
	 * @return
	 */
	public String readValue(){
		Properties props = new Properties();
		try {
			InputStream ips = new BufferedInputStream(new FileInputStream("\\resources\\returnPayResult.properties"));
			props.load(ips);
			String value = props.getProperty("mchmtcd");
			return value;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
