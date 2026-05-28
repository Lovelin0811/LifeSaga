package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.model.Achievement;
import com.lovelin.lifesaga.service.AchievementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping
    public Map<String, Object> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Achievement> achievements = achievementService.listAll(userId);
        return Map.of("code", 200, "data", achievements, "message", "success");
    }

    @GetMapping("/my")
    public Map<String, Object> myAchievements(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Achievement> achievements = achievementService.listMyAchievements(userId);
        return Map.of("code", 200, "data", achievements, "message", "success");
    }
}
