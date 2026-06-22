package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaNodeTest {

    @Test
    void shouldCreateSagaNodeWithRequiredFields() {
        SagaNode sagaNode = SagaNode.create(
                new SagaId(1),
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1)
        );

        assertAll(
                () -> assertNotNull(sagaNode),
                () -> assertNull(sagaNode.sagaNodeId()),
                () -> assertEquals(new SagaId(1), sagaNode.sagaId()),
                () -> assertEquals(new SagaNodeTitle("第一次旅行"), sagaNode.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeOrder(1), sagaNode.sagaNodeOrder()),
                () -> assertNull(sagaNode.sagaNodeDescription()),
                () -> assertNull(sagaNode.sagaNodeLocation()),
                () -> assertNull(sagaNode.sagaNodePhotos()),
                () -> assertNull(sagaNode.sagaNodeTime()),
                () -> assertFalse(sagaNode.milestone())
        );
    }

    @Test
    void shouldCreateSagaNodeWithOptionalFields() {
        SagaNodeDescription sagaNodeDescription = new SagaNodeDescription("第一次旅行的记录");
        SagaNodeLocation sagaNodeLocation = new SagaNodeLocation("东京塔");
        SagaNodePhotos sagaNodePhotos = new SagaNodePhotos(List.of("https://example.com/photo.jpg"));
        SagaNodeTime sagaNodeTime = new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0));

        SagaNode sagaNode = SagaNode.create(
                new SagaId(1),
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1),
                sagaNodeDescription,
                sagaNodeLocation,
                sagaNodePhotos,
                sagaNodeTime
        );

        assertAll(
                () -> assertEquals(sagaNodeDescription, sagaNode.sagaNodeDescription()),
                () -> assertEquals(sagaNodeLocation, sagaNode.sagaNodeLocation()),
                () -> assertEquals(sagaNodePhotos, sagaNode.sagaNodePhotos()),
                () -> assertEquals(sagaNodeTime, sagaNode.sagaNodeTime())
        );
    }

    @Test
    void shouldRejectNullSagaId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SagaNode.create(null, new SagaNodeTitle("第一次旅行"), new SagaNodeOrder(1))
        );
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SagaNode.create(new SagaId(1), null, new SagaNodeOrder(1))
        );
    }

    @Test
    void shouldRejectNullOrder() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SagaNode.create(new SagaId(1), new SagaNodeTitle("第一次旅行"), null)
        );
    }

    @Test
    void shouldRenameSagaNode() {
        SagaNode sagaNode = createSagaNode();
        SagaNodeTitle newSagaNodeTitle = new SagaNodeTitle("第二次旅行");

        sagaNode.rename(newSagaNodeTitle);

        assertEquals(newSagaNodeTitle, sagaNode.sagaNodeTitle());
    }

    @Test
    void shouldRejectNullTitleWhenRenamingSagaNode() {
        SagaNode sagaNode = createSagaNode();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sagaNode.rename(null)
        );

        assertEquals("节点标题不能为空", exception.getMessage());
    }

    @Test
    void shouldChangeSagaNodeOrder() {
        SagaNode sagaNode = createSagaNode();
        SagaNodeOrder newSagaNodeOrder = new SagaNodeOrder(2);

        sagaNode.changeOrder(newSagaNodeOrder);

        assertEquals(newSagaNodeOrder, sagaNode.sagaNodeOrder());
    }

    @Test
    void shouldRejectNullOrderWhenChangingSagaNodeOrder() {
        SagaNode sagaNode = createSagaNode();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sagaNode.changeOrder(null)
        );

        assertEquals("节点排序编号不能为空", exception.getMessage());
    }

    @Test
    void shouldChangeSagaNodeDescription() {
        SagaNode sagaNode = createSagaNode();
        SagaNodeDescription sagaNodeDescription = new SagaNodeDescription("更新后的节点描述");

        sagaNode.changeDescription(sagaNodeDescription);

        assertEquals(sagaNodeDescription, sagaNode.sagaNodeDescription());
    }

    @Test
    void shouldAllowClearingSagaNodeDescription() {
        SagaNode sagaNode = createSagaNode();
        sagaNode.changeDescription(new SagaNodeDescription("更新后的节点描述"));

        sagaNode.changeDescription(null);

        assertNull(sagaNode.sagaNodeDescription());
    }

    @Test
    void shouldChangeSagaNodeLocation() {
        SagaNode sagaNode = createSagaNode();
        SagaNodeLocation sagaNodeLocation = new SagaNodeLocation("东京塔");

        sagaNode.changeLocation(sagaNodeLocation);

        assertEquals(sagaNodeLocation, sagaNode.sagaNodeLocation());
    }

    @Test
    void shouldAllowClearingSagaNodeLocation() {
        SagaNode sagaNode = createSagaNode();
        sagaNode.changeLocation(new SagaNodeLocation("东京塔"));

        sagaNode.changeLocation(null);

        assertNull(sagaNode.sagaNodeLocation());
    }

    @Test
    void shouldChangeSagaNodePhotos() {
        SagaNode sagaNode = createSagaNode();
        SagaNodePhotos sagaNodePhotos = new SagaNodePhotos(List.of("https://example.com/photo.jpg"));

        sagaNode.changePhotos(sagaNodePhotos);

        assertEquals(sagaNodePhotos, sagaNode.sagaNodePhotos());
    }

    @Test
    void shouldAllowClearingSagaNodePhotos() {
        SagaNode sagaNode = createSagaNode();
        sagaNode.changePhotos(new SagaNodePhotos(List.of("https://example.com/photo.jpg")));

        sagaNode.changePhotos(null);

        assertNull(sagaNode.sagaNodePhotos());
    }

    @Test
    void shouldChangeSagaNodeTime() {
        SagaNode sagaNode = createSagaNode();
        SagaNodeTime sagaNodeTime = new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0));

        sagaNode.changeTime(sagaNodeTime);

        assertEquals(sagaNodeTime, sagaNode.sagaNodeTime());
    }

    @Test
    void shouldAllowClearingSagaNodeTime() {
        SagaNode sagaNode = createSagaNode();
        sagaNode.changeTime(new SagaNodeTime(LocalDateTime.of(2026, 6, 17, 12, 0)));

        sagaNode.changeTime(null);

        assertNull(sagaNode.sagaNodeTime());
    }

    @Test
    void shouldChangeSagaNodeMilestoneToTrue() {
        SagaNode sagaNode = createSagaNode();

        sagaNode.changeMilestone(true);

        assertTrue(sagaNode.milestone());
    }

    @Test
    void shouldChangeSagaNodeMilestoneToFalse() {
        SagaNode sagaNode = createSagaNode();
        sagaNode.changeMilestone(true);

        sagaNode.changeMilestone(false);

        assertFalse(sagaNode.milestone());
    }

    @Test
    void shouldToggleMilestoneFromFalseToTrue() {
        SagaNode sagaNode = createSagaNode();

        sagaNode.toggleMilestone();

        assertTrue(sagaNode.milestone());
    }

    @Test
    void shouldToggleMilestoneFromTrueToFalse() {
        SagaNode sagaNode = createSagaNode();
        sagaNode.toggleMilestone();

        sagaNode.toggleMilestone();

        assertFalse(sagaNode.milestone());
    }

    private SagaNode createSagaNode() {
        return SagaNode.create(
                new SagaId(1),
                new SagaNodeTitle("第一次旅行"),
                new SagaNodeOrder(1)
        );
    }
}
