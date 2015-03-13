package com.fuiou.mgr.ftp.payfor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ParseXMLFile {

	 /**
	   * 解析xml文件，获取渠道信息
	   * @param is：xml文件流
	   * @param channelName：渠道名称
	   * @return 匹配到的渠道
	   */
	  public static List<String> getMchntscd(InputStream is){
		 
		  if(null == is){
			  System.err.println("xml file path is empty");
			  return null;
		  }
		  
		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();//获取解析工程
		  try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);//获取文档对象
			
			Element root = document.getDocumentElement();//获取根节点
			
			NodeList mchnts = root.getChildNodes();//获取所有的子节点
			
			if(mchnts != null){
				List<String> mchntsList = new ArrayList<String>();
				Node mchnt = null;
				for(int i=0; i<mchnts.getLength() ;i++){
						mchnt = mchnts.item(i);
						if(mchnt.getNodeType() == Node.ELEMENT_NODE){
							mchntsList.add(mchnt.getFirstChild().getNodeValue().trim());
						}
				}
				return mchntsList;
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  return null;
	  }
}
