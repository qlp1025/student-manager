package com.student.controller;

import com.github.pagehelper.PageInfo;
import com.student.dto.ApiResponse;
import com.student.dto.StudentQueryDTO;
import com.student.entity.StudentInfo;
import com.student.service.StudentInfoService;
import com.student.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<StudentInfo>> list() {
        return ApiResponse.success(studentService.findAll());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<PageInfo<StudentInfo>> page(StudentQueryDTO queryDTO) {
        return ApiResponse.success(studentService.findPage(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<StudentInfo> getById(@PathVariable Long id) {
        return ApiResponse.success(studentService.findById(id));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<StudentInfo>> getByClassId(@PathVariable Long classId) {
        return ApiResponse.success(studentService.findByClassId(classId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> add(@RequestBody StudentInfo student) {
        studentService.insert(student);
        return ApiResponse.success("添加成功", null);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> update(@RequestBody StudentInfo student) {
        studentService.update(student);
        return ApiResponse.success("更新成功", null);
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        studentService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        studentService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> batchDelete(@RequestBody List<Long> ids) {
        studentService.batchDelete(ids);
        return ApiResponse.success("批量删除成功", null);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> export() throws IOException {
        List<StudentInfo> students = studentService.findAll();
        byte[] data = ExcelUtil.exportStudents(students);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("学生信息.xlsx", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ApiResponse<Integer> count() {
        return ApiResponse.success(studentService.count());
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] data = ExcelUtil.createStudentTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("学生信息导入模板.xlsx", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        int count = studentService.importStudents(file);
        return ApiResponse.success("导入成功", count);
    }
}
