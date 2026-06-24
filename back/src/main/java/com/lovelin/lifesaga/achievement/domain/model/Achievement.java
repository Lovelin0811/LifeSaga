package com.lovelin.lifesaga.achievement.domain.model;

import java.time.LocalDateTime;

public final class Achievement {

    private final AchievementId achievementId;
    private final AchievementCode achievementCode;
    private final AchievementName achievementName;
    private final AchievementDescription achievementDescription;
    private final AchievementIcon achievementIcon;
    private final AchievementRarity achievementRarity;
    private final AchievementConditionType achievementConditionType;
    private final int conditionValue;
    private final int experienceReward;
    private final LocalDateTime createdAt;

    private Achievement(
            AchievementId achievementId,
            AchievementCode achievementCode,
            AchievementName achievementName,
            AchievementDescription achievementDescription,
            AchievementIcon achievementIcon,
            AchievementRarity achievementRarity,
            AchievementConditionType achievementConditionType,
            int conditionValue,
            int experienceReward,
            LocalDateTime createdAt
    ) {
        this.achievementId = achievementId;
        this.achievementCode = achievementCode;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.achievementIcon = achievementIcon;
        this.achievementRarity = achievementRarity;
        this.achievementConditionType = achievementConditionType;
        this.conditionValue = conditionValue;
        this.experienceReward = experienceReward;
        this.createdAt = createdAt;
    }

    public static Achievement restore(
            AchievementId achievementId,
            AchievementCode achievementCode,
            AchievementName achievementName,
            AchievementDescription achievementDescription,
            AchievementIcon achievementIcon,
            AchievementRarity achievementRarity,
            AchievementConditionType achievementConditionType,
            int conditionValue,
            int experienceReward,
            LocalDateTime createdAt
    ) {
        if (achievementId == null) {
            throw new IllegalArgumentException("成就 ID 不能为空");
        }
        if (achievementCode == null) {
            throw new IllegalArgumentException("成就编码不能为空");
        }
        if (achievementName == null) {
            throw new IllegalArgumentException("成就名称不能为空");
        }
        if (achievementDescription == null) {
            throw new IllegalArgumentException("成就描述不能为空");
        }
        if (achievementIcon == null) {
            throw new IllegalArgumentException("成就图标不能为空");
        }
        if (achievementRarity == null) {
            throw new IllegalArgumentException("成就稀有度不能为空");
        }
        if (achievementConditionType == null) {
            throw new IllegalArgumentException("成就条件类型不能为空");
        }
        if (conditionValue < 0) {
            throw new IllegalArgumentException("成就条件值不能为负数");
        }
        if (experienceReward < 0) {
            throw new IllegalArgumentException("成就经验奖励不能为负数");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("成就创建时间不能为空");
        }
        return new Achievement(
                achievementId,
                achievementCode,
                achievementName,
                achievementDescription,
                achievementIcon,
                achievementRarity,
                achievementConditionType,
                conditionValue,
                experienceReward,
                createdAt
        );
    }

    public AchievementId achievementId() {
        return achievementId;
    }

    public AchievementCode achievementCode() {
        return achievementCode;
    }

    public AchievementName achievementName() {
        return achievementName;
    }

    public AchievementDescription achievementDescription() {
        return achievementDescription;
    }

    public AchievementIcon achievementIcon() {
        return achievementIcon;
    }

    public AchievementRarity achievementRarity() {
        return achievementRarity;
    }

    public AchievementConditionType achievementConditionType() {
        return achievementConditionType;
    }

    public int conditionValue() {
        return conditionValue;
    }

    public int experienceReward() {
        return experienceReward;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
