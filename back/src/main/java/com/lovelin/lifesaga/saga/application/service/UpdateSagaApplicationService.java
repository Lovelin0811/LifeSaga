package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.UpdateSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class UpdateSagaApplicationService {

    private final SagaRepository sagaRepository;

    public UpdateSagaApplicationService(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }

    public Saga updateSaga(UpdateSagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("更新副本命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());
        saga.rename(command.sagaName());
        saga.changeType(command.sagaType());
        saga.changeCover(command.coverUrl());
        saga.changeDescription(command.description());
        return sagaRepository.save(saga);
    }
}
