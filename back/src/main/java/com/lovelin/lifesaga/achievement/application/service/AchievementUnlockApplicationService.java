package com.lovelin.lifesaga.achievement.application.service;

import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.achievement.domain.model.AchievementConditionType;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievement;
import com.lovelin.lifesaga.achievement.domain.repository.AchievementProgressRepository;
import com.lovelin.lifesaga.achievement.domain.repository.AchievementRepository;
import com.lovelin.lifesaga.achievement.domain.repository.UserAchievementRepository;
import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AchievementUnlockApplicationService implements AchievementUnlockUseCase {

    private static final List<SagaType> COMPLETIONIST_REQUIRED_TYPES = List.of(
            SagaType.LIFE,
            SagaType.TRAVEL,
            SagaType.STUDY,
            SagaType.WORK,
            SagaType.HEALTH
    );

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementProgressRepository achievementProgressRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public AchievementUnlockApplicationService(
            AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            AchievementProgressRepository achievementProgressRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.achievementProgressRepository = achievementProgressRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public List<Achievement> checkOnSagaCreate(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        return unlock(userId, List.of(
                new Rule("first_saga", () -> true),
                new Rule("saga_types_count", () -> achievementProgressRepository.findDistinctSagaTypesByUserId(userId).size() >= 3),
                new Rule("total_sagas", () -> achievementProgressRepository.countSagasByUserId(userId) >= 10),
                new Rule("has_legendary", () -> achievementProgressRepository.hasSagaWithRarity(userId, SagaRarity.LEGENDARY)),
                new Rule("all_types_completed", () -> allTypesCompleted(userId))
        ));
    }

    @Override
    @Transactional
    public List<Achievement> checkOnSagaComplete(UserId userId, SagaType sagaType) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }

        List<Rule> rules = new ArrayList<>();
        if (sagaType != null) {
            rules.add(new Rule("completed_type_" + sagaType.name().toLowerCase(), () -> true));
        }
        rules.add(new Rule("all_types_completed", () -> allTypesCompleted(userId)));
        return unlock(userId, rules);
    }

    @Override
    @Transactional
    public List<Achievement> checkOnNodeCreate(UserId userId, SagaId sagaId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }

        return unlock(userId, List.of(
                new Rule("first_node", () -> true),
                new Rule("first_photo", () -> achievementProgressRepository.sagaHasPhotos(sagaId)),
                new Rule("streak_days", () -> hasStreak(userId, 7))
        ));
    }

    private List<Achievement> unlock(UserId userId, List<Rule> rules) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("用户不存在"));
        List<Achievement> unlockedAchievements = new ArrayList<>();
        int experienceRewardToAdd = 0;
        LocalDateTime now = LocalDateTime.now(clock);

        for (Rule rule : rules) {
            Achievement achievement = achievementRepository.findByAchievementConditionType(
                            new AchievementConditionType(rule.conditionType()))
                    .orElse(null);
            if (achievement == null) {
                continue;
            }
            if (userAchievementRepository.findByUserIdAndAchievementId(userId, achievement.achievementId()).isPresent()) {
                continue;
            }
            if (!rule.condition().matches()) {
                continue;
            }

            userAchievementRepository.save(UserAchievement.create(userId, achievement.achievementId(), now));
            unlockedAchievements.add(achievement);
            experienceRewardToAdd += achievement.experienceReward();
        }

        if (experienceRewardToAdd > 0) {
            user.addExperience(experienceRewardToAdd, now);
            userRepository.save(user);
        }
        return unlockedAchievements;
    }

    private boolean allTypesCompleted(UserId userId) {
        for (SagaType sagaType : COMPLETIONIST_REQUIRED_TYPES) {
            if (achievementProgressRepository.countCompletedSagasByType(userId, sagaType) == 0) {
                return false;
            }
        }
        return achievementProgressRepository.countCompletedSagasByType(userId, SagaType.CREATIVE) > 0
                || achievementProgressRepository.countCompletedSagasByType(userId, SagaType.RELATIONSHIP) > 0;
    }

    private boolean hasStreak(UserId userId, int requiredDays) {
        List<LocalDate> recentDays = achievementProgressRepository.findRecentNodeDates(userId, requiredDays);
        if (recentDays.size() < requiredDays) {
            return false;
        }

        LocalDate previousDay = null;
        for (int index = 0; index < recentDays.size(); index++) {
            LocalDate day = recentDays.get(index);
            if (index == 0) {
                LocalDate today = LocalDate.now(clock);
                if (!day.equals(today) && !day.equals(today.minusDays(1))) {
                    return false;
                }
                previousDay = day;
                continue;
            }
            if (!day.equals(previousDay.minusDays(1))) {
                return false;
            }
            previousDay = day;
        }
        return true;
    }

    private record Rule(String conditionType, Condition condition) {
    }

    @FunctionalInterface
    private interface Condition {
        boolean matches();
    }
}
