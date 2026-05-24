package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Animal;
import com.rescue.entity.AnimalType;
import com.rescue.entity.RescueStation;
import com.rescue.entity.User;
import com.rescue.entity.HealthRecord;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.AnimalTypeMapper;
import com.rescue.mapper.HealthRecordMapper;
import com.rescue.mapper.RescueStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animal")
public class AnimalController {
    @Autowired private AnimalMapper mapper;
    @Autowired private AnimalTypeMapper typeMapper;
    @Autowired private RescueStationMapper stationMapper;
    @Autowired private HealthRecordMapper healthRecordMapper;

    private void enrich(List<Animal> list) {
        if (list == null || list.isEmpty()) return;
        Map<Long, String> typeMap = typeMapper.selectList(null).stream()
            .collect(Collectors.toMap(AnimalType::getId, AnimalType::getName, (a, b) -> a));
        Map<Long, String> stMap = stationMapper.selectList(null).stream()
            .collect(Collectors.toMap(RescueStation::getId, RescueStation::getName, (a, b) -> a));
        for (Animal a : list) {
            if (a.getTypeId() != null) a.setTypeName(typeMap.get(a.getTypeId()));
            if (a.getStationId() != null) a.setStationName(stMap.get(a.getStationId()));
        }
    }

    private void fillHealthCount(List<Animal> list) {
        if (list == null || list.isEmpty()) return;
        List<Long> animalIds = list.stream().map(Animal::getId).collect(Collectors.toList());
        QueryWrapper<HealthRecord> q = new QueryWrapper<>();
        q.in("animal_id", animalIds).select("animal_id", "count(*) as id").groupBy("animal_id");
        List<Map<String, Object>> counts = healthRecordMapper.selectMaps(q);
        Map<Long, Integer> countMap = new HashMap<>();
        for (Map<String, Object> row : counts) {
            Long animalId = ((Number) row.get("animal_id")).longValue();
            Integer count = ((Number) row.get("id")).intValue();
            countMap.put(animalId, count);
        }
        for (Animal a : list) {
            a.setHealthCount(countMap.getOrDefault(a.getId(), 0));
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) Long typeId,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long stationId) {
        Page<Animal> p = new Page<>(current, size);
        QueryWrapper<Animal> q = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) q.like("name", name);
        if (typeId != null) q.eq("type_id", typeId);
        if (status != null && !status.isEmpty()) q.eq("status", status);
        User cur = TokenStore.current();
        if (cur != null && "station".equals(cur.getRole())) q.eq("station_id", cur.getStationId());
        else if (stationId != null) q.eq("station_id", stationId);
        q.orderByDesc("id");
        Page<Animal> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        fillHealthCount(res.getRecords());
        return Result.ok(res);
    }

    @GetMapping("/public/page")
    public Result<?> publicPage(@RequestParam(defaultValue = "1") int current,
                                @RequestParam(defaultValue = "12") int size,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Long typeId) {
        Page<Animal> p = new Page<>(current, size);
        QueryWrapper<Animal> q = new QueryWrapper<>();
        q.eq("status", "available");
        if (name != null && !name.isEmpty()) q.like("name", name);
        if (typeId != null) q.eq("type_id", typeId);
        q.orderByDesc("id");
        Page<Animal> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @GetMapping("/public/{id}")
    public Result<?> publicGet(@PathVariable Long id) {
        Animal a = mapper.selectById(id);
        if (a != null) enrich(java.util.Collections.singletonList(a));
        return Result.ok(a);
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        Animal a = mapper.selectById(id);
        if (a != null) enrich(java.util.Collections.singletonList(a));
        return Result.ok(a);
    }

    @GetMapping("/stats")
    public Result<?> stats() {
        Map<String, Object> m = new HashMap<>();
        m.put("total", mapper.selectCount(null));
        m.put("available", mapper.selectCount(new QueryWrapper<Animal>().eq("status", "available")));
        m.put("adopted", mapper.selectCount(new QueryWrapper<Animal>().eq("status", "adopted")));
        m.put("treatment", mapper.selectCount(new QueryWrapper<Animal>().eq("status", "treatment")));
        return Result.ok(m);
    }

    @PostMapping
    public Result<?> add(@RequestBody Animal a) {
        if (a.getStatus() == null) a.setStatus("available");
        User cur = TokenStore.current();
        if (cur != null && "station".equals(cur.getRole())) a.setStationId(cur.getStationId());
        mapper.insert(a);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody Animal a) {
        mapper.updateById(a);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
