package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.achievement.application.service.AchievementUnlockUseCase;
import com.lovelin.lifesaga.saga.application.command.AddSagaNodeCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddSagaNodeApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;
    private final AchievementUnlockUseCase achievementUnlockUseCase;

    public AddSagaNodeApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository,
            AchievementUnlockUseCase achievementUnlockUseCase
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
        this.achievementUnlockUseCase = achievementUnlockUseCase;
    }

    // 应用服务负责把“添加节点”这个用例在两个聚合和两个仓储之间串起来。
    @Transactional
    public SagaNode addSagaNode(AddSagaNodeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("添加节点命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());

        SagaNode sagaNode = SagaNode.create(
                command.sagaId(),
                command.sagaNodeTitle(),
                command.sagaNodeOrder(),
                command.sagaNodeDescription(),
                command.sagaNodeLocation(),
                command.sagaNodeGeoPoint(),
                command.sagaNodePhotos(),
                command.sagaNodeTime()
        );

        SagaNode savedSagaNode = sagaNodeRepository.save(sagaNode);
        Saga updatedSaga = sagaRepository.recordNodeAdded(command.sagaId(), command.sagaOwnerId());
        achievementUnlockUseCase.checkOnNodeCreate(
                updatedSaga.sagaOwnerId().toUserId(),
                updatedSaga.sagaId()
        );
        return savedSagaNode;
    }
}
