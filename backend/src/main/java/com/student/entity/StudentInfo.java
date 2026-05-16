package com.student.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentInfo {
    private Long id;
    private String studentNo;
    private String name;
    private Integer gender;
    private Integer age;
    private Long classId;
    private String phone;
    private String address;
    private LocalDate enrollDate;
    private String idCard;
    private String fatherName;
    private String fatherPhone;
    private String fatherIdCard;
    private String motherName;
    private String motherPhone;
    private String motherIdCard;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private String className;
    private String genderName;
}
