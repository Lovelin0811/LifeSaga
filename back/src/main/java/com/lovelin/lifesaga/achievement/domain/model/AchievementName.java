package com.lovelin.lifesaga.achievement.domain.model;

public record AchievementName(String value) {

    private static final int MAX_LENGTH = 128;

    public AchievementName {
        if (value == null) {
            throw new IllegalArgumentException("成就名称不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("成就名称不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("成就名称最多允许 128 个字符");
        }
    }
}
