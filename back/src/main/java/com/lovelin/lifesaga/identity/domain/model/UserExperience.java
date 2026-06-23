package com.lovelin.lifesaga.identity.domain.model;

public record UserExperience(int value) {

    public UserExperience {
        if (value < 0) {
            throw new IllegalArgumentException("用户经验不能为负数");
        }
    }
}
