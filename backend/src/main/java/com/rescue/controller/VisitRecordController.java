package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Adoption;
import com.rescue.entity.Animal;
import com.rescue.entity.User;
import com.rescue.entity.VisitRecord;
import com.rescue.mapper.AdoptionMapper;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.UserMapper;
import com.rescue.mapper.VisitRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/visit")
public class VisitRecordController {
    @Autowired private VisitRecordMapper mapper;
    @Autowired private AdoptionMapper adoptionMapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private UserMapper userMapper;

    private void enrich(List<VisitRecord> list) {
        if (list == null || list.isEmpty()) return;
        List<Adoption> adoptions = adoptionMapper.selectList(null);
        Map<Long, Adoption> adoptMap = adoptions.stream().collect(Collectors.toMap(Adoption::getId, a -> a, (a, b) -> a));
        Map<Long, String> animalMap = animalMapper.selectList(null).stream()
            .collect(Collectors.toMap(Animal::getId, Animal::getName, (a, b) -> a));
        Map<Long, String> userMap = userMapper.selectList(null).stream()
            .collect(Collectors.toMap(User::getId, u -> u.getName() != null ? u.getName() : u.getUsername(), (a, b) -> a));
        for (VisitRecord v : list) {
            Adoption a = v.getAdoptionId() == null ? null : adoptMap.get(v.getAdoptionId());
            if (a != null) {
                if (a.getAnimalId() != null) v.setAnimalName(animalMap.get(a.getAnimalId()));
                if (a.getUserId() != null) v.setAdopterName(userMap.get(a.getUserId()));
            }
        }
    }

    @GetMapping("/count")
    public Result<?> countByAdoption(@RequestParam Long adoptionId) {
        QueryWrapper<VisitRecord> q = new QueryWrapper<>();
        q.eq("adoption_id", adoptionId);
        return Result.ok(mapper.selectCount(q));
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long adoptionId) {
        Page<VisitRecord> p = new Page<>(current, size);
        QueryWrapper<VisitRecord> q = new QueryWrapper<>();
        if (adoptionId != null) q.eq("adoption_id", adoptionId);
        q.orderByDesc("id");
        Page<VisitRecord> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @PostMapping
    public Result<?> add(@RequestBody VisitRecord v) {
        User cur = TokenStore.current();
        if (cur != null && v.getVisitor() == null) v.setVisitor(cur.getName());
        mapper.insert(v);
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody VisitRecord v) {
        mapper.updateById(v);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
