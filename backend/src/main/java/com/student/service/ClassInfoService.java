package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.entity.ClassInfo;
import com.student.mapper.ClassInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassInfoService {

    private final ClassInfoMapper classMapper;

    public ClassInfo findById(Long id) {
        return classMapper.findById(id);
    }

    public List<ClassInfo> findAll() {
        return classMapper.findAll();
    }

    public List<ClassInfo> findByGrade(String grade) {
        return classMapper.findByGrade(grade);
    }

    public PageInfo<ClassInfo> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ClassInfo> list = classMapper.findAll();
        return new PageInfo<>(list);
    }

    @Transactional
    public void insert(ClassInfo classInfo) {
        classMapper.insert(classInfo);
    }

    @Transactional
    public void update(ClassInfo classInfo) {
        classMapper.update(classInfo);
    }

    @Transactional
    public void deleteById(Long id) {
        classMapper.deleteById(id);
    }
}
