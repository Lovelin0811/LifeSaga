package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.AddSagaNodeCommand;
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
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddSagaNodeApplicationServiceTest {

    @Test
    void shouldAddSagaNodeSuccessfully() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        AddSagaNodeApplicationService service = new AddSagaNodeApplicationService(sagaRepository, sagaNodeRepository);

        Saga saga = Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 16, 12, 0)
        );
        sagaRepository.store(new SagaId(1), saga);

        AddSagaNodeCommand command = new AddSagaNodeCommand(
                new SagaId(1),
                new SagaOwnerId(1),
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1),
                new SagaNodeDescription("第一次旅行的记录"),
                new SagaNodeLocation("东京塔"),
                new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
        );

        SagaNode sagaNode = service.addSagaNode(command);

        assertAll(
                () -> assertNotNull(sagaNode),
                () -> assertEquals(new SagaId(1), sagaNode.sagaId()),
                () -> assertEquals(new SagaNodeTitle("第一次旅行"), sagaNode.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeOrder(1), sagaNode.sagaNodeOrder()),
                () -> assertEquals(new SagaNodeDescription("第一次旅行的记录"), sagaNode.sagaNodeDescription()),
                () -> assertEquals(new SagaNodeLocation("东京塔"), sagaNode.sagaNodeLocation()),
                () -> assertEquals(
                        new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                        sagaNode.sagaNodePhotos()
                ),
                () -> assertEquals(
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0)),
                        sagaNode.sagaNodeTime()
                ),
                () -> assertEquals(1, sagaRepository.findBySagaId(new SagaId(1)).orElseThrow().nodeCount()),
                () -> assertEquals(1, sagaNodeRepository.savedCount()),
                () -> assertEquals(sagaNode, sagaNodeRepository.lastSavedSagaNode())
        );
    }

    @Test
    void shouldRejectAddingSagaNodeWhenCommandIsNull() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        AddSagaNodeApplicationService service = new AddSagaNodeApplicationService(sagaRepository, sagaNodeRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addSagaNode(null)
        );

        assertAll(
                () -> assertEquals("添加节点命令不能为空", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.savedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectAddingSagaNodeWhenSagaNotFound() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        AddSagaNodeApplicationService service = new AddSagaNodeApplicationService(sagaRepository, sagaNodeRepository);
        AddSagaNodeCommand command = createCommand(new SagaId(404), new SagaOwnerId(1));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.addSagaNode(command)
        );

        assertAll(
                () -> assertEquals("副本不存在", exception.getMessage()),
                () -> assertEquals(0, sagaNodeRepository.savedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldRejectAddingSagaNodeWhenOwnerDoesNotMatch() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        AddSagaNodeApplicationService service = new AddSagaNodeApplicationService(sagaRepository, sagaNodeRepository);
        Saga saga = Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 16, 12, 0)
        );
        sagaRepository.store(new SagaId(1), saga);
        AddSagaNodeCommand command = createCommand(new SagaId(1), new SagaOwnerId(2));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.addSagaNode(command)
        );

        assertAll(
                () -> assertEquals("无权操作该副本", exception.getMessage()),
                () -> assertEquals(0, saga.nodeCount()),
                () -> assertEquals(0, sagaNodeRepository.savedCount()),
                () -> assertEquals(0, sagaRepository.savedCount())
        );
    }

    @Test
    void shouldReActivateCompletedSagaAfterAddingSagaNode() {
        InMemorySagaRepository sagaRepository = new InMemorySagaRepository();
        InMemorySagaNodeRepository sagaNodeRepository = new InMemorySagaNodeRepository();
        AddSagaNodeApplicationService service = new AddSagaNodeApplicationService(sagaRepository, sagaNodeRepository);
        Saga saga = Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 16, 12, 0)
        );
        saga.recordNodeAdded();
        saga.complete(LocalDateTime.of(2026, 6, 17, 12, 0));
        sagaRepository.store(new SagaId(1), saga);

        service.addSagaNode(createCommand(new SagaId(1), new SagaOwnerId(1)));

        assertAll(
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertEquals(2, saga.nodeCount()),
                () -> assertEquals(SagaRarity.COMMON, saga.sagaRarity()),
                () -> assertEquals(1, sagaNodeRepository.savedCount()),
                () -> assertEquals(1, sagaRepository.savedCount())
        );
    }

    private AddSagaNodeCommand createCommand(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        return new AddSagaNodeCommand(
                sagaId,
                sagaOwnerId,
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1),
                null,
                new SagaNodeLocation("东京塔"),
                null,
                new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
        );
    }

    private static final class InMemorySagaRepository implements SagaRepository {

        private final Map<SagaId, Saga> storage = new LinkedHashMap<>();
        private int savedCount;
        private SagaNode lastSavedSagaNode;

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
            storage.put(saga.sagaId(), saga);
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

        private int savedCount;
        private SagaNode lastSavedSagaNode;

        @Override
        public Optional<SagaNode> findBySagaNodeId(SagaNodeId sagaNodeId) {
            return Optional.empty();
        }

        @Override
        public SagaNode save(SagaNode sagaNode) {
            savedCount++;
            lastSavedSagaNode = sagaNode;
            return sagaNode;
        }

        @Override
        public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
        }

        int savedCount() {
            return savedCount;
        }

        SagaNode lastSavedSagaNode() {
            return lastSavedSagaNode;
        }
    }
}
