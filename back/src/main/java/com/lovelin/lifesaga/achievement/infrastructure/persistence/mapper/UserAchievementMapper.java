package com.lovelin.lifesaga.achievement.infrastructure.persistence.mapper;

import com.lovelin.lifesaga.achievement.infrastructure.persistence.record.UserAchievementRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserAchievementMapper {

    List<UserAchievementRecord> findByUserId(@Param("userId") long userId);

    Optional<UserAchievementRecord> findByUserIdAndAchievementId(
            @Param("userId") long userId,
            @Param("achievementId") long achievementId
    );

    int insert(UserAchievementRecord userAchievementRecord);
}
