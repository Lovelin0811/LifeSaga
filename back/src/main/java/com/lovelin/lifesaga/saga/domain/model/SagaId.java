package com.lovelin.lifesaga.saga.domain.model;

public record SagaId(long value) {

    public SagaId {
        if (value <= 0) {
            throw new IllegalArgumentException("副本 ID 必须为正数");
        }
    }
}
