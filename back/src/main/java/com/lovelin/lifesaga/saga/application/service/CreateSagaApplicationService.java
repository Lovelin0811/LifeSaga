package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.CreateSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class CreateSagaApplicationService {

    private final SagaRepository sagaRepository;
    private final Clock clock;

    public CreateSagaApplicationService(SagaRepository sagaRepository, Clock clock) {
        this.sagaRepository = sagaRepository;
        this.clock = clock;
    }

    @Transactional
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
                LocalDateTime.now(clock)
        );
        saga.changePublicVisible(command.publicVisible());
        return sagaRepository.save(saga);
    }
}
