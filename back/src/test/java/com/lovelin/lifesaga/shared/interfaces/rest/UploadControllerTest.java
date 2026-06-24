package com.lovelin.lifesaga.shared.interfaces.rest;

import com.lovelin.lifesaga.identity.interfaces.rest.UserController;
import com.lovelin.lifesaga.shared.application.service.ImageUploadApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UploadControllerTest {

    @Test
    void shouldUploadImageAndReturnUrl() throws Exception {
        Path tempDirectory = Files.createTempDirectory("lifesaga-upload-test");
        UploadController uploadController = new UploadController(
                new ImageUploadApplicationService(tempDirectory.toString(), "")
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x01, 0x02, 0x03, 0x04}
        );

        UserController.ApiResponse<UploadController.UploadResponse> response = uploadController.upload(file);

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertTrue(response.data().url().startsWith("/uploads/"))
        );
    }
}
