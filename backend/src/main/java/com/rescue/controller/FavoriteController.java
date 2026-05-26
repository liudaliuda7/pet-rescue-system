package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Animal;
import com.rescue.entity.Favorite;
import com.rescue.entity.User;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired private FavoriteMapper mapper;
    @Autowired private AnimalMapper animalMapper;

    @PostMapping("/toggle/{animalId}")
    public Result<?> toggle(@PathVariable Long animalId) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error(401, "未登录");
        QueryWrapper<Favorite> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).eq("animal_id", animalId);
        Favorite exist = mapper.selectOne(q);
        if (exist != null) {
            mapper.deleteById(exist.getId());
            return Result.ok("已取消收藏");
        } else {
            Favorite f = new Favorite();
            f.setUserId(cur.getId());
            f.setAnimalId(animalId);
            mapper.insert(f);
            return Result.ok("已收藏");
        }
    }

    @GetMapping("/status/{animalId}")
    public Result<?> status(@PathVariable Long animalId) {
        User cur = TokenStore.current();
        if (cur == null) return Result.ok(false);
        QueryWrapper<Favorite> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).eq("animal_id", animalId);
        return Result.ok(mapper.selectCount(q) > 0);
    }

    @GetMapping("/my")
    public Result<?> my() {
        User cur = TokenStore.current();
        if (cur == null) return Result.error(401, "未登录");
        QueryWrapper<Favorite> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).orderByDesc("id");
        List<Favorite> list = mapper.selectList(q);
        if (!list.isEmpty()) {
            List<Long> animalIds = list.stream().map(Favorite::getAnimalId).collect(Collectors.toList());
            Map<Long, Animal> animalMap = animalMapper.selectBatchIds(animalIds).stream()
                .collect(Collectors.toMap(Animal::getId, a -> a, (a, b) -> a));
            for (Favorite f : list) {
                Animal a = animalMap.get(f.getAnimalId());
                if (a != null) {
                    f.setAnimalName(a.getName());
                    f.setAnimalImage(a.getImage());
                    f.setAnimalStatus(a.getStatus());
                    f.setTypeName(a.getTypeName());
                    f.setStationName(a.getStationName());
                }
            }
        }
        return Result.ok(list);
    }

    @DeleteMapping("/{animalId}")
    public Result<?> remove(@PathVariable Long animalId) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error(401, "未登录");
        QueryWrapper<Favorite> q = new QueryWrapper<>();
        q.eq("user_id", cur.getId()).eq("animal_id", animalId);
        mapper.delete(q);
        return Result.ok("已取消收藏");
    }
}
