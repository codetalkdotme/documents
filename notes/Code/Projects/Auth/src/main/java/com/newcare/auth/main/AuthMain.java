package com.newcare.auth.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.newcare.mesg",
		"com.newcare.p3.jsms.service.impl",
		"com.newcare.cache.config",
		"com.newcare.cache.service.impl",
		"com.newcare.auth.dao.impl",
		"com.newcare.auth.service.impl",
})
@ImportResource("classpath:dubbo-provider.xml")
public class AuthMain {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AuthMain.class);
	
	public static void main(String[] args) throws Exception {
        SpringApplication.run(AuthMain.class, args);
        
        LOGGER.debug("Auth server started...");
        
        System.in.read();
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
