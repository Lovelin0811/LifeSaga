package com.lovelin.lifesaga.identity.infrastructure.persistence;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserExperience;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserLevel;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.infrastructure.persistence.mapper.UserMapper;
import com.lovelin.lifesaga.identity.infrastructure.persistence.record.UserRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyBatisUserRepositoryTest {

    @Test
    void shouldInsertNewUserAndReturnGeneratedUserId() {
        FakeUserMapper userMapper = new FakeUserMapper();
        MyBatisUserRepository userRepository = new MyBatisUserRepository(userMapper);
        User user = User.create(
                new UserOpenId("wx_openid_001"),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );

        User savedUser = userRepository.save(user);

        assertAll(
                () -> assertEquals(new UserId(100), savedUser.userId()),
                () -> assertEquals(new UserOpenId("wx_openid_001"), savedUser.userOpenId()),
                () -> assertEquals("", userMapper.savedRecord.getNickname()),
                () -> assertEquals("", userMapper.savedRecord.getAvatarUrl()),
                () -> assertEquals(1, userMapper.savedRecord.getLevel()),
                () -> assertEquals(0, userMapper.savedRecord.getXp())
        );
    }

    @Test
    void shouldRestoreUserWhenFoundByUserId() {
        FakeUserMapper userMapper = new FakeUserMapper();
        UserRecord userRecord = new UserRecord();
        userRecord.setId(101L);
        userRecord.setOpenid("wx_openid_002");
        userRecord.setNickname("Lovelin");
        userRecord.setAvatarUrl("https://example.com/avatar.png");
        userRecord.setLevel(2);
        userRecord.setXp(100);
        userRecord.setCreatedAt(LocalDateTime.of(2026, 6, 20, 10, 0));
        userRecord.setUpdatedAt(LocalDateTime.of(2026, 6, 21, 10, 0));
        userMapper.recordToFindById = userRecord;
        MyBatisUserRepository userRepository = new MyBatisUserRepository(userMapper);

        Optional<User> foundUser = userRepository.findByUserId(new UserId(101));

        assertTrue(foundUser.isPresent());
        assertAll(
                () -> assertEquals(new UserId(101), foundUser.orElseThrow().userId()),
                () -> assertEquals(new UserNickname("Lovelin"), foundUser.orElseThrow().userNickname()),
                () -> assertEquals(new UserAvatarUrl("https://example.com/avatar.png"), foundUser.orElseThrow().userAvatarUrl()),
                () -> assertEquals(new UserLevel(2), foundUser.orElseThrow().userLevel()),
                () -> assertEquals(new UserExperience(100), foundUser.orElseThrow().userExperience())
        );
    }

    @Test
    void shouldUpdateExistingUser() {
        FakeUserMapper userMapper = new FakeUserMapper();
        MyBatisUserRepository userRepository = new MyBatisUserRepository(userMapper);
        User user = User.restore(
                new UserId(102),
                new UserOpenId("wx_openid_003"),
                new UserNickname("旧昵称"),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(0),
                LocalDateTime.of(2026, 6, 23, 10, 0),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
        user.updateProfile(new UserNickname("新昵称"), null, LocalDateTime.of(2026, 6, 23, 11, 0));

        User savedUser = userRepository.save(user);

        assertAll(
                () -> assertEquals(user, savedUser),
                () -> assertEquals(1, userMapper.updateCount),
                () -> assertEquals(102L, userMapper.savedRecord.getId()),
                () -> assertEquals("新昵称", userMapper.savedRecord.getNickname())
        );
    }

    private static final class FakeUserMapper implements UserMapper {

        private UserRecord recordToFindById;
        private UserRecord recordToFindByOpenid;
        private UserRecord savedRecord;
        private int updateCount;

        @Override
        public Optional<UserRecord> findById(long id) {
            return Optional.ofNullable(recordToFindById);
        }

        @Override
        public Optional<UserRecord> findByOpenid(String openid) {
            return Optional.ofNullable(recordToFindByOpenid);
        }

        @Override
        public int insert(UserRecord userRecord) {
            savedRecord = userRecord;
            userRecord.setId(100L);
            return 1;
        }

        @Override
        public int update(UserRecord userRecord) {
            savedRecord = userRecord;
            updateCount++;
            return 1;
        }
    }
}
