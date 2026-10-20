package com.example.studynote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "practical_question")
@Getter
@Setter
@NoArgsConstructor
public class PracticalQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer examYear;
    private Integer examRound;
    private Integer questionNo;

    @Column(columnDefinition = "TEXT")
    private String content;

    /** 정답. 복수 허용 답안은 '|'로 구분. 예: "캡슐화|인캡슐레이션" */
    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private LocalDateTime createdAt;

    @Builder
    public PracticalQuestion(Integer examYear, Integer examRound, Integer questionNo,
                              String content, String answer, String explanation) {
        this.examYear = examYear;
        this.examRound = examRound;
        this.questionNo = questionNo;
        this.content = content;
        this.answer = answer;
        this.explanation = explanation;
    }

    /** 사용자 입력이 정답으로 인정되는지 확인 (대소문자·공백 무시, | 구분 복수 정답 지원) */
    public boolean isCorrectAnswer(String input) {
        if (input == null || answer == null) return false;
        String norm = normalize(input);
        if (norm.isEmpty()) return false;
        for (String a : answer.split("\\|")) {
            if (normalize(a).equals(norm)) return true;
        }
        return false;
    }

    /** ``` 코드 블록을 &lt;pre&gt;&lt;code&gt;로 변환한 HTML을 반환한다. */
    public String getContentHtml() {
        if (content == null) return "";
        String[] parts = content.split("```");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 == 0) {
                sb.append(escapeHtml(parts[i]).replace("\n", "<br>"));
            } else {
                String code = parts[i];
                int nl = code.indexOf('\n');
                if (nl >= 0) code = code.substring(nl + 1);
                if (code.endsWith("\n")) code = code.substring(0, code.length() - 1);
                sb.append("<pre class=\"mt-2 mb-2 rounded bg-slate-100 dark:bg-zinc-700 px-3 py-2 text-sm font-mono overflow-x-auto\"><code>")
                  .append(escapeHtml(code))
                  .append("</code></pre>");
            }
        }
        return sb.toString();
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String normalize(String s) {
        return s.trim().toLowerCase().replaceAll("\\s+", "");
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
