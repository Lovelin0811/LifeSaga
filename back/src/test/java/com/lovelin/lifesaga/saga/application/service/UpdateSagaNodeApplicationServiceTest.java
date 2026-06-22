package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.UpdateSagaNodeCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeLocation;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodePhotos;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTime;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSagaNodeApplicationServiceTest {

    @Test
    void shouldUpdateSagaNodeSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        UpdateSagaNodeApplicationService service = new UpdateSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository
        );
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        SagaNode sagaNode = createSagaNode(sagaId);
        sagaRepository.store(sagaId, createSaga());
        sagaNodeRepository.store(sagaNodeId, sagaNode);

        SagaNode updatedSagaNode = service.updateSagaNode(createCommand(sagaId, sagaNodeId, new SagaOwnerId(1)));

        assertAll(
                () -> assertSame(sagaNode, updatedSagaNode),
                () -> assertEquals(new SagaNodeTitle("第二次旅行"), updatedSagaNode.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeOrder(2), updatedSagaNode.sagaNodeOrder()),
                () -> assertEquals(new SagaNodeDescription("更新后的节点描述"), updatedSagaNode.sagaNodeDescription()),
                () -> assertEquals(new SagaNodeLocation("东京塔"), updatedSagaNode.sagaNodeLocation()),
                () -> assertEquals(
                        new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                        updatedSagaNode.sagaNodePhotos()
                ),
                () -> assertEquals(
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        updatedSagaNode.sagaNodeTime()
                ),
                () -> assertEquals(true, updatedSagaNode.milestone()),
                () -> assertEquals(1, sagaNodeRepository.savedCount()),
                () -> assertSame(sagaNode, sagaNodeRepository.lastSavedSagaNode())
        );
    }

    @Test
    void shouldRejectUpdatingSagaNodeWhenCommandIsNull() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        UpdateSagaNodeApplicationService service = new UpdateSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateSagaNode(null)
        );

        assertAll(
                () -> assertEquals("更新节点命令不能为空", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.savedCount())
        );
    }

    @Test
    void shouldRejectUpdatingSagaNodeWhenSagaNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        UpdateSagaNodeApplicationService service = new UpdateSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateSagaNode(createCommand(new SagaId(404), new SagaNodeId(10), new SagaOwnerId(1)))
        );

        assertAll(
                () -> assertEquals("副本不存在", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.savedCount())
        );
    }

    @Test
    void shouldRejectUpdatingSagaNodeWhenSagaNodeNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        UpdateSagaNodeApplicationService service = new UpdateSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository
        );
        SagaId sagaId = new SagaId(1);
        sagaRepository.store(sagaId, createSaga());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateSagaNode(createCommand(sagaId, new SagaNodeId(404), new SagaOwnerId(1)))
        );

        assertAll(
                () -> assertEquals("节点不存在", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.savedCount())
        );
    }

    @Test
    void shouldRejectUpdatingSagaNodeWhenSagaNodeBelongsToAnotherSaga() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        UpdateSagaNodeApplicationService service = new UpdateSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository
        );
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        SagaNode sagaNode = createSagaNode(new SagaId(2));
        sagaRepository.store(sagaId, createSaga());
        sagaNodeRepository.store(sagaNodeId, sagaNode);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateSagaNode(createCommand(sagaId, sagaNodeId, new SagaOwnerId(1)))
        );

        assertAll(
                () -> assertEquals("节点不存在", exception.getMessage()),
                () -> assertEquals(new SagaNodeTitle("第一次旅行"), sagaNode.sagaNodeTitle()),
                () -> assertEquals(false, sagaNode.milestone()),
                () -> assertEquals(0, sagaNodeRepository.savedCount())
        );
    }

    @Test
    void shouldRejectUpdatingSagaNodeWhenOwnerDoesNotMatch() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        UpdateSagaNodeApplicationService service = new UpdateSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository
        );
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        SagaNode sagaNode = createSagaNode(sagaId);
        sagaRepository.store(sagaId, createSaga());
        sagaNodeRepository.store(sagaNodeId, sagaNode);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateSagaNode(createCommand(sagaId, sagaNodeId, new SagaOwnerId(2)))
        );

        assertAll(
                () -> assertEquals("无权操作该副本", exception.getMessage()),
                () -> assertEquals(new SagaNodeTitle("第一次旅行"), sagaNode.sagaNodeTitle()),
                () -> assertEquals(false, sagaNode.milestone()),
                () -> assertEquals(0, sagaNodeRepository.savedCount())
        );
    }

    private UpdateSagaNodeCommand createCommand(
            SagaId sagaId,
            SagaNodeId sagaNodeId,
            SagaOwnerId sagaOwnerId
    ) {
        return new UpdateSagaNodeCommand(
                sagaId,
                sagaNodeId,
                sagaOwnerId,
                new SagaNodeTitle("第二次旅行"),
                new SagaNodeOrder(2),
                new SagaNodeDescription("更新后的节点描述"),
                new SagaNodeLocation("东京塔"),
                new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                true
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

    private SagaNode createSagaNode(SagaId sagaId) {
        return SagaNode.create(
                sagaId,
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1),
                null,
                new SagaNodeLocation("大阪"),
                null,
                new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
        );
    }

    private static final class InMemorySagaRepository implements SagaRepository {

        private final Map<SagaId, Saga> storage = new LinkedHashMap<>();

        void store(SagaId sagaId, Saga saga) {
            storage.put(sagaId, saga);
        }

        @Override
        public Optional<Saga> findBySagaId(SagaId sagaId) {
            return Optional.ofNullable(storage.get(sagaId));
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
            return saga;
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
            storage.remove(sagaId);
        }
    }

    private static final class InMemorySagaNodeRepository implements SagaNodeRepository {

        private final Map<SagaNodeId, SagaNode> storage = new LinkedHashMap<>();
        private SagaNode lastSavedSagaNode;
        private int savedCount;

        void store(SagaNodeId sagaNodeId, SagaNode sagaNode) {
            storage.put(sagaNodeId, sagaNode);
        }

        @Override
        public Optional<SagaNode> findBySagaNodeId(SagaNodeId sagaNodeId) {
            return Optional.ofNullable(storage.get(sagaNodeId));
        }

        @Override
        public java.util.List<SagaNode> findBySagaId(SagaId sagaId) {
            return storage.values().stream()
                    .filter(sagaNode -> sagaNode.sagaId().equals(sagaId))
                    .toList();
        }

        @Override
        public SagaNode save(SagaNode sagaNode) {
            savedCount++;
            lastSavedSagaNode = sagaNode;
            return sagaNode;
        }

        @Override
        public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
            storage.remove(sagaNodeId);
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
            storage.values().removeIf(sagaNode -> sagaNode.sagaId().equals(sagaId));
        }

        int savedCount() {
            return savedCount;
        }

        SagaNode lastSavedSagaNode() {
            return lastSavedSagaNode;
        }
    }
}
