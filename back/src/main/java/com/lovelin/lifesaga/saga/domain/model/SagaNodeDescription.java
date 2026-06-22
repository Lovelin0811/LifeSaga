package com.lovelin.lifesaga.saga.domain.model;

public record SagaNodeDescription(String value) {

    private static final int MAX_LENGTH = 1000;

    public SagaNodeDescription {
        if (value == null) {
            throw new IllegalArgumentException("节点描述不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("节点描述不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("节点描述最多允许 1000 个字符");
        }
    }
}
