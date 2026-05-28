package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.entity.Notice;
import com.rescue.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired private NoticeMapper mapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String title) {
        Page<Notice> p = new Page<>(current, size);
        QueryWrapper<Notice> q = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) q.like("title", title);
        q.orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }

    @GetMapping("/public/page")
    public Result<?> publicPage(@RequestParam(defaultValue = "1") int current,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String title) {
        Page<Notice> p = new Page<>(current, size);
        QueryWrapper<Notice> q = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) q.like("title", title);
        q.orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }

    @GetMapping("/public/latest")
    public Result<?> latest() {
        QueryWrapper<Notice> q = new QueryWrapper<>();
        q.orderByDesc("id").last("limit 5");
        return Result.ok(mapper.selectList(q));
    }

    @GetMapping("/public/{id:\\d+}")
    public Result<?> publicGet(@PathVariable Long id) { return Result.ok(mapper.selectById(id)); }

    @GetMapping("/{id:\\d+}")
    public Result<?> get(@PathVariable Long id) { return Result.ok(mapper.selectById(id)); }

    @PostMapping
    public Result<?> add(@RequestBody Notice n) { mapper.insert(n); return Result.ok("添加成功"); }

    @PutMapping
    public Result<?> update(@RequestBody Notice n) { mapper.updateById(n); return Result.ok("更新成功"); }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) { mapper.deleteById(id); return Result.ok("删除成功"); }
}
