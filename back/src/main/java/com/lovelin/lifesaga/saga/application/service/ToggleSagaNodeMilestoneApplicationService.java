package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.ToggleSagaNodeMilestoneCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class ToggleSagaNodeMilestoneApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;

    public ToggleSagaNodeMilestoneApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
    }

    public SagaNode toggleSagaNodeMilestone(ToggleSagaNodeMilestoneCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("切换节点里程碑命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());

        SagaNode sagaNode = sagaNodeRepository.findBySagaNodeId(command.sagaNodeId())
                .filter(foundSagaNode -> command.sagaId().equals(foundSagaNode.sagaId()))
                .orElseThrow(() -> new IllegalStateException("节点不存在"));

        sagaNode.toggleMilestone();
        return sagaNodeRepository.save(sagaNode);
    }
}
