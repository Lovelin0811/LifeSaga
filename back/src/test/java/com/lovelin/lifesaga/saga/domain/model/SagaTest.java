package com.lovelin.lifesaga.saga.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SagaTest {

    @Test
    void shouldCreateSagaWithRequiredFields() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("日本旅行");
        SagaType sagaType = SagaType.TRAVEL;
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 11, 12, 0);

        Saga saga = Saga.create(sagaOwnerId, sagaName, sagaType, null, null, startedAt);

        assertNotNull(saga);
        assertEquals(sagaOwnerId, saga.sagaOwnerId());
        assertEquals(sagaName, saga.sagaName());
        assertEquals(sagaType, saga.sagaType());
        assertEquals(startedAt, saga.startedAt());
        assertNull(saga.coverUrl());
        assertNull(saga.description());
    }

    @Test
    void shouldCreateSagaWithOptionalFields() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("日本旅行");
        SagaType sagaType = SagaType.TRAVEL;
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 11, 12, 0);
        String coverUrl = "https://example.com/cover.jpg";
        String description = "记录一次日本旅行";

        Saga saga = Saga.create(sagaOwnerId, sagaName, sagaType, coverUrl, description, startedAt);

        assertEquals(coverUrl, saga.coverUrl());
        assertEquals(description, saga.description());
    }

    @Test
    void shouldInitializeSagaWithDefaultValues() {
        Saga saga = Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 11, 12, 0)
        );

        assertAll(
                () -> assertNull(saga.sagaId()),
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertEquals(0, saga.nodeCount()),
                () -> assertEquals(SagaRarity.COMMON, saga.sagaRarity()),
                () -> assertNull(saga.endedAt())
        );
    }

    @Test
    void shouldRejectNullOwnerId() {
        SagaName sagaName = new SagaName("日本旅行");
        SagaType sagaType = SagaType.TRAVEL;
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 11, 12, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Saga.create(null, sagaName, sagaType, null, null, startedAt)
        );

        assertEquals("副本所有者不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullName() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaType sagaType = SagaType.TRAVEL;
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 11, 12, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Saga.create(sagaOwnerId, null, sagaType, null, null, startedAt)
        );

        assertEquals("副本名称不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullType() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("日本旅行");
        LocalDateTime startedAt = LocalDateTime.of(2026, 6, 11, 12, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Saga.create(sagaOwnerId, sagaName, null, null, null, startedAt)
        );

        assertEquals("副本类型不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullStartedAt() {
        SagaOwnerId sagaOwnerId = new SagaOwnerId(1);
        SagaName sagaName = new SagaName("日本旅行");
        SagaType sagaType = SagaType.TRAVEL;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Saga.create(sagaOwnerId, sagaName, sagaType, null, null, null)
        );

        assertEquals("副本开始时间不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectCompletingSagaWithoutNodes() {
        Saga saga = Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 11, 12, 0)
        );

        assertThrows(
                IllegalStateException.class,
                () -> saga.complete(LocalDateTime.of(2026, 6, 15, 12, 0))
        );
    }

    @Test
    void shouldRejectCompletingSagaWithoutCompletedAt() {
        Saga saga = createSaga();
        saga.recordNodeAdded();

        assertThrows(
                IllegalArgumentException.class,
                () -> saga.complete(null)
        );
    }

    @Test
    void shouldCompleteSaga() {
        Saga saga = createSaga();
        LocalDateTime completedAt = LocalDateTime.of(2026, 6, 15, 12, 0);
        saga.recordNodeAdded();

        saga.complete(completedAt);

        assertAll(
                () -> assertEquals(SagaStatus.COMPLETED, saga.sagaStatus()),
                () -> assertEquals(completedAt, saga.endedAt()),
                () -> assertEquals(1, saga.nodeCount()),
                () -> assertEquals(SagaRarity.COMMON, saga.sagaRarity())
        );
    }

    @Test
    void shouldKeepCompletedSagaIdempotent() {
        Saga saga = createSaga();
        LocalDateTime firstCompletedAt = LocalDateTime.of(2026, 6, 15, 12, 0);
        LocalDateTime secondCompletedAt = LocalDateTime.of(2026, 6, 16, 12, 0);
        saga.recordNodeAdded();
        saga.complete(firstCompletedAt);

        saga.complete(secondCompletedAt);

        assertAll(
                () -> assertEquals(SagaStatus.COMPLETED, saga.sagaStatus()),
                () -> assertEquals(firstCompletedAt, saga.endedAt())
        );
    }

    @Test
    void shouldBackfillEndedAtWhenCompletedSagaEndedAtIsMissing() {
        Saga saga = Saga.restore(
                new SagaId(1),
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                SagaStatus.COMPLETED,
                false,
                1,
                SagaRarity.COMMON,
                LocalDateTime.of(2026, 6, 11, 12, 0),
                null
        );
        LocalDateTime completedAt = LocalDateTime.of(2026, 6, 16, 12, 0);

        saga.complete(completedAt);

        assertEquals(completedAt, saga.endedAt());
    }

    @Test
    void shouldRecordNodeAdded() {
        Saga saga = createSaga();

        saga.recordNodeAdded();

        assertAll(
                () -> assertEquals(1, saga.nodeCount()),
                () -> assertEquals(SagaRarity.COMMON, saga.sagaRarity()),
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertNull(saga.endedAt())
        );
    }

    @Test
    void shouldRecalculateRarityAfterNodeAdded() {
        Saga saga = createSaga();

        saga.recordNodeAdded();
        saga.recordNodeAdded();
        saga.recordNodeAdded();

        assertAll(
                () -> assertEquals(3, saga.nodeCount()),
                () -> assertEquals(SagaRarity.UNCOMMON, saga.sagaRarity())
        );
    }

    @Test
    void shouldRecordNodeDeleted() {
        Saga saga = createSaga();
        saga.recordNodeAdded();
        saga.recordNodeAdded();
        saga.complete(LocalDateTime.of(2026, 6, 15, 12, 0));

        saga.recordNodeDeleted();

        assertAll(
                () -> assertEquals(1, saga.nodeCount()),
                () -> assertEquals(SagaRarity.COMMON, saga.sagaRarity()),
                () -> assertEquals(SagaStatus.ACTIVE, saga.sagaStatus()),
                () -> assertNull(saga.endedAt())
        );
    }

    @Test
    void shouldRejectNodeDeletedWhenSagaHasNoNodes() {
        Saga saga = createSaga();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                saga::recordNodeDeleted
        );

        assertEquals("没有节点可删除", exception.getMessage());
    }

    @Test
    void shouldAllowOwnerOperation() {
        Saga saga = createSaga();

        saga.requireOwner(new SagaOwnerId(1));
    }

    @Test
    void shouldRejectNullOperatorId() {
        Saga saga = createSaga();

        assertThrows(
                IllegalArgumentException.class,
                () -> saga.requireOwner(null)
        );
    }

    @Test
    void shouldRejectNonOwnerOperation() {
        Saga saga = createSaga();

        assertThrows(
                IllegalStateException.class,
                () -> saga.requireOwner(new SagaOwnerId(2))
        );
    }

    @Test
    void shouldRenameSaga() {
        Saga saga = createSaga();
        SagaName newSagaName = new SagaName("北海道旅行");

        saga.rename(newSagaName);

        assertEquals(newSagaName, saga.sagaName());
    }

    @Test
    void shouldRejectNullNameWhenRenamingSaga() {
        Saga saga = createSaga();

        assertThrows(
                IllegalArgumentException.class,
                () -> saga.rename(null)
        );
    }

    @Test
    void shouldChangeSagaType() {
        Saga saga = createSaga();

        saga.changeType(SagaType.STUDY);

        assertEquals(SagaType.STUDY, saga.sagaType());
    }

    @Test
    void shouldRejectNullTypeWhenChangingSagaType() {
        Saga saga = createSaga();

        assertThrows(
                IllegalArgumentException.class,
                () -> saga.changeType(null)
        );
    }

    @Test
    void shouldChangeSagaCoverUrl() {
        Saga saga = createSaga();
        String coverUrl = "https://example.com/updated-cover.jpg";

        saga.changeCover(coverUrl);

        assertEquals(coverUrl, saga.coverUrl());
    }

    @Test
    void shouldAllowClearingSagaCoverUrl() {
        Saga saga = createSaga();
        saga.changeCover("https://example.com/updated-cover.jpg");

        saga.changeCover(null);

        assertNull(saga.coverUrl());
    }

    @Test
    void shouldChangeSagaDescription() {
        Saga saga = createSaga();
        String description = "更新后的副本简介";

        saga.changeDescription(description);

        assertEquals(description, saga.description());
    }

    @Test
    void shouldAllowClearingSagaDescription() {
        Saga saga = createSaga();
        saga.changeDescription("更新后的副本简介");

        saga.changeDescription(null);

        assertNull(saga.description());
    }

    private Saga createSaga() {
        return Saga.create(
                new SagaOwnerId(1),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                null,
                null,
                LocalDateTime.of(2026, 6, 11, 12, 0)
        );
    }
}
