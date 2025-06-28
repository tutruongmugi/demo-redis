package com.example.demo_redis;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRedisDocumentRepositories(basePackages = "com.example.demo_redis.*")
@SpringBootApplication
public class DemoRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoRedisApplication.class, args);
	}

}
