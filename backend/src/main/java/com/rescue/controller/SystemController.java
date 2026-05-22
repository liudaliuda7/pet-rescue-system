package com.rescue.controller;

import com.rescue.common.Result;
import com.rescue.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemController {
    @Autowired private UserMapper userMapper;
    @Autowired private RescueStationMapper stationMapper;
    @Autowired private AnimalTypeMapper animalTypeMapper;
    @Autowired private AnimalMapper animalMapper;
    @Autowired private HelpRequestMapper helpMapper;
    @Autowired private HelpRecordMapper helpRecordMapper;
    @Autowired private AdoptionMapper adoptionMapper;
    @Autowired private VisitRecordMapper visitMapper;
    @Autowired private HealthRecordMapper healthMapper;
    @Autowired private NoticeMapper noticeMapper;

    @GetMapping("/info")
    public Result<?> info() {
        Map<String, Object> m = new HashMap<>();
        m.put("appName", "流浪动物救助与领养系统");
        m.put("version", "1.0.0");
        m.put("javaVersion", System.getProperty("java.version"));
        m.put("os", System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration d = Duration.ofMillis(uptimeMs);
        m.put("uptime", String.format("%d天 %d时 %d分", d.toDays(), d.toHoursPart(), d.toMinutesPart()));
        Runtime rt = Runtime.getRuntime();
        m.put("memoryUsedMB", (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024);
        m.put("memoryTotalMB", rt.totalMemory() / 1024 / 1024);
        m.put("memoryMaxMB", rt.maxMemory() / 1024 / 1024);
        m.put("processors", rt.availableProcessors());

        List<Map<String, Object>> tables = new ArrayList<>();
        tables.add(row("用户", "user", userMapper.selectCount(null)));
        tables.add(row("救助站", "rescue_station", stationMapper.selectCount(null)));
        tables.add(row("动物种类", "animal_type", animalTypeMapper.selectCount(null)));
        tables.add(row("流浪动物", "animal", animalMapper.selectCount(null)));
        tables.add(row("求助信息", "help_request", helpMapper.selectCount(null)));
        tables.add(row("求助记录", "help_record", helpRecordMapper.selectCount(null)));
        tables.add(row("领养记录", "adoption", adoptionMapper.selectCount(null)));
        tables.add(row("回访档案", "visit_record", visitMapper.selectCount(null)));
        tables.add(row("健康档案", "health_record", healthMapper.selectCount(null)));
        tables.add(row("公告", "notice", noticeMapper.selectCount(null)));
        m.put("tables", tables);
        return Result.ok(m);
    }

    private Map<String, Object> row(String label, String table, long count) {
        Map<String, Object> r = new HashMap<>();
        r.put("label", label);
        r.put("table", table);
        r.put("count", count);
        return r;
    }
}
