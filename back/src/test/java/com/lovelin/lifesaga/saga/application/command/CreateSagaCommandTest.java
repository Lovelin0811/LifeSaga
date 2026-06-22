package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSagaCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("日本旅行");
        SagaType sagaType = SagaType.TRAVEL;
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 17, 12, 0);

        CreateSagaCommand command = new CreateSagaCommand(
                sagaOwnerId,
                sagaName,
                sagaType,
                null,
                null,
                startedAt
        );

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(sagaOwnerId, command.sagaOwnerId()),
                () -> assertEquals(sagaName, command.sagaName()),
                () -> assertEquals(sagaType, command.sagaType()),
                () -> assertNull(command.coverUrl()),
                () -> assertNull(command.description()),
                () -> assertEquals(startedAt, command.startedAt())
        );
    }

    @Test
    void shouldCreateCommandWithOptionalFields() {
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 17, 12, 0);

        CreateSagaCommand command = new CreateSagaCommand(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                "https://example.com/cover.jpg",
                "记录一次日本旅行",
                startedAt
        );

        assertAll(
                () -> assertEquals("https://example.com/cover.jpg", command.coverUrl()),
                () -> assertEquals("记录一次日本旅行", command.description())
        );
    }

    @Test
    void shouldRejectNullSagaOwnerId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateSagaCommand(
                        null,
                        new SagaName("日本旅行"),
                        SagaType.TRAVEL,
                        null,
                        null,
                        LocalDateTime.of(2026, 6, 17, 12, 0)
                )
        );

        assertEquals("副本所有者 ID 不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateSagaCommand(
                        new SagaOwnerId(1),
                        null,
                        SagaType.TRAVEL,
                        null,
                        null,
                        LocalDateTime.of(2026, 6, 17, 12, 0)
                )
        );

        assertEquals("副本名称不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullSagaType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateSagaCommand(
                        new SagaOwnerId(1),
                        new SagaName("日本旅行"),
                        null,
                        null,
                        null,
                        LocalDateTime.of(2026, 6, 17, 12, 0)
                )
        );

        assertEquals("副本类型不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullStartedAt() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateSagaCommand(
                        new SagaOwnerId(1),
                        new SagaName("日本旅行"),
                        SagaType.TRAVEL,
                        null,
                        null,
                        null
                )
        );

        assertEquals("副本开始时间不能为空", exception.getMessage());
    }
}
