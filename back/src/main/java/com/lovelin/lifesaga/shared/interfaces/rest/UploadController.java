package com.lovelin.lifesaga.shared.interfaces.rest;

import com.lovelin.lifesaga.identity.interfaces.rest.UserController;
import com.lovelin.lifesaga.shared.application.service.ImageUploadApplicationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final ImageUploadApplicationService imageUploadApplicationService;

    public UploadController(ImageUploadApplicationService imageUploadApplicationService) {
        this.imageUploadApplicationService = imageUploadApplicationService;
    }

    @PostMapping
    public UserController.ApiResponse<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        ImageUploadApplicationService.UploadResult uploadResult = imageUploadApplicationService.upload(file);
        return new UserController.ApiResponse<>(200, new UploadResponse(uploadResult.url()), "success");
    }

    public record UploadResponse(String url) {
    }
}
