package com.student.controller;

import com.student.dto.ApiResponse;
import com.student.entity.Exam;
import com.student.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping("/list")
    public ApiResponse<List<Exam>> list() {
        return ApiResponse.success(examService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Exam> getById(@PathVariable Long id) {
        return ApiResponse.success(examService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> add(@RequestBody Exam exam) {
        examService.insert(exam);
        return ApiResponse.success("添加成功", null);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> update(@RequestBody Exam exam) {
        examService.update(exam);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        examService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
