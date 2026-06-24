package com.lovelin.lifesaga.achievement.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AchievementProgressMapper {

    int countSagasByUserId(@Param("userId") long userId);

    List<String> findDistinctSagaTypesByUserId(@Param("userId") long userId);

    int countCompletedSagasByType(@Param("userId") long userId, @Param("type") String type);

    int countSagasByUserIdAndRarity(@Param("userId") long userId, @Param("rarity") String rarity);

    int countNodesWithPhotosBySagaId(@Param("sagaId") long sagaId);

    List<LocalDate> findRecentNodeDates(@Param("userId") long userId, @Param("limit") int limit);
}
