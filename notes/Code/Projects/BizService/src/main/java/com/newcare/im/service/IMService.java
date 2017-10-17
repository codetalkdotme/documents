package com.newcare.im.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * IM服务接口
 * 
 * @author guobxu
 *
 */
public interface IMService {
	
    // 统计未及时回复数
	public Map<Long, Integer> countSlowRepliesWithin(List<Long> userIdList, Long startTime, Long endTime, Long timeInterval) ;

    // 获取指定时间内未及时处理消息所有专干ID
    public List<Long> lazyHedcareListWithin(Long timeInterval);
    
}
