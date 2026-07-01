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
import com.lovelin.lifesaga.achievement.domain.repository.AchievementProgressRepository;
import com.lovelin.lifesaga.achievement.domain.repository.AchievementRepository;
import com.lovelin.lifesaga.achievement.domain.repository.UserAchievementRepository;
import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserAvatarUrl;
import com.lovelin.lifesaga.identity.domain.model.UserExperience;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserLevel;
import com.lovelin.lifesaga.identity.domain.model.UserNickname;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementUnlockApplicationServiceTest {

    @Test
    void shouldUnlockFirstSagaAndIncreaseExperience() {
        Achievement achievement = achievement(1, "first_saga", "first_saga", 10);
        FakeAchievementRepository achievementRepository = new FakeAchievementRepository(List.of(achievement));
        FakeUserAchievementRepository userAchievementRepository = new FakeUserAchievementRepository();
        FakeAchievementProgressRepository achievementProgressRepository = new FakeAchievementProgressRepository();
        FakeUserRepository userRepository = new FakeUserRepository();
        User user = User.restore(
                new UserId(7),
                new UserOpenId("wx_openid_001"),
                new UserNickname(""),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(0),
                LocalDateTime.of(2026, 6, 23, 10, 0),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
        userRepository.user = user;
        AchievementUnlockApplicationService service = new AchievementUnlockApplicationService(
                achievementRepository,
                userAchievementRepository,
                achievementProgressRepository,
                userRepository,
                Clock.fixed(Instant.parse("2026-06-23T03:30:00Z"), ZoneId.of("Asia/Shanghai"))
        );

        List<Achievement> unlockedAchievements = service.checkOnSagaCreate(new UserId(7));

        assertAll(
                () -> assertEquals(1, unlockedAchievements.size()),
                () -> assertEquals("first_saga", unlockedAchievements.get(0).achievementCode().value()),
                () -> assertEquals(1, userAchievementRepository.userAchievements.size()),
                () -> assertEquals(10, userRepository.user.userExperience().value())
        );
    }

    @Test
    void shouldUnlockFirstPhotoWhenSagaHasPhotos() {
        Achievement achievement = achievement(2, "first_photo", "first_photo", 10);
        FakeAchievementRepository achievementRepository = new FakeAchievementRepository(List.of(achievement));
        FakeUserAchievementRepository userAchievementRepository = new FakeUserAchievementRepository();
        FakeAchievementProgressRepository achievementProgressRepository = new FakeAchievementProgressRepository();
        achievementProgressRepository.sagaHasPhotos = true;
        FakeUserRepository userRepository = new FakeUserRepository();
        userRepository.user = User.restore(
                new UserId(7),
                new UserOpenId("wx_openid_001"),
                new UserNickname(""),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(0),
                LocalDateTime.of(2026, 6, 23, 10, 0),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
        AchievementUnlockApplicationService service = new AchievementUnlockApplicationService(
                achievementRepository,
                userAchievementRepository,
                achievementProgressRepository,
                userRepository,
                Clock.fixed(Instant.parse("2026-06-23T03:30:00Z"), ZoneId.of("Asia/Shanghai"))
        );

        List<Achievement> unlockedAchievements = service.checkOnNodeCreate(new UserId(7), new SagaId(1));

        assertAll(
                () -> assertEquals(1, unlockedAchievements.size()),
                () -> assertEquals("first_photo", unlockedAchievements.get(0).achievementCode().value()),
                () -> assertEquals(10, userRepository.user.userExperience().value())
        );
    }

    @Test
    void shouldTreatRelationshipAsCreativeSlotForCompletionist() {
        Achievement achievement = achievement(3, "completionist", "all_types_completed", 200);
        FakeAchievementRepository achievementRepository = new FakeAchievementRepository(List.of(achievement));
        FakeUserAchievementRepository userAchievementRepository = new FakeUserAchievementRepository();
        FakeAchievementProgressRepository achievementProgressRepository = new FakeAchievementProgressRepository();
        achievementProgressRepository.completedLife = 1;
        achievementProgressRepository.completedTravel = 1;
        achievementProgressRepository.completedStudy = 1;
        achievementProgressRepository.completedWork = 1;
        achievementProgressRepository.completedHealth = 1;
        achievementProgressRepository.completedRelationship = 1;
        FakeUserRepository userRepository = new FakeUserRepository();
        userRepository.user = User.restore(
                new UserId(7),
                new UserOpenId("wx_openid_001"),
                new UserNickname(""),
                new UserAvatarUrl(""),
                new UserLevel(1),
                new UserExperience(0),
                LocalDateTime.of(2026, 6, 23, 10, 0),
                LocalDateTime.of(2026, 6, 23, 10, 0)
        );
        AchievementUnlockApplicationService service = new AchievementUnlockApplicationService(
                achievementRepository,
                userAchievementRepository,
                achievementProgressRepository,
                userRepository,
                Clock.fixed(Instant.parse("2026-06-23T03:30:00Z"), ZoneId.of("Asia/Shanghai"))
        );

        List<Achievement> unlockedAchievements = service.checkOnSagaCreate(new UserId(7));

        assertAll(
                () -> assertEquals(1, unlockedAchievements.size()),
                () -> assertEquals("completionist", unlockedAchievements.get(0).achievementCode().value()),
                () -> assertEquals(200, userRepository.user.userExperience().value())
        );
    }

    private Achievement achievement(long id, String code, String conditionType, int experienceReward) {
        return Achievement.restore(
                new AchievementId(id),
                new AchievementCode(code),
                new AchievementName(code),
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

    private static final class FakeUserAchievementRepository implements UserAchievementRepository {

        private final List<UserAchievement> userAchievements = new ArrayList<>();

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
            userAchievements.add(userAchievement);
            return userAchievement;
        }
    }

    private static final class FakeAchievementProgressRepository implements AchievementProgressRepository {

        private boolean sagaHasPhotos;
        private int completedLife;
        private int completedTravel;
        private int completedStudy;
        private int completedWork;
        private int completedHealth;
        private int completedRelationship;
        private int completedCreative;

        @Override
        public int countSagasByUserId(UserId userId) {
            return 0;
        }

        @Override
        public List<SagaType> findDistinctSagaTypesByUserId(UserId userId) {
            return List.of();
        }

        @Override
        public int countCompletedSagasByType(UserId userId, SagaType sagaType) {
            return switch (sagaType) {
                case LIFE -> completedLife;
                case TRAVEL -> completedTravel;
                case STUDY -> completedStudy;
                case WORK -> completedWork;
                case HEALTH -> completedHealth;
                case RELATIONSHIP -> completedRelationship;
                case CREATIVE -> completedCreative;
            };
        }

        @Override
        public boolean hasSagaWithRarity(UserId userId, SagaRarity sagaRarity) {
            return false;
        }

        @Override
        public boolean sagaHasPhotos(SagaId sagaId) {
            return sagaHasPhotos;
        }

        @Override
        public List<LocalDate> findRecentNodeDates(UserId userId, int limit) {
            return List.of();
        }
    }

    private static final class FakeUserRepository implements UserRepository {

        private User user;

        @Override
        public Optional<User> findByUserId(UserId userId) {
            return Optional.ofNullable(user);
        }

        @Override
        public Optional<User> findByUserOpenId(UserOpenId userOpenId) {
            return Optional.empty();
        }

        @Override
        public User save(User user) {
            this.user = user;
            return user;
        }
    }
}
