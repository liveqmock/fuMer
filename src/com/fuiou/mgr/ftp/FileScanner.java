package com.fuiou.mgr.ftp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.TDataDictConst;

/**
 * Richard Xiong
 * 上传文件扫描器基类，调用子类的getFiles方法获取各种上传文件列表，子类需要设置pattern对象，pattern表示文件名的正则表达式样式
 */
public abstract class FileScanner{
    private static Pattern MCHNT_CD_PATTERN = Pattern.compile("^[0-9a-zA-Z]{15}$");
    protected Pattern pattern;
    private TDataDictService tDataDictService = new TDataDictService();
    private TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
    private static Logger logger = LoggerFactory.getLogger(FileScanner.class);

    public Pattern getPattern(){
        return pattern;
    }

    /**
     * 设置上传文件正则表达式样式
     * @param pattern
     */
    public void setPattern(Pattern pattern){
        this.pattern = pattern;
    }

    /**
     * 获取上传文件列表
     * @return 上传文件列表，null表示不存在
     */
    public List<File> getFiles(){
        logger.info("scan ftp upload files begin");
        //获取ftp主目录路径
        String ftpRootDirStr = tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.SYS_CONST, TDataDictConst.FTP_ROOT_DIR);
        File ftpRootDir = new File(ftpRootDirStr);
        if(!ftpRootDir.exists() || ftpRootDir.isFile()){
            logger.error("ftpRootDir does not exist");
            return null;
        }
        //获取ftp主目录下所有商户目录，商户必须是有效商户
        IOFileFilter dirFileFilter = DirectoryFileFilter.DIRECTORY;
        IOFileFilter regexFileFilter = new RegexFileFilter(MCHNT_CD_PATTERN);
        FilenameFilter filenameFilter = new AndFileFilter(dirFileFilter, regexFileFilter);
        File[] mchntDirs = ftpRootDir.listFiles(filenameFilter);
        if(mchntDirs == null || mchntDirs.length == 0){
            logger.info("mchnt dirs does not exist");
            return null;
        }
        //获取上传文件列表
        List<File> uploadFiles = new ArrayList<File>();
        for(int i=0; i<mchntDirs.length; i++){
            String mchntCd = mchntDirs[i].getName();
            TInsMchntInf tInsInf =tInsMchntInfService.selectTInsInfByMchntCd(mchntCd);
            if(null == tInsInf){
                continue;
            }
            //上传文件在商户目录的upload目录下
            File uploadDir = new File(mchntDirs[i], TDataDictConst.FTP_DIR_UPLOAD);
            if(!uploadDir.exists() || uploadDir.isFile()){
                continue;
            }
            IOFileFilter fileFileFilter = FileFileFilter.FILE;
            IOFileFilter regexFileFilter2 = new RegexFileFilter(pattern);
            FilenameFilter filenameFilter2 = new AndFileFilter(fileFileFilter, regexFileFilter2);
            File[] files = uploadDir.listFiles(filenameFilter2);
            if(files == null || files.length == 0){
                continue;
            }
            uploadFiles.addAll(Arrays.asList(files));
        }
        if(uploadFiles == null || uploadFiles.size() == 0){
            logger.info("upload files does not exist");
            return null;
        }else{
            logger.info("upload files " + String.valueOf(uploadFiles.size()) +" exist");
        }
        return uploadFiles;
    }
}
