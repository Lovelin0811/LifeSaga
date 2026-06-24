package com.lovelin.lifesaga.achievement.application.service;

import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.achievement.domain.model.AchievementCode;
import com.lovelin.lifesaga.achievement.domain.model.AchievementConditionType;
import com.lovelin.lifesaga.achievement.domain.model.AchievementDescription;
import com.lovelin.lifesaga.achievement.domain.model.AchievementIcon;
import com.lovelin.lifesaga.achievement.domain.model.AchievementId;
import com.lovelin.lifesaga.achievement.domain.model.AchievementName;
import com.lovelin.lifesaga.achievement.domain.model.AchievementRarity;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievement;
import com.lovelin.lifesaga.achievement.domain.model.UserAchievementId;
import com.lovelin.lifesaga.achievement.domain.repository.AchievementRepository;
import com.lovelin.lifesaga.achievement.domain.repository.UserAchievementRepository;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementQueryApplicationServiceTest {

    @Test
    void shouldListAllAchievementsWithUnlockedState() {
        Achievement firstSagaAchievement = achievement(
                1,
                "first_saga",
                "冒险新手",
                "first_saga",
                10
        );
        Achievement firstNodeAchievement = achievement(
                2,
                "first_node",
                "记录者",
                "first_node",
                10
        );
        FakeAchievementRepository achievementRepository = new FakeAchievementRepository(
                List.of(firstSagaAchievement, firstNodeAchievement)
        );
        FakeUserAchievementRepository userAchievementRepository = new FakeUserAchievementRepository(
                List.of(
                        UserAchievement.restore(
                                new UserAchievementId(1),
                                new UserId(7),
                                new AchievementId(1),
                                LocalDateTime.of(2026, 6, 23, 10, 0)
                        )
                )
        );
        AchievementQueryApplicationService service = new AchievementQueryApplicationService(
                achievementRepository,
                userAchievementRepository
        );

        List<AchievementQueryApplicationService.AchievementView> achievementViews = service.listAll(new UserId(7));

        assertAll(
                () -> assertEquals(2, achievementViews.size()),
                () -> assertEquals(true, achievementViews.get(0).unlocked()),
                () -> assertEquals(false, achievementViews.get(1).unlocked()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 23, 10, 0), achievementViews.get(0).unlockedAt())
        );
    }

    @Test
    void shouldListMyAchievementsOnlyUnlockedOnes() {
        Achievement firstSagaAchievement = achievement(
                1,
                "first_saga",
                "冒险新手",
                "first_saga",
                10
        );
        FakeAchievementRepository achievementRepository = new FakeAchievementRepository(List.of(firstSagaAchievement));
        FakeUserAchievementRepository userAchievementRepository = new FakeUserAchievementRepository(
                List.of(
                        UserAchievement.restore(
                                new UserAchievementId(1),
                                new UserId(7),
                                new AchievementId(1),
                                LocalDateTime.of(2026, 6, 23, 10, 0)
                        )
                )
        );
        AchievementQueryApplicationService service = new AchievementQueryApplicationService(
                achievementRepository,
                userAchievementRepository
        );

        List<AchievementQueryApplicationService.AchievementView> achievementViews = service.listMine(new UserId(7));

        assertAll(
                () -> assertEquals(1, achievementViews.size()),
                () -> assertEquals("first_saga", achievementViews.get(0).achievement().achievementCode().value()),
                () -> assertEquals(true, achievementViews.get(0).unlocked())
        );
    }

    private Achievement achievement(
            long id,
            String code,
            String name,
            String conditionType,
            int experienceReward
    ) {
        return Achievement.restore(
                new AchievementId(id),
                new AchievementCode(code),
                new AchievementName(name),
                new AchievementDescription(""),
                new AchievementIcon("icon"),
                AchievementRarity.COMMON,
                new AchievementConditionType(conditionType),
                1,
                experienceReward,
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
    }

    private record FakeAchievementRepository(List<Achievement> achievements) implements AchievementRepository {

        @Override
        public List<Achievement> findAll() {
            return achievements;
        }

        @Override
        public Optional<Achievement> findByAchievementId(AchievementId achievementId) {
            return achievements.stream()
                    .filter(achievement -> achievement.achievementId().equals(achievementId))
                    .findFirst();
        }

        @Override
        public Optional<Achievement> findByAchievementConditionType(AchievementConditionType achievementConditionType) {
            return achievements.stream()
                    .filter(achievement -> achievement.achievementConditionType().equals(achievementConditionType))
                    .findFirst();
        }
    }

    private record FakeUserAchievementRepository(List<UserAchievement> userAchievements)
            implements UserAchievementRepository {

        @Override
        public List<UserAchievement> findByUserId(UserId userId) {
            return userAchievements.stream()
                    .filter(userAchievement -> userAchievement.userId().equals(userId))
                    .toList();
        }

        @Override
        public Optional<UserAchievement> findByUserIdAndAchievementId(UserId userId, AchievementId achievementId) {
            return userAchievements.stream()
                    .filter(userAchievement -> userAchievement.userId().equals(userId))
                    .filter(userAchievement -> userAchievement.achievementId().equals(achievementId))
                    .findFirst();
        }

        @Override
        public UserAchievement save(UserAchievement userAchievement) {
            return userAchievement;
        }
    }
}
