package com.student.controller;

import com.student.dto.ApiResponse;
import com.student.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;

    @GetMapping("/tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> getTables() {
        return ApiResponse.success(databaseService.getAllTables());
    }

    @GetMapping("/table/{tableName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getTableData(@PathVariable String tableName,
                                                         @RequestParam(defaultValue = "100") Integer limit,
                                                         @RequestParam(defaultValue = "0") Integer offset) {
        return ApiResponse.success(databaseService.getTableData(tableName, limit, offset));
    }

    @GetMapping("/table/{tableName}/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getTableCount(@PathVariable String tableName) {
        return ApiResponse.success(databaseService.getTableCount(tableName));
    }

    @GetMapping("/table/{tableName}/columns")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> getTableColumns(@PathVariable String tableName) {
        return ApiResponse.success(databaseService.getTableColumns(tableName));
    }

    @PostMapping("/query")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> executeQuery(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        if (sql == null || sql.trim().isEmpty()) {
            return ApiResponse.error("SQL语句不能为空");
        }
        try {
            return ApiResponse.success(databaseService.executeQuery(sql.trim()));
        } catch (SecurityException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
