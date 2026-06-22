package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.CreateSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSagaApplicationServiceTest {

    @Test
    void shouldCreateSagaSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        CreateSagaApplicationService service = new CreateSagaApplicationService(sagaRepository);
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("日本旅行");
        SagaType sagaType = SagaType.TRAVEL;
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 17, 12, 0);
        CreateSagaCommand command = new CreateSagaCommand(
                sagaOwnerId,
                sagaName,
                sagaType,
                "https://example.com/cover.jpg",
                "记录一次日本旅行",
                startedAt
        );

        Saga saga = service.createSaga(command);

        assertAll(
                () -> assertNotNull(saga),
                () -> assertNull(saga.sagaId()),
                () -> assertEquals(sagaOwnerId, saga.sagaOwnerId()),
                () -> assertEquals(sagaName, saga.sagaName()),
                () -> assertEquals(sagaType, saga.sagaType()),
                () -> assertEquals("https://example.com/cover.jpg", saga.coverUrl()),
                () -> assertEquals("记录一次日本旅行", saga.description()),
                () -> assertEquals(startedAt, saga.startedAt()),
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertEquals(0, saga.nodeCount()),
                () -> assertEquals(SagaRarity.COMMON, saga.sagaRarity()),
                () -> assertNull(saga.endedAt()),
                () -> assertEquals(1, sagaRepository.savedCount()),
                () -> assertSame(saga, sagaRepository.lastSavedSaga())
        );
    }

    @Test
    void shouldRejectCreatingSagaWhenCommandIsNull() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        CreateSagaApplicationService service = new CreateSagaApplicationService(sagaRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createSaga(null)
        );

        assertAll(
                () -> assertEquals("创建副本命令不能为空", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.savedCount()),
                () -> assertNull(sagaRepository.lastSavedSaga())
        );
    }

    private static final class InMemorySagaRepository implements SagaRepository {

        private Saga lastSavedSaga;
        private int savedCount;

        @Override
        public Optional<Saga> findBySagaId(SagaId sagaId) {
            return Optional.empty();
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
