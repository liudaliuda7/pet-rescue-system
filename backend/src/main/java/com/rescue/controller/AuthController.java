package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.User;
import com.rescue.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private UserMapper userMapper;
    @Autowired private TokenStore tokenStore;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");
        if (username == null || password == null) return Result.error("用户名或密码不能为空");
        QueryWrapper<User> q = new QueryWrapper<>();
        q.eq("username", username).eq("password", password);
        if (role != null && !role.isEmpty()) q.eq("role", role);
        User user = userMapper.selectOne(q);
        if (user == null) return Result.error("账号或密码错误");
        if (user.getStatus() != null && user.getStatus() == 0) return Result.error("账号已被禁用");
        user.setPassword(null);
        String token = tokenStore.create(user);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.ok(data);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User u) {
        if (u.getUsername() == null || u.getPassword() == null) return Result.error("用户名或密码不能为空");
        QueryWrapper<User> q = new QueryWrapper<>();
        q.eq("username", u.getUsername());
        if (userMapper.selectCount(q) > 0) return Result.error("用户名已存在");
        u.setRole("user");
        u.setStatus(1);
        userMapper.insert(u);
        return Result.ok("注册成功");
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String auth,
                            @RequestHeader(value = "token", required = false) String tk) {
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        if (token == null) token = tk;
        tokenStore.remove(token);
        return Result.ok("已退出");
    }

    @GetMapping("/info")
    public Result<?> info() {
        User u = TokenStore.current();
        if (u != null) u.setPassword(null);
        return Result.ok(u);
    }
}
