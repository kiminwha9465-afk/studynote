package com.example.studynote.config;

import com.example.studynote.StudynoteApplication;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 시간대를 Asia/Seoul로 고정하기 전, 서버 기본 시간대(UTC 등)로 저장된 기존 시각을 한국 시간으로 1회 보정한다.
 * 실행 여부는 app_migration 테이블에 기록하여 재시작해도 다시 적용하지 않는다.
 */
@Slf4j
@Component
@Order(0)
public class TimeZoneMigration implements CommandLineRunner {

    private static final String NAME = "kst-shift-2026-10-21";

    /** 테이블명, 시각 컬럼명 */
    private static final List<String[]> TARGETS = List.of(
            new String[]{"solve_record", "solved_at"},
            new String[]{"practical_solve_record", "solved_at"},
            new String[]{"exam_session", "created_at"},
            new String[]{"free_note", "created_at"},
            new String[]{"free_note", "updated_at"},
            new String[]{"question", "created_at"},
            new String[]{"practical_question", "created_at"});

    private final JdbcTemplate jdbc;

    public TimeZoneMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(String... args) {
        jdbc.execute("CREATE TABLE IF NOT EXISTS app_migration (name VARCHAR(100) PRIMARY KEY, applied_at TIMESTAMP)");
        Integer done = jdbc.queryForObject("SELECT COUNT(*) FROM app_migration WHERE name = ?", Integer.class, NAME);
        if (done != null && done > 0) return;

        Instant now = Instant.now();
        long diffHours = Duration.ofSeconds(StudynoteApplication.APP_ZONE.getRules().getOffset(now).getTotalSeconds()
                - StudynoteApplication.originalZone.getRules().getOffset(now).getTotalSeconds()).toHours();

        if (diffHours != 0) {
            // 시작 시각 이전 기록만 보정 (시작 후 한국 시간으로 새로 저장된 기록은 제외)
            Timestamp cutoff = Timestamp.valueOf(StudynoteApplication.startedAt);
            for (String[] t : TARGETS) {
                int n = jdbc.update("UPDATE " + t[0] + " SET " + t[1] + " = DATEADD('HOUR', ?, " + t[1] + ")"
                        + " WHERE " + t[1] + " < ?", diffHours, cutoff);
                log.info("시간대 보정: {}.{} {}건 +{}시간", t[0], t[1], n, diffHours);
            }
        } else {
            log.info("시간대 보정 불필요: 기존 시간대 {}", StudynoteApplication.originalZone);
        }
        jdbc.update("INSERT INTO app_migration (name, applied_at) VALUES (?, CURRENT_TIMESTAMP)", NAME);
    }
}
