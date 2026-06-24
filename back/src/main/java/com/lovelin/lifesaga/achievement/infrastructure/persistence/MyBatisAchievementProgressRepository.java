package com.lovelin.lifesaga.achievement.infrastructure.persistence;

import com.lovelin.lifesaga.achievement.domain.repository.AchievementProgressRepository;
import com.lovelin.lifesaga.achievement.infrastructure.persistence.mapper.AchievementProgressMapper;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MyBatisAchievementProgressRepository implements AchievementProgressRepository {

    private final AchievementProgressMapper achievementProgressMapper;

    public MyBatisAchievementProgressRepository(AchievementProgressMapper achievementProgressMapper) {
        this.achievementProgressMapper = achievementProgressMapper;
    }

    @Override
    public int countSagasByUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return achievementProgressMapper.countSagasByUserId(userId.value());
    }

    @Override
    public List<SagaType> findDistinctSagaTypesByUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return achievementProgressMapper.findDistinctSagaTypesByUserId(userId.value()).stream()
                .map(String::toUpperCase)
                .map(SagaType::valueOf)
                .toList();
    }

    @Override
    public int countCompletedSagasByType(UserId userId, SagaType sagaType) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (sagaType == null) {
            throw new IllegalArgumentException("副本类型不能为空");
        }
        return achievementProgressMapper.countCompletedSagasByType(
                userId.value(),
                sagaType.name()
        );
    }

    @Override
    public boolean hasSagaWithRarity(UserId userId, SagaRarity sagaRarity) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (sagaRarity == null) {
            throw new IllegalArgumentException("副本稀有度不能为空");
        }
        return achievementProgressMapper.countSagasByUserIdAndRarity(userId.value(), sagaRarity.name()) > 0;
    }

    @Override
    public boolean sagaHasPhotos(SagaId sagaId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        return achievementProgressMapper.countNodesWithPhotosBySagaId(sagaId.value()) > 0;
    }

    @Override
    public List<LocalDate> findRecentNodeDates(UserId userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("查询天数必须为正数");
        }
        return achievementProgressMapper.findRecentNodeDates(userId.value(), limit);
    }
}
