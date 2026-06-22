package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeLocationTest {

    @Test
    void shouldCreateLocation() {
        SagaNodeLocation sagaNodeLocation = new SagaNodeLocation(" 东京塔 ");

        assertEquals("东京塔", sagaNodeLocation.value());
    }

    @Test
    void shouldRejectBlankLocation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeLocation(" ")
        );
    }
}
