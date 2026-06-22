package com.lovelin.lifesaga.saga.infrastructure.persistence;

import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaName;
import com.lovelin.lifesaga.saga.domain.model.SagaOwnerId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaStatus;
import com.lovelin.lifesaga.saga.domain.model.SagaType;
import com.lovelin.lifesaga.saga.infrastructure.persistence.mapper.SagaMapper;
import com.lovelin.lifesaga.saga.infrastructure.persistence.record.SagaRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyBatisSagaRepositoryTest {

    @Test
    void shouldInsertNewSagaAndReturnGeneratedSagaId() {
        FakeSagaMapper sagaMapper = new FakeSagaMapper();
        MyBatisSagaRepository sagaRepository = new MyBatisSagaRepository(sagaMapper);
        Saga saga = Saga.create(
                new SagaOwnerId(7),
                new SagaName("日本旅行"),
                SagaType.TRAVEL,
                "https://example.com/cover.jpg",
                "旅行记录",
                LocalDateTime.of(2026, 6, 22, 10, 0)
        );

        Saga savedSaga = sagaRepository.save(saga);

        assertAll(
                () -> assertEquals(new SagaId(100), savedSaga.sagaId()),
                () -> assertEquals(new SagaOwnerId(7), savedSaga.sagaOwnerId()),
                () -> assertEquals(SagaType.TRAVEL, savedSaga.sagaType()),
                () -> assertEquals(SagaStatus.ACTIVE, savedSaga.sagaStatus()),
                () -> assertEquals(SagaRarity.COMMON, savedSaga.sagaRarity()),
                () -> assertEquals("TRAVEL", sagaMapper.savedRecord.getType()),
                () -> assertEquals("ACTIVE", sagaMapper.savedRecord.getStatus()),
                () -> assertEquals("COMMON", sagaMapper.savedRecord.getRarity())
        );
    }

    @Test
    void shouldRestoreSagaWhenFoundBySagaId() {
        FakeSagaMapper sagaMapper = new FakeSagaMapper();
        SagaRecord sagaRecord = new SagaRecord();
        sagaRecord.setId(101L);
        sagaRecord.setUserId(8L);
        sagaRecord.setName("学习计划");
        sagaRecord.setType("STUDY");
        sagaRecord.setCoverUrl(null);
        sagaRecord.setDescription("DDD 学习");
        sagaRecord.setStatus("COMPLETED");
        sagaRecord.setNodeCount(3);
        sagaRecord.setRarity("UNCOMMON");
        sagaRecord.setStartedAt(LocalDateTime.of(2026, 6, 20, 9, 0));
        sagaRecord.setEndedAt(LocalDateTime.of(2026, 6, 22, 9, 0));
        sagaMapper.recordToFind = sagaRecord;
        MyBatisSagaRepository sagaRepository = new MyBatisSagaRepository(sagaMapper);

        Optional<Saga> foundSaga = sagaRepository.findBySagaId(new SagaId(101));

        assertTrue(foundSaga.isPresent());
        Saga saga = foundSaga.orElseThrow();
        assertAll(
                () -> assertEquals(new SagaId(101), saga.sagaId()),
                () -> assertEquals(new SagaOwnerId(8), saga.sagaOwnerId()),
                () -> assertEquals(new SagaName("学习计划"), saga.sagaName()),
                () -> assertEquals(SagaType.STUDY, saga.sagaType()),
                () -> assertEquals(SagaStatus.COMPLETED, saga.sagaStatus()),
                () -> assertEquals(3, saga.nodeCount()),
                () -> assertEquals(SagaRarity.UNCOMMON, saga.sagaRarity()),
                () -> assertNotNull(saga.endedAt())
        );
    }

    @Test
    void shouldUpdateExistingSaga() {
        FakeSagaMapper sagaMapper = new FakeSagaMapper();
        MyBatisSagaRepository sagaRepository = new MyBatisSagaRepository(sagaMapper);
        Saga saga = Saga.restore(
                new SagaId(102),
                new SagaOwnerId(9),
                new SagaName("旧名称"),
                SagaType.LIFE,
                null,
                null,
                SagaStatus.ACTIVE,
                false,
                1,
                SagaRarity.COMMON,
                LocalDateTime.of(2026, 6, 21, 9, 0),
                null
        );
        saga.rename(new SagaName("新名称"));

        Saga savedSaga = sagaRepository.save(saga);

        assertAll(
                () -> assertEquals(saga, savedSaga),
                () -> assertEquals(1, sagaMapper.updateCount),
                () -> assertEquals(102L, sagaMapper.savedRecord.getId()),
                () -> assertEquals("新名称", sagaMapper.savedRecord.getName())
        );
    }

    private static final class FakeSagaMapper implements SagaMapper {

        private SagaRecord recordToFind;
        private SagaRecord savedRecord;
        private int updateCount;

        @Override
        public Optional<SagaRecord> findById(long id) {
            return Optional.ofNullable(recordToFind);
        }

        @Override
        public java.util.List<SagaRecord> findByUserId(long userId) {
            return java.util.List.of();
        }

        @Override
        public java.util.List<SagaRecord> findPublic() {
            return java.util.List.of();
        }

        @Override
        public int insert(SagaRecord sagaRecord) {
            savedRecord = sagaRecord;
            sagaRecord.setId(100L);
            return 1;
        }

        @Override
        public int update(SagaRecord sagaRecord) {
            savedRecord = sagaRecord;
            updateCount++;
            return 1;
        }

        @Override
        public int deleteById(long id) {
            return 1;
        }
    }
}
