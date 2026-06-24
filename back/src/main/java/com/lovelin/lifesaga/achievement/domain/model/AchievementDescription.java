package com.lovelin.lifesaga.achievement.domain.model;

public record AchievementDescription(String value) {

    private static final int MAX_LENGTH = 512;

    public AchievementDescription {
        if (value == null) {
            throw new IllegalArgumentException("成就描述不能为空");
        }

        value = value.strip();
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("成就描述最多允许 512 个字符");
        }
    }
}
