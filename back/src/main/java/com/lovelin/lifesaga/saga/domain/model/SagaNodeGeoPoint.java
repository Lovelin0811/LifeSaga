package com.lovelin.lifesaga.saga.domain.model;

import java.math.BigDecimal;

public record SagaNodeGeoPoint(BigDecimal latitude, BigDecimal longitude) {

    private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
    private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

    public SagaNodeGeoPoint {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("节点经纬度必须同时存在");
        }
        if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
            throw new IllegalArgumentException("节点纬度范围必须在 -90 到 90 之间");
        }
        if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
            throw new IllegalArgumentException("节点经度范围必须在 -180 到 180 之间");
        }
    }
}
