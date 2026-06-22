package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.UpdateSagaNodeCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class UpdateSagaNodeApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;

    public UpdateSagaNodeApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
    }

    public SagaNode updateSagaNode(UpdateSagaNodeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("更新节点命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());

        SagaNode sagaNode = sagaNodeRepository.findBySagaNodeId(command.sagaNodeId())
                .filter(foundSagaNode -> command.sagaId().equals(foundSagaNode.sagaId()))
                .orElseThrow(() -> new IllegalStateException("节点不存在"));

        sagaNode.rename(command.sagaNodeTitle());
        sagaNode.changeOrder(command.sagaNodeOrder());
        sagaNode.changeDescription(command.sagaNodeDescription());
        sagaNode.changeLocation(command.sagaNodeLocation());
        sagaNode.changePhotos(command.sagaNodePhotos());
        sagaNode.changeTime(command.sagaNodeTime());
        sagaNode.changeMilestone(command.milestone());
        return sagaNodeRepository.save(sagaNode);
    }
}
