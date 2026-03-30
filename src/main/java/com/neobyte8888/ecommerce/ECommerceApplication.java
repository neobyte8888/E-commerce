package com.neobyte8888.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl") // Bật công tắc cho phép JPA tự động theo dõi thời gian
public class ECommerceApplication {
	
	private static final Logger log = LoggerFactory.getLogger(ECommerceApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
		
		log.info("🚀🚀🚀 APP STARTED SUCCESSFULLY !!! 🔥🔥🔥");
	}

}
