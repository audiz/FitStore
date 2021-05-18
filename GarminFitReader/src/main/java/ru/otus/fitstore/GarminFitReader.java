package ru.otus.fitstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@EnableFeignClients
@EnableMongoRepositories
@SpringBootApplication
public class GarminFitReader {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(GarminFitReader.class, args);
	}
}
