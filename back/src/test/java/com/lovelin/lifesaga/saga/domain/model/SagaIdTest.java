package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaIdTest {

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaId(-1));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaId(0));
    }

    @Test
    void shouldCreateWithPositiveValue() {
        SagaId sagaId = new SagaId(1);

        assertEquals(1, sagaId.value());
    }
}
