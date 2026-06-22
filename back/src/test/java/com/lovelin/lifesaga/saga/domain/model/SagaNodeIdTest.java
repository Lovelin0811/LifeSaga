package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeIdTest {

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaNodeId(-1));
    }

    @Test
    void shouldRejectZeroValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaNodeId(0));
    }

    @Test
    void shouldCreateWithPositiveValue() {
        SagaNodeId sagaNodeId = new SagaNodeId(1);

        assertEquals(1, sagaNodeId.value());
    }
}
