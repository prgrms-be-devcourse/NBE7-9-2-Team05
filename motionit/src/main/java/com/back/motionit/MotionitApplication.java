package com.back.motionit;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MotionitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotionitApplication.class, args);
	}

	@Bean
	ApplicationRunner perspectiveProbe(Environment env) {
		return args -> {
			String envVar = System.getenv("PERSPECTIVE_API_KEY");                 // OS/RunConfig 환경변수
			String sysProp = System.getProperty("moderation.perspective.api-key"); // VM 옵션 -D로 주입
			String spring = env.getProperty("moderation.perspective.api-key");    // application.yml로 해석된 값
			System.out.println("=== PERSPECTIVE DEBUG ===");
			System.out.println("ENV     PERSPECTIVE_API_KEY = " + mask(envVar));
			System.out.println("SYS     moderation.perspective.api-key = " + mask(sysProp));
			System.out.println("SPRING  moderation.perspective.api-key = " + mask(spring));
			System.out.println("==========================================");
		};
	}

	private static String mask(String s) {
		if (s == null || s.isBlank())
			return "(null)";
		return s.length() < 8 ? "********" : s.substring(0, 4) + "****" + s.substring(s.length() - 4);
	}

}
