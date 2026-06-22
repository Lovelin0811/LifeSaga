package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeTimeTest {

    @Test
    void shouldCreateTime() {
        LocalDateTime value = LocalDateTime.of(2026, 6, 17, 12, 0);

        SagaNodeTime sagaNodeTime = new SagaNodeTime(value);

        assertEquals(value, sagaNodeTime.value());
    }

    @Test
    void shouldRejectNullTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeTime(null)
        );
    }
}
