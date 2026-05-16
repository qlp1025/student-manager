package com.student.mapper;

import com.student.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysLogMapper {
    int insert(SysLog log);
    List<SysLog> findAll();
    List<SysLog> findByUsername(@Param("username") String username);
    int deleteById(@Param("id") Long id);
}
