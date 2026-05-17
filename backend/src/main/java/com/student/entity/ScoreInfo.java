package com.student.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScoreInfo {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private BigDecimal score;
    private LocalDate examDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private String studentName;
    private String studentNo;
    private String className;
    private Long classId;
    private String subjectName;
    private BigDecimal totalScore;
    private Integer rank;
    private Long examId;
    private String examName;
}
