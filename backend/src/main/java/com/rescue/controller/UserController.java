package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.User;
import com.rescue.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired private UserMapper userMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String role) {
        Page<User> p = new Page<>(current, size);
        QueryWrapper<User> q = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) q.like("username", username);
        if (role != null && !role.isEmpty()) q.eq("role", role);
        q.orderByDesc("id");
        Page<User> res = userMapper.selectPage(p, q);
        res.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(res);
    }

    @GetMapping("/{id:\\d+}")
    public Result<?> get(@PathVariable Long id) {
        User u = userMapper.selectById(id);
        if (u != null) u.setPassword(null);
        return Result.ok(u);
    }

    @PostMapping
    public Result<?> add(@RequestBody User u) {
        QueryWrapper<User> q = new QueryWrapper<>();
        q.eq("username", u.getUsername());
        if (userMapper.selectCount(q) > 0) return Result.error("用户名已存在");
        if (u.getStatus() == null) u.setStatus(1);
        if (u.getPassword() == null) u.setPassword("123456");
        userMapper.insert(u);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody User u) {
        if (u.getPassword() != null && u.getPassword().isEmpty()) u.setPassword(null);
        userMapper.updateById(u);
        return Result.ok("更新成功");
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User u) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        u.setId(cur.getId());
        u.setRole(null);
        u.setStatus(null);
        if (u.getPassword() != null && u.getPassword().isEmpty()) u.setPassword(null);
        userMapper.updateById(u);
        return Result.ok("已更新");
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody java.util.Map<String, String> body) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        User db = userMapper.selectById(cur.getId());
        if (!db.getPassword().equals(body.get("oldPassword"))) return Result.error("原密码错误");
        db.setPassword(body.get("newPassword"));
        userMapper.updateById(db);
        return Result.ok("密码已修改");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
