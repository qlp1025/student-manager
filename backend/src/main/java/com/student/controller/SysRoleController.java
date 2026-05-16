package com.student.controller;

import com.student.dto.ApiResponse;
import com.student.entity.SysRole;
import com.student.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleMapper roleMapper;

    @GetMapping("/list")
    public ApiResponse<List<SysRole>> list() {
        return ApiResponse.success(roleMapper.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SysRole> getById(@PathVariable Long id) {
        return ApiResponse.success(roleMapper.findById(id));
    }
}
