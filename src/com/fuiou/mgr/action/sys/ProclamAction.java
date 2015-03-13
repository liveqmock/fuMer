package com.fuiou.mgr.action.sys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.fuiou.mer.model.SysPlatformAttachment;
import com.fuiou.mer.model.SysPlatformNotice;
import com.fuiou.mer.service.NoticeService;
import com.fuiou.mgr.action.BaseAction;
import com.opensymphony.xwork2.Action;

public class ProclamAction extends BaseAction{

	private static final long serialVersionUID = 2085542158840009943L;
	
	private NoticeService noticeService = new NoticeService();
	private List<SysPlatformNotice> notices;
	private SysPlatformNotice notice;
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<SysPlatformNotice> getNotices() {
		return notices;
	}
	public void setNotices(List<SysPlatformNotice> notices) {
		this.notices = notices;
	}
	public SysPlatformNotice getNotice() {
		return notice;
	}
	public void setNotice(SysPlatformNotice notice) {
		this.notice = notice;
	}
	public NoticeService getNoticeService() {
		return noticeService;
	}
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	public String findNotices(){
		notices = noticeService.findNoticeByMchntCd(tOperatorInf.getMCHNT_CD());
		return "proclamAtionsList";
	}
	
	public String findNoticeByNoticeno(){
		String noticeNo = notice.getNOTICE_NO();
		Integer cnt = noticeService.isReadNotice(noticeNo, tOperatorInf);
		if(cnt==0){
			noticeService.readNotice(noticeNo,tOperatorInf);
		}
		notice = noticeService.findNoticeByNo(notice.getNOTICE_NO());
		SysPlatformAttachment attachment = noticeService.findAttachmentByNoticeno(notice.getNOTICE_NO());
		if(attachment!=null){
			notice.setAttachmentName(attachment.getATT_SYS_NAME());
		}
		return "detailTNotices";
	}
	
	public InputStream getDownloadFile() throws FileNotFoundException {
		SysPlatformAttachment attachment = noticeService.findAttachmentByNoticeno(notice.getNOTICE_NO());
		if(attachment!=null){
			notice.setAttachmentName(attachment.getATT_SYS_NAME());
			setFileName(attachment.getATT_SYS_NAME());
		}
		return new FileInputStream(fileName);
	}
	
	@Override
	public String execute() throws Exception {
		return Action.SUCCESS;
	}
	
}

