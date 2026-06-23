package com.lovelin.lifesaga.identity.domain.model;

import java.time.LocalDateTime;

public final class User {

    private final UserId userId;
    private final UserOpenId userOpenId;
    private UserNickname userNickname;
    private UserAvatarUrl userAvatarUrl;
    private UserLevel userLevel;
    private UserExperience userExperience;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(
            UserId userId,
            UserOpenId userOpenId,
            UserNickname userNickname,
            UserAvatarUrl userAvatarUrl,
            UserLevel userLevel,
            UserExperience userExperience,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.userId = userId;
        this.userOpenId = userOpenId;
        this.userNickname = userNickname;
        this.userAvatarUrl = userAvatarUrl;
        this.userLevel = userLevel;
        this.userExperience = userExperience;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(UserOpenId userOpenId, LocalDateTime createdAt) {
        if (userOpenId == null) {
            throw new IllegalArgumentException("用户 openid 不能为空");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("用户创建时间不能为空");
        }
        return new User(
                null,
                userOpenId,
                new UserNickname(""),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(0),
                createdAt,
                createdAt
        );
    }

    public static User restore(
            UserId userId,
            UserOpenId userOpenId,
            UserNickname userNickname,
            UserAvatarUrl userAvatarUrl,
            UserLevel userLevel,
            UserExperience userExperience,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (userOpenId == null) {
            throw new IllegalArgumentException("用户 openid 不能为空");
        }
        if (userNickname == null) {
            throw new IllegalArgumentException("用户昵称不能为空");
        }
        if (userAvatarUrl == null) {
            throw new IllegalArgumentException("用户头像地址不能为空");
        }
        if (userLevel == null) {
            throw new IllegalArgumentException("用户等级不能为空");
        }
        if (userExperience == null) {
            throw new IllegalArgumentException("用户经验不能为空");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("用户创建时间不能为空");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("用户更新时间不能为空");
        }
        return new User(
                userId,
                userOpenId,
                userNickname,
                userAvatarUrl,
                userLevel,
                userExperience,
                createdAt,
                updatedAt
        );
    }

    public void updateProfile(
            UserNickname newUserNickname,
            UserAvatarUrl newUserAvatarUrl,
            LocalDateTime updatedAt
    ) {
        if (updatedAt == null) {
            throw new IllegalArgumentException("用户更新时间不能为空");
        }
        if (newUserNickname != null) {
            userNickname = newUserNickname;
        }
        if (newUserAvatarUrl != null) {
            userAvatarUrl = newUserAvatarUrl;
        }
        this.updatedAt = updatedAt;
    }

    public void addExperience(int experienceToAdd, LocalDateTime updatedAt) {
        if (experienceToAdd < 0) {
            throw new IllegalArgumentException("增加的经验不能为负数");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("用户更新时间不能为空");
        }
        try {
            userExperience = new UserExperience(Math.addExact(userExperience.value(), experienceToAdd));
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("用户经验超出允许范围", exception);
        }
        this.updatedAt = updatedAt;
    }

    public UserId userId() {
        return userId;
    }

    public UserOpenId userOpenId() {
        return userOpenId;
    }

    public UserNickname userNickname() {
        return userNickname;
    }

    public UserAvatarUrl userAvatarUrl() {
        return userAvatarUrl;
    }

    public UserLevel userLevel() {
        return userLevel;
    }

    public UserExperience userExperience() {
        return userExperience;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }
}
