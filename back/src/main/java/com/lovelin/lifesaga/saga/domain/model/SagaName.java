package com.lovelin.lifesaga.saga.domain.model;

public record SagaName(String value) {

    private static final int MAX_LENGTH = 20;

    public SagaName {
        if (value == null) {
            throw new IllegalArgumentException("副本名称不能为空");
        }

        value = value.strip();
        if (value.isBlank()) {
            throw new IllegalArgumentException("副本名称不能为空");
        }
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("副本名称最多允许 20 个字符");
        }
    }
}
