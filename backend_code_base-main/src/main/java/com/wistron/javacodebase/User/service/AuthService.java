package com.wistron.javacodebase.User.service;

import com.wistron.javacodebase.User.dto.LoginRequest;
import com.wistron.javacodebase.User.dto.LoginResponse;
import com.wistron.javacodebase.User.entity.User;
import com.wistron.javacodebase.User.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userMapper.findByAccount(request.getAccount());

        if (user == null) {
            return new LoginResponse(false, "帳號不存在", null, null, null);
        }

        if (user.getEnabled() == null || !user.getEnabled()) {
            return new LoginResponse(false, "帳號已停用", null, null, null);
        }

        if (!request.getPassword().equals(user.getPassword())) {
            return new LoginResponse(false, "密碼錯誤", null, null, null);
        }

        return new LoginResponse(
                true,
                "登入成功",
                user.getId(),
                user.getAccount(),
                user.getName()
        );
    }
}