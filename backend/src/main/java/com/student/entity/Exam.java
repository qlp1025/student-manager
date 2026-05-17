package com.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Exam {
    private Long id;
    private String examName;
    private String remark;
    private LocalDateTime createTime;
}
