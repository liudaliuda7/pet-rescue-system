package com.rescue.controller;

import com.rescue.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${upload.path}") private String uploadPath;

    @PostMapping
    public Result<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return Result.error("文件为空");
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        String orig = file.getOriginalFilename();
        String ext = orig != null && orig.contains(".") ? orig.substring(orig.lastIndexOf('.')) : "";
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        File out = new File(dir, name);
        file.transferTo(out);
        Map<String, String> m = new HashMap<>();
        m.put("url", "/api/uploads/" + name);
        m.put("name", name);
        return Result.ok(m);
    }
}
