package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;

public record DeleteSagaCommand(
        SagaId sagaId,
        SagaOwnerId sagaOwnerId
) {

    public DeleteSagaCommand {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
    }
}
