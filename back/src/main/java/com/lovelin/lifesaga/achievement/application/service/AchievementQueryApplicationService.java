package com.lovelin.lifesaga.achievement.application.service;

import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.achievement.domain.model.AchievementId;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievement;
import com.lovelin.lifesaga.achievement.domain.repository.AchievementRepository;
import com.lovelin.lifesaga.achievement.domain.repository.UserAchievementRepository;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AchievementQueryApplicationService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    public AchievementQueryApplicationService(
            AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository
    ) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    @Transactional(readOnly = true)
    public List<AchievementView> listAll(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        List<Achievement> achievements = achievementRepository.findAll();
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);
        Set<AchievementId> unlockedAchievementIds = userAchievements.stream()
                .map(UserAchievement::achievementId)
                .collect(Collectors.toSet());
        Map<AchievementId, LocalDateTime> unlockedAtMap = userAchievements.stream()
                .collect(Collectors.toMap(UserAchievement::achievementId, UserAchievement::unlockedAt));

        return achievements.stream()
                .map(achievement -> new AchievementView(
                        achievement,
                        unlockedAchievementIds.contains(achievement.achievementId()),
                        unlockedAtMap.get(achievement.achievementId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AchievementView> listMine(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        return userAchievementRepository.findByUserId(userId).stream()
                .map(userAchievement -> achievementRepository.findByAchievementId(userAchievement.achievementId())
                        .map(achievement -> new AchievementView(achievement, true, userAchievement.unlockedAt()))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public record AchievementView(
            Achievement achievement,
            boolean unlocked,
            LocalDateTime unlockedAt
    ) {
    }
}
