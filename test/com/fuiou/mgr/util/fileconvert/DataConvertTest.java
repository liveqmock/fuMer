package com.fuiou.mgr.util.fileconvert;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.fuiou.mgr.util.fileconvert.DataConvert;
import com.fuiou.mgr.util.fileconvert.payfor.huaan.HuaAnPayForFileFormat;

public class DataConvertTest{
    DataConvert dataConvert = new DataConvert();

    @Before
    public void setUp() throws Exception{
    }

    @Test
    public void testBankConvert(){
        String bankCd = dataConvert.bankConvert("顺德农村商业银行", "00011200M000000");
        assertEquals("0314", bankCd);
    }
    
    @Test
    public void testBankConvert2(){
        String bankCd = dataConvert.bankConvert("顺德农村信用社金源分社", "00011200M000000");
        assertEquals("0402", bankCd);
    }
    
    @Test
    public void testBankConvert3(){
        String bankCd = dataConvert.bankConvert("顺德aaa", "00011200M000000");
        assertEquals("", bankCd);
    }

    @Test
    public void testCityConvert(){
        fail("Not yet implemented");
    }

}
