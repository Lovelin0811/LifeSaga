package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSagaCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        SagaId sagaId = new SagaId(1);
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("北海道旅行");
        SagaType sagaType = SagaType.TRAVEL;

        UpdateSagaCommand command = new UpdateSagaCommand(
                sagaId,
                sagaOwnerId,
                sagaName,
                sagaType,
                null,
                null,
                false
        );

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(sagaId, command.sagaId()),
                () -> assertEquals(sagaOwnerId, command.sagaOwnerId()),
                () -> assertEquals(sagaName, command.sagaName()),
                () -> assertEquals(sagaType, command.sagaType()),
                () -> assertNull(command.coverUrl()),
                () -> assertNull(command.description())
        );
    }

    @Test
    void shouldCreateCommandWithOptionalFields() {
        UpdateSagaCommand command = new UpdateSagaCommand(
                new SagaId(1),
                new SagaOwnerId(1),
                new SagaName("北海道旅行"),
                SagaType.TRAVEL,
                "https://example.com/cover.jpg",
                "更新后的副本简介",
                true
        );

        assertAll(
                () -> assertEquals("https://example.com/cover.jpg", command.coverUrl()),
                () -> assertEquals("更新后的副本简介", command.description()),
                () -> assertEquals(true, command.publicVisible())
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaCommand(
                        null,
                        new SagaOwnerId(1),
                        new SagaName("北海道旅行"),
                        SagaType.TRAVEL,
                        null,
                        null,
                        false
                )
        );

        assertEquals("副本 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaOwnerId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaCommand(
                        new SagaId(1),
                        null,
                        new SagaName("北海道旅行"),
                        SagaType.TRAVEL,
                        null,
                        null,
                        false
                )
        );

        assertEquals("副本所有者 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaCommand(
                        new SagaId(1),
                        new SagaOwnerId(1),
                        null,
                        SagaType.TRAVEL,
                        null,
                        null,
                        false
                )
        );

        assertEquals("副本名称不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaCommand(
                        new SagaId(1),
                        new SagaOwnerId(1),
                        new SagaName("北海道旅行"),
                        null,
                        null,
                        null,
                        false
                )
        );

        assertEquals("副本类型不能为空", exception.getMessage());
    }
}
