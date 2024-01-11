package kr.co.teacherforboss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeacherforbossApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeacherforbossApplication.class, args);
	}

}
