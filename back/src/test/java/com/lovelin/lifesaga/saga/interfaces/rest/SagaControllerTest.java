package com.lovelin.lifesaga.saga.interfaces.rest;

import com.lovelin.lifesaga.achievement.application.service.AchievementUnlockUseCase;
import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.saga.application.service.CreateSagaApplicationService;
import com.lovelin.lifesaga.saga.application.service.CompleteSagaApplicationService;
import com.lovelin.lifesaga.saga.application.service.DeleteSagaApplicationService;
import com.lovelin.lifesaga.saga.application.service.SagaQueryApplicationService;
import com.lovelin.lifesaga.saga.application.service.UpdateSagaApplicationService;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeFavoriteRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SagaControllerTest {

    @Test
    void shouldCreateSagaForCurrentUser() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-06-22T10:00:00Z"),
                ZoneId.of("Asia/Shanghai")
        );
        FakeSagaRepository sagaRepository = new FakeSagaRepository();
        SagaController sagaController = createSagaController(sagaRepository, fixedClock);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 7L);

        SagaController.ApiResponse<SagaController.SagaResponse> response = sagaController.create(
                new SagaController.CreateSagaRequest(
                        "日本旅行",
                        "travel",
                        "https://example.com/cover.jpg",
                        "旅行记录",
                        false
                ),
                httpServletRequest
        );

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals("success", response.message()),
                () -> assertEquals(100L, response.data().id()),
                () -> assertEquals(7L, response.data().ownerId()),
                () -> assertEquals("日本旅行", response.data().name()),
                () -> assertEquals("TRAVEL", response.data().type()),
                () -> assertEquals("ACTIVE", response.data().status()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 22, 18, 0), response.data().startedAt()),
                () -> assertEquals(new SagaOwnerId(7), sagaRepository.savedSaga.sagaOwnerId())
        );
    }

    @Test
    void shouldUpdateSagaForCurrentUser() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-06-22T10:00:00Z"), ZoneId.of("Asia/Shanghai"));
        FakeSagaRepository sagaRepository = new FakeSagaRepository();
        sagaRepository.sagaToFind = Saga.restore(
                new SagaId(101),
                new SagaOwnerId(7),
                new SagaName("旧名称"),
                SagaType.LIFE,
                null,
                null,
                SagaStatus.ACTIVE,
                false,
                0,
                SagaRarity.COMMON,
                LocalDateTime.of(2026, 6, 20, 10, 0),
                null
        );
        SagaController sagaController = createSagaController(sagaRepository, fixedClock);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 7L);

        SagaController.ApiResponse<SagaController.SagaResponse> response = sagaController.update(
                101L,
                new SagaController.UpdateSagaRequest(
                        "新名称",
                        "study",
                        "https://example.com/new-cover.jpg",
                        "新简介",
                        true
                ),
                httpServletRequest
        );

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals(101L, response.data().id()),
                () -> assertEquals("新名称", response.data().name()),
                () -> assertEquals("STUDY", response.data().type()),
                () -> assertEquals("https://example.com/new-cover.jpg", response.data().coverUrl()),
                () -> assertEquals("新简介", response.data().description())
        );
    }

    @Test
    void shouldCompleteSagaForCurrentUser() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-06-22T10:00:00Z"), ZoneId.of("Asia/Shanghai"));
        FakeSagaRepository sagaRepository = new FakeSagaRepository();
        sagaRepository.sagaToFind = Saga.restore(
                new SagaId(102),
                new SagaOwnerId(7),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                SagaStatus.ACTIVE,
                false,
                1,
                SagaRarity.COMMON,
                LocalDateTime.of(2026, 6, 20, 10, 0),
                null
        );
        SagaController sagaController = createSagaController(sagaRepository, fixedClock);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 7L);

        SagaController.ApiResponse<SagaController.SagaResponse> response = sagaController.complete(102L, httpServletRequest);

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals(102L, response.data().id()),
                () -> assertEquals("COMPLETED", response.data().status()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 22, 18, 0), response.data().endedAt())
        );
    }

    @Test
    void shouldDeleteSagaForCurrentUser() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-06-22T10:00:00Z"), ZoneId.of("Asia/Shanghai"));
        FakeSagaRepository sagaRepository = new FakeSagaRepository();
        sagaRepository.sagaToFind = Saga.restore(
                new SagaId(103),
                new SagaOwnerId(7),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                SagaStatus.ACTIVE,
                false,
                0,
                SagaRarity.COMMON,
                LocalDateTime.of(2026, 6, 20, 10, 0),
                null
        );
        SagaController sagaController = createSagaController(sagaRepository, fixedClock);
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setAttribute("userId", 7L);

        SagaController.ApiResponse<Void> response = sagaController.delete(103L, httpServletRequest);

        assertAll(
                () -> assertEquals(200, response.code()),
                () -> assertEquals("success", response.message()),
                () -> assertEquals(new SagaId(103), sagaRepository.deletedSagaId)
        );
    }

    private SagaController createSagaController(FakeSagaRepository sagaRepository, Clock clock) {
        return new SagaController(
                new CreateSagaApplicationService(
                        sagaRepository,
                        new NoOpAchievementUnlockUseCase(),
                        clock
                ),
                new UpdateSagaApplicationService(sagaRepository),
                new CompleteSagaApplicationService(
                        sagaRepository,
                        new NoOpAchievementUnlockUseCase(),
                        clock
                ),
                new DeleteSagaApplicationService(
                        sagaRepository,
                        new FakeSagaNodeRepository(),
                        new FakeSagaNodeFavoriteRepository()
                ),
                new SagaQueryApplicationService(
                        sagaRepository,
                        new FakeSagaNodeRepository(),
                        new FakeSagaNodeFavoriteRepository()
                )
        );
    }

    private static final class FakeSagaRepository implements SagaRepository {

        private Saga savedSaga;
        private Saga sagaToFind;
        private SagaId deletedSagaId;

        @Override
        public Optional<Saga> findBySagaId(SagaId sagaId) {
            return Optional.ofNullable(sagaToFind);
        }

        @Override
        public java.util.List<Saga> findBySagaOwnerId(SagaOwnerId sagaOwnerId) {
            return java.util.List.of();
        }

        @Override
        public java.util.List<Saga> findPublic() {
            return java.util.List.of();
        }

        @Override
        public Saga save(Saga saga) {
            savedSaga = saga;
            if (saga.sagaId() != null) {
                return saga;
            }
            return Saga.restore(
                    new SagaId(100),
                    saga.sagaOwnerId(),
                    new SagaName(saga.sagaName().value()),
                    saga.sagaType(),
                    saga.coverUrl(),
                    saga.description(),
                    SagaStatus.ACTIVE,
                    saga.publicVisible(),
                    0,
                    SagaRarity.COMMON,
                    saga.startedAt(),
                    null
            );
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
            deletedSagaId = sagaId;
        }
    }

    private static final class FakeSagaNodeRepository implements SagaNodeRepository {

        @Override
        public Optional<SagaNode> findBySagaNodeId(SagaNodeId sagaNodeId) {
            return Optional.empty();
        }

        @Override
        public java.util.List<SagaNode> findBySagaId(SagaId sagaId) {
            return java.util.List.of();
        }

        @Override
        public SagaNode save(SagaNode sagaNode) {
            return sagaNode;
        }

        @Override
        public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
        }
    }

    private static final class FakeSagaNodeFavoriteRepository implements SagaNodeFavoriteRepository {

        @Override
        public boolean isFavorited(SagaOwnerId sagaOwnerId, SagaNodeId sagaNodeId) {
            return false;
        }

        @Override
        public boolean toggle(SagaOwnerId sagaOwnerId, SagaNodeId sagaNodeId) {
            return true;
        }

        @Override
        public java.util.List<SagaNodeId> findFavoritedSagaNodeIds(
                SagaOwnerId sagaOwnerId,
                java.util.List<SagaNodeId> sagaNodeIds
        ) {
            return java.util.List.of();
        }

        @Override
        public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
        }
    }

    private static final class NoOpAchievementUnlockUseCase implements AchievementUnlockUseCase {

        @Override
        public java.util.List<Achievement> checkOnSagaCreate(UserId userId) {
            return java.util.List.of();
        }

        @Override
        public java.util.List<Achievement> checkOnSagaComplete(UserId userId, SagaType sagaType) {
            return java.util.List.of();
        }

        @Override
        public java.util.List<Achievement> checkOnNodeCreate(UserId userId, SagaId sagaId) {
            return java.util.List.of();
        }
    }
}
