package com.lovelin.lifesaga.achievement.domain.model;

public record AchievementId(long value) {

    public AchievementId {
        if (value <= 0) {
            throw new IllegalArgumentException("成就 ID 必须为正数");
        }
    }
}
