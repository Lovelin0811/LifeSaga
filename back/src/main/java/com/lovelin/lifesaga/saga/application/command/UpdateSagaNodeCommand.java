package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeGeoPoint;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeLocation;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodePhotos;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTime;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;

public record UpdateSagaNodeCommand(
        SagaId sagaId,
        SagaNodeId sagaNodeId,
        SagaOwnerId sagaOwnerId,
        SagaNodeTitle sagaNodeTitle,
        SagaNodeOrder sagaNodeOrder,
        SagaNodeDescription sagaNodeDescription,
        SagaNodeLocation sagaNodeLocation,
        SagaNodeGeoPoint sagaNodeGeoPoint,
        SagaNodePhotos sagaNodePhotos,
        SagaNodeTime sagaNodeTime,
        boolean milestone
) {

    public UpdateSagaNodeCommand(
            SagaId sagaId,
            SagaNodeId sagaNodeId,
            SagaOwnerId sagaOwnerId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder,
            SagaNodeDescription sagaNodeDescription,
            SagaNodeLocation sagaNodeLocation,
            SagaNodePhotos sagaNodePhotos,
            SagaNodeTime sagaNodeTime,
            boolean milestone
    ) {
        this(
                sagaId,
                sagaNodeId,
                sagaOwnerId,
                sagaNodeTitle,
                sagaNodeOrder,
                sagaNodeDescription,
                sagaNodeLocation,
                null,
                sagaNodePhotos,
                sagaNodeTime,
                milestone
        );
    }

    public UpdateSagaNodeCommand {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaNodeId == null) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
        if (sagaNodeTitle == null) {
            throw new IllegalArgumentException("节点标题不能为空");
        }
        if (sagaNodeOrder == null) {
            throw new IllegalArgumentException("节点排序编号不能为空");
        }
        if (sagaNodeLocation == null) {
            throw new IllegalArgumentException("节点地点不能为空");
        }
        if (sagaNodeTime == null) {
            throw new IllegalArgumentException("节点时间不能为空");
        }
    }
}
