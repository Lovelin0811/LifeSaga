package com.lovelin.lifesaga.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void shouldCreateUserWithDefaultProfileAndLevel() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 23, 10, 0);

        User user = User.create(new UserOpenId("wx_openid_001"), createdAt);

        assertAll(
                () -> assertNull(user.userId()),
                () -> assertEquals(new UserOpenId("wx_openid_001"), user.userOpenId()),
                () -> assertEquals(new UserNickname(""), user.userNickname()),
                () -> assertEquals(new UserAvatarUrl(""), user.userAvatarUrl()),
                () -> assertEquals(new UserLevel(1), user.userLevel()),
                () -> assertEquals(new UserExperience(0), user.userExperience()),
                () -> assertEquals(createdAt, user.createdAt()),
                () -> assertEquals(createdAt, user.updatedAt())
        );
    }

    @Test
    void shouldRestoreExistingUser() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 20, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 6, 21, 10, 0);

        User user = User.restore(
                new UserId(1),
                new UserOpenId("wx_openid_001"),
                new UserNickname("Lovelin"),
                new UserAvatarUrl("https://example.com/avatar.png"),
                new UserLevel(2),
                new UserExperience(100),
                createdAt,
                updatedAt
        );

        assertAll(
                () -> assertEquals(new UserId(1), user.userId()),
                () -> assertEquals(new UserNickname("Lovelin"), user.userNickname()),
                () -> assertEquals(new UserAvatarUrl("https://example.com/avatar.png"), user.userAvatarUrl()),
                () -> assertEquals(new UserLevel(2), user.userLevel()),
                () -> assertEquals(new UserExperience(100), user.userExperience()),
                () -> assertEquals(createdAt, user.createdAt()),
                () -> assertEquals(updatedAt, user.updatedAt())
        );
    }

    @Test
    void shouldUpdateProfilePartially() {
        User user = User.create(
                new UserOpenId("wx_openid_001"),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
        LocalDateTime updatedAt = LocalDateTime.of(2026, 6, 23, 11, 0);

        user.updateProfile(new UserNickname("新昵称"), null, updatedAt);

        assertAll(
                () -> assertEquals(new UserNickname("新昵称"), user.userNickname()),
                () -> assertEquals(new UserAvatarUrl(""), user.userAvatarUrl()),
                () -> assertEquals(updatedAt, user.updatedAt())
        );
    }

    @Test
    void shouldAddExperience() {
        User user = User.create(
                new UserOpenId("wx_openid_001"),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
        LocalDateTime updatedAt = LocalDateTime.of(2026, 6, 23, 11, 0);

        user.addExperience(50, updatedAt);

        assertAll(
                () -> assertEquals(new UserExperience(50), user.userExperience()),
                () -> assertEquals(updatedAt, user.updatedAt())
        );
    }

    @Test
    void shouldRejectNegativeExperienceToAdd() {
        User user = User.create(
                new UserOpenId("wx_openid_001"),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.addExperience(-1, LocalDateTime.of(2026, 6, 23, 11, 0))
        );

        assertEquals("增加的经验不能为负数", exception.getMessage());
    }

    @Test
    void shouldRejectExperienceOverflow() {
        User user = User.restore(
                new UserId(1),
                new UserOpenId("wx_openid_001"),
                new UserNickname("Lovelin"),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(Integer.MAX_VALUE),
                LocalDateTime.of(2026, 6, 23, 10, 0),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.addExperience(1, LocalDateTime.of(2026, 6, 23, 11, 0))
        );

        assertEquals("用户经验超出允许范围", exception.getMessage());
    }
}
