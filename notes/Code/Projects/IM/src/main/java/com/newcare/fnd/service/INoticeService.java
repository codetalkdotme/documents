package com.newcare.fnd.service;

import java.util.List;
import java.util.Map;

/**
 * 通知消息服务
 * @author guobxu
 *
 */
public interface INoticeService {

	/**
	 * 将所有未读APP通知标志为已读
	 * 
	 * @param userId	用户ID
	 * @param srcType	专干 / 居民
	 * @param type		通知类别
	 * 
	 * @return 返回更新条数
	 * 
	 */
	public int updateAllRead(Long userId, Integer srcType, Integer type);
	
	public int updateAllRead(Long userId, Integer srcType);
	
	/**
	 * IM 接口
	 * @param fromUserId 来自用户
	 * @param toUserId	目标用户
	 * @param srcType	源类型
	 * @param mode
	 */
	public void addIMNotice(Long fromUserId, Long toUserId, String srcType, Long mesgId, String content, int mode);
	
	public void updateIMReadByList(List<Long> mesgIdList);
	
}
