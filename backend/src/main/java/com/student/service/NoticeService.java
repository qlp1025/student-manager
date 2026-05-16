package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.entity.Notice;
import com.student.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public Notice findById(Long id) {
        return noticeMapper.findById(id);
    }

    public List<Notice> findAll() {
        return noticeMapper.findAll();
    }

    public List<Notice> findByStatus(Integer status) {
        return noticeMapper.findByStatus(status);
    }

    public PageInfo<Notice> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> list = noticeMapper.findAll();
        return new PageInfo<>(list);
    }

    @Transactional
    public void insert(Notice notice) {
        if (notice.getPublishTime() == null) {
            notice.setPublishTime(LocalDateTime.now());
        }
        noticeMapper.insert(notice);
    }

    @Transactional
    public void update(Notice notice) {
        noticeMapper.update(notice);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        noticeMapper.updateStatus(id, status);
    }

    @Transactional
    public void deleteById(Long id) {
        noticeMapper.deleteById(id);
    }
}
