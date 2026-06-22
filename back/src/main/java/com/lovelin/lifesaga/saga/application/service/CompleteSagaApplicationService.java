package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.CompleteSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

import java.time.Clock;
import java.time.LocalDateTime;

public class CompleteSagaApplicationService {

    private final SagaRepository sagaRepository;
    private final Clock clock;

    public CompleteSagaApplicationService(SagaRepository sagaRepository, Clock clock) {
        this.sagaRepository = sagaRepository;
        this.clock = clock;
    }

    public Saga completeSaga(CompleteSagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("完成副本命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());
        saga.complete(LocalDateTime.now(clock));
        return sagaRepository.save(saga);
    }
}
