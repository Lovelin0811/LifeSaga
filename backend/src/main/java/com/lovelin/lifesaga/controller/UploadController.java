package com.lovelin.lifesaga.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    private static final long MAX_SIZE = 20 * 1024 * 1024L;
    private static final java.util.Set<String> ALLOWED_TYPES = java.util.Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    /** 文件头 magic bytes 校验 */
    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
            "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png",  new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/gif",  new byte[]{'G', 'I', 'F', '8'},
            "image/webp", new byte[]{'R', 'I', 'F', 'F'}  // RIFF 头，webp 包含在 RIFF 容器内
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

        // 校验文件头 magic bytes，防止伪装的恶意文件
        try {
            byte[] fileHeader = new byte[8];
            var inputStream = file.getInputStream();
            int read = inputStream.read(fileHeader);
            if (read < 4) {
                return Map.of("code", 400, "message", "文件内容异常");
            }
            byte[] expectedMagic = MAGIC_BYTES.get(contentType);
            if (expectedMagic != null && !startsWith(fileHeader, expectedMagic)) {
                return Map.of("code", 400, "message", "文件内容与类型不匹配");
            }
        } catch (IOException e) {
            return Map.of("code", 400, "message", "文件读取失败");
        }

        try {
            String today = java.time.LocalDate.now().toString().replace("-", "/");
            Path dir = Paths.get(uploadDir, today);
            Files.createDirectories(dir);

            String ext = contentType.split("/")[1];
            String filename = UUID.randomUUID() + "." + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            String host = request.getHeader("X-Forwarded-Host");
            String proto = request.getHeader("X-Forwarded-Proto");
            if (host == null || host.isEmpty()) host = request.getServerName();
            if (proto == null || proto.isEmpty()) proto = request.getScheme();
            String baseUrl = proto + "://" + host;
            String fileUrl = baseUrl + "/uploads/" + today + "/" + filename;

            return Map.of("code", 200, "data", Map.of("url", fileUrl), "message", "success");
        } catch (Exception e) {
            log.error("Upload failed", e);
            return Map.of("code", 500, "message", "上传失败");
        }
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }
}
