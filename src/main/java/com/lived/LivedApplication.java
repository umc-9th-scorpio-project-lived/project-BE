package com.lived;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LivedApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivedApplication.class, args);
	}

}
