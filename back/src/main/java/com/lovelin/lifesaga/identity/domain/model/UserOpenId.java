package com.lovelin.lifesaga.identity.domain.model;

public record UserOpenId(String value) {

    private static final int MAX_LENGTH = 64;

    public UserOpenId {
        if (value == null) {
            throw new IllegalArgumentException("用户 openid 不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("用户 openid 不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("用户 openid 最多允许 64 个字符");
        }
    }
}
