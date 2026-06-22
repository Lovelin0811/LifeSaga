package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeDescriptionTest {

    @Test
    void shouldCreateDescription() {
        SagaNodeDescription sagaNodeDescription = new SagaNodeDescription(" 节点描述 ");

        assertEquals("节点描述", sagaNodeDescription.value());
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SagaNodeDescription(" ")
        );
    }
}
