package com.newcare.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableScheduling
@Configuration
@SpringBootApplication
@ComponentScan(basePackages = {
		"com.newcare.mesg",
		"com.newcare.cache.config",
		"com.newcare.auth.dao.impl",
		"com.newcare.*.service.impl",
		"com.newcare.p3.*.service.impl",
		"com.newcare.service.impl",
		"com.newcare.cache.service.impl",
		"com.newcare.timedtask.timer"
})
@MapperScan(value = {"com.newcare.*.mapper", "com.newcare.p3.*.mapper"})
@ImportResource("classpath:dubbo-provider.xml")
public class BizMain
{

	public static void main(String[] args) throws Exception	{

		SpringApplication.run(BizMain.class, args);
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
