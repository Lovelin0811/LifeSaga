package com.lovelin.lifesaga.saga.domain.repository;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;

import java.util.List;

public interface SagaNodeFavoriteRepository {

    boolean isFavorited(SagaOwnerId sagaOwnerId, SagaNodeId sagaNodeId);

    boolean toggle(SagaOwnerId sagaOwnerId, SagaNodeId sagaNodeId);

    List<SagaNodeId> findFavoritedSagaNodeIds(SagaOwnerId sagaOwnerId, List<SagaNodeId> sagaNodeIds);

    void deleteBySagaNodeId(SagaNodeId sagaNodeId);

    void deleteBySagaId(SagaId sagaId);
}
