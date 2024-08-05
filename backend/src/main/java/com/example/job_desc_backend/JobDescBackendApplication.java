package com.example.job_desc_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class JobDescBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(JobDescBackendApplication.class, args);

	}

}
