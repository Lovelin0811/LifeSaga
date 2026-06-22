package com.lovelin.lifesaga.saga.domain.model;

import java.time.LocalDateTime;

public record SagaNodeTime(LocalDateTime value) {

    public SagaNodeTime {
        if (value == null) {
            throw new IllegalArgumentException("节点时间不能为空");
        }
    }
}
