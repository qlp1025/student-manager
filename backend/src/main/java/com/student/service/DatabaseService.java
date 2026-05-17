package com.student.service;

import com.student.mapper.DatabaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final DatabaseMapper databaseMapper;

    public List<String> getAllTables() {
        return databaseMapper.getAllTables();
    }

    public Map<String, Object> getTableData(String tableName, int limit, int offset) {
        List<Map<String, Object>> columns = databaseMapper.getTableColumns(tableName);
        List<Map<String, Object>> rows = databaseMapper.getTableData(tableName, limit, offset);
        Long total = databaseMapper.getTableCount(tableName);

        return Map.of(
            "columns", columns,
            "rows", rows,
            "total", total
        );
    }

    public Long getTableCount(String tableName) {
        return databaseMapper.getTableCount(tableName);
    }

    public List<Map<String, Object>> getTableColumns(String tableName) {
        return databaseMapper.getTableColumns(tableName);
    }

    public List<Map<String, Object>> executeQuery(String sql) {
        String upperSql = sql.toUpperCase();
        if (!upperSql.startsWith("SELECT")) {
            throw new SecurityException("只允许执行SELECT查询语句");
        }
        if (upperSql.contains("DROP") || upperSql.contains("DELETE") ||
            upperSql.contains("UPDATE") || upperSql.contains("INSERT") ||
            upperSql.contains("ALTER") || upperSql.contains("CREATE") ||
            upperSql.contains("TRUNCATE") || upperSql.contains("GRANT") ||
            upperSql.contains("REVOKE")) {
            throw new SecurityException("禁止执行危险SQL语句");
        }
        return databaseMapper.executeQuery(sql);
    }
}
