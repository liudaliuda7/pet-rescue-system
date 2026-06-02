package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.Adoption;
import com.rescue.entity.Animal;
import com.rescue.entity.StationReview;
import com.rescue.entity.User;
import com.rescue.mapper.AdoptionMapper;
import com.rescue.mapper.AnimalMapper;
import com.rescue.mapper.StationReviewMapper;
import com.rescue.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
public class StationReviewController {
    @Autowired private StationReviewMapper mapper;
    @Autowired private AdoptionMapper adoptionMapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private UserMapper userMapper;

    @GetMapping("/public/station/{stationId}")
    public Result<?> publicPage(@PathVariable Long stationId,
                                @RequestParam(defaultValue = "1") int current,
                                @RequestParam(defaultValue = "10") int size) {
        Page<StationReview> p = new Page<>(current, size);
        QueryWrapper<StationReview> q = new QueryWrapper<>();
        q.eq("station_id", stationId).orderByDesc("create_time");
        Page<StationReview> res = mapper.selectPage(p, q);
        if (res.getRecords() != null && !res.getRecords().isEmpty()) {
            List<Long> userIds = res.getRecords().stream().map(StationReview::getUserId).distinct().collect(Collectors.toList());
            Map<Long, String> userNameMap = userMapper.selectList(
                new QueryWrapper<User>().in("id", userIds))
                .stream().collect(Collectors.toMap(User::getId, u -> u.getName() != null ? u.getName() : u.getUsername(), (a, b) -> a));
            for (StationReview r : res.getRecords()) {
                r.setUserName(userNameMap.getOrDefault(r.getUserId(), "匿名用户"));
            }
        }
        return Result.ok(res);
    }

    @PostMapping
    public Result<?> add(@RequestBody StationReview review) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error(401, "请先登录");
        review.setUserId(cur.getId());

        Adoption adoption = adoptionMapper.selectById(review.getAdoptionId());
        if (adoption == null) return Result.error("领养记录不存在");
        if (!"approved".equals(adoption.getStatus())) return Result.error("只有已通过的领养才能评价");
        if (!adoption.getUserId().equals(cur.getId())) return Result.error("无权评价此领养记录");

        Animal animal = animalMapper.selectById(adoption.getAnimalId());
        if (animal == null) return Result.error("动物信息不存在");
        review.setStationId(animal.getStationId());

        long exists = mapper.selectCount(new QueryWrapper<StationReview>()
            .eq("user_id", cur.getId()).eq("station_id", review.getStationId()));
        if (exists > 0) return Result.error("您已经评价过该救助站");

        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return Result.error("评分需在1-5之间");
        }

        mapper.insert(review);
        return Result.ok("评价成功");
    }

    @GetMapping("/check")
    public Result<?> check(@RequestParam Long stationId) {
        User cur = TokenStore.current();
        if (cur == null) return Result.error(401, "请先登录");
        long exists = mapper.selectCount(new QueryWrapper<StationReview>()
            .eq("user_id", cur.getId()).eq("station_id", stationId));
        return Result.ok(exists > 0);
    }
}
