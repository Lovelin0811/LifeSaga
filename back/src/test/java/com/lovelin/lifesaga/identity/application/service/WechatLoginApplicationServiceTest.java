package com.lovelin.lifesaga.identity.application.service;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserExperience;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserLevel;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import com.lovelin.lifesaga.identity.infrastructure.security.JwtTokenService;
import com.lovelin.lifesaga.identity.infrastructure.wechat.WechatOpenIdResolver;
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

class WechatLoginApplicationServiceTest {

    @Test
    void shouldCreateUserAndReturnTokenOnWechatLogin() {
        FakeUserRepository userRepository = new FakeUserRepository();
        JwtTokenService jwtTokenService = new JwtTokenService("12345678901234567890123456789012", 604800000L);
        WechatLoginApplicationService service = new WechatLoginApplicationService(
                userRepository,
                code -> "dev_" + code,
                jwtTokenService,
                Clock.fixed(Instant.parse("2026-06-24T02:00:00Z"), ZoneId.of("Asia/Shanghai"))
        );

        WechatLoginApplicationService.LoginResult loginResult = service.wechatLogin("abc123");

        assertAll(
                () -> assertEquals(1L, loginResult.user().userId().value()),
                () -> assertEquals("dev_abc123", loginResult.user().userOpenId().value()),
                () -> assertEquals(true, jwtTokenService.validateToken(loginResult.token()))
        );
    }

    private static final class FakeUserRepository implements UserRepository {

        private final Map<UserId, User> storage = new LinkedHashMap<>();
        private long nextId = 1L;

        @Override
        public Optional<User> findByUserId(UserId userId) {
            return Optional.ofNullable(storage.get(userId));
        }

        @Override
        public Optional<User> findByUserOpenId(UserOpenId userOpenId) {
            return storage.values().stream()
                    .filter(user -> user.userOpenId().equals(userOpenId))
                    .findFirst();
        }

        @Override
        public User save(User user) {
            if (user.userId() == null) {
                User savedUser = User.restore(
                        new UserId(nextId++),
                        user.userOpenId(),
                        user.userNickname(),
                        user.userAvatarUrl(),
                        user.userLevel(),
                        user.userExperience(),
                        user.createdAt(),
                        user.updatedAt()
                );
                storage.put(savedUser.userId(), savedUser);
                return savedUser;
            }
            storage.put(user.userId(), user);
            return user;
        }
    }
}
