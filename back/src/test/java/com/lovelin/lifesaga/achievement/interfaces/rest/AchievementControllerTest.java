package com.lovelin.lifesaga.achievement.interfaces.rest;

import com.lovelin.lifesaga.achievement.application.service.AchievementQueryApplicationService;
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
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementControllerTest {

    @Test
    void shouldListAchievementsForCurrentUser() {
        AchievementController achievementController = createAchievementController(
                List.of(
                        achievement(1, "first_saga", "冒险新手", "first_saga"),
                        achievement(2, "first_node", "记录者", "first_node")
                ),
                List.of(
                        UserAchievement.restore(
                                new UserAchievementId(1),
                                new UserId(7),
                                new AchievementId(1),
                                LocalDateTime.of(2026, 6, 23, 10, 0)
                        )
                )
        );
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 7L);

        AchievementController.ApiResponse<List<AchievementController.AchievementResponse>> response =
                achievementController.list(httpServletRequest);

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals(2, response.data().size()),
                () -> assertEquals("first_saga", response.data().get(0).code()),
                () -> assertEquals(true, response.data().get(0).unlocked()),
                () -> assertEquals(false, response.data().get(1).unlocked())
        );
    }

    @Test
    void shouldListMyAchievementsForCurrentUser() {
        AchievementController achievementController = createAchievementController(
                List.of(achievement(1, "first_saga", "冒险新手", "first_saga")),
                List.of(
                        UserAchievement.restore(
                                new UserAchievementId(1),
                                new UserId(7),
                                new AchievementId(1),
                                LocalDateTime.of(2026, 6, 23, 10, 0)
                        )
                )
        );
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 7L);

        AchievementController.ApiResponse<List<AchievementController.AchievementResponse>> response =
                achievementController.my(httpServletRequest);

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals(1, response.data().size()),
                () -> assertEquals("first_saga", response.data().get(0).code()),
                () -> assertEquals(true, response.data().get(0).unlocked()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 23, 10, 0), response.data().get(0).unlockedAt())
        );
    }

    private AchievementController createAchievementController(
            List<Achievement> achievements,
            List<UserAchievement> userAchievements
    ) {
        return new AchievementController(
                new AchievementQueryApplicationService(
                        new FakeAchievementRepository(achievements),
                        new FakeUserAchievementRepository(userAchievements)
                )
        );
    }

    private Achievement achievement(long id, String code, String name, String conditionType) {
        return Achievement.restore(
                new AchievementId(id),
                new AchievementCode(code),
                new AchievementName(name),
                new AchievementDescription(""),
                new AchievementIcon("icon"),
                AchievementRarity.COMMON,
                new AchievementConditionType(conditionType),
                1,
                10,
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
