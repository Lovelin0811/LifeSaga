package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.CompleteSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompleteSagaApplicationServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-06-17T04:00:00Z"),
            ZoneId.of("Asia/Shanghai")
    );

    @Test
    void shouldCompleteSagaSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        CompleteSagaApplicationService service = new CompleteSagaApplicationService(sagaRepository, FIXED_CLOCK);
        Saga saga = createSagaWithNode();
        sagaRepository.store(new SagaId(1), saga);
        CompleteSagaCommand command = new CompleteSagaCommand(new SagaId(1), new SagaOwnerId(1));

        Saga completedSaga = service.completeSaga(command);

        assertAll(
                () -> assertSame(saga, completedSaga),
                () -> assertEquals(SagaStatus.COMPLETED, completedSaga.sagaStatus()),
                () -> assertEquals(LocalDateTime.of(2026, 6, 17, 12, 0), completedSaga.endedAt()),
                () -> assertEquals(1, completedSaga.nodeCount()),
                () -> assertEquals(1, sagaRepository.savedCount()),
                () -> assertSame(saga, sagaRepository.lastSavedSaga())
        );
    }

    @Test
    void shouldRejectCompletingSagaWhenSagaNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        CompleteSagaApplicationService service = new CompleteSagaApplicationService(sagaRepository, FIXED_CLOCK);
        CompleteSagaCommand command = new CompleteSagaCommand(new SagaId(404), new SagaOwnerId(1));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.completeSaga(command)
        );

        assertAll(
                () -> assertEquals("副本不存在", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectCompletingSagaWhenOwnerDoesNotMatch() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        CompleteSagaApplicationService service = new CompleteSagaApplicationService(sagaRepository, FIXED_CLOCK);
        Saga saga = createSagaWithNode();
        sagaRepository.store(new SagaId(1), saga);
        CompleteSagaCommand command = new CompleteSagaCommand(new SagaId(1), new SagaOwnerId(2));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.completeSaga(command)
        );

        assertAll(
                () -> assertEquals("无权操作该副本", exception.getMessage()),
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    private Saga createSagaWithNode() {
        Saga saga = Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 16, 12, 0)
        );
        saga.recordNodeAdded();
        return saga;
    }

    private static final class InMemorySagaRepository implements SagaRepository {

        private final Map<SagaId, Saga> storage = new LinkedHashMap<>();
        private Saga lastSavedSaga;
        private int savedCount;

        void store(SagaId sagaId, Saga saga) {
            storage.put(sagaId, saga);
        }

        @Override
        public Optional<Saga> findBySagaId(SagaId sagaId) {
            return Optional.ofNullable(storage.get(sagaId));
        }

        @Override
        public Saga save(Saga saga) {
            savedCount++;
            lastSavedSaga = saga;
            return saga;
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
        }

        int savedCount() {
            return savedCount;
        }

        Saga lastSavedSaga() {
            return lastSavedSaga;
        }
    }
}
