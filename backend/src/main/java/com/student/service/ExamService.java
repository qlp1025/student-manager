package com.student.service;

import com.student.mapper.ExamMapper;
import com.student.entity.Exam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamMapper examMapper;

    public List<Exam> findAll() {
        return examMapper.findAll();
    }

    public Exam findById(Long id) {
        return examMapper.findById(id);
    }

    public void insert(Exam exam) {
        examMapper.insert(exam);
    }

    public void update(Exam exam) {
        examMapper.update(exam);
    }

    public void deleteById(Long id) {
        examMapper.deleteById(id);
    }
}
