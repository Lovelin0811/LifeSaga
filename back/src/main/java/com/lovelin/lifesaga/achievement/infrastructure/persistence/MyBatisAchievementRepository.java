package com.lovelin.lifesaga.achievement.infrastructure.persistence;

import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.achievement.domain.model.AchievementCode;
import com.lovelin.lifesaga.achievement.domain.model.AchievementConditionType;
import com.lovelin.lifesaga.achievement.domain.model.AchievementDescription;
import com.lovelin.lifesaga.achievement.domain.model.AchievementIcon;
import com.lovelin.lifesaga.achievement.domain.model.AchievementId;
import com.lovelin.lifesaga.achievement.domain.model.AchievementName;
import com.lovelin.lifesaga.achievement.domain.model.AchievementRarity;
import com.lovelin.lifesaga.achievement.domain.repository.AchievementRepository;
import com.lovelin.lifesaga.achievement.infrastructure.persistence.mapper.AchievementMapper;
import com.lovelin.lifesaga.achievement.infrastructure.persistence.record.AchievementRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MyBatisAchievementRepository implements AchievementRepository {

    private final AchievementMapper achievementMapper;

    public MyBatisAchievementRepository(AchievementMapper achievementMapper) {
        this.achievementMapper = achievementMapper;
    }

    @Override
    public List<Achievement> findAll() {
        return achievementMapper.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Achievement> findByAchievementId(AchievementId achievementId) {
        if (achievementId == null) {
            throw new IllegalArgumentException("成就 ID 不能为空");
        }
        return achievementMapper.findById(achievementId.value()).map(this::toDomain);
    }

    @Override
    public Optional<Achievement> findByAchievementConditionType(AchievementConditionType achievementConditionType) {
        if (achievementConditionType == null) {
            throw new IllegalArgumentException("成就条件类型不能为空");
        }
        return achievementMapper.findByConditionType(achievementConditionType.value()).map(this::toDomain);
    }

    private Achievement toDomain(AchievementRecord achievementRecord) {
        return Achievement.restore(
                new AchievementId(achievementRecord.getId()),
                new AchievementCode(achievementRecord.getCode()),
                new AchievementName(achievementRecord.getName()),
                new AchievementDescription(achievementRecord.getDescription()),
                new AchievementIcon(achievementRecord.getIcon()),
                AchievementRarity.valueOf(achievementRecord.getRarity()),
                new AchievementConditionType(achievementRecord.getConditionType()),
                achievementRecord.getConditionValue(),
                achievementRecord.getExperienceReward(),
                achievementRecord.getCreatedAt()
        );
    }
}
