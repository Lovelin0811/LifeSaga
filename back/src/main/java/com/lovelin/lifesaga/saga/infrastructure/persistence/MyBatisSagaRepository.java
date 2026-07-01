package com.lovelin.lifesaga.saga.infrastructure.persistence;

import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import com.lovelin.lifesaga.saga.infrastructure.persistence.mapper.SagaMapper;
import com.lovelin.lifesaga.saga.infrastructure.persistence.record.SagaRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MyBatisSagaRepository implements SagaRepository {

    private final SagaMapper sagaMapper;

    public MyBatisSagaRepository(SagaMapper sagaMapper) {
        this.sagaMapper = sagaMapper;
    }

    @Override
    public Optional<Saga> findBySagaId(SagaId sagaId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        return sagaMapper.findById(sagaId.value()).map(this::toDomain);
    }

    @Override
    public List<Saga> findBySagaOwnerId(SagaOwnerId sagaOwnerId) {
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }
        return sagaMapper.findByUserId(sagaOwnerId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Saga> findPublic() {
        return sagaMapper.findPublic().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Saga save(Saga saga) {
        if (saga == null) {
            throw new IllegalArgumentException("副本不能为空");
        }

        SagaRecord sagaRecord = toRecord(saga);
        if (saga.sagaId() == null) {
            sagaMapper.insert(sagaRecord);
            return toDomain(sagaRecord);
        }

        int updatedRows = sagaMapper.update(sagaRecord);
        if (updatedRows != 1) {
            throw new IllegalStateException("副本保存失败");
        }
        return saga;
    }

    @Override
    public Saga recordNodeAdded(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }

        int updatedRows = sagaMapper.recordNodeAdded(sagaId.value(), sagaOwnerId.value());
        if (updatedRows != 1) {
            throw new IllegalStateException("副本节点统计更新失败");
        }
        return findBySagaId(sagaId).orElseThrow(() -> new IllegalStateException("副本不存在"));
    }

    @Override
    public Saga recordNodeDeleted(SagaId sagaId, SagaOwnerId sagaOwnerId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        if (sagaOwnerId == null) {
            throw new IllegalArgumentException("副本所有者 ID 不能为空");
        }

        int updatedRows = sagaMapper.recordNodeDeleted(sagaId.value(), sagaOwnerId.value());
        if (updatedRows != 1) {
            throw new IllegalStateException("副本节点统计更新失败");
        }
        return findBySagaId(sagaId).orElseThrow(() -> new IllegalStateException("副本不存在"));
    }

    @Override
    public void deleteBySagaId(SagaId sagaId) {
        if (sagaId == null) {
            throw new IllegalArgumentException("副本 ID 不能为空");
        }
        sagaMapper.deleteById(sagaId.value());
    }

    private SagaRecord toRecord(Saga saga) {
        SagaRecord sagaRecord = new SagaRecord();
        sagaRecord.setId(saga.sagaId() == null ? null : saga.sagaId().value());
        sagaRecord.setUserId(saga.sagaOwnerId().value());
        sagaRecord.setName(saga.sagaName().value());
        sagaRecord.setType(saga.sagaType().name());
        sagaRecord.setCoverUrl(saga.coverUrl());
        sagaRecord.setDescription(saga.description());
        sagaRecord.setStatus(saga.sagaStatus().name());
        sagaRecord.setIsPublic(saga.publicVisible());
        sagaRecord.setNodeCount(saga.nodeCount());
        sagaRecord.setRarity(saga.sagaRarity().name());
        sagaRecord.setStartedAt(saga.startedAt());
        sagaRecord.setEndedAt(saga.endedAt());
        return sagaRecord;
    }

    private Saga toDomain(SagaRecord sagaRecord) {
        return Saga.restore(
                new SagaId(sagaRecord.getId()),
                new SagaOwnerId(sagaRecord.getUserId()),
                new SagaName(sagaRecord.getName()),
                SagaType.valueOf(sagaRecord.getType()),
                sagaRecord.getCoverUrl(),
                sagaRecord.getDescription(),
                SagaStatus.valueOf(sagaRecord.getStatus()),
                sagaRecord.getIsPublic() != null && sagaRecord.getIsPublic(),
                sagaRecord.getNodeCount(),
                SagaRarity.valueOf(sagaRecord.getRarity()),
                sagaRecord.getStartedAt(),
                sagaRecord.getEndedAt()
        );
    }
}
