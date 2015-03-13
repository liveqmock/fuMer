package com.fuiou.mgr.ftp.payfor;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.ftp.FtpJob;

public class PayForFtpJobTest {

	@Test
	public void testPayForFtpJob() {
		try {
			SystemParams.paramInit();
			FtpJob ftpJob = new PayForFtpJob();
			ftpJob.doJob();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
