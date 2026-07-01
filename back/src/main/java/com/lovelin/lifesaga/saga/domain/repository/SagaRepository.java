package com.lovelin.lifesaga.saga.domain.repository;

import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;

import java.util.List;
import java.util.Optional;

public interface SagaRepository {

    Optional<Saga> findBySagaId(SagaId sagaId);

    List<Saga> findBySagaOwnerId(SagaOwnerId sagaOwnerId);

    List<Saga> findPublic();

    Saga save(Saga saga);

    default Saga recordNodeAdded(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
        Saga saga = findBySagaId(sagaId).orElseThrow(() -> new IllegalStateException("副本不存在"));
        saga.requireOwner(sagaOwnerId);
        saga.recordNodeAdded();
        return save(saga);
    }

    default Saga recordNodeDeleted(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
        Saga saga = findBySagaId(sagaId).orElseThrow(() -> new IllegalStateException("副本不存在"));
        saga.requireOwner(sagaOwnerId);
        saga.recordNodeDeleted();
        return save(saga);
    }

    void deleteBySagaId(SagaId sagaId);
}
