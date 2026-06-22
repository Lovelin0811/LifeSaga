package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeTitleTest {

    @Test
    void shouldCreateWithValidValue() {
        SagaNodeTitle sagaNodeTitle = new SagaNodeTitle("  第一次旅行  ");

        assertEquals("第一次旅行", sagaNodeTitle.value());
    }

    @Test
    void shouldRejectNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaNodeTitle(null));
    }

    @Test
    void shouldRejectBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaNodeTitle("   "));
    }

    @Test
    void shouldCreateWithExactlyOneHundredCharacters() {
        String value = "标".repeat(100);

        SagaNodeTitle sagaNodeTitle = new SagaNodeTitle(value);

        assertEquals(value, sagaNodeTitle.value());
    }

    @Test
    void shouldRejectValueLongerThanOneHundredCharacters() {
        String value = "标".repeat(101);

        assertThrows(IllegalArgumentException.class, () -> new SagaNodeTitle(value));
    }
}
