package com.cognizant.TwitterApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cognizant.util.TokenGenerator;

@EntityScan("com.*")
@EnableJpaRepositories("com.*")
@SpringBootApplication(scanBasePackages = "com.*")
public class TwitterAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterAppApplication.class, args);
	
	}

}
