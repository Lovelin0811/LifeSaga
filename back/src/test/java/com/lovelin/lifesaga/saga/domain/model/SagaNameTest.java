package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNameTest {

    @Test
    void shouldCreateSagaNameWithValidValue() {
        SagaName sagaName = new SagaName("  日本旅行  ");

        assertEquals("日本旅行", sagaName.value());
    }

    @Test
    void shouldRejectBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaName("   "));
    }

    @Test
    void shouldRejectNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new SagaName(null));
    }

    @Test
    void shouldCreateSagaNameWithExactlyTwentyCharacters() {
        String value = "一二三四五六七八九十一二三四五六七八九十";

        SagaName sagaName = new SagaName(value);

        assertEquals(value, sagaName.value());
    }

    @Test
    void shouldRejectValueLongerThanTwentyCharacters() {
        String value = "一二三四五六七八九十一二三四五六七八九十一";

        assertThrows(IllegalArgumentException.class, () -> new SagaName(value));
    }
}
