package com.lovelin.lifesaga.achievement.domain.model;

import com.lovelin.lifesaga.identity.domain.model.UserId;

import java.time.LocalDateTime;

public final class UserAchievement {

    private final UserAchievementId userAchievementId;
    private final UserId userId;
    private final AchievementId achievementId;
    private final LocalDateTime unlockedAt;

    private UserAchievement(
            UserAchievementId userAchievementId,
            UserId userId,
            AchievementId achievementId,
            LocalDateTime unlockedAt
    ) {
        this.userAchievementId = userAchievementId;
        this.userId = userId;
        this.achievementId = achievementId;
        this.unlockedAt = unlockedAt;
    }

    public static UserAchievement create(
            UserId userId,
            AchievementId achievementId,
            LocalDateTime unlockedAt
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (achievementId == null) {
            throw new IllegalArgumentException("成就 ID 不能为空");
        }
        if (unlockedAt == null) {
            throw new IllegalArgumentException("成就解锁时间不能为空");
        }
        return new UserAchievement(null, userId, achievementId, unlockedAt);
    }

    public static UserAchievement restore(
            UserAchievementId userAchievementId,
            UserId userId,
            AchievementId achievementId,
            LocalDateTime unlockedAt
    ) {
        if (userAchievementId == null) {
            throw new IllegalArgumentException("用户成就 ID 不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (achievementId == null) {
            throw new IllegalArgumentException("成就 ID 不能为空");
        }
        if (unlockedAt == null) {
            throw new IllegalArgumentException("成就解锁时间不能为空");
        }
        return new UserAchievement(userAchievementId, userId, achievementId, unlockedAt);
    }

    public UserAchievementId userAchievementId() {
        return userAchievementId;
    }

    public UserId userId() {
        return userId;
    }

    public AchievementId achievementId() {
        return achievementId;
    }

    public LocalDateTime unlockedAt() {
        return unlockedAt;
    }
}
