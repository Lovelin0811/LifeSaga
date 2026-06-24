package com.lovelin.lifesaga.achievement.domain.repository;

import com.lovelin.lifesaga.achievement.domain.model.AchievementId;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievement;
import com.lovelin.lifesaga.identity.domain.model.UserId;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository {

    List<UserAchievement> findByUserId(UserId userId);

    Optional<UserAchievement> findByUserIdAndAchievementId(UserId userId, AchievementId achievementId);

    UserAchievement save(UserAchievement userAchievement);
}
