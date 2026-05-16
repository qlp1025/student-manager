package com.student.service;

import com.student.dto.LoginDTO;
import com.student.dto.LoginResponse;
import com.student.entity.SysUser;
import com.student.mapper.SysUserMapper;
import com.student.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginDTO loginDTO) {
        SysUser user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
//        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
//            throw new RuntimeException("密码错误");
//        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRoleKey());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRoleKey(user.getRoleKey());
        response.setAvatar(user.getAvatar());

        return response;
    }
}
