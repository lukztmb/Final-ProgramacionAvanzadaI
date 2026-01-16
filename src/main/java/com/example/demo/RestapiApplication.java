package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.demo",
        "application",
        "domain",
        "infrastructure"
})
// linea para decirle dónde buscar las interfaces JPA
@EnableJpaRepositories(basePackages = "infrastructure.persistence.repository")

@EntityScan(basePackages = "infrastructure.persistence.entities")
public class RestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}

}
