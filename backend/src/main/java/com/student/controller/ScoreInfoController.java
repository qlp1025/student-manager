package com.student.controller;

import com.github.pagehelper.PageInfo;
import com.student.dto.ApiResponse;
import com.student.dto.ScoreQueryDTO;
import com.student.entity.ScoreInfo;
import com.student.service.ScoreInfoService;
import com.student.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreInfoController {

    private final ScoreInfoService scoreService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<ScoreInfo>> list() {
        return ApiResponse.success(scoreService.findAll());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<PageInfo<ScoreInfo>> page(ScoreQueryDTO queryDTO) {
        return ApiResponse.success(scoreService.findPage(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<ScoreInfo> getById(@PathVariable Long id) {
        return ApiResponse.success(scoreService.findById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<ScoreInfo>> getByStudentId(@PathVariable Long studentId) {
        return ApiResponse.success(scoreService.findByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<BigDecimal> getTotalScore(@PathVariable Long studentId) {
        return ApiResponse.success(scoreService.getTotalScore(studentId));
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<ScoreInfo>> getRanking(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) List<Long> subjectIds) {
        return ApiResponse.success(scoreService.findRanking(classId, subjectIds));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> add(@RequestBody ScoreInfo score) {
        scoreService.insert(score);
        return ApiResponse.success("添加成功", null);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> batchAdd(@RequestBody List<ScoreInfo> scores) {
        scoreService.batchInsert(scores);
        return ApiResponse.success("批量添加成功", null);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> update(@RequestBody ScoreInfo score) {
        scoreService.update(score);
        return ApiResponse.success("更新成功", null);
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        scoreService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        scoreService.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> export(ScoreQueryDTO queryDTO) throws IOException {
        List<ScoreInfo> scores = scoreService.findByCondition(queryDTO);
        byte[] data = ExcelUtil.exportScores(scores);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("成绩信息.xlsx", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] data = ExcelUtil.createScoreTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("成绩导入模板.xlsx", StandardCharsets.UTF_8);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Integer> importScores(@RequestParam("file") MultipartFile file) throws IOException {
        int count = scoreService.importScores(file);
        return ApiResponse.success("导入成功", count);
    }
}
