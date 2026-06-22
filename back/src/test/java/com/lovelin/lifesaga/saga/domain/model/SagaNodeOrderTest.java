package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeOrderTest {

    @Test
    void shouldRejectNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaNodeOrder(-1));
    }

    @Test
    void shouldAllowZeroValue() {
        SagaNodeOrder sagaNodeOrder = new SagaNodeOrder(0);

        assertEquals(0, sagaNodeOrder.value());
    }

    @Test
    void shouldCreateWithPositiveValue() {
        SagaNodeOrder sagaNodeOrder = new SagaNodeOrder(3);

        assertEquals(3, sagaNodeOrder.value());
    }
}
