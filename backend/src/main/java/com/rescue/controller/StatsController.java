package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.*;
import com.rescue.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {
    @Autowired private UserMapper userMapper;
    @Autowired private RescueStationMapper stationMapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private HelpRequestMapper helpMapper;
    @Autowired private AdoptionMapper adoptionMapper;

    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        User cur = TokenStore.current();
        Map<String, Object> m = new HashMap<>();
        if (cur != null && "station".equals(cur.getRole())) {
            Long sid = cur.getStationId();
            m.put("animalTotal", animalMapper.selectCount(new QueryWrapper<Animal>().eq("station_id", sid)));
            m.put("animalAvailable", animalMapper.selectCount(new QueryWrapper<Animal>().eq("station_id", sid).eq("status", "available")));
            m.put("animalAdopted", animalMapper.selectCount(new QueryWrapper<Animal>().eq("station_id", sid).eq("status", "adopted")));
            m.put("helpTotal", helpMapper.selectCount(new QueryWrapper<HelpRequest>().eq("station_id", sid)));
            m.put("helpPending", helpMapper.selectCount(new QueryWrapper<HelpRequest>().eq("station_id", sid).eq("status", "pending")));
            m.put("helpProcessing", helpMapper.selectCount(new QueryWrapper<HelpRequest>().eq("station_id", sid).eq("status", "processing")));
        } else {
            m.put("userTotal", userMapper.selectCount(null));
            m.put("stationTotal", stationMapper.selectCount(null));
            m.put("animalTotal", animalMapper.selectCount(null));
            m.put("animalAvailable", animalMapper.selectCount(new QueryWrapper<Animal>().eq("status", "available")));
            m.put("animalAdopted", animalMapper.selectCount(new QueryWrapper<Animal>().eq("status", "adopted")));
            m.put("helpTotal", helpMapper.selectCount(null));
            m.put("helpPending", helpMapper.selectCount(new QueryWrapper<HelpRequest>().eq("status", "pending")));
            m.put("adoptionTotal", adoptionMapper.selectCount(null));
        }
        return Result.ok(m);
    }
}
