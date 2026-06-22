package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSagaNodeCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(1);
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);

        DeleteSagaNodeCommand command = new DeleteSagaNodeCommand(sagaId, sagaNodeId, sagaOwnerId);

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(sagaId, command.sagaId()),
                () -> assertEquals(sagaNodeId, command.sagaNodeId()),
                () -> assertEquals(sagaOwnerId, command.sagaOwnerId())
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DeleteSagaNodeCommand(
                        null,
                        new SagaNodeId(1),
                        new SagaOwnerId(1)
                )
        );

        assertEquals("副本 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaNodeId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DeleteSagaNodeCommand(
                        new SagaId(1),
                        null,
                        new SagaOwnerId(1)
                )
        );

        assertEquals("节点 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaOwnerId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DeleteSagaNodeCommand(
                        new SagaId(1),
                        new SagaNodeId(1),
                        null
                )
        );

        assertEquals("副本所有者 ID 不能为空", exception.getMessage());
    }
}
