package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.CreateSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class CreateSagaApplicationService {

    private final SagaRepository sagaRepository;

    public CreateSagaApplicationService(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }

    public Saga createSaga(CreateSagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("创建副本命令不能为空");
        }

        Saga saga = Saga.create(
                command.sagaOwnerId(),
                command.sagaName(),
                command.sagaType(),
                command.coverUrl(),
                command.description(),
                command.startedAt()
        );
        return sagaRepository.save(saga);
    }
}
