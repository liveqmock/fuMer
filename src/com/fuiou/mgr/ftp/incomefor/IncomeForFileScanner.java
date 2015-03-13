package com.fuiou.mgr.ftp.incomefor;

import java.util.regex.Pattern;

import com.fuiou.mgr.ftp.FileScanner;

public class IncomeForFileScanner extends FileScanner{

    private static Pattern pattern = Pattern.compile("^AC01_\\d{8}_[a-zA-z0-9]+\\.txt$");
    
    public IncomeForFileScanner(){
        setPattern(pattern);
    }

}
