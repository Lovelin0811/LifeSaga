package com.lovelin.lifesaga.achievement.domain.model;

public record AchievementIcon(String value) {

    private static final int MAX_LENGTH = 64;

    public AchievementIcon {
        if (value == null) {
            throw new IllegalArgumentException("成就图标不能为空");
        }

        value = value.strip();
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("成就图标最多允许 64 个字符");
        }
    }
}
