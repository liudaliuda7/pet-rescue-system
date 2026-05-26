package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.common.Result;
import com.rescue.common.TokenStore;
import com.rescue.entity.HelpRequest;
import com.rescue.entity.Message;
import com.rescue.entity.RescueStation;
import com.rescue.entity.User;
import com.rescue.mapper.HelpRequestMapper;
import com.rescue.mapper.MessageMapper;
import com.rescue.mapper.RescueStationMapper;
import com.rescue.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/help")
public class HelpRequestController {
    @Autowired private HelpRequestMapper mapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RescueStationMapper stationMapper;
    @Autowired private MessageMapper messageMapper;

    private void enrich(List<HelpRequest> list) {
        if (list == null || list.isEmpty()) return;
        Map<Long, String> userMap = userMapper.selectList(null).stream()
            .collect(Collectors.toMap(User::getId, u -> u.getName() != null ? u.getName() : u.getUsername(), (a, b) -> a));
        Map<Long, String> stMap = stationMapper.selectList(null).stream()
            .collect(Collectors.toMap(RescueStation::getId, RescueStation::getName, (a, b) -> a));
        for (HelpRequest r : list) {
            if (r.getUserId() != null) r.setUserName(userMap.get(r.getUserId()));
            if (r.getStationId() != null) r.setStationName(stMap.get(r.getStationId()));
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") int current,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String status) {
        Page<HelpRequest> p = new Page<>(current, size);
        QueryWrapper<HelpRequest> q = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) q.like("title", title);
        if (status != null && !status.isEmpty()) q.eq("status", status);
        User cur = TokenStore.current();
        if (cur != null) {
            if ("user".equals(cur.getRole())) q.eq("user_id", cur.getId());
            else if ("station".equals(cur.getRole())) q.and(w -> w.eq("station_id", cur.getStationId()).or().isNull("station_id"));
        }
        q.orderByDesc("id");
        Page<HelpRequest> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @GetMapping("/public/page")
    public Result<?> publicPage(@RequestParam(defaultValue = "1") int current,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String title) {
        Page<HelpRequest> p = new Page<>(current, size);
        QueryWrapper<HelpRequest> q = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) q.like("title", title);
        q.orderByDesc("id");
        Page<HelpRequest> res = mapper.selectPage(p, q);
        enrich(res.getRecords());
        return Result.ok(res);
    }

    @GetMapping("/public/{id}")
    public Result<?> publicGet(@PathVariable Long id) {
        HelpRequest r = mapper.selectById(id);
        if (r != null) enrich(java.util.Collections.singletonList(r));
        return Result.ok(r);
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        HelpRequest r = mapper.selectById(id);
        if (r != null) enrich(java.util.Collections.singletonList(r));
        return Result.ok(r);
    }

    @GetMapping("/stats")
    public Result<?> stats() {
        Map<String, Object> m = new HashMap<>();
        m.put("total", mapper.selectCount(null));
        m.put("pending", mapper.selectCount(new QueryWrapper<HelpRequest>().eq("status", "pending")));
        m.put("processing", mapper.selectCount(new QueryWrapper<HelpRequest>().eq("status", "processing")));
        m.put("done", mapper.selectCount(new QueryWrapper<HelpRequest>().eq("status", "done")));
        return Result.ok(m);
    }

    @PostMapping
    public Result<?> add(@RequestBody HelpRequest r) {
        User cur = TokenStore.current();
        if (cur != null) r.setUserId(cur.getId());
        if (r.getStatus() == null) r.setStatus("pending");
        mapper.insert(r);
        return Result.ok("提交成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody HelpRequest r) {
        mapper.updateById(r);
        return Result.ok("更新成功");
    }

    @PutMapping("/assign")
    public Result<?> assign(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Long stationId = body.get("stationId") != null ? Long.valueOf(body.get("stationId").toString()) : null;
        HelpRequest r = mapper.selectById(id);
        if (r == null) return Result.error("记录不存在");
        r.setStationId(stationId);
        if ("pending".equals(r.getStatus())) r.setStatus("processing");
        mapper.updateById(r);
        if (r.getUserId() != null) {
            Message userMsg = new Message();
            userMsg.setUserId(r.getUserId());
            userMsg.setType("help_assigned");
            userMsg.setTitle("求助信息正在处理");
            userMsg.setContent("您的求助「" + r.getTitle() + "」已被指派给救助站处理。");
            userMsg.setRelatedId(r.getId());
            userMsg.setIsRead(0);
            messageMapper.insert(userMsg);
        }
        if (stationId != null) {
            List<User> stationUsers = userMapper.selectList(
                new QueryWrapper<User>().eq("station_id", stationId).eq("role", "station"));
            for (User su : stationUsers) {
                Message stationMsg = new Message();
                stationMsg.setUserId(su.getId());
                stationMsg.setType("help_new");
                stationMsg.setTitle("新的求助任务");
                stationMsg.setContent("有一条新的求助「" + r.getTitle() + "」已指派给您的救助站。");
                stationMsg.setRelatedId(r.getId());
                stationMsg.setIsRead(0);
                messageMapper.insert(stationMsg);
            }
        }
        return Result.ok("已指派");
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable Long id) {
        mapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
