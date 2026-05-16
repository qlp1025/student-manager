package com.student.controller;

import com.student.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取表是否存在
     */
    @GetMapping("/tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> listTables() {
        String sql = "SHOW TABLES LIKE 'I_%'";
        try {
            List<String> tables = jdbcTemplate.queryForList(sql, String.class);
            return ApiResponse.success(tables);
        } catch (Exception e) {
            return ApiResponse.success(new ArrayList<>());
        }
    }

    /**
     * 获取表的所有列
     */
    @GetMapping("/columns")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> getTableColumns(@RequestParam String tableName) {
        String sql = "SHOW COLUMNS FROM " + tableName;
        try {
            List<Map<String, Object>> columns = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                Map<String, Object> col = new HashMap<>();
                col.put("columnName", row.get("Field"));
                col.put("dataType", row.get("Type"));
                col.put("nullable", "YES".equals(row.get("Null")) ? "YES" : "NO");
                col.put("columnKey", row.get("Key"));
                col.put("extra", row.get("Extra"));
                columns.add(col);
            }
            return ApiResponse.success(columns);
        } catch (Exception e) {
            return ApiResponse.error("表不存在或无法访问: " + e.getMessage());
        }
    }

    /**
     * 创建新表并导入数据
     * 表名格式: I_用户输入的表名
     * 列格式: COL_A, COL_B, COL_C... (按Excel列顺序)
     */
    @PostMapping("/createAndImport")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> createAndImport(
            @RequestParam String tableName,
            @RequestParam("file") MultipartFile file) throws IOException {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();

        // 清理表名，只保留字母数字下划线
        String cleanName = tableName.replaceAll("[^a-zA-Z0-9_]", "");
        if (cleanName.isEmpty()) {
            return ApiResponse.error("表名不能为空");
        }

        // 最终表名
        String finalTableName = "I_" + cleanName;

        // 解析Excel获取列数和数据
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        if (sheet.getLastRowNum() < 1) {
            workbook.close();
            return ApiResponse.error("Excel文件没有数据");
        }

        // 获取第一行（标题行）的列数
        Row headerRow = sheet.getRow(0);
        int columnCount = headerRow.getLastCellNum();

        // 获取数据行数
        int dataRowCount = sheet.getLastRowNum();

        // 构建创建表的SQL
        StringBuilder createSql = new StringBuilder("CREATE TABLE ");
        createSql.append(finalTableName).append(" (");
        createSql.append("id BIGINT AUTO_INCREMENT PRIMARY KEY, ");

        for (int i = 0; i < columnCount; i++) {
            String colName = "COL_" + (char) ('A' + i);
            // 根据Excel第一行内容设置列注释
            Cell headerCell = headerRow.getCell(i);
            String headerValue = getCellStringValue(headerCell);
            if (headerValue != null && !headerValue.trim().isEmpty()) {
                createSql.append(colName).append(" VARCHAR(500) COMMENT '").append(headerValue.trim()).append("', ");
            } else {
                createSql.append(colName).append(" VARCHAR(500), ");
            }
        }
        createSql.append("create_time DATETIME DEFAULT CURRENT_TIMESTAMP");
        createSql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        try {
            // 删除已存在的表
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + finalTableName);
            // 创建新表
            jdbcTemplate.execute(createSql.toString());
        } catch (Exception e) {
            workbook.close();
            return ApiResponse.error("创建表失败: " + e.getMessage());
        }

        // 导入数据
        int successCount = 0;
        int failedCount = 0;

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            try {
                StringBuilder insertSql = new StringBuilder("INSERT INTO ");
                insertSql.append(finalTableName).append(" (");

                List<String> colNames = new ArrayList<>();
                List<Object> values = new ArrayList<>();

                for (int i = 0; i < columnCount; i++) {
                    String colName = "COL_" + (char) ('A' + i);
                    Cell cell = row.getCell(i);
                    Object value = getCellValue(cell);

                    colNames.add(colName);
                    values.add(value);
                }

                insertSql.append(String.join(", ", colNames));
                insertSql.append(") VALUES (");
                insertSql.append(String.join(", ", Collections.nCopies(colNames.size(), "?")));
                insertSql.append(")");

                jdbcTemplate.update(insertSql.toString(), values.toArray());
                successCount++;
            } catch (Exception e) {
                failedCount++;
                Map<String, String> error = new HashMap<>();
                error.put("row", String.valueOf(rowIndex + 1));
                error.put("message", e.getMessage());
                errors.add(error);
            }
        }

        workbook.close();

        result.put("tableName", finalTableName);
        result.put("columnCount", columnCount);
        result.put("dataRowCount", dataRowCount);
        result.put("success", successCount);
        result.put("failed", failedCount);
        result.put("errors", errors);

        return ApiResponse.success(result);
    }

    /**
     * 下载模板 - 按指定列数生成模板
     */
    @GetMapping("/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadTemplate(
            @RequestParam(defaultValue = "5") int columnCount) throws IOException {

        try (Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("导入模板");

            // 表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnCount; i++) {
                Cell cell = headerRow.createCell(i);
                String colLetter = String.valueOf((char) ('A' + i));
                cell.setCellValue("列" + colLetter + " (COL_" + colLetter + ")");
            }

            // 示例数据行
            Row exampleRow = sheet.createRow(1);
            for (int i = 0; i < columnCount; i++) {
                Cell cell = exampleRow.createCell(i);
                String colLetter = String.valueOf((char) ('A' + i));
                cell.setCellValue("示例数据" + colLetter);
            }

            // 设置列宽
            for (int i = 0; i < columnCount; i++) {
                sheet.setColumnWidth(i, 4000);
            }

            workbook.write(out);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = URLEncoder.encode("导入模板_" + columnCount + "列.xlsx", StandardCharsets.UTF_8);
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
        }
    }

    /**
     * 删除表
     */
    @DeleteMapping("/table/{tableName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> dropTable(@PathVariable String tableName) {
        if (!tableName.startsWith("I_")) {
            return ApiResponse.error("只能删除I_开头的表");
        }
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
            return ApiResponse.success("表已删除: " + tableName);
        } catch (Exception e) {
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取表的所有数据
     */
    @GetMapping("/table/{tableName}/data")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        try {
            String sql = "SELECT * FROM " + tableName + " ORDER BY id DESC";
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                }
                if (cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) {
                    return (long) cell.getNumericCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN: return cell.getBooleanCellValue();
            case BLANK: return null;
            default: return null;
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER);
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }
}
