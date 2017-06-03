package org.group.mmd.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={"org.group.mmd"},exclude = {SecurityAutoConfiguration.class })
@EntityScan("org.group.mmd.model")
@EnableJpaRepositories("org.group.mmd.repository")
public class MmdApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmdApplication.class, args);
	}
}
