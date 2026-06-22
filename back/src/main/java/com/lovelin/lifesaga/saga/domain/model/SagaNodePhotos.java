package com.lovelin.lifesaga.saga.domain.model;

import java.util.List;

public record SagaNodePhotos(List<String> values) {

    private static final int MAX_PHOTO_COUNT = 9;

    public SagaNodePhotos {
        if (values == null) {
            throw new IllegalArgumentException("节点照片不能为空");
        }
        if (values.isEmpty()) {
            throw new IllegalArgumentException("节点照片不能为空");
        }
        if (values.size() > MAX_PHOTO_COUNT) {
            throw new IllegalArgumentException("节点照片最多允许 9 张");
        }
        if (values.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException("节点照片地址不能为空");
        }

        values = List.copyOf(values);
    }
}
