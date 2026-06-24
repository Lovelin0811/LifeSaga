package com.lovelin.lifesaga.identity.interfaces.rest;

import com.lovelin.lifesaga.identity.application.service.WechatLoginApplicationService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    @Test
    void shouldLoginWithWechatCode() {
        AuthController authController = new AuthController(new WechatLoginApplicationService(
                new FakeUserRepository(),
                code -> "dev_" + code,
                new JwtTokenService("12345678901234567890123456789012", 604800000L),
                Clock.fixed(Instant.parse("2026-06-24T02:00:00Z"), ZoneId.of("Asia/Shanghai"))
        ));

        UserController.ApiResponse<AuthController.LoginResponse> response =
                authController.wechatLogin(new AuthController.WechatLoginRequest("abc123"));

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals(true, response.data().token().length() > 10),
                () -> assertEquals(7L, response.data().user().id())
        );
    }

    private static final class FakeUserRepository implements UserRepository {

        @Override
        public Optional<User> findByUserId(UserId userId) {
            return Optional.empty();
        }

        @Override
        public Optional<User> findByUserOpenId(UserOpenId userOpenId) {
            return Optional.empty();
        }

        @Override
        public User save(User user) {
            return User.restore(
                    new UserId(7),
                    user.userOpenId(),
                    new UserNickname(""),
                    new UserAvatarUrl(""),
                    new UserLevel(1),
                    new UserExperience(0),
                    user.createdAt(),
                    user.updatedAt()
            );
        }
    }
}
