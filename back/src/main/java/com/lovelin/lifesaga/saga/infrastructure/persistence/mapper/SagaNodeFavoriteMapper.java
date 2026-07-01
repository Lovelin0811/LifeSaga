package com.lovelin.lifesaga.saga.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SagaNodeFavoriteMapper {

    int countByUserIdAndNodeId(@Param("userId") long userId, @Param("nodeId") long nodeId);

    int insert(@Param("userId") long userId, @Param("nodeId") long nodeId);

    int insertIgnore(@Param("userId") long userId, @Param("nodeId") long nodeId);

    int deleteByUserIdAndNodeId(@Param("userId") long userId, @Param("nodeId") long nodeId);

    List<Long> findNodeIdsByUserIdAndNodeIds(@Param("userId") long userId, @Param("nodeIds") List<Long> nodeIds);

    int deleteByNodeId(@Param("nodeId") long nodeId);

    int deleteBySagaId(@Param("sagaId") long sagaId);
}
