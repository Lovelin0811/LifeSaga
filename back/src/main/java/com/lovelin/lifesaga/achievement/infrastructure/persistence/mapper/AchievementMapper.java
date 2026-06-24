package com.lovelin.lifesaga.achievement.infrastructure.persistence.mapper;

import com.lovelin.lifesaga.achievement.infrastructure.persistence.record.AchievementRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AchievementMapper {

    List<AchievementRecord> findAll();

    Optional<AchievementRecord> findById(@Param("id") long id);

    Optional<AchievementRecord> findByConditionType(@Param("conditionType") String conditionType);
}
