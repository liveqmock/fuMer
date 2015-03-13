package com.fuiou.mgr.ftp.incomefor;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class IncomeForFileScannerTest{
    IncomeForFileScanner incomeForFileScanner;

    @Before
    public void setUp() throws Exception{
        incomeForFileScanner = new IncomeForFileScanner();
    }

    @Test
    public void testGetFiles(){
        List<File> files = incomeForFileScanner.getFiles();
        assertNotNull(files);
    }

}
