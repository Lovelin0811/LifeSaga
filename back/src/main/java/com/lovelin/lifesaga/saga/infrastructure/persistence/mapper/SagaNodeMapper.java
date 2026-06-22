package com.lovelin.lifesaga.saga.infrastructure.persistence.mapper;

import com.lovelin.lifesaga.saga.infrastructure.persistence.record.SagaNodeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SagaNodeMapper {

    Optional<SagaNodeRecord> findById(@Param("id") long id);

    List<SagaNodeRecord> findBySagaId(@Param("sagaId") long sagaId);

    int insert(SagaNodeRecord sagaNodeRecord);

    int update(SagaNodeRecord sagaNodeRecord);

    int deleteById(@Param("id") long id);

    int deleteBySagaId(@Param("sagaId") long sagaId);
}
