package com.newcare.fnd.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.newcare.fnd.enums.NoticeType;
import com.newcare.fnd.enums.SourceType;
import com.newcare.fnd.pojo.Notice;
import com.newcare.main.BizMain;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=BizMain.class)
public class NoticeServiceTest {

	private static Logger LOGGER = LoggerFactory.getLogger(NoticeServiceTest.class);
	
	@Autowired
	private INoticeService noticeService;
	
	@Test
	public void testAppNoticeList() {
		Notice notice = new Notice();
		notice.setUserId(1068L);
		notice.setSrcType(SourceType.HECADRE.getCode());
		notice.setType(NoticeType.HAPPO.getCode());
		
		List<Notice> noticeList = noticeService.getAppNoticeList(notice, 0, 20);
		LOGGER.info("Notice List: " + noticeList);
	}
	
//	@Test
//	public void testProcessIMMesg() {
//		String textMsg = "<text>很好</text>", 
//				imgMsg = "<image>group1/M00/00/02/wKhQBFlZrYqAJ1pvAABLK6vYjks9182453</image>";
//		
//		Assert.assertEquals("很好", noticeService.processIMMesg(textMsg));
//		Assert.assertEquals("发来一张图片", noticeService.processIMMesg(imgMsg));
//	}
	
}
