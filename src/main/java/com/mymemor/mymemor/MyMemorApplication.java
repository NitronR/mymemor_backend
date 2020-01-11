package com.mymemor.mymemor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MyMemorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMemorApplication.class, args);
	}

}
