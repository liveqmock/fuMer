package com.fuiou.mgr.ftp.incomefor;

import com.fuiou.mgr.ftp.FileProcessor;
import com.fuiou.mgr.ftp.FileScanner;
import com.fuiou.mgr.ftp.FtpJob;

public class IncomforJob extends FtpJob{
    @Override
	public FileScanner createFileScanner() {
		return new IncomeForFileScanner();
	}

    @Override
	public FileProcessor createFileProcessor() {
		return new IncomeForFileProcessor();
	}

}
