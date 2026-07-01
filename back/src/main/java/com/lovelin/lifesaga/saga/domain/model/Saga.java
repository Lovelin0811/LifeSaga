package com.lovelin.lifesaga.saga.domain.model;

import java.time.LocalDateTime;

public final class Saga {

    private final SagaId sagaId;
    private final SagaOwnerId sagaOwnerId;
    private SagaName sagaName;
    private SagaType sagaType;
    private String coverUrl;
    private String description;
    private SagaStatus sagaStatus;
    private boolean publicVisible;
    private int nodeCount;
    private SagaRarity sagaRarity;
    private final LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private Saga(
            SagaId sagaId,
            SagaOwnerId sagaOwnerId,
            SagaName sagaName,
            SagaType sagaType,
            String coverUrl,
            String description,
            SagaStatus sagaStatus,
            boolean publicVisible,
            int nodeCount,
            SagaRarity sagaRarity,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        this.sagaId = sagaId;
        this.sagaOwnerId = sagaOwnerId;
        this.sagaName = sagaName;
        this.sagaType = sagaType;
        this.coverUrl = coverUrl;
        this.description = description;
        this.sagaStatus = sagaStatus;
        this.publicVisible = publicVisible;
        this.nodeCount = nodeCount;
        this.sagaRarity = sagaRarity;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public static Saga create(
            SagaOwnerId sagaOwnerId,
            SagaName sagaName,
            SagaType sagaType,
            String coverUrl,
            String description,
            LocalDateTime startedAt
    ) {
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者不能为空");
        }
        if (sagaName == null) {
            throw new IllegalArgumentException("副本名称不能为空");
        }
        if (sagaType == null) {
            throw new IllegalArgumentException("副本类型不能为空");
        }
        if (startedAt == null) {
            throw new IllegalArgumentException("副本开始时间不能为空");
        }
        return new Saga(
                null,
                sagaOwnerId,
                sagaName,
                sagaType,
                coverUrl,
                description,
                SagaStatus.ACTIVE,
                false,
                0,
                SagaRarity.COMMON,
                startedAt,
                null
        );
    }

    public static Saga restore(
            SagaId sagaId,
            SagaOwnerId sagaOwnerId,
            SagaName sagaName,
            SagaType sagaType,
            String coverUrl,
            String description,
            SagaStatus sagaStatus,
            boolean publicVisible,
            int nodeCount,
            SagaRarity sagaRarity,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者不能为空");
        }
        if (sagaName == null) {
            throw new IllegalArgumentException("副本名称不能为空");
        }
        if (sagaType == null) {
            throw new IllegalArgumentException("副本类型不能为空");
        }
        if (sagaStatus == null) {
            throw new IllegalArgumentException("副本状态不能为空");
        }
        if (nodeCount < 0) {
            throw new IllegalArgumentException("节点数量不能为负数");
        }
        if (sagaRarity == null) {
            throw new IllegalArgumentException("副本稀有度不能为空");
        }
        if (startedAt == null) {
            throw new IllegalArgumentException("副本开始时间不能为空");
        }
        return new Saga(
                sagaId,
                sagaOwnerId,
                sagaName,
                sagaType,
                coverUrl,
                description,
                sagaStatus,
                publicVisible,
                nodeCount,
                sagaRarity,
                startedAt,
                endedAt
        );
    }

    public void complete(LocalDateTime completedAt) {
        if (sagaStatus == SagaStatus.COMPLETED) {
            if (endedAt == null && completedAt != null) {
                endedAt = completedAt;
            }
            return;
        }
        if (nodeCount == 0) {
            throw new IllegalStateException("没有节点的副本不能完成");
        }
        if (completedAt == null) {
            throw new IllegalArgumentException("副本完成时间不能为空");
        }
        sagaStatus = SagaStatus.COMPLETED;
        endedAt = completedAt;
    }

    // 添加节点后，副本会重新进入进行中状态，并按最新节点数重算稀有度。
    public void recordNodeAdded() {
        nodeCount++;
        sagaRarity = SagaRarity.fromNodeCount(nodeCount);
        sagaStatus = SagaStatus.ACTIVE;
        endedAt = null;
    }

    // 删除节点后，副本统计要同步回退，并重新进入进行中状态。
    public void recordNodeDeleted() {
        if (nodeCount == 0) {
            throw new IllegalStateException("没有节点可删除");
        }
        nodeCount--;
        sagaRarity = SagaRarity.fromNodeCount(nodeCount);
        sagaStatus = SagaStatus.ACTIVE;
        endedAt = null;
    }

    public void requireOwner(SagaOwnerId sagaOwnerId) {
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("操作者不能为空");
        }
        if (!this.sagaOwnerId.equals(sagaOwnerId)) {
            throw new IllegalStateException("无权操作该副本");
        }
    }

    public void rename(SagaName newName) {
        if (newName == null) {
            throw new IllegalArgumentException("副本名称不能为空");
        }
        sagaName = newName;
    }

    public void changeType(SagaType newSagaType) {
        if (newSagaType == null) {
            throw new IllegalArgumentException("副本类型不能为空");
        }
        sagaType = newSagaType;
    }

    public void changeCover(String newCoverUrl) {
        coverUrl = newCoverUrl;
    }

    public void changeDescription(String newDescription) {
        description = newDescription;
    }

    public void changePublicVisible(boolean newPublicVisible) {
        publicVisible = newPublicVisible;
    }

    public SagaId sagaId() {
        return sagaId;
    }

    public SagaOwnerId sagaOwnerId() {
        return sagaOwnerId;
    }

    public SagaName sagaName() {
        return sagaName;
    }

    public SagaType sagaType() {
        return sagaType;
    }

    public String coverUrl() {
        return coverUrl;
    }

    public String description() {
        return description;
    }

    public SagaStatus sagaStatus() {
        return sagaStatus;
    }

    public boolean publicVisible() {
        return publicVisible;
    }

    public int nodeCount() {
        return nodeCount;
    }

    public SagaRarity sagaRarity() {
        return sagaRarity;
    }

    public LocalDateTime startedAt() {
        return startedAt;
    }

    public LocalDateTime endedAt() {
        return endedAt;
    }
}
