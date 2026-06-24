package com.lovelin.lifesaga.achievement.domain.repository;

import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.achievement.domain.model.AchievementConditionType;
import com.lovelin.lifesaga.achievement.domain.model.AchievementId;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository {

    List<Achievement> findAll();

    Optional<Achievement> findByAchievementId(AchievementId achievementId);

    Optional<Achievement> findByAchievementConditionType(AchievementConditionType achievementConditionType);
}
