package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.UpdateSagaCommand;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSagaApplicationServiceTest {

    @Test
    void shouldUpdateSagaSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        UpdateSagaApplicationService service = new UpdateSagaApplicationService(sagaRepository);
        SagaId sagaId = new SagaId(1);
        Saga saga = createSaga();
        sagaRepository.store(sagaId, saga);
        UpdateSagaCommand command = new UpdateSagaCommand(
                sagaId,
                new SagaOwnerId(1),
                new SagaName("北海道旅行"),
                SagaType.STUDY,
                "https://example.com/updated-cover.jpg",
                "更新后的副本简介"
        );

        Saga updatedSaga = service.updateSaga(command);

        assertAll(
                () -> assertSame(saga, updatedSaga),
                () -> assertEquals(new SagaName("北海道旅行"), updatedSaga.sagaName()),
                () -> assertEquals(SagaType.STUDY, updatedSaga.sagaType()),
                () -> assertEquals("https://example.com/updated-cover.jpg", updatedSaga.coverUrl()),
                () -> assertEquals("更新后的副本简介", updatedSaga.description()),
                () -> assertEquals(1, sagaRepository.savedCount()),
                () -> assertSame(saga, sagaRepository.lastSavedSaga())
        );
    }

    @Test
    void shouldRejectUpdatingSagaWhenCommandIsNull() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        UpdateSagaApplicationService service = new UpdateSagaApplicationService(sagaRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSaga(null)
        );

        assertAll(
                () -> assertEquals("更新副本命令不能为空", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectUpdatingSagaWhenSagaNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        UpdateSagaApplicationService service = new UpdateSagaApplicationService(sagaRepository);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateSaga(createCommand(new SagaId(404), new SagaOwnerId(1)))
        );

        assertAll(
                () -> assertEquals("副本不存在", exception.getMessage()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectUpdatingSagaWhenOwnerDoesNotMatch() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        UpdateSagaApplicationService service = new UpdateSagaApplicationService(sagaRepository);
        SagaId sagaId = new SagaId(1);
        Saga saga = createSaga();
        sagaRepository.store(sagaId, saga);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateSaga(createCommand(sagaId, new SagaOwnerId(2)))
        );

        assertAll(
                () -> assertEquals("无权操作该副本", exception.getMessage()),
                () -> assertEquals(new SagaName("日本旅行"), saga.sagaName()),
                () -> assertEquals(SagaType.TRAVEL, saga.sagaType()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    private UpdateSagaCommand createCommand(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        return new UpdateSagaCommand(
                sagaId,
                sagaOwnerId,
                new SagaName("北海道旅行"),
                SagaType.STUDY,
                "https://example.com/updated-cover.jpg",
                "更新后的副本简介"
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
            storage.remove(sagaId);
        }

        int savedCount() {
            return savedCount;
        }

        Saga lastSavedSaga() {
            return lastSavedSaga;
        }
    }
}
