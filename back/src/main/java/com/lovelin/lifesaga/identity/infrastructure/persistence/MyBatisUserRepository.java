package com.lovelin.lifesaga.identity.infrastructure.persistence;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserExperience;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserLevel;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import com.lovelin.lifesaga.identity.infrastructure.persistence.mapper.UserMapper;
import com.lovelin.lifesaga.identity.infrastructure.persistence.record.UserRecord;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisUserRepository implements UserRepository {

    private final UserMapper userMapper;

    public MyBatisUserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return userMapper.findById(userId.value()).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUserOpenId(UserOpenId userOpenId) {
        if (userOpenId == null) {
            throw new IllegalArgumentException("用户 openid 不能为空");
        }
        return userMapper.findByOpenid(userOpenId.value()).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }

        UserRecord userRecord = toRecord(user);
        if (user.userId() == null) {
            userMapper.insert(userRecord);
            return toDomain(userRecord);
        }

        int updatedRows = userMapper.update(userRecord);
        if (updatedRows != 1) {
            throw new IllegalStateException("用户保存失败");
        }
        return user;
    }

    private UserRecord toRecord(User user) {
        UserRecord userRecord = new UserRecord();
        userRecord.setId(user.userId() == null ? null : user.userId().value());
        userRecord.setOpenid(user.userOpenId().value());
        userRecord.setNickname(user.userNickname().value());
        userRecord.setAvatarUrl(user.userAvatarUrl().value());
        userRecord.setLevel(user.userLevel().value());
        userRecord.setXp(user.userExperience().value());
        userRecord.setCreatedAt(user.createdAt());
        userRecord.setUpdatedAt(user.updatedAt());
        return userRecord;
    }

    private User toDomain(UserRecord userRecord) {
        return User.restore(
                new UserId(userRecord.getId()),
                new UserOpenId(userRecord.getOpenid()),
                new UserNickname(userRecord.getNickname()),
                new UserAvatarUrl(userRecord.getAvatarUrl()),
                new UserLevel(userRecord.getLevel()),
                new UserExperience(userRecord.getXp()),
                userRecord.getCreatedAt(),
                userRecord.getUpdatedAt()
        );
    }
}
