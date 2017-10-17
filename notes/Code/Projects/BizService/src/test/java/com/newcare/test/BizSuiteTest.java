package com.newcare.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.newcare.fnd.service.NoticeServiceTest;
import com.newcare.p3.jsms.service.JsmsServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		JsmsServiceTest.class, 
		NoticeServiceTest.class
})
public class BizSuiteTest {

}
