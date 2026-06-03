package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.AnimalClaim;
import com.rescue.entity.Animal;
import com.rescue.entity.User;
import com.rescue.entity.Message;
import com.rescue.mapper.AnimalClaimMapper;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.UserMapper;
import com.rescue.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animal-claim")
public class AnimalClaimController {
    @Autowired private AnimalClaimMapper mapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private MessageMapper messageMapper;

    private void enrich(List<AnimalClaim> list) {
        if (list == null || list.isEmpty()) return;
        Map<Long, Animal> animalMap = animalMapper.selectList(null).stream()
            .collect(Collectors.toMap(Animal::getId, a -> a, (a, b) -> a));
        Map<Long, User> userMap = userMapper.selectList(null).stream()
            .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
        for (AnimalClaim c : list) {
            Animal a = c.getAnimalId() != null ? animalMap.get(c.getAnimalId()) : null;
            if (a != null) {
                c.setAnimalName(a.getName());
                c.setAnimalImage(a.getImage());
            }
            User u = c.getUserId() != null ? userMap.get(c.getUserId()) : null;
            if (u != null) {
                c.setUserName(u.getName() != null ? u.getName() : u.getUsername());
                c.setUserPhone(u.getPhone());
            }
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long animalId) {
        Page<AnimalClaim> p = new Page<>(current, size);
        QueryWrapper<AnimalClaim> q = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) q.eq("status", status);
        if (animalId != null) q.eq("animal_id", animalId);
        User cur = TokenStore.current();
        if (cur != null && "user".equals(cur.getRole())) {
            q.eq("user_id", cur.getId());
        }
        q.orderByDesc("id");
        Page<AnimalClaim> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        AnimalClaim r = mapper.selectById(id);
        if (r != null) enrich(java.util.Collections.singletonList(r));
        return Result.ok(r);
    }

    @GetMapping("/my")
    public Result<?> my(@RequestParam Long animalId) {
        User cur = TokenStore.current();
        if (cur == null) return Result.ok(null);
        QueryWrapper<AnimalClaim> q = new QueryWrapper<>();
        q.eq("animal_id", animalId).eq("user_id", cur.getId()).orderByDesc("id").last("limit 1");
        AnimalClaim r = mapper.selectOne(q);
        return Result.ok(r);
    }

    @PostMapping
    public Result<?> add(@RequestBody AnimalClaim r) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error("请先登录");
        r.setUserId(cur.getId());
        if (r.getStatus() == null) r.setStatus("pending");
        mapper.insert(r);
        return Result.ok("认领申请已提交");
    }

    @PutMapping("/audit")
    public Result<?> audit(@RequestBody AnimalClaim r) {
        AnimalClaim db = mapper.selectById(r.getId());
        if (db == null) return Result.error("记录不存在");
        if (!"pending".equals(db.getStatus())) return Result.error("只有待审核状态的申请才能审核");
        db.setStatus(r.getStatus());
        db.setRemark(r.getRemark());
        mapper.updateById(db);
        if ("approved".equals(r.getStatus()) && db.getAnimalId() != null) {
            Animal a = animalMapper.selectById(db.getAnimalId());
            if (a != null) { a.setStatus("claimed"); animalMapper.updateById(a); }
        }
        if (db.getUserId() != null) {
            Message msg = new Message();
            msg.setUserId(db.getUserId());
            msg.setIsRead(0);
            Animal a = db.getAnimalId() != null ? animalMapper.selectById(db.getAnimalId()) : null;
            String animalName = a != null ? a.getName() : "动物";
            if ("approved".equals(r.getStatus())) {
                msg.setTitle("认领申请已通过");
                msg.setContent("恭喜！您对「" + animalName + "」的认领申请已通过审核。" + (r.getRemark() != null ? "备注：" + r.getRemark() : ""));
            } else {
                msg.setTitle("认领申请未通过");
                msg.setContent("很遗憾，您对「" + animalName + "」的认领申请未通过审核。" + (r.getRemark() != null ? "原因：" + r.getRemark() : ""));
            }
            messageMapper.insert(msg);
        }
        return Result.ok("审核完成");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
