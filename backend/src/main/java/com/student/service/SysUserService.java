package com.student.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.entity.SysUser;
import com.student.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUser findById(Long id) {
        return userMapper.findById(id);
    }

    public SysUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public List<SysUser> findAll() {
        return userMapper.findAll();
    }

    public List<SysUser> findByRoleId(Long roleId) {
        return userMapper.findByRoleId(roleId);
    }

    public PageInfo<SysUser> findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = userMapper.findAll();
        return new PageInfo<>(list);
    }

    public void insert(SysUser user) {
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.insert(user);
    }

    public void update(SysUser user) {
        userMapper.update(user);
    }

    public void updatePassword(Long id, String password) {
        userMapper.updatePassword(id, passwordEncoder.encode(password));
    }

    public void resetPassword(Long id) {
        userMapper.updatePassword(id, passwordEncoder.encode("123456"));
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    public void batchDelete(List<Long> ids) {
        userMapper.batchDelete(ids);
    }
}
