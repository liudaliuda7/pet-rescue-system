package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.entity.Adoption;
import com.rescue.entity.Animal;
import com.rescue.entity.HelpRequest;
import com.rescue.entity.RescueStation;
import com.rescue.mapper.AdoptionMapper;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.HelpRequestMapper;
import com.rescue.mapper.RescueStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/station")
public class StationController {
    @Autowired private RescueStationMapper mapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private AdoptionMapper adoptionMapper;
    @Autowired private HelpRequestMapper helpRequestMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String sortField,
                          @RequestParam(required = false) String sortOrder) {
        Page<RescueStation> p = new Page<>(current, size);
        QueryWrapper<RescueStation> q = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) q.like("name", name);
        q.orderByDesc("id");
        Page<RescueStation> res = mapper.selectPage(p, q);

        List<Map<String, Object>> enriched = new ArrayList<>();
        if (res.getRecords() != null && !res.getRecords().isEmpty()) {
            List<Long> ids = res.getRecords().stream().map(RescueStation::getId).collect(Collectors.toList());

            Map<Long, Long> animalTotalMap = new HashMap<>();
            Map<Long, Long> animalAvailableMap = new HashMap<>();
            Map<Long, Long> adoptedMap = new HashMap<>();
            Map<Long, Long> helpTotalMap = new HashMap<>();

            List<Map<String, Object>> animalCounts = animalMapper.selectMaps(
                new QueryWrapper<Animal>().select("station_id", "count(*) as cnt").in("station_id", ids).groupBy("station_id"));
            for (Map<String, Object> m : animalCounts) {
                Long sid = ((Number) (m.get("station_id") != null ? m.get("station_id") : m.get("STATION_ID"))).longValue();
                animalTotalMap.put(sid, ((Number) (m.get("cnt") != null ? m.get("cnt") : m.get("CNT"))).longValue());
            }

            List<Map<String, Object>> availCounts = animalMapper.selectMaps(
                new QueryWrapper<Animal>().select("station_id", "count(*) as cnt").in("station_id", ids).eq("status", "available").groupBy("station_id"));
            for (Map<String, Object> m : availCounts) {
                Long sid = ((Number) (m.get("station_id") != null ? m.get("station_id") : m.get("STATION_ID"))).longValue();
                animalAvailableMap.put(sid, ((Number) (m.get("cnt") != null ? m.get("cnt") : m.get("CNT"))).longValue());
            }

            List<Map<String, Object>> adoptedCounts = animalMapper.selectMaps(
                new QueryWrapper<Animal>().select("station_id", "count(*) as cnt").in("station_id", ids).eq("status", "adopted").groupBy("station_id"));
            for (Map<String, Object> m : adoptedCounts) {
                Long sid = ((Number) (m.get("station_id") != null ? m.get("station_id") : m.get("STATION_ID"))).longValue();
                adoptedMap.put(sid, ((Number) (m.get("cnt") != null ? m.get("cnt") : m.get("CNT"))).longValue());
            }

            List<Map<String, Object>> helpCounts = helpRequestMapper.selectMaps(
                new QueryWrapper<HelpRequest>().select("station_id", "count(*) as cnt").in("station_id", ids).groupBy("station_id"));
            for (Map<String, Object> m : helpCounts) {
                Long sid = ((Number) (m.get("station_id") != null ? m.get("station_id") : m.get("STATION_ID"))).longValue();
                helpTotalMap.put(sid, ((Number) (m.get("cnt") != null ? m.get("cnt") : m.get("CNT"))).longValue());
            }

            for (RescueStation s : res.getRecords()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", s.getId());
                row.put("name", s.getName());
                row.put("address", s.getAddress());
                row.put("phone", s.getPhone());
                row.put("contact", s.getContact());
                row.put("description", s.getDescription());
                row.put("image", s.getImage());
                row.put("createTime", s.getCreateTime());
                row.put("animalTotal", animalTotalMap.getOrDefault(s.getId(), 0L));
                row.put("animalAvailable", animalAvailableMap.getOrDefault(s.getId(), 0L));
                row.put("adoptedTotal", adoptedMap.getOrDefault(s.getId(), 0L));
                row.put("helpTotal", helpTotalMap.getOrDefault(s.getId(), 0L));
                enriched.add(row);
            }

            if (sortField != null && !sortField.isEmpty()) {
                boolean asc = "asc".equalsIgnoreCase(sortOrder);
                enriched.sort((a, b) -> {
                    long va = ((Number) a.getOrDefault(sortField, 0L)).longValue();
                    long vb = ((Number) b.getOrDefault(sortField, 0L)).longValue();
                    return asc ? Long.compare(va, vb) : Long.compare(vb, va);
                });
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", enriched);
        result.put("total", res.getTotal());
        return Result.ok(result);
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(mapper.selectList(null));
    }

    @GetMapping("/public/list")
    public Result<?> publicList() {
        return Result.ok(mapper.selectList(null));
    }

    @GetMapping("/public/{id}")
    public Result<?> publicGet(@PathVariable Long id) {
        return Result.ok(mapper.selectById(id));
    }

    @GetMapping("/{id}")
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
