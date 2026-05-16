package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.entity.Subject;
import com.student.mapper.SubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectMapper subjectMapper;

    public Subject findById(Long id) {
        return subjectMapper.findById(id);
    }

    public List<Subject> findAll() {
        return subjectMapper.findAll();
    }

    public PageInfo<Subject> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> list = subjectMapper.findAll();
        return new PageInfo<>(list);
    }

    public void insert(Subject subject) {
        subjectMapper.insert(subject);
    }

    public void update(Subject subject) {
        subjectMapper.update(subject);
    }

    public void deleteById(Long id) {
        subjectMapper.deleteById(id);
    }
}
