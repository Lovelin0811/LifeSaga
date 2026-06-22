package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeLocation;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodePhotos;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTime;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddSagaNodeCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        AddSagaNodeCommand command = new AddSagaNodeCommand(
                new SagaId(1),
                new SagaOwnerId(1),
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1),
                null,
                new SagaNodeLocation("东京塔"),
                null,
                new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
        );

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(new SagaId(1), command.sagaId()),
                () -> assertEquals(new SagaOwnerId(1), command.sagaOwnerId()),
                () -> assertEquals(new SagaNodeTitle("第一次旅行"), command.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeOrder(1), command.sagaNodeOrder()),
                () -> assertNull(command.sagaNodeDescription()),
                () -> assertEquals(new SagaNodeLocation("东京塔"), command.sagaNodeLocation()),
                () -> assertNull(command.sagaNodePhotos()),
                () -> assertEquals(
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0)),
                        command.sagaNodeTime()
                )
        );
    }

    @Test
    void shouldCreateCommandWithOptionalFields() {
        AddSagaNodeCommand command = new AddSagaNodeCommand(
                new SagaId(1),
                new SagaOwnerId(1),
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1),
                new SagaNodeDescription("第一次旅行的记录"),
                new SagaNodeLocation("东京塔"),
                new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
        );

        assertAll(
                () -> assertEquals(new SagaNodeDescription("第一次旅行的记录"), command.sagaNodeDescription()),
                () -> assertEquals(
                        new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                        command.sagaNodePhotos()
                )
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AddSagaNodeCommand(
                        null,
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第一次旅行"),
                        new SagaNodeOrder(1),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
                )
        );
    }

    @Test
    void shouldRejectNullOperatorId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AddSagaNodeCommand(
                        new SagaId(1),
                        null,
                        new SagaNodeTitle("第一次旅行"),
                        new SagaNodeOrder(1),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
                )
        );
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AddSagaNodeCommand(
                        new SagaId(1),
                        new SagaOwnerId(1),
                        null,
                        new SagaNodeOrder(1),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
                )
        );
    }

    @Test
    void shouldRejectNullOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AddSagaNodeCommand(
                        new SagaId(1),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第一次旅行"),
                        null,
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
                )
        );
    }

    @Test
    void shouldRejectNullLocation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AddSagaNodeCommand(
                        new SagaId(1),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第一次旅行"),
                        new SagaNodeOrder(1),
                        null,
                        null,
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0))
                )
        );
    }

    @Test
    void shouldRejectNullTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AddSagaNodeCommand(
                        new SagaId(1),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第一次旅行"),
                        new SagaNodeOrder(1),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        null
                )
        );
    }
}
