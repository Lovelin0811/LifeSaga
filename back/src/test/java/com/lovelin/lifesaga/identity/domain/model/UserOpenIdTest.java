package com.lovelin.lifesaga.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserOpenIdTest {

    @Test
    void shouldTrimOpenId() {
        UserOpenId userOpenId = new UserOpenId("  wx_openid_001  ");

        assertEquals("wx_openid_001", userOpenId.value());
    }

    @Test
    void shouldRejectNullOpenId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserOpenId(null)
        );

        assertEquals("用户 openid 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectBlankOpenId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserOpenId(" ")
        );

        assertEquals("用户 openid 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectTooLongOpenId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserOpenId("o".repeat(65))
        );

        assertEquals("用户 openid 最多允许 64 个字符", exception.getMessage());
    }
}
