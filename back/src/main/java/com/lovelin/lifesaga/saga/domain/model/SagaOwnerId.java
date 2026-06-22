package com.lovelin.lifesaga.saga.domain.model;

public record SagaOwnerId(long value) {

    public SagaOwnerId {
        if (value <= 0) {
            throw new IllegalArgumentException("副本所有者 ID 必须为正数");
        }
    }
}
