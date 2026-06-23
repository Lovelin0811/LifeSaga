package com.lovelin.lifesaga.identity.interfaces.rest;

import com.lovelin.lifesaga.identity.application.service.UpdateUserProfileApplicationService;
import com.lovelin.lifesaga.identity.application.service.UserQueryApplicationService;
import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserExperience;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserLevel;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    @Test
    void shouldReturnCurrentUser() {
        FakeUserRepository userRepository = new FakeUserRepository();
        UserController userController = createUserController(userRepository);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 1L);
        userRepository.userToFind = User.restore(
                new UserId(1),
                new UserOpenId("wx_openid_001"),
                new UserNickname("Lovelin"),
                new UserAvatarUrl("https://example.com/avatar.png"),
                new UserLevel(2),
                new UserExperience(100),
                LocalDateTime.of(2026, 6, 20, 10, 0),
                LocalDateTime.of(2026, 6, 21, 10, 0)
        );

        UserController.ApiResponse<UserController.UserResponse> response = userController.me(httpServletRequest);

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals(1L, response.data().id()),
                () -> assertEquals("Lovelin", response.data().nickname()),
                () -> assertEquals("https://example.com/avatar.png", response.data().avatarUrl()),
                () -> assertEquals(2, response.data().level()),
                () -> assertEquals(100, response.data().xp())
        );
    }

    @Test
    void shouldUpdateCurrentUser() {
        FakeUserRepository userRepository = new FakeUserRepository();
        UserController userController = createUserController(userRepository);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 1L);
        userRepository.userToFind = User.restore(
                new UserId(1),
                new UserOpenId("wx_openid_001"),
                new UserNickname("旧昵称"),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(0),
                LocalDateTime.of(2026, 6, 20, 10, 0),
                LocalDateTime.of(2026, 6, 21, 10, 0)
        );

        UserController.ApiResponse<UserController.UserResponse> response = userController.update(
                new UserController.UpdateUserRequest("新昵称", "https://example.com/new-avatar.png"),
                httpServletRequest
        );

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals("新昵称", response.data().nickname()),
                () -> assertEquals("https://example.com/new-avatar.png", response.data().avatarUrl())
        );
    }

    private UserController createUserController(FakeUserRepository userRepository) {
        Clock clock = Clock.fixed(Instant.parse("2026-06-23T03:30:00Z"), ZoneId.of("Asia/Shanghai"));
        return new UserController(
                new UserQueryApplicationService(userRepository),
                new UpdateUserProfileApplicationService(userRepository, clock)
        );
    }

    private static final class FakeUserRepository implements UserRepository {

        private final Map<UserId, User> storage = new LinkedHashMap<>();
        private User userToFind;

        @Override
        public Optional<User> findByUserId(UserId userId) {
            if (userToFind != null) {
                storage.put(userId, userToFind);
                return Optional.of(userToFind);
            }
            return Optional.ofNullable(storage.get(userId));
        }

        @Override
        public Optional<User> findByUserOpenId(UserOpenId userOpenId) {
            return Optional.empty();
        }

        @Override
        public User save(User user) {
            if (user.userId() != null) {
                storage.put(user.userId(), user);
            }
            userToFind = user;
            return user;
        }
    }
}
