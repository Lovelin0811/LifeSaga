package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.DeleteSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSagaApplicationServiceTest {

    @Test
    void shouldDeleteSagaSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        DeleteSagaApplicationService service = new DeleteSagaApplicationService(sagaRepository);
        SagaId sagaId = new SagaId(1);
        sagaRepository.store(sagaId, createSaga());

        service.deleteSaga(new DeleteSagaCommand(sagaId, new SagaOwnerId(1)));

        assertAll(
                () -> assertEquals(1, sagaRepository.deletedCount()),
                () -> assertEquals(sagaId, sagaRepository.lastDeletedSagaId()),
                () -> assertEquals(Optional.empty(), sagaRepository.findBySagaId(sagaId))
        );
    }

    @Test
    void shouldRejectDeletingSagaWhenCommandIsNull() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        DeleteSagaApplicationService service = new DeleteSagaApplicationService(sagaRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteSaga(null)
        );

        assertAll(
                () -> assertEquals("删除副本命令不能为空", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.deletedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaWhenSagaNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        DeleteSagaApplicationService service = new DeleteSagaApplicationService(sagaRepository);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteSaga(new DeleteSagaCommand(new SagaId(404), new SagaOwnerId(1)))
        );

        assertAll(
                () -> assertEquals("副本不存在", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.deletedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaWhenOwnerDoesNotMatch() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        DeleteSagaApplicationService service = new DeleteSagaApplicationService(sagaRepository);
        SagaId sagaId = new SagaId(1);
        sagaRepository.store(sagaId, createSaga());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteSaga(new DeleteSagaCommand(sagaId, new SagaOwnerId(2)))
        );

        assertAll(
                () -> assertEquals("无权操作该副本", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.deletedCount()),
                () -> assertTrue(sagaRepository.findBySagaId(sagaId).isPresent())
        );
    }

    private Saga createSaga() {
        return Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 17, 12, 0)
        );
    }

    private static final class InMemorySagaRepository implements SagaRepository {

        private final Map<SagaId, Saga> storage = new LinkedHashMap<>();
        private SagaId lastDeletedSagaId;
        private int deletedCount;

        void store(SagaId sagaId, Saga saga) {
            storage.put(sagaId, saga);
        }

        @Override
        public Optional<Saga> findBySagaId(SagaId sagaId) {
            return Optional.ofNullable(storage.get(sagaId));
        }

        @Override
        public Saga save(Saga saga) {
            return saga;
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
            deletedCount++;
            lastDeletedSagaId = sagaId;
            storage.remove(sagaId);
        }

        int deletedCount() {
            return deletedCount;
        }

        SagaId lastDeletedSagaId() {
            return lastDeletedSagaId;
        }
    }
}
