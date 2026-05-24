package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Animal;
import com.rescue.entity.HealthRecord;
import com.rescue.entity.User;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.HealthRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/health")
public class HealthRecordController {
    @Autowired private HealthRecordMapper mapper;
    @Autowired private AnimalMapper animalMapper;

    private void enrich(List<HealthRecord> list) {
        if (list == null || list.isEmpty()) return;
        Map<Long, String> animalMap = animalMapper.selectList(null).stream()
            .collect(Collectors.toMap(Animal::getId, Animal::getName, (a, b) -> a));
        for (HealthRecord r : list)
            if (r.getAnimalId() != null) r.setAnimalName(animalMap.get(r.getAnimalId()));
    }

    @GetMapping("/public/list")
    public Result<?> publicList(@RequestParam Long animalId) {
        QueryWrapper<HealthRecord> q = new QueryWrapper<>();
        q.eq("animal_id", animalId).orderByDesc("record_date");
        List<HealthRecord> list = mapper.selectList(q);
        return Result.ok(list);
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long animalId) {
        Page<HealthRecord> p = new Page<>(current, size);
        QueryWrapper<HealthRecord> q = new QueryWrapper<>();
        if (animalId != null) q.eq("animal_id", animalId);
        User cur = TokenStore.current();
        if (cur != null && "station".equals(cur.getRole())) {
            List<Long> ids = animalMapper.selectList(new QueryWrapper<Animal>().eq("station_id", cur.getStationId()))
                .stream().map(Animal::getId).collect(Collectors.toList());
            if (ids.isEmpty()) ids.add(-1L);
            q.in("animal_id", ids);
        }
        q.orderByDesc("id");
        Page<HealthRecord> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @PostMapping
    public Result<?> add(@RequestBody HealthRecord r) {
        mapper.insert(r);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody HealthRecord r) {
        mapper.updateById(r);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
