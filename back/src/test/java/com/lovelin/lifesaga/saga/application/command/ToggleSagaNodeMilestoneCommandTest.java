package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToggleSagaNodeMilestoneCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        SagaId sagaId = new SagaId(1);
        SagaNodeId sagaNodeId = new SagaNodeId(10);
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);

        ToggleSagaNodeMilestoneCommand command = new ToggleSagaNodeMilestoneCommand(
                sagaId,
                sagaNodeId,
                sagaOwnerId
        );

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(sagaId, command.sagaId()),
                () -> assertEquals(sagaNodeId, command.sagaNodeId()),
                () -> assertEquals(sagaOwnerId, command.sagaOwnerId())
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ToggleSagaNodeMilestoneCommand(null, new SagaNodeId(10), new SagaOwnerId(1))
        );
    }

    @Test
    void shouldRejectNullSagaNodeId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ToggleSagaNodeMilestoneCommand(new SagaId(1), null, new SagaOwnerId(1))
        );
    }

    @Test
    void shouldRejectNullSagaOwnerId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ToggleSagaNodeMilestoneCommand(new SagaId(1), new SagaNodeId(10), null)
        );
    }
}
