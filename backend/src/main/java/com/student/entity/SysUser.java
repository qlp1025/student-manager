package com.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private Long roleId;
    private Integer status;
    private String avatar;
    private String phone;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private String roleName;
    private String roleKey;
}
