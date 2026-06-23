package com.lovelin.lifesaga.identity.application.service;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserQueryApplicationServiceTest {

    @Test
    void shouldGetUserById() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        UserQueryApplicationService service = new UserQueryApplicationService(userRepository);
        User user = User.create(new UserOpenId("wx_openid_001"), LocalDateTime.of(2026, 6, 23, 10, 0));
        userRepository.store(new UserId(1), user);

        User foundUser = service.getUserById(new UserId(1));

        assertSame(user, foundUser);
    }

    @Test
    void shouldRejectNullUserId() {
        UserQueryApplicationService service = new UserQueryApplicationService(new InMemoryUserRepository());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getUserById(null)
        );

        assertEquals("用户 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectUserNotFound() {
        UserQueryApplicationService service = new UserQueryApplicationService(new InMemoryUserRepository());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.getUserById(new UserId(404))
        );

        assertEquals("用户不存在", exception.getMessage());
    }

    private static final class InMemoryUserRepository implements UserRepository {

        private final Map<UserId, User> storage = new LinkedHashMap<>();

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
            return user;
        }
    }
}
