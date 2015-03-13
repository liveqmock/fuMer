package com.fuiou.mgr.ftp.payfor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.ftp.FtpJob;

public class PayForFtpQuartzJob implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        if(SystemParams.bankMap == null){
            return;
        }
        FtpJob ftpJob = new PayForFtpJob();
        ftpJob.doJob();
    }

}
