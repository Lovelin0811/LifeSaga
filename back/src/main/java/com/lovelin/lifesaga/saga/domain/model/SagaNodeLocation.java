package com.lovelin.lifesaga.saga.domain.model;

public record SagaNodeLocation(String value) {

    private static final int MAX_LENGTH = 200;

    public SagaNodeLocation {
        if (value == null) {
            throw new IllegalArgumentException("节点地点不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("节点地点不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("节点地点最多允许 200 个字符");
        }
    }
}
