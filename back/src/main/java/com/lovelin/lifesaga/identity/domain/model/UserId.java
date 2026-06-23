package com.lovelin.lifesaga.identity.domain.model;

public record UserId(long value) {

    public UserId {
        if (value <= 0) {
            throw new IllegalArgumentException("用户 ID 必须为正数");
        }
    }
}
