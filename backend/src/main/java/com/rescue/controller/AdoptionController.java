package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Adoption;
import com.rescue.entity.Animal;
import com.rescue.entity.User;
import com.rescue.mapper.AdoptionMapper;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.RescueStationMapper;
import com.rescue.mapper.UserMapper;
import com.rescue.mapper.VisitRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rescue.entity.RescueStation;
import com.rescue.entity.VisitRecord;

@RestController
@RequestMapping("/adoption")
public class AdoptionController {
    @Autowired private AdoptionMapper mapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private RescueStationMapper stationMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private VisitRecordMapper visitRecordMapper;

    private void enrich(List<Adoption> list) {
        if (list == null || list.isEmpty()) return;
        Map<Long, Animal> animalMap = animalMapper.selectList(null).stream()
            .collect(Collectors.toMap(Animal::getId, a -> a, (a, b) -> a));
        Map<Long, String> userMap = userMapper.selectList(null).stream()
            .collect(Collectors.toMap(User::getId, u -> u.getName() != null ? u.getName() : u.getUsername(), (a, b) -> a));
        Map<Long, String> stationNameMap = stationMapper.selectList(null).stream()
            .collect(Collectors.toMap(RescueStation::getId, RescueStation::getName, (a, b) -> a));
        QueryWrapper<VisitRecord> vcq = new QueryWrapper<>();
        vcq.select("adoption_id", "count(*) as cnt").groupBy("adoption_id");
        Map<Long, Integer> visitCountMap = visitRecordMapper.selectMaps(vcq).stream()
            .collect(Collectors.toMap(
                m -> ((Number) (m.get("adoption_id") != null ? m.get("adoption_id") : m.get("ADOPTION_ID"))).longValue(),
                m -> ((Number) (m.get("cnt") != null ? m.get("cnt") : m.get("CNT"))).intValue(),
                (a, b) -> a));
        for (Adoption r : list) {
            Animal a = r.getAnimalId() == null ? null : animalMap.get(r.getAnimalId());
            if (a != null) {
                r.setAnimalName(a.getName());
                r.setAnimalImage(a.getImage());
                r.setStationId(a.getStationId());
                if (a.getStationId() != null) {
                    r.setStationName(stationNameMap.get(a.getStationId()));
                }
            }
            if (r.getUserId() != null) r.setUserName(userMap.get(r.getUserId()));
            r.setVisitCount(visitCountMap.getOrDefault(r.getId(), 0));
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status) {
        Page<Adoption> p = new Page<>(current, size);
        QueryWrapper<Adoption> q = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) q.eq("status", status);
        User cur = TokenStore.current();
        if (cur != null) {
            if ("user".equals(cur.getRole())) q.eq("user_id", cur.getId());
        }
        q.orderByDesc("id");
        Page<Adoption> res = mapper.selectPage(p, q);
        if (cur != null && "station".equals(cur.getRole())) {
            enrich(res.getRecords());
            res.setRecords(res.getRecords().stream()
                .filter(a -> a.getStationId() != null && a.getStationId().equals(cur.getStationId()))
                .collect(Collectors.toList()));
        } else {
            enrich(res.getRecords());
        }
        return Result.ok(res);
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        Adoption r = mapper.selectById(id);
        if (r != null) enrich(java.util.Collections.singletonList(r));
        return Result.ok(r);
    }

    @PostMapping
    public Result<?> add(@RequestBody Adoption r) {
        User cur = TokenStore.current();
        if (cur != null) r.setUserId(cur.getId());
        if (r.getStatus() == null) r.setStatus("pending");
        mapper.insert(r);
        return Result.ok("申请已提交");
    }

    @PutMapping("/audit")
    public Result<?> audit(@RequestBody Adoption r) {
        Adoption db = mapper.selectById(r.getId());
        if (db == null) return Result.error("记录不存在");
        db.setStatus(r.getStatus());
        db.setRemark(r.getRemark());
        mapper.updateById(db);
        if ("approved".equals(r.getStatus()) && db.getAnimalId() != null) {
            Animal a = animalMapper.selectById(db.getAnimalId());
            if (a != null) { a.setStatus("adopted"); animalMapper.updateById(a); }
        }
        return Result.ok("审核完成");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
