package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.HelpRecord;
import com.rescue.entity.HelpRequest;
import com.rescue.entity.RescueStation;
import com.rescue.entity.User;
import com.rescue.mapper.HelpRecordMapper;
import com.rescue.mapper.HelpRequestMapper;
import com.rescue.mapper.RescueStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/help-record")
public class HelpRecordController {
    @Autowired private HelpRecordMapper mapper;
    @Autowired private HelpRequestMapper requestMapper;
    @Autowired private RescueStationMapper stationMapper;

    private void enrich(List<HelpRecord> list) {
        if (list == null || list.isEmpty()) return;
        Map<Long, String> reqMap = requestMapper.selectList(null).stream()
            .collect(Collectors.toMap(HelpRequest::getId, HelpRequest::getTitle, (a, b) -> a));
        Map<Long, String> stMap = stationMapper.selectList(null).stream()
            .collect(Collectors.toMap(RescueStation::getId, RescueStation::getName, (a, b) -> a));
        for (HelpRecord r : list) {
            if (r.getRequestId() != null) r.setRequestTitle(reqMap.get(r.getRequestId()));
            if (r.getStationId() != null) r.setStationName(stMap.get(r.getStationId()));
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long requestId) {
        Page<HelpRecord> p = new Page<>(current, size);
        QueryWrapper<HelpRecord> q = new QueryWrapper<>();
        if (requestId != null) q.eq("request_id", requestId);
        User cur = TokenStore.current();
        if (cur != null && "station".equals(cur.getRole())) q.eq("station_id", cur.getStationId());
        q.orderByDesc("id");
        Page<HelpRecord> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @PostMapping
    public Result<?> add(@RequestBody HelpRecord r) {
        User cur = TokenStore.current();
        if (cur != null) {
            if (r.getStationId() == null && cur.getStationId() != null) r.setStationId(cur.getStationId());
            if (r.getHandler() == null) r.setHandler(cur.getName());
        }
        mapper.insert(r);
        if (r.getRequestId() != null) {
            HelpRequest req = requestMapper.selectById(r.getRequestId());
            if (req != null && "pending".equals(req.getStatus())) {
                req.setStatus("processing");
                if (req.getStationId() == null && r.getStationId() != null) req.setStationId(r.getStationId());
                requestMapper.updateById(req);
            }
        }
        return Result.ok("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody HelpRecord r) {
        mapper.updateById(r);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
