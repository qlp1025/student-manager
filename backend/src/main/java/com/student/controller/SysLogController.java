package com.student.controller;

import com.github.pagehelper.PageInfo;
import com.student.dto.ApiResponse;
import com.student.entity.SysLog;
import com.student.service.SysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService logService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysLog>> list() {
        return ApiResponse.success(logService.findAll());
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageInfo<SysLog>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(logService.findPage(pageNum, pageSize));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysLog>> getByUsername(@PathVariable String username) {
        return ApiResponse.success(logService.findByUsername(username));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        logService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
