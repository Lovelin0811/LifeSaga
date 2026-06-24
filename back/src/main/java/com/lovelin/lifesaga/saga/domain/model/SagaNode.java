package com.lovelin.lifesaga.saga.domain.model;

public final class SagaNode {

    private final SagaNodeId sagaNodeId;
    private final SagaId sagaId;
    private SagaNodeTitle sagaNodeTitle;
    private SagaNodeOrder sagaNodeOrder;
    private SagaNodeDescription sagaNodeDescription;
    private SagaNodeLocation sagaNodeLocation;
    private SagaNodeGeoPoint sagaNodeGeoPoint;
    private SagaNodePhotos sagaNodePhotos;
    private SagaNodeTime sagaNodeTime;
    private boolean milestone;

    private SagaNode(
            SagaNodeId sagaNodeId,
            SagaId sagaId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder,
            SagaNodeDescription sagaNodeDescription,
            SagaNodeLocation sagaNodeLocation,
            SagaNodeGeoPoint sagaNodeGeoPoint,
            SagaNodePhotos sagaNodePhotos,
            SagaNodeTime sagaNodeTime,
            boolean milestone
    ) {
        this.sagaNodeId = sagaNodeId;
        this.sagaId = sagaId;
        this.sagaNodeTitle = sagaNodeTitle;
        this.sagaNodeOrder = sagaNodeOrder;
        this.sagaNodeDescription = sagaNodeDescription;
        this.sagaNodeLocation = sagaNodeLocation;
        this.sagaNodeGeoPoint = sagaNodeGeoPoint;
        this.sagaNodePhotos = sagaNodePhotos;
        this.sagaNodeTime = sagaNodeTime;
        this.milestone = milestone;
    }

    public static SagaNode create(
            SagaId sagaId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder
    ) {
        return create(sagaId, sagaNodeTitle, sagaNodeOrder, null, null, null, null);
    }

    public static SagaNode create(
            SagaId sagaId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder,
            SagaNodeDescription sagaNodeDescription,
            SagaNodeLocation sagaNodeLocation,
            SagaNodePhotos sagaNodePhotos,
            SagaNodeTime sagaNodeTime
    ) {
        return create(
                sagaId,
                sagaNodeTitle,
                sagaNodeOrder,
                sagaNodeDescription,
                sagaNodeLocation,
                null,
                sagaNodePhotos,
                sagaNodeTime
        );
    }

    public static SagaNode create(
            SagaId sagaId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder,
            SagaNodeDescription sagaNodeDescription,
            SagaNodeLocation sagaNodeLocation,
            SagaNodeGeoPoint sagaNodeGeoPoint,
            SagaNodePhotos sagaNodePhotos,
            SagaNodeTime sagaNodeTime
    ) {
        if (sagaId == null) {
            throw new IllegalArgumentException("节点所属副本不能为空");
        }
        if (sagaNodeTitle == null) {
            throw new IllegalArgumentException("节点标题不能为空");
        }
        if (sagaNodeOrder == null) {
            throw new IllegalArgumentException("节点排序编号不能为空");
        }
        return new SagaNode(
                null,
                sagaId,
                sagaNodeTitle,
                sagaNodeOrder,
                sagaNodeDescription,
                sagaNodeLocation,
                sagaNodeGeoPoint,
                sagaNodePhotos,
                sagaNodeTime,
                false
        );
    }

    public static SagaNode restore(
            SagaNodeId sagaNodeId,
            SagaId sagaId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder,
            SagaNodeDescription sagaNodeDescription,
            SagaNodeLocation sagaNodeLocation,
            SagaNodePhotos sagaNodePhotos,
            SagaNodeTime sagaNodeTime,
            boolean milestone
    ) {
        return restore(
                sagaNodeId,
                sagaId,
                sagaNodeTitle,
                sagaNodeOrder,
                sagaNodeDescription,
                sagaNodeLocation,
                null,
                sagaNodePhotos,
                sagaNodeTime,
                milestone
        );
    }

    public static SagaNode restore(
            SagaNodeId sagaNodeId,
            SagaId sagaId,
            SagaNodeTitle sagaNodeTitle,
            SagaNodeOrder sagaNodeOrder,
            SagaNodeDescription sagaNodeDescription,
            SagaNodeLocation sagaNodeLocation,
            SagaNodeGeoPoint sagaNodeGeoPoint,
            SagaNodePhotos sagaNodePhotos,
            SagaNodeTime sagaNodeTime,
            boolean milestone
    ) {
        if (sagaNodeId == null) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
        if (sagaId == null) {
            throw new IllegalArgumentException("节点所属副本不能为空");
        }
        if (sagaNodeTitle == null) {
            throw new IllegalArgumentException("节点标题不能为空");
        }
        if (sagaNodeOrder == null) {
            throw new IllegalArgumentException("节点排序编号不能为空");
        }
        return new SagaNode(
                sagaNodeId,
                sagaId,
                sagaNodeTitle,
                sagaNodeOrder,
                sagaNodeDescription,
                sagaNodeLocation,
                sagaNodeGeoPoint,
                sagaNodePhotos,
                sagaNodeTime,
                milestone
        );
    }

    public SagaNodeId sagaNodeId() {
        return sagaNodeId;
    }

    public SagaId sagaId() {
        return sagaId;
    }

    public SagaNodeTitle sagaNodeTitle() {
        return sagaNodeTitle;
    }

    public SagaNodeOrder sagaNodeOrder() {
        return sagaNodeOrder;
    }

    public SagaNodeDescription sagaNodeDescription() {
        return sagaNodeDescription;
    }

    public SagaNodeLocation sagaNodeLocation() {
        return sagaNodeLocation;
    }

    public SagaNodeGeoPoint sagaNodeGeoPoint() {
        return sagaNodeGeoPoint;
    }

    public SagaNodePhotos sagaNodePhotos() {
        return sagaNodePhotos;
    }

    public SagaNodeTime sagaNodeTime() {
        return sagaNodeTime;
    }

    public void rename(SagaNodeTitle newSagaNodeTitle) {
        if (newSagaNodeTitle == null) {
            throw new IllegalArgumentException("节点标题不能为空");
        }
        sagaNodeTitle = newSagaNodeTitle;
    }

    public void changeOrder(SagaNodeOrder newSagaNodeOrder) {
        if (newSagaNodeOrder == null) {
            throw new IllegalArgumentException("节点排序编号不能为空");
        }
        sagaNodeOrder = newSagaNodeOrder;
    }

    public void changeDescription(SagaNodeDescription newSagaNodeDescription) {
        sagaNodeDescription = newSagaNodeDescription;
    }

    public void changeLocation(SagaNodeLocation newSagaNodeLocation) {
        sagaNodeLocation = newSagaNodeLocation;
    }

    public void changeGeoPoint(SagaNodeGeoPoint newSagaNodeGeoPoint) {
        sagaNodeGeoPoint = newSagaNodeGeoPoint;
    }

    public void changePhotos(SagaNodePhotos newSagaNodePhotos) {
        sagaNodePhotos = newSagaNodePhotos;
    }

    public void changeTime(SagaNodeTime newSagaNodeTime) {
        sagaNodeTime = newSagaNodeTime;
    }

    public void changeMilestone(boolean newMilestone) {
        milestone = newMilestone;
    }

    // 里程碑是节点自身状态，因此切换行为保留在节点聚合内部。
    public void toggleMilestone() {
        milestone = !milestone;
    }

    public boolean milestone() {
        return milestone;
    }
}
