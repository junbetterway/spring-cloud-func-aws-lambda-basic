package com.junbetterway.serverless.springcloudfunc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.junbetterway.serverless.springcloudfunc.functions.CreateAccount;
import com.junbetterway.serverless.springcloudfunc.functions.ReadAccount;

@SpringBootApplication
public class SpringcloudfuncApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudfuncApplication.class, args); 
	}
	
	@Bean
	public CreateAccount createAccount() {
		return new CreateAccount();
	}

	@Bean
	public ReadAccount readAccount() {
		return new ReadAccount();
	}
	
}
