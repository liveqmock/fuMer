package com.fuiou.mgr.remakeTest;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fuiou.mgr.adapter.treaty.CustmrTreatyAdapter;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.util.TDataDictConst;

public class CustmrTreatyAdapterTest {
	@Test
	public void analysisTxnTest(){
		CustmrTreatyAdapter custmr=new CustmrTreatyAdapter();
		File file=new File("D:\\百博时代\\工作文件\\协议库上传文件\\客户协议库\\客户协议库.xls");
		try {
			FileAccess fileAccess = new FileAccess();
			fileAccess.setFile(file);
			fileAccess.setFileName("客户协议库.xls");
			fileAccess.setBusiCd(TDataDictConst.CUSTMR_TREATY);
			Map<String, Object> map=custmr.analysisTxn(fileAccess);
			List<TCustmrBusi> tInsMchntInfs=(List<TCustmrBusi>) map.get("tCustmrBusis");
			Assert.assertTrue(tInsMchntInfs.size()>0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
