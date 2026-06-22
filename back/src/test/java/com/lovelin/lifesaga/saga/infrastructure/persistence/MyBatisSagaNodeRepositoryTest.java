package com.lovelin.lifesaga.saga.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaNode;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeDescription;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeId;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeLocation;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeOrder;
import com.lovelin.lifesaga.saga.domain.model.SagaNodePhotos;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTime;
import com.lovelin.lifesaga.saga.domain.model.SagaNodeTitle;
import com.lovelin.lifesaga.saga.infrastructure.persistence.mapper.SagaNodeMapper;
import com.lovelin.lifesaga.saga.infrastructure.persistence.record.SagaNodeRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyBatisSagaNodeRepositoryTest {

    @Test
    void shouldInsertNewSagaNodeAndReturnGeneratedSagaNodeId() {
        FakeSagaNodeMapper sagaNodeMapper = new FakeSagaNodeMapper();
        MyBatisSagaNodeRepository sagaNodeRepository = new MyBatisSagaNodeRepository(sagaNodeMapper, new ObjectMapper());
        SagaNode sagaNode = SagaNode.create(
                new SagaId(10),
                new SagaNodeTitle("东京塔"),
                new SagaNodeOrder(1),
                new SagaNodeDescription("第一次看到东京塔"),
                new SagaNodeLocation("东京"),
                new SagaNodePhotos(List.of("https://example.com/1.jpg", "https://example.com/2.jpg")),
                new SagaNodeTime(LocalDateTime.of(2026, 6, 22, 12, 0))
        );

        SagaNode savedSagaNode = sagaNodeRepository.save(sagaNode);

        assertAll(
                () -> assertEquals(new SagaNodeId(200), savedSagaNode.sagaNodeId()),
                () -> assertEquals(new SagaId(10), savedSagaNode.sagaId()),
                () -> assertEquals(new SagaNodeTitle("东京塔"), savedSagaNode.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeOrder(1), savedSagaNode.sagaNodeOrder()),
                () -> assertEquals("[\"https://example.com/1.jpg\",\"https://example.com/2.jpg\"]", sagaNodeMapper.savedRecord.getPhotos()),
                () -> assertEquals(false, sagaNodeMapper.savedRecord.getIsMilestone())
        );
    }

    @Test
    void shouldRestoreSagaNodeWhenFoundBySagaNodeId() {
        FakeSagaNodeMapper sagaNodeMapper = new FakeSagaNodeMapper();
        SagaNodeRecord sagaNodeRecord = new SagaNodeRecord();
        sagaNodeRecord.setId(201L);
        sagaNodeRecord.setSagaId(11L);
        sagaNodeRecord.setTitle("图书馆");
        sagaNodeRecord.setContent("学习 DDD");
        sagaNodeRecord.setLocation("上海");
        sagaNodeRecord.setNodeTime(LocalDateTime.of(2026, 6, 22, 13, 0));
        sagaNodeRecord.setPhotos("[\"https://example.com/study.jpg\"]");
        sagaNodeRecord.setIsMilestone(true);
        sagaNodeRecord.setSortOrder(2);
        sagaNodeMapper.recordToFind = sagaNodeRecord;
        MyBatisSagaNodeRepository sagaNodeRepository = new MyBatisSagaNodeRepository(sagaNodeMapper, new ObjectMapper());

        Optional<SagaNode> foundSagaNode = sagaNodeRepository.findBySagaNodeId(new SagaNodeId(201));

        assertTrue(foundSagaNode.isPresent());
        SagaNode sagaNode = foundSagaNode.orElseThrow();
        assertAll(
                () -> assertEquals(new SagaNodeId(201), sagaNode.sagaNodeId()),
                () -> assertEquals(new SagaId(11), sagaNode.sagaId()),
                () -> assertEquals(new SagaNodeTitle("图书馆"), sagaNode.sagaNodeTitle()),
                () -> assertEquals(new SagaNodeDescription("学习 DDD"), sagaNode.sagaNodeDescription()),
                () -> assertEquals(new SagaNodeLocation("上海"), sagaNode.sagaNodeLocation()),
                () -> assertEquals(new SagaNodePhotos(List.of("https://example.com/study.jpg")), sagaNode.sagaNodePhotos()),
                () -> assertEquals(new SagaNodeOrder(2), sagaNode.sagaNodeOrder()),
                () -> assertTrue(sagaNode.milestone())
        );
    }

    @Test
    void shouldUpdateExistingSagaNode() {
        FakeSagaNodeMapper sagaNodeMapper = new FakeSagaNodeMapper();
        MyBatisSagaNodeRepository sagaNodeRepository = new MyBatisSagaNodeRepository(sagaNodeMapper, new ObjectMapper());
        SagaNode sagaNode = SagaNode.restore(
                new SagaNodeId(202),
                new SagaId(12),
                new SagaNodeTitle("旧标题"),
                new SagaNodeOrder(1),
                null,
                null,
                null,
                null,
                false
        );
        sagaNode.rename(new SagaNodeTitle("新标题"));
        sagaNode.changeMilestone(true);

        SagaNode savedSagaNode = sagaNodeRepository.save(sagaNode);

        assertAll(
                () -> assertEquals(sagaNode, savedSagaNode),
                () -> assertEquals(1, sagaNodeMapper.updateCount),
                () -> assertEquals(202L, sagaNodeMapper.savedRecord.getId()),
                () -> assertEquals("新标题", sagaNodeMapper.savedRecord.getTitle()),
                () -> assertEquals(true, sagaNodeMapper.savedRecord.getIsMilestone())
        );
    }

    private static final class FakeSagaNodeMapper implements SagaNodeMapper {

        private SagaNodeRecord recordToFind;
        private SagaNodeRecord savedRecord;
        private int updateCount;

        @Override
        public Optional<SagaNodeRecord> findById(long id) {
            return Optional.ofNullable(recordToFind);
        }

        @Override
        public List<SagaNodeRecord> findBySagaId(long sagaId) {
            return List.of();
        }

        @Override
        public int insert(SagaNodeRecord sagaNodeRecord) {
            savedRecord = sagaNodeRecord;
            sagaNodeRecord.setId(200L);
            return 1;
        }

        @Override
        public int update(SagaNodeRecord sagaNodeRecord) {
            savedRecord = sagaNodeRecord;
            updateCount++;
            return 1;
        }

        @Override
        public int deleteById(long id) {
            return 1;
        }

        @Override
        public int deleteBySagaId(long sagaId) {
            return 1;
        }
    }
}
