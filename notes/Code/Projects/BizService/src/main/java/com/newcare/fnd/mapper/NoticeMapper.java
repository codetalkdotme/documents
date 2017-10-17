package com.newcare.fnd.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.newcare.fnd.pojo.Notice;

/**
 * 通知消息mapper
 * @author guobxu
 *
 */
public interface NoticeMapper {

	/**************************** APP ****************************/
	
	public void insertNotice(Notice notice);
	
	public void insertNoticeList(List<Notice> noticeList);
	
	public Notice selectAppNoticeById(Long id);
	
	public List<Map<String, Integer>> countUnreadAppNotice(Map<String, Object> params);
	
	public List<Notice> selectAppNoticeList(Map<String, Object> params);
	
	public List<Notice> selectAppNoticeByTypeList(Map<String, Object> params);
	
	public int updateAppNoticeReplied(Notice notice);
	
	public int updateAllRead(Map<String, Object> params);
	
	public int updateAllReadByTypeList(Map<String, Object> params);
	
	public void updateIMReadByList(List<Long> mesgIdList);
	
	/**************************** PUSH ****************************/

	public List<Notice> listUnreadPushNoticeByUser(Long userId);
	
	public void updatePushStatusByList(@Param("noticeIdList") List<Long> noticeIdList, @Param("pushStatus") Integer pushStatus);
	
	public List<Notice> listUnreadPushNotice(@Param("days") Integer days, @Param("count") Integer count);
	
}












