package com.student.dto;

import lombok.Data;

@Data
public class StudentQueryDTO {
    private String keyword;
    private Long classId;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
