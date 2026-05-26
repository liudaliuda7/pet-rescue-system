package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String type) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        Page<Message> p = new Page<>(current, size);
        QueryWrapper<Message> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId());
        if (type != null && !type.isEmpty()) q.eq("type", type);
        q.orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }

    @GetMapping("/unread-count")
    public Result<?> unreadCount() {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        Long count = mapper.selectCount(new QueryWrapper<Message>()
            .eq("user_id", cur.getId()).eq("is_read", 0));
        return Result.ok(count);
    }

    @PutMapping("/read/{id}")
    public Result<?> read(@PathVariable Long id) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        Message msg = mapper.selectById(id);
        if (msg == null || !msg.getUserId().equals(cur.getId())) return Result.error("无权操作");
        msg.setIsRead(1);
        mapper.updateById(msg);
        return Result.ok("已读");
    }

    @PutMapping("/read-all")
    public Result<?> readAll() {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("未登录");
        Message entity = new Message();
        entity.setIsRead(1);
        mapper.update(entity, new UpdateWrapper<Message>()
            .eq("user_id", cur.getId()).eq("is_read", 0));
        return Result.ok("全部已读");
    }

    @GetMapping("/admin/page")
    public Result<?> adminPage(@RequestParam(defaultValue = "1") int current,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) Long userId,
                               @RequestParam(required = false) Integer isRead) {
        Page<Message> p = new Page<>(current, size);
        QueryWrapper<Message> q = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) q.eq("type", type);
        if (userId != null) q.eq("user_id", userId);
        if (isRead != null) q.eq("is_read", isRead);
        q.orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }
}
