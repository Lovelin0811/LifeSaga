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
public class SagaQueryApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;
    private final SagaNodeFavoriteRepository sagaNodeFavoriteRepository;

    public SagaQueryApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository,
            SagaNodeFavoriteRepository sagaNodeFavoriteRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
        this.sagaNodeFavoriteRepository = sagaNodeFavoriteRepository;
    }

    @Transactional(readOnly = true)
    public List<Saga> listOwnerSagas(SagaOwnerId sagaOwnerId) {
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
        return sagaRepository.findBySagaOwnerId(sagaOwnerId);
    }

    @Transactional(readOnly = true)
    public List<Saga> listPublicSagas() {
        return sagaRepository.findPublic();
    }

    @Transactional(readOnly = true)
    public SagaDetail getSagaDetail(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new IllegalStateException("副本不存在"));
        boolean ownerView = saga.sagaOwnerId().equals(sagaOwnerId);
        if (!ownerView && !saga.publicVisible()) {
            throw new IllegalStateException("无权查看该副本");
        }

        List<SagaNode> sagaNodes = sagaNodeRepository.findBySagaId(sagaId);
        Set<SagaNodeId> favoritedSagaNodeIds = Set.copyOf(sagaNodeFavoriteRepository.findFavoritedSagaNodeIds(
                sagaOwnerId,
                sagaNodes.stream().map(SagaNode::sagaNodeId).toList()
        ));
        List<SagaNodeDetail> nodeDetails = sagaNodes.stream()
                .map(sagaNode -> new SagaNodeDetail(
                        sagaNode,
                        favoritedSagaNodeIds.contains(sagaNode.sagaNodeId())
                ))
                .toList();
        return new SagaDetail(saga, nodeDetails, ownerView);
    }

    public record SagaDetail(Saga saga, List<SagaNodeDetail> sagaNodes, boolean ownerView) {
    }

    public record SagaNodeDetail(SagaNode sagaNode, boolean favorited) {
    }
}
