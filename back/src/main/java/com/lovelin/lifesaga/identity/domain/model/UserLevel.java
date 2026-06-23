package com.lovelin.lifesaga.identity.domain.model;

public record UserLevel(int value) {

    public UserLevel {
        if (value <= 0) {
            throw new IllegalArgumentException("用户等级必须为正数");
        }
    }
}
