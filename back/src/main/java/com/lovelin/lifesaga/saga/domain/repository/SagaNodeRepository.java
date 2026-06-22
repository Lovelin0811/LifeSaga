package com.lovelin.lifesaga.saga.domain.repository;

import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;

import java.util.List;
import java.util.Optional;

public interface SagaNodeRepository {

    Optional<SagaNode> findBySagaNodeId(SagaNodeId sagaNodeId);

    List<SagaNode> findBySagaId(SagaId sagaId);

    SagaNode save(SagaNode sagaNode);

    void deleteBySagaNodeId(SagaNodeId sagaNodeId);

    void deleteBySagaId(SagaId sagaId);
}
