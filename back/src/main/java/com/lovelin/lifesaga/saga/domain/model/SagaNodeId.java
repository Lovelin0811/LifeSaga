package com.lovelin.lifesaga.saga.domain.model;

public record SagaNodeId(long value) {

    public SagaNodeId {
        if (value <= 0) {
            throw new IllegalArgumentException("节点 ID 必须为正数");
        }
    }
}
