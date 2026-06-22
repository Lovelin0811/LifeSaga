package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.DeleteSagaNodeCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class DeleteSagaNodeApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;

    public DeleteSagaNodeApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
    }

    public void deleteSagaNode(DeleteSagaNodeCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("删除节点命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());

        sagaNodeRepository.findBySagaNodeId(command.sagaNodeId())
                .filter(foundSagaNode -> command.sagaId().equals(foundSagaNode.sagaId()))
                .orElseThrow(() -> new IllegalStateException("节点不存在"));

        sagaNodeRepository.deleteBySagaNodeId(command.sagaNodeId());
        saga.recordNodeDeleted();
        sagaRepository.save(saga);
    }
}
