package com.fuiou.mgr.ftp.verify;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.ftp.FtpJob;

public class VerifyFtpJobTest{

    @Before
    public void setUp() throws Exception{
    }

    @Test
    public void testDoJob(){
        try {
            SystemParams.paramInit();
            FtpJob ftpJob = new VerifyFtpJob();
            ftpJob.doJob();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
