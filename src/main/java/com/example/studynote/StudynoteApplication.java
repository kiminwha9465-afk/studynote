package com.example.studynote;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudynoteApplication {

	public static final ZoneId APP_ZONE = ZoneId.of("Asia/Seoul");

	/** 시간대를 고정하기 전 JVM 기본 시간대 (기존 기록이 어떤 시간대로 저장됐는지 판단용) */
	public static ZoneId originalZone = ZoneId.systemDefault();

	/** 앱 시작 시각(한국 시간). 이 시각 이전 기록만 시간대 보정 대상이 된다. */
	public static LocalDateTime startedAt = LocalDateTime.now(APP_ZONE);

	public static void main(String[] args) {
		originalZone = ZoneId.systemDefault();
		startedAt = LocalDateTime.now(APP_ZONE);
		TimeZone.setDefault(TimeZone.getTimeZone(APP_ZONE));
		SpringApplication.run(StudynoteApplication.class, args);
	}

}
