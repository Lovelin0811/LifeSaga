package com.lovelin.lifesaga.saga.domain.model;

public record SagaNodeTitle(String value) {

    private static final int MAX_LENGTH = 100;

    public SagaNodeTitle {
        if (value == null) {
            throw new IllegalArgumentException("节点标题不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("节点标题不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("节点标题最多允许 100 个字符");
        }
    }
}
