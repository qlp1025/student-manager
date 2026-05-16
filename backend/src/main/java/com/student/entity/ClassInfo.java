package com.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassInfo {
    private Long id;
    private String className;
    private String grade;
    private Long teacherId;
    private Integer studentCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private String teacherName;
}
