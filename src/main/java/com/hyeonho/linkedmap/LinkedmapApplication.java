package com.hyeonho.linkedmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// spring Boot Security Auto Configuration을 비활성화하기 위해
// 왜 비활성화 하는지는 이유를 모르겠음

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class LinkedmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedmapApplication.class, args);
	}

}
