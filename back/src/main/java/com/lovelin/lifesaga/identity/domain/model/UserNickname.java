package com.lovelin.lifesaga.identity.domain.model;

public record UserNickname(String value) {

    private static final int MAX_LENGTH = 30;

    public UserNickname {
        if (value == null) {
            throw new IllegalArgumentException("用户昵称不能为空");
        }

        value = value.strip();
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("用户昵称最多允许 30 个字符");
        }
    }
}
