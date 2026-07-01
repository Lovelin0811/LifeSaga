package com.lovelin.lifesaga.saga.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeGeoPoint;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeLocation;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodePhotos;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTime;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.infrastructure.persistence.mapper.SagaNodeMapper;
import com.lovelin.lifesaga.saga.infrastructure.persistence.record.SagaNodeRecord;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class MyBatisSagaNodeRepository implements SagaNodeRepository {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final SagaNodeMapper sagaNodeMapper;
    private final ObjectMapper objectMapper;

    public MyBatisSagaNodeRepository(SagaNodeMapper sagaNodeMapper, ObjectMapper objectMapper) {
        this.sagaNodeMapper = sagaNodeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<SagaNode> findBySagaNodeId(SagaNodeId sagaNodeId) {
        if (sagaNodeId == null) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
        return sagaNodeMapper.findById(sagaNodeId.value()).map(this::toDomain);
    }

    @Override
    public List<SagaNode> findBySagaId(SagaId sagaId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        return sagaNodeMapper.findBySagaId(sagaId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public SagaNode save(SagaNode sagaNode) {
        if (sagaNode == null) {
            throw new IllegalArgumentException("节点不能为空");
        }

        SagaNodeRecord sagaNodeRecord = toRecord(sagaNode);
        if (sagaNode.sagaNodeId() == null) {
            sagaNodeMapper.insert(sagaNodeRecord);
            return toDomain(sagaNodeRecord);
        }

        int updatedRows = sagaNodeMapper.update(sagaNodeRecord);
        if (updatedRows != 1) {
            throw new IllegalStateException("节点保存失败");
        }
        return sagaNode;
    }

    @Override
    public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
        if (sagaNodeId == null) {
            throw new IllegalArgumentException("节点 ID 不能为空");
        }
        sagaNodeMapper.deleteById(sagaNodeId.value());
    }

    @Override
    public void deleteBySagaId(SagaId sagaId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        sagaNodeMapper.deleteBySagaId(sagaId.value());
    }

    private SagaNodeRecord toRecord(SagaNode sagaNode) {
        SagaNodeRecord sagaNodeRecord = new SagaNodeRecord();
        sagaNodeRecord.setId(sagaNode.sagaNodeId() == null ? null : sagaNode.sagaNodeId().value());
        sagaNodeRecord.setSagaId(sagaNode.sagaId().value());
        sagaNodeRecord.setTitle(sagaNode.sagaNodeTitle().value());
        sagaNodeRecord.setContent(valueOf(sagaNode.sagaNodeDescription()));
        sagaNodeRecord.setLocation(valueOf(sagaNode.sagaNodeLocation()));
        sagaNodeRecord.setLatitude(latitudeOf(sagaNode.sagaNodeGeoPoint()));
        sagaNodeRecord.setLongitude(longitudeOf(sagaNode.sagaNodeGeoPoint()));
        sagaNodeRecord.setNodeTime(sagaNode.sagaNodeTime() == null ? null : sagaNode.sagaNodeTime().value());
        sagaNodeRecord.setPhotos(toPhotosText(sagaNode.sagaNodePhotos()));
        sagaNodeRecord.setIsMilestone(sagaNode.milestone());
        sagaNodeRecord.setSortOrder(sagaNode.sagaNodeOrder().value());
        return sagaNodeRecord;
    }

    private SagaNode toDomain(SagaNodeRecord sagaNodeRecord) {
        return SagaNode.restore(
                new SagaNodeId(sagaNodeRecord.getId()),
                new SagaId(sagaNodeRecord.getSagaId()),
                new SagaNodeTitle(sagaNodeRecord.getTitle()),
                new SagaNodeOrder(sagaNodeRecord.getSortOrder()),
                sagaNodeDescriptionOf(sagaNodeRecord.getContent()),
                sagaNodeLocationOf(sagaNodeRecord.getLocation()),
                sagaNodeGeoPointOf(sagaNodeRecord.getLatitude(), sagaNodeRecord.getLongitude()),
                sagaNodePhotosOf(sagaNodeRecord.getPhotos()),
                sagaNodeRecord.getNodeTime() == null ? null : new SagaNodeTime(sagaNodeRecord.getNodeTime()),
                Boolean.TRUE.equals(sagaNodeRecord.getIsMilestone())
        );
    }

    private String toPhotosText(SagaNodePhotos sagaNodePhotos) {
        if (sagaNodePhotos == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(sagaNodePhotos.values());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("节点照片序列化失败", exception);
        }
    }

    private SagaNodePhotos sagaNodePhotosOf(String photos) {
        if (photos == null || photos.isBlank() || "[]".equals(photos)) {
            return null;
        }
        try {
            return new SagaNodePhotos(objectMapper.readValue(photos, STRING_LIST_TYPE));
        } catch (JsonProcessingException exception) {
            return parseLegacyPhotos(photos, exception);
        }
    }

    private SagaNodeDescription sagaNodeDescriptionOf(String value) {
        return value == null || value.isBlank() ? null : new SagaNodeDescription(value);
    }

    private SagaNodeLocation sagaNodeLocationOf(String value) {
        return value == null || value.isBlank() ? null : new SagaNodeLocation(value);
    }

    private SagaNodeGeoPoint sagaNodeGeoPointOf(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null && longitude == null) {
            return null;
        }
        if (latitude == null || longitude == null) {
            return null;
        }
        return new SagaNodeGeoPoint(latitude, longitude);
    }

    private SagaNodePhotos parseLegacyPhotos(String photos, JsonProcessingException exception) {
        String normalizedPhotos = photos == null ? "" : photos.trim();
        if (normalizedPhotos.isEmpty()) {
            return null;
        }
        if (normalizedPhotos.contains(",")) {
            List<String> urls = java.util.Arrays.stream(normalizedPhotos.split(","))
                    .map(String::trim)
                    .filter(url -> !url.isEmpty())
                    .toList();
            if (!urls.isEmpty()) {
                return new SagaNodePhotos(urls);
            }
        }
        if (normalizedPhotos.startsWith("http://") || normalizedPhotos.startsWith("https://") || normalizedPhotos.startsWith("/")) {
            return new SagaNodePhotos(List.of(normalizedPhotos));
        }
        throw new IllegalStateException("节点照片反序列化失败", exception);
    }

    private String valueOf(SagaNodeDescription sagaNodeDescription) {
        return sagaNodeDescription == null ? null : sagaNodeDescription.value();
    }

    private String valueOf(SagaNodeLocation sagaNodeLocation) {
        return sagaNodeLocation == null ? null : sagaNodeLocation.value();
    }

    private BigDecimal latitudeOf(SagaNodeGeoPoint sagaNodeGeoPoint) {
        return sagaNodeGeoPoint == null ? null : sagaNodeGeoPoint.latitude();
    }

    private BigDecimal longitudeOf(SagaNodeGeoPoint sagaNodeGeoPoint) {
        return sagaNodeGeoPoint == null ? null : sagaNodeGeoPoint.longitude();
    }
}
