package com.student.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScoreQueryDTO {
    private Long studentId;
    private Long classId;
    private Long subjectId;
    private List<Long> subjectIds;
    private Double minScore;
    private Double maxScore;
    private LocalDate examDate;
    private Long examId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
