package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.entity.AnimalType;
import com.rescue.mapper.AnimalTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animal-type")
public class AnimalTypeController {
    @Autowired private AnimalTypeMapper mapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String name) {
        Page<AnimalType> p = new Page<>(current, size);
        QueryWrapper<AnimalType> q = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) q.like("name", name);
        q.orderByDesc("id");
        return Result.ok(mapper.selectPage(p, q));
    }

    @GetMapping("/list")
    public Result<?> list() { return Result.ok(mapper.selectList(null)); }

    @GetMapping("/public/list")
    public Result<?> publicList() { return Result.ok(mapper.selectList(null)); }

    @PostMapping
    public Result<?> add(@RequestBody AnimalType t) { mapper.insert(t); return Result.ok("添加成功"); }
    @PutMapping
    public Result<?> update(@RequestBody AnimalType t) { mapper.updateById(t); return Result.ok("更新成功"); }
    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) { mapper.deleteById(id); return Result.ok("删除成功"); }
}
