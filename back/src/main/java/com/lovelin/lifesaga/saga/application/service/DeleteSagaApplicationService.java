package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.DeleteSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class DeleteSagaApplicationService {

    private final SagaRepository sagaRepository;

    public DeleteSagaApplicationService(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }

    public void deleteSaga(DeleteSagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("删除副本命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());
        sagaRepository.deleteBySagaId(command.sagaId());
    }
}
