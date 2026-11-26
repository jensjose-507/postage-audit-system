package com.postal.apv.revenue_audit_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "com.postal.apv")
@EnableJpaRepositories(basePackages = "com.postal.apv")
@EntityScan(basePackages = "com.postal.apv")
public class RevenueAuditServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevenueAuditServiceApplication.class, args);
	}

}
