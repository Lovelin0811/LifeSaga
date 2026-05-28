package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.repository.NodeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeServiceTest {

    @Test
    void getByIdRejectsNodeFromAnotherSaga() {
        Saga saga = new Saga();
        saga.setId(1L);

        SagaNode node = new SagaNode();
        node.setId(99L);
        node.setSagaId(2L);

        FakeNodeRepository nodeRepository = new FakeNodeRepository();
        nodeRepository.findByIdResult = Optional.of(node);

        NodeService nodeService = new NodeService(
                nodeRepository,
                new FakeSagaService(saga),
                new FakeAchievementService()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> nodeService.getById(1L, 99L));

        assertEquals("节点不属于该副本", exception.getMessage());
    }

    @Test
    void deleteUsesSagaScopedNodeDelete() {
        Saga saga = new Saga();
        saga.setId(1L);

        SagaNode node = new SagaNode();
        node.setId(99L);
        node.setSagaId(1L);

        FakeNodeRepository nodeRepository = new FakeNodeRepository();
        nodeRepository.findByIdResult = Optional.of(node);

        FakeSagaService sagaService = new FakeSagaService(saga);
        NodeService nodeService = new NodeService(nodeRepository, sagaService, new FakeAchievementService());

        nodeService.delete(1L, 99L);

        assertEquals(List.of("delete:1:99"), nodeRepository.calls);
        assertEquals(List.of(1L), sagaService.updatedSagaNodeCounts);
    }

    @Test
    void updateRejectsNodeFromAnotherSaga() {
        Saga saga = new Saga();
        saga.setId(1L);

        SagaNode incoming = new SagaNode();
        incoming.setId(99L);
        incoming.setSagaId(1L);

        SagaNode stored = new SagaNode();
        stored.setId(99L);
        stored.setSagaId(2L);

        FakeNodeRepository nodeRepository = new FakeNodeRepository();
        nodeRepository.findByIdResult = Optional.of(stored);

        NodeService nodeService = new NodeService(
                nodeRepository,
                new FakeSagaService(saga),
                new FakeAchievementService()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> nodeService.update(incoming));

        assertEquals("节点不属于该副本", exception.getMessage());
    }

    private static final class FakeNodeRepository extends NodeRepository {
        private Optional<SagaNode> findByIdResult = Optional.empty();
        private List<SagaNode> findBySagaIdResult = List.of();
        private final java.util.List<String> calls = new java.util.ArrayList<>();

        private FakeNodeRepository() {
            super(null);
        }

        @Override
        public List<SagaNode> findBySagaId(Long sagaId) {
            return findBySagaIdResult;
        }

        @Override
        public Optional<SagaNode> findById(Long id) {
            return findByIdResult;
        }

        @Override
        public SagaNode save(SagaNode node) {
            return node;
        }

        @Override
        public int update(SagaNode node) {
            calls.add("update:" + node.getSagaId() + ":" + node.getId());
            return 1;
        }

        @Override
        public int delete(Long sagaId, Long nodeId) {
            calls.add("delete:" + sagaId + ":" + nodeId);
            return 1;
        }
    }

    private static final class FakeSagaService extends SagaService {
        private final Saga saga;
        private final java.util.List<Long> updatedSagaNodeCounts = new java.util.ArrayList<>();

        private FakeSagaService(Saga saga) {
            super(null, null, null);
            this.saga = saga;
        }

        @Override
        public Saga getById(Long id) {
            return saga;
        }

        @Override
        public void updateSagaNodeCount(Long sagaId) {
            updatedSagaNodeCounts.add(sagaId);
        }
    }

    private static final class FakeAchievementService extends AchievementService {
        private FakeAchievementService() {
            super(null, null, null, null, null);
        }
    }
}
