package com.lovelin.lifesaga.achievement.domain.model;

public record AchievementConditionType(String value) {

    private static final int MAX_LENGTH = 64;

    public AchievementConditionType {
        if (value == null) {
            throw new IllegalArgumentException("成就条件类型不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("成就条件类型不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("成就条件类型最多允许 64 个字符");
        }
    }
}
