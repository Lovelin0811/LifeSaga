package com.lovelin.lifesaga.identity.application.service;

import com.lovelin.lifesaga.identity.application.command.UpdateUserProfileCommand;
import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateUserProfileApplicationServiceTest {

    @Test
    void shouldUpdateUserProfile() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-06-23T03:30:00Z"), ZoneId.of("Asia/Shanghai"));
        UpdateUserProfileApplicationService service = new UpdateUserProfileApplicationService(userRepository, clock);
        User user = User.create(new UserOpenId("wx_openid_001"), LocalDateTime.of(2026, 6, 23, 10, 0));
        userRepository.store(new UserId(1), user);

        User updatedUser = service.updateUserProfile(new UpdateUserProfileCommand(
                new UserId(1),
                new UserNickname("Lovelin"),
                new UserAvatarUrl("https://example.com/avatar.png")
        ));

        assertAll(
                () -> assertSame(user, updatedUser),
                () -> assertEquals(new UserNickname("Lovelin"), updatedUser.userNickname()),
                () -> assertEquals(new UserAvatarUrl("https://example.com/avatar.png"), updatedUser.userAvatarUrl()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 23, 11, 30), updatedUser.updatedAt()),
                () -> assertEquals(1, userRepository.savedCount())
        );
    }

    @Test
    void shouldRejectNullCommand() {
        UpdateUserProfileApplicationService service = new UpdateUserProfileApplicationService(
                new InMemoryUserRepository(),
                Clock.systemDefaultZone()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateUserProfile(null)
        );

        assertEquals("更新用户资料命令不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectUserNotFound() {
        UpdateUserProfileApplicationService service = new UpdateUserProfileApplicationService(
                new InMemoryUserRepository(),
                Clock.systemDefaultZone()
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateUserProfile(new UpdateUserProfileCommand(
                        new UserId(1),
                        new UserNickname("Lovelin"),
                        null
                ))
        );

        assertEquals("用户不存在", exception.getMessage());
    }

    private static final class InMemoryUserRepository implements UserRepository {

        private final Map<UserId, User> storage = new LinkedHashMap<>();
        private int savedCount;

        void store(UserId userId, User user) {
            storage.put(userId, user);
        }

        @Override
        public Optional<User> findByUserId(UserId userId) {
            return Optional.ofNullable(storage.get(userId));
        }

        @Override
        public Optional<User> findByUserOpenId(UserOpenId userOpenId) {
            return Optional.empty();
        }

        @Override
        public User save(User user) {
            savedCount++;
            return user;
        }

        int savedCount() {
            return savedCount;
        }
    }
}
