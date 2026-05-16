package com.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysRole {
    private Long id;
    private String roleName;
    private String roleKey;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
