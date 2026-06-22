package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeFavoriteRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class SagaNodeQueryApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;
    private final SagaNodeFavoriteRepository sagaNodeFavoriteRepository;

    public SagaNodeQueryApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository,
            SagaNodeFavoriteRepository sagaNodeFavoriteRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
        this.sagaNodeFavoriteRepository = sagaNodeFavoriteRepository;
    }

    @Transactional(readOnly = true)
    public List<SagaNodeDetail> listSagaNodes(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        Saga saga = requireOwnedSaga(sagaId, sagaOwnerId);
        List<SagaNode> sagaNodes = sagaNodeRepository.findBySagaId(saga.sagaId());
        Set<SagaNodeId> favoritedSagaNodeIds = Set.copyOf(sagaNodeFavoriteRepository.findFavoritedSagaNodeIds(
                sagaOwnerId,
                sagaNodes.stream().map(SagaNode::sagaNodeId).toList()
        ));
        return sagaNodes.stream()
                .map(sagaNode -> new SagaNodeDetail(
                        sagaNode,
                        favoritedSagaNodeIds.contains(sagaNode.sagaNodeId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public SagaNodeDetail getSagaNode(SagaId sagaId, SagaNodeId sagaNodeId, SagaOwnerId sagaOwnerId) {
        requireOwnedSaga(sagaId, sagaOwnerId);
        SagaNode sagaNode = findSagaNodeInSaga(sagaId, sagaNodeId);
        return new SagaNodeDetail(
                sagaNode,
                sagaNodeFavoriteRepository.isFavorited(sagaOwnerId, sagaNodeId)
        );
    }

    private Saga requireOwnedSaga(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new IllegalStateException("副本不存在"));
        saga.requireOwner(sagaOwnerId);
        return saga;
    }

    private SagaNode findSagaNodeInSaga(SagaId sagaId, SagaNodeId sagaNodeId) {
        if (sagaNodeId == null) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
        return sagaNodeRepository.findBySagaNodeId(sagaNodeId)
                .filter(foundSagaNode -> sagaId.equals(foundSagaNode.sagaId()))
                .orElseThrow(() -> new IllegalStateException("节点不存在"));
    }

    public record SagaNodeDetail(SagaNode sagaNode, boolean favorited) {
    }
}
