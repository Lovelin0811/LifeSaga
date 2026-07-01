package com.lovelin.lifesaga.saga.infrastructure.persistence.mapper;

import com.lovelin.lifesaga.saga.infrastructure.persistence.record.SagaRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SagaMapper {

    Optional<SagaRecord> findById(@Param("id") long id);

    List<SagaRecord> findByUserId(@Param("userId") long userId);

    List<SagaRecord> findPublic();

    int insert(SagaRecord sagaRecord);

    int update(SagaRecord sagaRecord);

    int recordNodeAdded(@Param("id") long id, @Param("userId") long userId);

    int recordNodeDeleted(@Param("id") long id, @Param("userId") long userId);

    int deleteById(@Param("id") long id);
}
