package com.lovelin.lifesaga.saga.domain.repository;

import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;

import java.util.Optional;

public interface SagaRepository {

    Optional<Saga> findBySagaId(SagaId sagaId);

    Saga save(Saga saga);

    void deleteBySagaId(SagaId sagaId);
}
