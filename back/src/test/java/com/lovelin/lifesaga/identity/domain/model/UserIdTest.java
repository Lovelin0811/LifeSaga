package com.lovelin.lifesaga.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdTest {

    @Test
    void shouldCreateUserIdWithPositiveValue() {
        UserId userId = new UserId(1);

        assertEquals(1, userId.value());
    }

    @Test
    void shouldRejectZeroUserId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserId(0)
        );

        assertEquals("用户 ID 必须为正数", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeUserId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserId(-1)
        );

        assertEquals("用户 ID 必须为正数", exception.getMessage());
    }
}
