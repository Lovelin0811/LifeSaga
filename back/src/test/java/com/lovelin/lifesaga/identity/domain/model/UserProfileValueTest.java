package com.lovelin.lifesaga.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserProfileValueTest {

    @Test
    void shouldTrimNicknameAndAvatarUrl() {
        UserNickname userNickname = new UserNickname("  Lovelin  ");
        UserAvatarUrl userAvatarUrl = new UserAvatarUrl("  https://example.com/avatar.png  ");

        assertEquals("Lovelin", userNickname.value());
        assertEquals("https://example.com/avatar.png", userAvatarUrl.value());
    }

    @Test
    void shouldAllowEmptyNicknameAndAvatarUrlForNewUser() {
        UserNickname userNickname = new UserNickname("");
        UserAvatarUrl userAvatarUrl = new UserAvatarUrl("");

        assertEquals("", userNickname.value());
        assertEquals("", userAvatarUrl.value());
    }

    @Test
    void shouldRejectTooLongNickname() {
        String tooLongNickname = "一".repeat(31);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserNickname(tooLongNickname)
        );

        assertEquals("用户昵称最多允许 30 个字符", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidLevelAndExperience() {
        assertThrows(IllegalArgumentException.class, () -> new UserLevel(0));
        assertThrows(IllegalArgumentException.class, () -> new UserExperience(-1));
    }
}
