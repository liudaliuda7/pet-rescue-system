package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.entity.RescueStation;
import com.rescue.mapper.RescueStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/station")
public class StationController {
    @Autowired private RescueStationMapper mapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String name) {
        Page<RescueStation> p = new Page<>(current, size);
        QueryWrapper<RescueStation> q = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) q.like("name", name);
        q.orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(mapper.selectList(null));
    }

    @GetMapping("/public/list")
    public Result<?> publicList() {
        return Result.ok(mapper.selectList(null));
    }

    @GetMapping("/{id:\\d+}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(mapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody RescueStation s) {
        mapper.insert(s);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody RescueStation s) {
        mapper.updateById(s);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
