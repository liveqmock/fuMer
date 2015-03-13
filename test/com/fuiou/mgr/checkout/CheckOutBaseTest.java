package com.fuiou.mgr.checkout;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CheckOutBaseTest{

    @Before
    public void setUp() throws Exception{
    }

    @Test
    public void testSubBankName(){
        fail("Not yet implemented");
    }

    @Test
    public void testExceedDbLenth(){
        fail("Not yet implemented");
    }

    @Test
    public void testValidMoney(){
        
        assertTrue(CheckOutBase.validMoney("0.1"));
        assertTrue(CheckOutBase.validMoney("0.01"));
        assertTrue(CheckOutBase.validMoney("1234"));
        assertTrue(CheckOutBase.validMoney("1234.0"));
        assertTrue(CheckOutBase.validMoney("1234.00"));
        assertFalse(CheckOutBase.validMoney("0"));
        assertFalse(CheckOutBase.validMoney("0.0"));
        assertFalse(CheckOutBase.validMoney("0.00"));
        assertFalse(CheckOutBase.validMoney(".00"));
        assertFalse(CheckOutBase.validMoney("111111111111111"));
        assertFalse(CheckOutBase.validMoney("aaa"));
    }
    
    @Test
    public void testGetMaxAmtIncomeFor(){
        assertEquals(1000000, new CheckOutBase().getMaxAmtIncomeFor());
    }
    
    @Test
    public void testGetMaxAmtPayFor(){
        assertEquals(1000000, new CheckOutBase().getMaxAmtPayFor());
    }

}
