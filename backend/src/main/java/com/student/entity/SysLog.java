package com.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysLog {
    private Long id;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ip;
    private LocalDateTime createTime;
}
