package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Message;
import com.rescue.entity.User;
import com.rescue.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired private MessageMapper mapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        Page<Message> p = new Page<>(current, size);
        QueryWrapper<Message> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }

    @GetMapping("/unread")
    public Result<?> unread() {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        QueryWrapper<Message> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).eq("is_read", 0);
        return Result.ok(mapper.selectCount(q));
    }

    @PutMapping("/read/{id}")
    public Result<?> read(@PathVariable Long id) {
        Message m = mapper.selectById(id);
        if (m != null) { m.setIsRead(1); mapper.updateById(m); }
        return Result.ok("已读");
    }

    @PutMapping("/readAll")
    public Result<?> readAll() {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        Message upd = new Message();
        upd.setIsRead(1);
        QueryWrapper<Message> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).eq("is_read", 0);
        mapper.update(upd, q);
        return Result.ok("全部已读");
    }
}
