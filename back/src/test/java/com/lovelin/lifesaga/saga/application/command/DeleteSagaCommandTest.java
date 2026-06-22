package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteSagaCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        SagaId sagaId = new SagaId(1);
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);

        DeleteSagaCommand command = new DeleteSagaCommand(sagaId, sagaOwnerId);

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(sagaId, command.sagaId()),
                () -> assertEquals(sagaOwnerId, command.sagaOwnerId())
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DeleteSagaCommand(null, new SagaOwnerId(1))
        );

        assertEquals("副本 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaOwnerId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DeleteSagaCommand(new SagaId(1), null)
        );

        assertEquals("副本所有者 ID 不能为空", exception.getMessage());
    }
}
