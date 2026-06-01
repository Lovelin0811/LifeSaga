package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.repository.NodeFavoriteRepository;
import com.lovelin.lifesaga.repository.NodeRepository;
import com.lovelin.lifesaga.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SagaServiceTest {

    @Test
    void completeMarksSagaFinishedAndUnlocksAchievements() {
        Saga saga = new Saga();
        saga.setId(12L);
        saga.setUserId(99L);
        saga.setType("travel");
        saga.setStatus("active");

        FakeSagaRepository sagaRepository = new FakeSagaRepository();
        sagaRepository.findByIdResult = Optional.of(saga);

        FakeAchievementService achievementService = new FakeAchievementService();
        SagaService sagaService = new SagaService(sagaRepository, new FakeNodeRepository(), new FakeNodeFavoriteRepository(), achievementService);

        Saga completed = sagaService.complete(12L);

        assertEquals("completed", completed.getStatus());
        assertNotNull(completed.getEndedAt());
        assertEquals(1, sagaRepository.updateCount);
        assertEquals("travel", achievementService.completedType);
        assertEquals(99L, achievementService.completedUserId);
    }

    private static final class FakeSagaRepository extends SagaRepository {
        private Optional<Saga> findByIdResult = Optional.empty();
        private int updateCount = 0;

        private FakeSagaRepository() {
            super(null);
        }

        @Override
        public Optional<Saga> findById(Long id) {
            return findByIdResult;
        }

        @Override
        public int update(Saga saga) {
            updateCount++;
            return 1;
        }
    }

    private static final class FakeNodeRepository extends NodeRepository {
        private FakeNodeRepository() {
            super(null);
        }
    }

    private static final class FakeNodeFavoriteRepository extends NodeFavoriteRepository {
        private FakeNodeFavoriteRepository() {
            super(null);
        }

        @Override
        public int deleteBySagaId(Long sagaId) {
            return 1;
        }
    }

    private static final class FakeAchievementService extends AchievementService {
        private Long completedUserId;
        private String completedType;

        private FakeAchievementService() {
            super(null, null, null, null, null);
        }

        @Override
        public List<com.lovelin.lifesaga.model.Achievement> checkAchievementsOnSagaComplete(Long userId, String type) {
            this.completedUserId = userId;
            this.completedType = type;
            return List.of();
        }
    }
}
