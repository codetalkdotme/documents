package com.newcare.im.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.newcare.mesg",
		"com.newcare.*.service.impl",
		"com.newcare.im.*.service.impl"
})
@MapperScan(value = {"com.newcare.im.*.mapper"})
@ImportResource("classpath:dubbo-provider.xml")
public class IMMain {

	public static void main(String[] args) throws Exception	{

		SpringApplication.run(IMMain.class, args);
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

