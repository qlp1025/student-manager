package com.student.mapper;

import com.student.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper {
    SysUser findByUsername(@Param("username") String username);
    SysUser findById(@Param("id") Long id);
    List<SysUser> findAll();
    List<SysUser> findByRoleId(@Param("roleId") Long roleId);
    int insert(SysUser user);
    int update(SysUser user);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    int deleteById(@Param("id") Long id);
    int batchDelete(@Param("ids") List<Long> ids);
}
