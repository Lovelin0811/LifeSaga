package com.lovelin.lifesaga.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    // 前端需在微信公众平台配置 uploadFile 合法域名
    private static final long MAX_SIZE = 20 * 1024 * 1024L;
    private static final java.util.Set<String> ALLOWED_TYPES = java.util.Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @PostMapping
    public Map<String, Object> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        if (file.isEmpty()) {
            return Map.of("code", 400, "message", "文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            return Map.of("code", 400, "message", "文件超过20MB限制");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return Map.of("code", 400, "message", "只支持图片文件");
        }

        try {
            // 按日期分目录
            String today = java.time.LocalDate.now().toString().replace("-", "/");
            Path dir = Paths.get(uploadDir, today);
            Files.createDirectories(dir);

            String ext = contentType.split("/")[1];
            String filename = UUID.randomUUID() + "." + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            // 返回可访问的 URL（nginx 反代后需用域名，不能用内网 IP）
            String host = request.getHeader("X-Forwarded-Host");
            String proto = request.getHeader("X-Forwarded-Proto");
            if (host == null || host.isEmpty()) host = request.getServerName();
            if (proto == null || proto.isEmpty()) proto = request.getScheme();
            String baseUrl = proto + "://" + host;
            String fileUrl = baseUrl + "/uploads/" + today + "/" + filename;

            return Map.of("code", 200, "data", Map.of("url", fileUrl), "message", "success");
        } catch (Exception e) {
            return Map.of("code", 500, "message", "上传失败: " + e.getMessage());
        }
    }
}
