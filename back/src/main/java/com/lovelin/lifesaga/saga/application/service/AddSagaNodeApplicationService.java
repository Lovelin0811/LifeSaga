package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.AddSagaNodeCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;

public class AddSagaNodeApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;

    public AddSagaNodeApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
    }

    // 应用服务负责把“添加节点”这个用例在两个聚合和两个仓储之间串起来。
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
                command.sagaNodePhotos(),
                command.sagaNodeTime()
        );

        SagaNode savedSagaNode = sagaNodeRepository.save(sagaNode);
        saga.recordNodeAdded();
        sagaRepository.save(saga);
        return savedSagaNode;
    }
}
