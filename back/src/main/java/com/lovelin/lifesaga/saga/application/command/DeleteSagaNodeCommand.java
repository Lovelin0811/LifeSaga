package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;

public record DeleteSagaNodeCommand(
        SagaId sagaId,
        SagaNodeId sagaNodeId,
        SagaOwnerId sagaOwnerId
) {

    public DeleteSagaNodeCommand {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaNodeId == null) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
    }
}
