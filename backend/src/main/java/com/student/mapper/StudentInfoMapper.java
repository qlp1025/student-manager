package com.student.mapper;

import com.student.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StudentInfoMapper {
    StudentInfo findById(@Param("id") Long id);
    StudentInfo findByStudentNo(@Param("studentNo") String studentNo);
    List<StudentInfo> findAll();
    List<StudentInfo> findByCondition(@Param("keyword") String keyword, @Param("classId") Long classId, @Param("status") Integer status);
    List<StudentInfo> findByClassId(@Param("classId") Long classId);
    int insert(StudentInfo student);
    int update(StudentInfo student);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
    int batchDelete(@Param("ids") List<Long> ids);
    int count();
    int countByClassId(@Param("classId") Long classId);
}
