package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.achievement.application.service.AchievementUnlockUseCase;
import com.lovelin.lifesaga.saga.application.command.CompleteSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class CompleteSagaApplicationService {

    private final SagaRepository sagaRepository;
    private final AchievementUnlockUseCase achievementUnlockUseCase;
    private final Clock clock;

    public CompleteSagaApplicationService(
            SagaRepository sagaRepository,
            AchievementUnlockUseCase achievementUnlockUseCase,
            Clock clock
    ) {
        this.sagaRepository = sagaRepository;
        this.achievementUnlockUseCase = achievementUnlockUseCase;
        this.clock = clock;
    }

    @Transactional
    public Saga completeSaga(CompleteSagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("完成副本命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());
        saga.complete(LocalDateTime.now(clock));
        Saga savedSaga = sagaRepository.save(saga);
        achievementUnlockUseCase.checkOnSagaComplete(
                savedSaga.sagaOwnerId().toUserId(),
                savedSaga.sagaType()
        );
        return savedSaga;
    }
}
