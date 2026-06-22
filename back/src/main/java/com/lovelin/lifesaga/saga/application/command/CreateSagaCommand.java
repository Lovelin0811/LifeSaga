package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;

import java.time.LocalDateTime;

public record CreateSagaCommand(
        SagaOwnerId sagaOwnerId,
        SagaName sagaName,
        SagaType sagaType,
        String coverUrl,
        String description,
        LocalDateTime startedAt
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
        if (startedAt == null) {
            throw new IllegalArgumentException("副本开始时间不能为空");
        }
    }
}
