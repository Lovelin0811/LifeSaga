package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.DeleteSagaNodeCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeFavoriteRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSagaNodeApplicationServiceTest {

    @Test
    void shouldDeleteSagaNodeSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        InMemorySagaNodeFavoriteRepository sagaNodeFavoriteRepository = new InMemorySagaNodeFavoriteRepository();
        DeleteSagaNodeApplicationService service = new DeleteSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository,
                sagaNodeFavoriteRepository
        );
        Saga saga = createSagaWithNode();
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        sagaRepository.store(sagaId, saga);
        sagaNodeRepository.store(sagaNodeId, createSagaNode(sagaId));

        service.deleteSagaNode(new DeleteSagaNodeCommand(sagaId, sagaNodeId, new SagaOwnerId(1)));

        assertAll(
                () -> assertEquals(0, saga.nodeCount()),
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertNull(saga.endedAt()),
                () -> assertEquals(1, sagaNodeRepository.deletedCount()),
                () -> assertEquals(sagaNodeId, sagaNodeRepository.lastDeletedSagaNodeId()),
                () -> assertEquals(sagaNodeId, sagaNodeFavoriteRepository.deletedSagaNodeId),
                () -> assertEquals(1, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaNodeWhenCommandIsNull() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        DeleteSagaNodeApplicationService service = createService(sagaRepository, sagaNodeRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteSagaNode(null)
        );

        assertAll(
                () -> assertEquals("删除节点命令不能为空", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.deletedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaNodeWhenSagaNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        DeleteSagaNodeApplicationService service = createService(sagaRepository, sagaNodeRepository);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteSagaNode(
                        new DeleteSagaNodeCommand(new SagaId(404), new SagaNodeId(10), new SagaOwnerId(1))
                )
        );

        assertAll(
                () -> assertEquals("副本不存在", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.deletedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaNodeWhenSagaNodeNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        DeleteSagaNodeApplicationService service = createService(sagaRepository, sagaNodeRepository);
        Saga saga = createSagaWithNode();
        sagaRepository.store(new SagaId(1), saga);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteSagaNode(
                        new DeleteSagaNodeCommand(new SagaId(1), new SagaNodeId(404), new SagaOwnerId(1))
                )
        );

        assertAll(
                () -> assertEquals("节点不存在", exception.getMessage()),
                () -> assertEquals(1, saga.nodeCount()),
                () -> assertEquals(0, sagaNodeRepository.deletedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaNodeWhenSagaNodeBelongsToAnotherSaga() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        DeleteSagaNodeApplicationService service = createService(sagaRepository, sagaNodeRepository);
        Saga saga = createSagaWithNode();
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        sagaRepository.store(sagaId, saga);
        sagaNodeRepository.store(sagaNodeId, createSagaNode(new SagaId(2)));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteSagaNode(new DeleteSagaNodeCommand(sagaId, sagaNodeId, new SagaOwnerId(1)))
        );

        assertAll(
                () -> assertEquals("节点不存在", exception.getMessage()),
                () -> assertEquals(1, saga.nodeCount()),
                () -> assertEquals(0, sagaNodeRepository.deletedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectDeletingSagaNodeWhenOwnerDoesNotMatch() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        DeleteSagaNodeApplicationService service = createService(sagaRepository, sagaNodeRepository);
        Saga saga = createSagaWithNode();
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        sagaRepository.store(sagaId, saga);
        sagaNodeRepository.store(sagaNodeId, createSagaNode(sagaId));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteSagaNode(new DeleteSagaNodeCommand(sagaId, sagaNodeId, new SagaOwnerId(2)))
        );

        assertAll(
                () -> assertEquals("无权操作该副本", exception.getMessage()),
                () -> assertEquals(1, saga.nodeCount()),
                () -> assertEquals(0, sagaNodeRepository.deletedCount()),
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

    private SagaNode createSagaNode(SagaId sagaId) {
        return SagaNode.create(
                sagaId,
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1)
        );
    }

    private DeleteSagaNodeApplicationService createService(
            InMemorySagaRepository sagaRepository,
            InMemorySagaNodeRepository sagaNodeRepository
    ) {
        return new DeleteSagaNodeApplicationService(
                sagaRepository,
                sagaNodeRepository,
                new InMemorySagaNodeFavoriteRepository()
        );
    }

    private static final class InMemorySagaRepository implements SagaRepository {

        private final Map<SagaId, Saga> storage = new LinkedHashMap<>();
        private int savedCount;

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
            savedCount++;
            return saga;
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
            storage.remove(sagaId);
        }

        int savedCount() {
            return savedCount;
        }
    }

    private static final class InMemorySagaNodeRepository implements SagaNodeRepository {

        private final Map<SagaNodeId, SagaNode> storage = new LinkedHashMap<>();
        private SagaNodeId lastDeletedSagaNodeId;
        private int deletedCount;

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
            return sagaNode;
        }

        @Override
        public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
            deletedCount++;
            lastDeletedSagaNodeId = sagaNodeId;
            storage.remove(sagaNodeId);
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
            storage.values().removeIf(sagaNode -> sagaNode.sagaId().equals(sagaId));
        }

        int deletedCount() {
            return deletedCount;
        }

        SagaNodeId lastDeletedSagaNodeId() {
            return lastDeletedSagaNodeId;
        }
    }

    private static final class InMemorySagaNodeFavoriteRepository implements SagaNodeFavoriteRepository {

        private SagaNodeId deletedSagaNodeId;

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
            deletedSagaNodeId = sagaNodeId;
        }

        @Override
        public void deleteBySagaId(SagaId sagaId) {
        }
    }
}
