package com.lovelin.lifesaga.identity.domain.model;

public record UserAvatarUrl(String value) {

    private static final int MAX_LENGTH = 500;

    public UserAvatarUrl {
        if (value == null) {
            throw new IllegalArgumentException("用户头像地址不能为空");
        }

        value = value.strip();
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("用户头像地址最多允许 500 个字符");
        }
    }
}
