package com.student.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DatabaseMapper {

    List<String> getAllTables();

    List<Map<String, Object>> getTableData(String tableName, int limit, int offset);

    Long getTableCount(String tableName);

    List<Map<String, Object>> getTableColumns(String tableName);

    List<Map<String, Object>> executeQuery(String sql);
}
