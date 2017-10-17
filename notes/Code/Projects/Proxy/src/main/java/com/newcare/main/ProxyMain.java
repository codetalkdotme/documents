package com.newcare.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Created by guobxu on 31/3/2017.
 */

@SpringBootApplication
@ComponentScan(value = {
		"com.newcare.proxy",
		"com.newcare.im",
		"com.newcare.mesg",
		"com.newcare.*.service.impl",
		"com.newcare.param.checker.impl",
})
@ImportResource("classpath:dubbo-provider.xml")
public class ProxyMain {
	
    public static void main(String[] args) throws Exception {
    	System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    	
        SpringApplication.run(ProxyMain.class, args);
    }
    
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setCacheSeconds(3600); //refresh cache once per hour
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}