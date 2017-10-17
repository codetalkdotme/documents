package com.newcare.p3.jsms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.newcare.main.BizMain;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=BizMain.class)
public class JsmsServiceTest {

	@Autowired 
	private IJsmsService smsService;
	
	@Test
	public void testAddHecadreAlertList() {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		// hecadreUid/supervisorUid/hecadreName/hecadreMobile/supervisorMobile/orderType
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("hecadreUid", 1011);
		map1.put("supervisorUid", 1099);
		map1.put("hecadreName", "许xx");
		map1.put("hecadreMobile", "13509110011");
		map1.put("supervisorMobile", "18711001190");
		map1.put("orderType", "预约挂号单");
		
		dataList.add(map1);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("hecadreUid", 1054);
		map2.put("supervisorUid", 2098);
		map2.put("hecadreName", "王yx");
		map2.put("hecadreMobile", "13867980945");
		map2.put("supervisorMobile", "13622334411");
		map2.put("orderType", "专家咨询消息");
		
		dataList.add(map2);
		
		smsService.addHecadreAlertList(dataList);
	}
	
	@Test
	public void testAccountCreationSms() {
		smsService.sendAccountCreationSms("13590180201", "potti", "123456");
	}
	
}








