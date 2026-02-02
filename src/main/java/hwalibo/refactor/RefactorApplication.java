package hwalibo.refactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class RefactorApplication {
	public static void main(String[] args) {
		SpringApplication.run(RefactorApplication.class, args);
	}
}
