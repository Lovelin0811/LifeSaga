package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.Achievement;
import com.lovelin.lifesaga.model.UserAchievement;
import com.lovelin.lifesaga.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final SagaRepository sagaRepository;
    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository,
                              SagaRepository sagaRepository,
                              NodeRepository nodeRepository,
                              UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.sagaRepository = sagaRepository;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    public List<Achievement> listAll(Long userId) {
        List<Achievement> achievements = achievementRepository.findAll();
        List<UserAchievement> unlocked = userAchievementRepository.findByUserId(userId);
        Set<Long> unlockedIds = unlocked.stream()
                .map(UserAchievement::getAchievementId)
                .collect(Collectors.toSet());
        Map<Long, java.time.LocalDateTime> unlockedTimes = unlocked.stream()
                .collect(Collectors.toMap(UserAchievement::getAchievementId, UserAchievement::getUnlockedAt));

        for (Achievement a : achievements) {
            a.setUnlocked(unlockedIds.contains(a.getId()));
            if (a.isUnlocked()) {
                a.setUnlockedAt(unlockedTimes.get(a.getId()));
            }
        }
        return achievements;
    }

    public List<Achievement> listMyAchievements(Long userId) {
        List<UserAchievement> unlocked = userAchievementRepository.findByUserId(userId);
        List<Achievement> result = new ArrayList<>();
        for (UserAchievement ua : unlocked) {
            achievementRepository.findById(ua.getAchievementId()).ifPresent(a -> {
                a.setUnlocked(true);
                a.setUnlockedAt(ua.getUnlockedAt());
                result.add(a);
            });
        }
        return result;
    }

    @Transactional
    public List<Achievement> checkAchievementsOnSagaCreate(Long userId) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        tryUnlock(userId, "first_saga", newlyUnlocked);
        tryUnlock(userId, "saga_types_count", () -> {
            return sagaRepository.findDistinctTypesByUserId(userId).size() >= 3;
        }, newlyUnlocked);
        tryUnlock(userId, "total_sagas", () -> {
            return sagaRepository.countByUserId(userId) >= 10;
        }, newlyUnlocked);
        tryUnlock(userId, "has_legendary", () -> {
            return sagaRepository.hasLegendary(userId);
        }, newlyUnlocked);
        tryUnlock(userId, "all_types_completed", () -> {
            String[] types = {"life", "travel", "study", "work", "health", "relationship"};
            for (String type : types) {
                if (sagaRepository.countCompletedByType(userId, type) == 0) return false;
            }
            return true;
        }, newlyUnlocked);
        return newlyUnlocked;
    }

    @Transactional
    public List<Achievement> checkAchievementsOnSagaComplete(Long userId, String type) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        if (type != null && !type.isBlank()) {
            tryUnlock(userId, "completed_type_" + type, newlyUnlocked);
        }
        tryUnlock(userId, "all_types_completed", () -> {
            String[] types = {"life", "travel", "study", "work", "health", "relationship"};
            for (String sagaType : types) {
                if (sagaRepository.countCompletedByType(userId, sagaType) == 0) return false;
            }
            return true;
        }, newlyUnlocked);
        return newlyUnlocked;
    }

    @Transactional
    public List<Achievement> checkAchievementsOnNodeCreate(Long userId, Long sagaId) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        tryUnlock(userId, "first_node", newlyUnlocked);
        tryUnlock(userId, "first_photo", () -> {
            return nodeRepository.hasPhotos(sagaId);
        }, newlyUnlocked);
        tryUnlock(userId, "streak_days", () -> {
            return checkStreakDays(userId, 7);
        }, newlyUnlocked);
        return newlyUnlocked;
    }

    private boolean checkStreakDays(Long userId, int requiredDays) {
        List<java.time.LocalDateTime> recentDays = nodeRepository.findRecentNodeTimes(userId, requiredDays);
        if (recentDays.size() < requiredDays) return false;

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate previousDay = today;

        for (int i = 0; i < recentDays.size(); i++) {
            java.time.LocalDate day = recentDays.get(i).toLocalDate();
            if (i == 0) {
                if (!day.equals(today) && !day.equals(today.minusDays(1))) return false;
                previousDay = day;
            } else {
                if (!day.equals(previousDay.minusDays(1))) return false;
                previousDay = day;
            }
        }
        return true;
    }

    private void tryUnlock(Long userId, String conditionType, List<Achievement> newlyUnlocked) {
        tryUnlock(userId, conditionType, () -> true, newlyUnlocked);
    }

    private void tryUnlock(Long userId, String conditionType, RunnableBoolean condition, List<Achievement> newlyUnlocked) {
        Optional<Achievement> opt = achievementRepository.findByCode(conditionType);
        if (opt.isEmpty()) return;
        Achievement achievement = opt.get();

        if (userAchievementRepository.findByUserIdAndAchievementId(userId, achievement.getId()).isPresent()) {
            return;
        }

        if (condition.check()) {
            UserAchievement ua = new UserAchievement();
            ua.setUserId(userId);
            ua.setAchievementId(achievement.getId());
            userAchievementRepository.save(ua);
            userRepository.addXp(userId, achievement.getXpReward());
            newlyUnlocked.add(achievement);
        }
    }

    @FunctionalInterface
    private interface RunnableBoolean {
        boolean check();
    }
}
