package com.lovelin.lifesaga.achievement.domain.model;

public record AchievementCode(String value) {

    private static final int MAX_LENGTH = 64;

    public AchievementCode {
        if (value == null) {
            throw new IllegalArgumentException("成就编码不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("成就编码不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("成就编码最多允许 64 个字符");
        }
    }
}
