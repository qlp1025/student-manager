package com.student.mapper;

import com.student.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ClassInfoMapper {
    ClassInfo findById(@Param("id") Long id);
    List<ClassInfo> findAll();
    List<ClassInfo> findByGrade(@Param("grade") String grade);
    int insert(ClassInfo classInfo);
    int update(ClassInfo classInfo);
    int deleteById(@Param("id") Long id);
    int updateStudentCount(@Param("id") Long id, @Param("count") Integer count);
}
