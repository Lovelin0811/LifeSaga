package com.lovelin.lifesaga.achievement.domain.model;

public record UserAchievementId(long value) {

    public UserAchievementId {
        if (value <= 0) {
            throw new IllegalArgumentException("用户成就 ID 必须为正数");
        }
    }
}
