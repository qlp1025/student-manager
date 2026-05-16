package com.student.controller;

import com.github.pagehelper.PageInfo;
import com.student.dto.ApiResponse;
import com.student.entity.Notice;
import com.student.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public ApiResponse<List<Notice>> list() {
        return ApiResponse.success(noticeService.findAll());
    }

    @GetMapping("/published")
    public ApiResponse<List<Notice>> published() {
        return ApiResponse.success(noticeService.findByStatus(1));
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageInfo<Notice>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(noticeService.findPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<Notice> getById(@PathVariable Long id) {
        return ApiResponse.success(noticeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> add(@RequestBody Notice notice) {
        noticeService.insert(notice);
        return ApiResponse.success("发布成功", null);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> update(@RequestBody Notice notice) {
        noticeService.update(notice);
        return ApiResponse.success("更新成功", null);
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        noticeService.updateStatus(id, status);
        String msg = status == 1 ? "发布成功" : "下架成功";
        return ApiResponse.success(msg, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        noticeService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
