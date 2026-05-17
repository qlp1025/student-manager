package com.student.mapper;

import com.student.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExamMapper {
    List<Exam> findAll();
    Exam findById(Long id);
    int insert(Exam exam);
    int update(Exam exam);
    int deleteById(Long id);
}
