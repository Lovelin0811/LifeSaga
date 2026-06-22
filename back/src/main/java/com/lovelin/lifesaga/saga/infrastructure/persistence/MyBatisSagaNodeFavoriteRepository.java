package com.lovelin.lifesaga.saga.infrastructure.persistence;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeFavoriteRepository;
import com.lovelin.lifesaga.saga.infrastructure.persistence.mapper.SagaNodeFavoriteMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisSagaNodeFavoriteRepository implements SagaNodeFavoriteRepository {

    private final SagaNodeFavoriteMapper sagaNodeFavoriteMapper;

    public MyBatisSagaNodeFavoriteRepository(SagaNodeFavoriteMapper sagaNodeFavoriteMapper) {
        this.sagaNodeFavoriteMapper = sagaNodeFavoriteMapper;
    }

    @Override
    public boolean isFavorited(SagaOwnerId sagaOwnerId, SagaNodeId sagaNodeId) {
        return sagaNodeFavoriteMapper.countByUserIdAndNodeId(sagaOwnerId.value(), sagaNodeId.value()) > 0;
    }

    @Override
    public boolean toggle(SagaOwnerId sagaOwnerId, SagaNodeId sagaNodeId) {
        if (isFavorited(sagaOwnerId, sagaNodeId)) {
            sagaNodeFavoriteMapper.deleteByUserIdAndNodeId(sagaOwnerId.value(), sagaNodeId.value());
            return false;
        }
        sagaNodeFavoriteMapper.insert(sagaOwnerId.value(), sagaNodeId.value());
        return true;
    }

    @Override
    public List<SagaNodeId> findFavoritedSagaNodeIds(SagaOwnerId sagaOwnerId, List<SagaNodeId> sagaNodeIds) {
        if (sagaNodeIds == null || sagaNodeIds.isEmpty()) {
            return List.of();
        }
        List<Long> ids = sagaNodeIds.stream().map(SagaNodeId::value).toList();
        return sagaNodeFavoriteMapper.findNodeIdsByUserIdAndNodeIds(sagaOwnerId.value(), ids).stream()
                .map(SagaNodeId::new)
                .toList();
    }

    @Override
    public void deleteBySagaNodeId(SagaNodeId sagaNodeId) {
        sagaNodeFavoriteMapper.deleteByNodeId(sagaNodeId.value());
    }

    @Override
    public void deleteBySagaId(SagaId sagaId) {
        sagaNodeFavoriteMapper.deleteBySagaId(sagaId.value());
    }
}
