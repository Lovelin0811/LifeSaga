package com.lovelin.lifesaga.achievement.interfaces.rest;

import com.lovelin.lifesaga.achievement.application.service.AchievementQueryApplicationService;
import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementQueryApplicationService achievementQueryApplicationService;

    public AchievementController(AchievementQueryApplicationService achievementQueryApplicationService) {
        this.achievementQueryApplicationService = achievementQueryApplicationService;
    }

    @GetMapping
    public ApiResponse<List<AchievementResponse>> list(HttpServletRequest httpServletRequest) {
        List<AchievementResponse> achievements = achievementQueryApplicationService
                .listAll(new UserId(currentUserId(httpServletRequest)))
                .stream()
                .map(AchievementResponse::from)
                .toList();
        return ApiResponse.success(achievements);
    }

    @GetMapping("/my")
    public ApiResponse<List<AchievementResponse>> my(HttpServletRequest httpServletRequest) {
        List<AchievementResponse> achievements = achievementQueryApplicationService
                .listMine(new UserId(currentUserId(httpServletRequest)))
                .stream()
                .map(AchievementResponse::from)
                .toList();
        return ApiResponse.success(achievements);
    }

    private long currentUserId(HttpServletRequest httpServletRequest) {
        Object userId = httpServletRequest.getAttribute("userId");
        if (!(userId instanceof Long value)) {
            throw new IllegalStateException("未登录");
        }
        return value;
    }

    public record AchievementResponse(
            Long id,
            String code,
            String name,
            String description,
            String icon,
            String rarity,
            String conditionType,
            int conditionValue,
            int experienceReward,
            boolean unlocked,
            LocalDateTime unlockedAt
    ) {

        static AchievementResponse from(AchievementQueryApplicationService.AchievementView achievementView) {
            Achievement achievement = achievementView.achievement();
            return new AchievementResponse(
                    achievement.achievementId().value(),
                    achievement.achievementCode().value(),
                    achievement.achievementName().value(),
                    achievement.achievementDescription().value(),
                    achievement.achievementIcon().value(),
                    achievement.achievementRarity().name(),
                    achievement.achievementConditionType().value(),
                    achievement.conditionValue(),
                    achievement.experienceReward(),
                    achievementView.unlocked(),
                    achievementView.unlockedAt()
            );
        }
    }

    public record ApiResponse<T>(int code, T data, String message) {

        static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(200, data, "success");
        }
    }
}
