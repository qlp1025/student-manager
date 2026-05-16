package com.student.controller;

import com.github.pagehelper.PageInfo;
import com.student.dto.ApiResponse;
import com.student.entity.SysUser;
import com.student.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysUser>> list() {
        return ApiResponse.success(userService.findAll());
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageInfo<SysUser>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(userService.findPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<SysUser> getById(@PathVariable Long id) {
        return ApiResponse.success(userService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> add(@RequestBody SysUser user) {
        userService.insert(user);
        return ApiResponse.success("添加成功", null);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> update(@RequestBody SysUser user) {
        userService.update(user);
        return ApiResponse.success("更新成功", null);
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<String> updatePassword(@RequestParam Long id, @RequestParam String password) {
        userService.updatePassword(id, password);
        return ApiResponse.success("密码修改成功", null);
    }

    @PutMapping("/resetPassword/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ApiResponse.success("密码重置成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> batchDelete(@RequestBody List<Long> ids) {
        userService.batchDelete(ids);
        return ApiResponse.success("批量删除成功", null);
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<SysUser>> getTeachers() {
        return ApiResponse.success(userService.findByRoleId(2L));
    }
}
