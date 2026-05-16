package com.student.controller;

import com.github.pagehelper.PageInfo;
import com.student.dto.ApiResponse;
import com.student.entity.ClassInfo;
import com.student.service.ClassInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassInfoController {

    private final ClassInfoService classService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<ClassInfo>> list() {
        return ApiResponse.success(classService.findAll());
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageInfo<ClassInfo>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(classService.findPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<ClassInfo> getById(@PathVariable Long id) {
        return ApiResponse.success(classService.findById(id));
    }

    @GetMapping("/grade/{grade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<ClassInfo>> getByGrade(@PathVariable String grade) {
        return ApiResponse.success(classService.findByGrade(grade));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> add(@RequestBody ClassInfo classInfo) {
        classService.insert(classInfo);
        return ApiResponse.success("添加成功", null);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> update(@RequestBody ClassInfo classInfo) {
        classService.update(classInfo);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        classService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
