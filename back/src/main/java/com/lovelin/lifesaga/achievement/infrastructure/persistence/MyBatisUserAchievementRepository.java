package com.lovelin.lifesaga.achievement.infrastructure.persistence;

import com.lovelin.lifesaga.achievement.domain.model.AchievementId;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievement;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievementId;
import com.lovelin.lifesaga.achievement.domain.repository.UserAchievementRepository;
import com.lovelin.lifesaga.achievement.infrastructure.persistence.mapper.UserAchievementMapper;
import com.lovelin.lifesaga.achievement.infrastructure.persistence.record.UserAchievementRecord;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MyBatisUserAchievementRepository implements UserAchievementRepository {

    private final UserAchievementMapper userAchievementMapper;

    public MyBatisUserAchievementRepository(UserAchievementMapper userAchievementMapper) {
        this.userAchievementMapper = userAchievementMapper;
    }

    @Override
    public List<UserAchievement> findByUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return userAchievementMapper.findByUserId(userId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<UserAchievement> findByUserIdAndAchievementId(UserId userId, AchievementId achievementId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (achievementId == null) {
            throw new IllegalArgumentException("成就 ID 不能为空");
        }
        return userAchievementMapper.findByUserIdAndAchievementId(userId.value(), achievementId.value())
                .map(this::toDomain);
    }

    @Override
    public UserAchievement save(UserAchievement userAchievement) {
        if (userAchievement == null) {
            throw new IllegalArgumentException("用户成就不能为空");
        }

        UserAchievementRecord userAchievementRecord = toRecord(userAchievement);
        userAchievementMapper.insert(userAchievementRecord);
        return toDomain(userAchievementRecord);
    }

    private UserAchievementRecord toRecord(UserAchievement userAchievement) {
        UserAchievementRecord userAchievementRecord = new UserAchievementRecord();
        userAchievementRecord.setId(
                userAchievement.userAchievementId() == null ? null : userAchievement.userAchievementId().value()
        );
        userAchievementRecord.setUserId(userAchievement.userId().value());
        userAchievementRecord.setAchievementId(userAchievement.achievementId().value());
        userAchievementRecord.setUnlockedAt(userAchievement.unlockedAt());
        return userAchievementRecord;
    }

    private UserAchievement toDomain(UserAchievementRecord userAchievementRecord) {
        return UserAchievement.restore(
                new UserAchievementId(userAchievementRecord.getId()),
                new UserId(userAchievementRecord.getUserId()),
                new AchievementId(userAchievementRecord.getAchievementId()),
                userAchievementRecord.getUnlockedAt()
        );
    }
}
