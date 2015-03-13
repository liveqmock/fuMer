package com.fuiou.mgr.ftp.verify;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class VerifyFileScannerTest{
    VerifyFileScanner verifyFileScanner;

    @Before
    public void setUp() throws Exception{
        verifyFileScanner = new VerifyFileScanner();
    }

    @Test
    public void testGetFiles(){
        List<File> files = verifyFileScanner.getFiles();
        assertNotNull(files);
    }

}
