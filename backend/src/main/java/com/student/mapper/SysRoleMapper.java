package com.student.mapper;

import com.student.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper {
    SysRole findById(@Param("id") Long id);
    SysRole findByRoleKey(@Param("roleKey") String roleKey);
    List<SysRole> findAll();
    int insert(SysRole role);
    int update(SysRole role);
    int deleteById(@Param("id") Long id);
}
