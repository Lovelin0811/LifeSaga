package com.lovelin.lifesaga.shared.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageUploadApplicationService {

    private static final long MAX_SIZE = 20 * 1024 * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
            "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/gif", new byte[]{'G', 'I', 'F', '8'},
            "image/webp", new byte[]{'R', 'I', 'F', 'F'}
    );

    private final String uploadDir;
    private final String publicBaseUrl;

    public ImageUploadApplicationService(
            @Value("${upload.dir:./uploads}") String uploadDir,
            @Value("${upload.public-base-url:}") String publicBaseUrl
    ) {
        this.uploadDir = uploadDir;
        this.publicBaseUrl = publicBaseUrl;
    }

    public UploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("文件超过20MB限制");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("只支持图片文件");
        }

        validateMagicBytes(file, contentType);

        try {
            String today = java.time.LocalDate.now().toString().replace("-", "/");
            Path directory = Path.of(uploadDir, today);
            Files.createDirectories(directory);

            String extension = contentType.split("/")[1];
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = directory.resolve(fileName);
            file.transferTo(targetPath.toFile());

            String relativeUrl = "/uploads/" + today + "/" + fileName;
            if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
                return new UploadResult(relativeUrl);
            }
            return new UploadResult(publicBaseUrl.replaceAll("/+$", "") + relativeUrl);
        } catch (IOException exception) {
            throw new IllegalStateException("上传失败", exception);
        }
    }

    private void validateMagicBytes(MultipartFile file, String contentType) {
        try (var inputStream = file.getInputStream()) {
            byte[] fileHeader = new byte[8];
            int read = inputStream.read(fileHeader);
            if (read < 4) {
                throw new IllegalArgumentException("文件内容异常");
            }
            byte[] expectedMagicBytes = MAGIC_BYTES.get(contentType);
            if (expectedMagicBytes != null && !startsWith(fileHeader, expectedMagicBytes)) {
                throw new IllegalArgumentException("文件内容与类型不匹配");
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("文件读取失败", exception);
        }
    }

    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int index = 0; index < prefix.length; index++) {
            if (data[index] != prefix[index]) {
                return false;
            }
        }
        return true;
    }

    public record UploadResult(String url) {
    }
}
