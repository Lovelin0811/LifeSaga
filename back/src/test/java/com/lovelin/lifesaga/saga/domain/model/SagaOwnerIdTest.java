package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaOwnerIdTest {

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaOwnerId(-1));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaOwnerId(0));
    }

    @Test
    void shouldCreateWithPositiveValue() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);

        assertEquals(1, sagaOwnerId.value());
    }
}
