package com.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Subject {
    private Long id;
    private String subjectName;
    private Long teacherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private String teacherName;
}
