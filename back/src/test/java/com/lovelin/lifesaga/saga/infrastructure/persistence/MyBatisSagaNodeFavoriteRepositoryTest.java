package com.lovelin.lifesaga.saga.infrastructure.persistence;

import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.infrastructure.persistence.mapper.SagaNodeFavoriteMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MyBatisSagaNodeFavoriteRepositoryTest {

    @Test
    void shouldFavoriteWhenInsertIgnoreSucceeds() {
        FakeSagaNodeFavoriteMapper mapper = new FakeSagaNodeFavoriteMapper();
        mapper.insertIgnoreResult = 1;
        MyBatisSagaNodeFavoriteRepository repository = new MyBatisSagaNodeFavoriteRepository(mapper);

        boolean favorited = repository.toggle(new SagaOwnerId(1), new SagaNodeId(2));

        assertAll(
                () -> assertEquals(true, favorited),
                () -> assertEquals(1, mapper.insertIgnoreCount),
                () -> assertEquals(0, mapper.deleteCount)
        );
    }

    @Test
    void shouldUnfavoriteWhenRecordAlreadyExists() {
        FakeSagaNodeFavoriteMapper mapper = new FakeSagaNodeFavoriteMapper();
        mapper.insertIgnoreResult = 0;
        mapper.deleteResult = 1;
        MyBatisSagaNodeFavoriteRepository repository = new MyBatisSagaNodeFavoriteRepository(mapper);

        boolean favorited = repository.toggle(new SagaOwnerId(1), new SagaNodeId(2));

        assertAll(
                () -> assertEquals(false, favorited),
                () -> assertEquals(1, mapper.insertIgnoreCount),
                () -> assertEquals(1, mapper.deleteCount)
        );
    }

    @Test
    void shouldRetryFavoriteWhenConcurrentDeleteHappens() {
        FakeSagaNodeFavoriteMapper mapper = new FakeSagaNodeFavoriteMapper();
        mapper.insertIgnoreResults = new int[]{0, 1};
        mapper.deleteResult = 0;
        MyBatisSagaNodeFavoriteRepository repository = new MyBatisSagaNodeFavoriteRepository(mapper);

        boolean favorited = repository.toggle(new SagaOwnerId(1), new SagaNodeId(2));

        assertAll(
                () -> assertEquals(true, favorited),
                () -> assertEquals(2, mapper.insertIgnoreCount),
                () -> assertEquals(1, mapper.deleteCount)
        );
    }

    private static final class FakeSagaNodeFavoriteMapper implements SagaNodeFavoriteMapper {

        private int insertIgnoreResult;
        private int[] insertIgnoreResults;
        private int deleteResult;
        private int insertIgnoreCount;
        private int deleteCount;

        @Override
        public int countByUserIdAndNodeId(long userId, long nodeId) {
            return 0;
        }

        @Override
        public int insert(long userId, long nodeId) {
            return 1;
        }

        @Override
        public int insertIgnore(long userId, long nodeId) {
            insertIgnoreCount++;
            if (insertIgnoreResults != null && insertIgnoreCount <= insertIgnoreResults.length) {
                return insertIgnoreResults[insertIgnoreCount - 1];
            }
            return insertIgnoreResult;
        }

        @Override
        public int deleteByUserIdAndNodeId(long userId, long nodeId) {
            deleteCount++;
            return deleteResult;
        }

        @Override
        public List<Long> findNodeIdsByUserIdAndNodeIds(long userId, List<Long> nodeIds) {
            return List.of();
        }

        @Override
        public int deleteByNodeId(long nodeId) {
            return 0;
        }

        @Override
        public int deleteBySagaId(long sagaId) {
            return 0;
        }
    }
}
