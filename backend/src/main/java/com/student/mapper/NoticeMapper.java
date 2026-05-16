package com.student.mapper;

import com.student.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NoticeMapper {
    Notice findById(@Param("id") Long id);
    List<Notice> findAll();
    List<Notice> findByStatus(@Param("status") Integer status);
    int insert(Notice notice);
    int update(Notice notice);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
}
