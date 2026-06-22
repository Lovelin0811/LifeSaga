package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;

public record CreateSagaCommand(
        SagaOwnerId sagaOwnerId,
        SagaName sagaName,
        SagaType sagaType,
        String coverUrl,
        String description,
        boolean publicVisible
) {

    public CreateSagaCommand {
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
        if (sagaName == null) {
            throw new IllegalArgumentException("副本名称不能为空");
        }
        if (sagaType == null) {
            throw new IllegalArgumentException("副本类型不能为空");
        }
    }
}
