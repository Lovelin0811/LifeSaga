package com.lovelin.lifesaga.saga.application.command;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
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

class UpdateSagaNodeCommandTest {

    @Test
    void shouldCreateCommandWithRequiredFields() {
        UpdateSagaNodeCommand command = new UpdateSagaNodeCommand(
                new SagaId(1),
                new SagaNodeId(10),
                new SagaOwnerId(1),
                new SagaNodeTitle("第二次旅行"),
                new SagaNodeOrder(2),
                null,
                new SagaNodeLocation("东京塔"),
                null,
                new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                true
        );

        assertAll(
                () -> assertNotNull(command),
                () -> assertEquals(new SagaId(1), command.sagaId()),
                () -> assertEquals(new SagaNodeId(10), command.sagaNodeId()),
                () -> assertEquals(new SagaOwnerId(1), command.sagaOwnerId()),
                () -> assertEquals(new SagaNodeTitle("第二次旅行"), command.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeOrder(2), command.sagaNodeOrder()),
                () -> assertNull(command.sagaNodeDescription()),
                () -> assertEquals(new SagaNodeLocation("东京塔"), command.sagaNodeLocation()),
                () -> assertNull(command.sagaNodePhotos()),
                () -> assertEquals(
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        command.sagaNodeTime()
                ),
                () -> assertEquals(true, command.milestone())
        );
    }

    @Test
    void shouldCreateCommandWithOptionalFields() {
        UpdateSagaNodeCommand command = createCommand();

        assertAll(
                () -> assertEquals(new SagaNodeDescription("更新后的节点描述"), command.sagaNodeDescription()),
                () -> assertEquals(
                        new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                        command.sagaNodePhotos()
                ),
                () -> assertEquals(false, command.milestone())
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        null,
                        new SagaNodeId(10),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第二次旅行"),
                        new SagaNodeOrder(2),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        true
                )
        );
    }

    @Test
    void shouldRejectNullSagaNodeId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        new SagaId(1),
                        null,
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第二次旅行"),
                        new SagaNodeOrder(2),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        true
                )
        );
    }

    @Test
    void shouldRejectNullSagaOwnerId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        new SagaId(1),
                        new SagaNodeId(10),
                        null,
                        new SagaNodeTitle("第二次旅行"),
                        new SagaNodeOrder(2),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        true
                )
        );
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        new SagaId(1),
                        new SagaNodeId(10),
                        new SagaOwnerId(1),
                        null,
                        new SagaNodeOrder(2),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        true
                )
        );
    }

    @Test
    void shouldRejectNullOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        new SagaId(1),
                        new SagaNodeId(10),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第二次旅行"),
                        null,
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        true
                )
        );
    }

    @Test
    void shouldRejectNullLocation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        new SagaId(1),
                        new SagaNodeId(10),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第二次旅行"),
                        new SagaNodeOrder(2),
                        null,
                        null,
                        null,
                        new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                        true
                )
        );
    }

    @Test
    void shouldRejectNullTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new UpdateSagaNodeCommand(
                        new SagaId(1),
                        new SagaNodeId(10),
                        new SagaOwnerId(1),
                        new SagaNodeTitle("第二次旅行"),
                        new SagaNodeOrder(2),
                        null,
                        new SagaNodeLocation("东京塔"),
                        null,
                        null,
                        true
                )
        );
    }

    private UpdateSagaNodeCommand createCommand() {
        return new UpdateSagaNodeCommand(
                new SagaId(1),
                new SagaNodeId(10),
                new SagaOwnerId(1),
                new SagaNodeTitle("第二次旅行"),
                new SagaNodeOrder(2),
                new SagaNodeDescription("更新后的节点描述"),
                new SagaNodeLocation("东京塔"),
                new SagaNodePhotos(List.of("https://example.com/photo.jpg")),
                new SagaNodeTime(LocalDateTime.of(2026, 6, 18, 12, 0)),
                false
        );
    }
}
