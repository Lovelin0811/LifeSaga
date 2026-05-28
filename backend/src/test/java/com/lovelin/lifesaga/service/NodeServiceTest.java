package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.repository.NodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private SagaService sagaService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private NodeService nodeService;

    @Test
    void getByIdRejectsNodeFromAnotherSaga() {
        Saga saga = new Saga();
        saga.setId(1L);

        SagaNode node = new SagaNode();
        node.setId(99L);
        node.setSagaId(2L);

        when(sagaService.getById(1L)).thenReturn(saga);
        when(nodeRepository.findById(99L)).thenReturn(Optional.of(node));

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

        when(sagaService.getById(1L)).thenReturn(saga);
        when(nodeRepository.findById(99L)).thenReturn(Optional.of(node));

        nodeService.delete(1L, 99L);

        verify(nodeRepository).delete(1L, 99L);
        verify(sagaService).updateSagaNodeCount(1L);
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

        when(sagaService.getById(1L)).thenReturn(saga);
        when(nodeRepository.findById(99L)).thenReturn(Optional.of(stored));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> nodeService.update(incoming));

        assertEquals("节点不属于该副本", exception.getMessage());
    }
}
