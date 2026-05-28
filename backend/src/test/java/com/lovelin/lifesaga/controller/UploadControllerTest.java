package com.lovelin.lifesaga.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UploadControllerTest {

    @Test
    void uploadReturnsRelativeUrlWhenPublicBaseUrlIsMissing() throws Exception {
        UploadController controller = new UploadController();
        Path tempDir = Files.createTempDirectory("lifesaga-upload-test");
        setField(controller, "uploadDir", tempDir.toString());
        setField(controller, "publicBaseUrl", "");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{
                        (byte) 0x89, 0x50, 0x4E, 0x47, 0x00, 0x00, 0x00, 0x00
                }
        );

        Map<String, Object> response = controller.upload(file, new MockHttpServletRequest());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");

        assertEquals(200, response.get("code"));
        assertTrue(((String) data.get("url")).startsWith("/uploads/"));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
