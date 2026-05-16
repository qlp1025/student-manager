package com.student.mapper;

import com.student.entity.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SubjectMapper {
    Subject findById(@Param("id") Long id);
    List<Subject> findAll();
    int insert(Subject subject);
    int update(Subject subject);
    int deleteById(@Param("id") Long id);
}
