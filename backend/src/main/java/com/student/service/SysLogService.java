package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.entity.SysLog;
import com.student.mapper.SysLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysLogService {

    private final SysLogMapper logMapper;

    public void insert(SysLog log) {
        logMapper.insert(log);
    }

    public List<SysLog> findAll() {
        return logMapper.findAll();
    }

    public List<SysLog> findByUsername(String username) {
        return logMapper.findByUsername(username);
    }

    public PageInfo<SysLog> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysLog> list = logMapper.findAll();
        return new PageInfo<>(list);
    }

    public void deleteById(Long id) {
        logMapper.deleteById(id);
    }
}
