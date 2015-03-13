package com.fuiou.mgr.http.connector;
/**
 * http直连接口交易处理统一接口
 * yangliehui
 *
 */
public interface HttpReqProcessInterface {
	/**
	 * 交易处理方法
	 * @param o		用户请求的xml转换的对象，在实现类中强制转换成具体类型
	 * @return		返回给用户的xml结果
	 */
	public String httpProcess(Object o,String mchnt,String busiCd);
}
