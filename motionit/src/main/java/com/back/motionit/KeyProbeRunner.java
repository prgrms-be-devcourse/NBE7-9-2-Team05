package com.back.motionit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KeyProbeRunner implements CommandLineRunner {

	@Value("${moderation.perspective.api-key:}")
	private String apiKey;

	@Override
	public void run(String... args) {
		// 1) JVM이 받은 OS/Run Config 환경변수 원본 체크
		String envRaw = System.getenv("PERSPECTIVE_API_KEY");
		System.out.println("[Perspective] ENV PERSPECTIVE_API_KEY = " + (envRaw == null ? "(null)" : mask(envRaw)));

		// 2) Spring Property로 매핑된 값 체크
		if (apiKey == null || apiKey.isBlank()) {
			System.out.println("[Perspective] Property moderation.perspective.api-key = (NOT FOUND) ❌");
		} else {
			System.out.println("[Perspective] Property moderation.perspective.api-key = " + mask(apiKey) + " ✅");
		}
	}

	private String mask(String s) {
		if (s == null || s.length() < 8)
			return "********";
		return s.substring(0, 4) + "****" + s.substring(s.length() - 4);
	}
}