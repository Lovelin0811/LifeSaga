package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeGeoPointTest {

    @Test
    void shouldCreateSagaNodeGeoPoint() {
        SagaNodeGeoPoint sagaNodeGeoPoint = new SagaNodeGeoPoint(
                new BigDecimal("31.230416"),
                new BigDecimal("121.473701")
        );

        assertEquals(new BigDecimal("31.230416"), sagaNodeGeoPoint.latitude());
        assertEquals(new BigDecimal("121.473701"), sagaNodeGeoPoint.longitude());
    }

    @Test
    void shouldRejectNullLatitude() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeGeoPoint(null, new BigDecimal("121.473701"))
        );
    }

    @Test
    void shouldRejectNullLongitude() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeGeoPoint(new BigDecimal("31.230416"), null)
        );
    }

    @Test
    void shouldRejectLatitudeOutOfRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeGeoPoint(new BigDecimal("90.000001"), new BigDecimal("121.473701"))
        );
    }

    @Test
    void shouldRejectLongitudeOutOfRange() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeGeoPoint(new BigDecimal("31.230416"), new BigDecimal("180.000001"))
        );
    }
}
