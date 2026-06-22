package com.lovelin.lifesaga.saga.domain.model;

public record SagaNodeOrder(int value) {

    public SagaNodeOrder {
        if (value < 0) {
            throw new IllegalArgumentException("节点排序编号不能为负数");
        }
    }
}
